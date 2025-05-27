package udemy.clone.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.Like;
import co.elastic.clients.elasticsearch._types.query_dsl.MoreLikeThisQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import udemy.clone.model.User;
import udemy.clone.model.elastic.CourseDocument;
import udemy.clone.model.elastic.UserDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticService {
    private final ElasticsearchClient esClient;

    public void indexCourse(CourseDocument courseDocument) {
        if (courseDocument == null) {
            return;
        }
        try {
            esClient.index(i -> i
                    .index("courses")
                    .id(courseDocument.getId())
                    .document(courseDocument)
                    .refresh(Refresh.True)
            );
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.info("Индексирован курс ID={}", courseDocument.getId());
    }

    public void indexTeacher(UserDocument user) {
        if (user == null || user.getRole().equals(User.Role.STUDENT)) {
            return;
        }
        try {
            esClient.index(i -> i
                    .index("teachers")
                    .id(user.getId())
                    .document(user)
                    .refresh(Refresh.True)
            );
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.info("Индексирован юзер ID={}", user.getId());
    }

    public void deleteDocument(Document document, String index) {
        String docId = document.getObjectId("_id").toHexString();
        DeleteRequest request = DeleteRequest.of(d -> d
                .index(index)
                .id(docId)
        );
        try {
            esClient.delete(request);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.info("Удалён документ ID=" + docId);
    }

    public Page<CourseDocument> findMoreLikeThisCourses(String courseId, int page, int pageSize) {
        Like like = Like.of(l -> l.document(doc -> doc.id(courseId).index("courses")));
        Query moreLikeThisQuery = Query.of(q -> q.moreLikeThis(
                MoreLikeThisQuery.of(
                        mlt -> mlt
                                .fields(List.of("title", "description"))
                                .like(like)
                                .minTermFreq(1)
                                .maxQueryTerms(20)
                                .minDocFreq(1)
                                .minimumShouldMatch("30%")
                                .boost(2.0f)
                )
        ));
        SearchResponse<CourseDocument> response;
        try {
            response = esClient.search(s -> s
                            .index("courses")
                            .query(moreLikeThisQuery)
                            .from(page * pageSize)
                            .size(pageSize),
                    CourseDocument.class
            );
            System.out.println(response);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        List<CourseDocument> results = response.hits().hits().stream().map(Hit::source).toList();
        long total = response.hits().total() != null ? response.hits().total().value() : results.size();
        return new PageImpl<>(results, PageRequest.of(page, pageSize), total);
    }

    public Page<CourseDocument> findCoursesByFuzzyQuery(String query, int page, int pageSize) {
        Query fuzzyQuery = Query.of(q -> q.multiMatch(multi -> multi
                .query(query)
                .fields(List.of("title", "description")) // Поля, где искать
                .fuzziness("AUTO") // Автоматическая степень нечеткости
                .operator(Operator.Or) // Условие "И" для более точного поиска
        ));

        SearchResponse<CourseDocument> response;
        try {
            response = esClient.search(s -> s
                            .index("courses")
                            .query(fuzzyQuery)
                            .from(page * pageSize)
                            .size(pageSize),
                    CourseDocument.class
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        List<CourseDocument> results = response.hits().hits().stream().map(Hit::source).toList();
        long total = response.hits().total() != null ? response.hits().total().value() : results.size();
        return new PageImpl<>(results, PageRequest.of(page, pageSize), total);
    }

    public Page<UserDocument> searchTeachersByCourseRelevance(String searchText, int page, int pageSize) {
        Query courseQuery = MultiMatchQuery.of(m -> m
                .fields("title", "description")
                .query(searchText)
        )._toQuery();
        SearchResponse<Void> response;
        try {
            response = esClient.search(s -> s
                            .index("courses")
                            .size(0)
                            .query(courseQuery)
                            .aggregations("teachers_aggregation", a -> a
                                    .terms(t -> t.field("teacherId").size(1000))
                                    .aggregations("total_score", subAgg -> subAgg
                                            .sum(sum -> sum.field("_score"))
                                    )
                                    .aggregations("sorted_teachers", subAgg -> subAgg
                                            .bucketSort(bs -> bs
                                                    .sort(sort -> sort.field(so -> so
                                                            .field("total_score")
                                                            .order(SortOrder.Desc)
                                                    ))
                                                    .from(page * pageSize)
                                                    .size(pageSize)
                                            )
                                    )
                            ),
                    Void.class
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        List<UserDocument> sortedTeachers = new ArrayList<>();
        var teacherBuckets = response.aggregations()
                .get("teachers_aggregation")
                .sterms()
                .buckets()
                .array();

        for (StringTermsBucket bucket : teacherBuckets) {
            String teacherId = bucket.key()._get().toString();
            GetResponse<UserDocument> teacherResp = null;
            try {
                teacherResp = esClient.get(g -> g
                        .index("teachers")
                        .id(teacherId), UserDocument.class);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            if (teacherResp.found()) {
                sortedTeachers.add(teacherResp.source());
            }
        }
        long total = teacherBuckets.size();
        return new PageImpl<>(sortedTeachers, PageRequest.of(page, pageSize), total);
    }

    public Page<CourseDocument> findAllCourses(int page, int pageSize) {
        SearchResponse<CourseDocument> response;
        try {
            response = esClient.search(s -> s
                            .index("courses")
                            .query(Query.of(q -> q.matchAll(m -> m)))
                            .from(page*pageSize)
                            .size(pageSize),
                    CourseDocument.class
            );
        } catch (IOException e) {
            log.error("Ошибка при поиске курсов: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        List<CourseDocument> results = response.hits().hits().stream().map(Hit::source).toList();
        long total = response.hits().total() != null ? response.hits().total().value() : results.size();
        return new PageImpl<>(results, PageRequest.of(page, pageSize), total);
    }
}
