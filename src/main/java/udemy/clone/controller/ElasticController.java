package udemy.clone.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udemy.clone.model.course.CourseCreateDto;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static co.elastic.clients.elasticsearch._types.query_dsl.LikeBuilders.document;

@RestController
@RequestMapping("/elastic")
@RequiredArgsConstructor
public class ElasticController {
    private final ElasticsearchClient esClient;

    @GetMapping("/get")
    @PreAuthorize("isAnonymous()")
    public List<Object> getObjects() {
        SearchResponse<Object> search;
        try {
            search = esClient.search(s -> s
                            .index("courses")
                            .query(q -> q
                                    .term(t -> t
                                            .field("title")
                                            .value(v -> v.stringValue("Machine Learning"))
                                    )),
                    Object.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return search.hits().hits().stream().map(Hit::source).toList();
    }

    @GetMapping("/getAllCourses")
    @PreAuthorize("isAnonymous()")
    public List<Object> getAllCourses() {
        SearchResponse<Object> search;
        try {
            search = esClient.search(s -> s
                            .index("courses")
                            .query(q -> q.matchAll(m -> m)), // Fetches all documents
                    Object.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return search.hits().hits().stream().map(Hit::source).toList();
    }

}
