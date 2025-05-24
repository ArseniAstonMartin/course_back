package udemy.clone.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.ScriptSource;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Like;
import co.elastic.clients.elasticsearch._types.query_dsl.MoreLikeThisQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Service;
import udemy.clone.model.course.CourseDto;
import udemy.clone.model.user.UserDto;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.ContentDisposition.inline;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticService {
    private final ElasticsearchClient esClient;

    public void indexDocument(Document document, String index) throws IOException {
        String docId = document.get("_id").toString();
        document.remove("_id");
        esClient.index(i -> i
                .index(index)
                .id(docId)
                .document(document)
                .refresh(Refresh.True)
        );
        log.info("Индексирован документ ID={}", docId);
    }

    public void deleteDocument(Document document, String index) throws IOException {
        String docId = document.getObjectId("_id").toHexString();
        DeleteRequest request = DeleteRequest.of(d -> d
                .index(index)
                .id(docId)
        );
        esClient.delete(request);
        log.info("Удалён документ ID=" + docId);
    }

    public List<CourseDto> findMoreLikeThisCourses(String courseId) {
        Query moreLikeThisQuery = Query.of(q -> q.moreLikeThis(
           MoreLikeThisQuery.of(
                   mlt -> mlt
                           .fields(List.of("title", "description")) // add comparing by lesson contents
                           .like(Like.of(l -> l.document(doc -> doc.index("courses").id(courseId))))
                           .minTermFreq(1)
                           .maxQueryTerms(12)
           )
        ));
        SearchResponse<CourseDto> response;
        try {
            response = esClient.search(s -> s
                            .index("courses")
                            .query(moreLikeThisQuery),
                    CourseDto.class
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return response.hits().hits().stream().map(Hit::source).toList();
    }

    public List<CourseDto> findCoursesByFuzzyQuery(String query) {
        Query fuzzyQuery = Query.of(q -> q.multiMatch(multi -> multi
                .query(query)
                .fields(List.of("title", "description")) // Поля, где искать
                .fuzziness("AUTO") // Автоматическая степень нечеткости
                .operator(Operator.Or) // Условие "И" для более точного поиска
        ));

        SearchResponse<CourseDto> response;
        try {
            response = esClient.search(s -> s
                            .index("courses")
                            .query(fuzzyQuery), // Используем fuzzy query
                    CourseDto.class
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return response.hits().hits().stream().map(Hit::source).toList();
    }

    public List<UserDto> findRelevantTeachers(String query) {
        List<FieldValue> relevantCourseIds = findCoursesByFuzzyQuery(query).stream()
                .map(CourseDto::getId)
                .map(FieldValue::of)
                .toList();
        if (relevantCourseIds.isEmpty()) return List.of();

        Query teachersQuery = Query.of(q -> q.functionScore(f -> f
                .query(Query.of(q2 -> q2.terms(t -> t.field("courseIds").terms(terms -> terms.value(relevantCourseIds)))))
                .functions(fn -> fn.scriptScore(ss -> ss.script(
                        script -> script.source("doc['courseIds'].size()")))
                )
                .boostMode(FunctionBoostMode.Sum) // Суммируем значения
        ));
        new Script.Builder();

        SearchResponse<UserDto> response;
        try {
            response = esClient.search(s -> s
                            .index("teachers") // Индекс учителей
                            .query(teachersQuery)
                            .sort(sort -> sort.field(f -> f.field("_score").order(SortOrder.Desc))), // Сортировка по релевантности
                    UserDto.class);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка поиска учителей: " + e.getMessage());
        }

        return response.hits().hits().stream().map(Hit::source).toList();
    }
}
