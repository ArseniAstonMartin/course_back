package udemy.clone.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ElasticsearchConfig {
    @Bean
    public ElasticsearchClient elasticsearchClient() throws IOException {
        ElasticsearchClient client = ElasticsearchClient.of(b -> b.host("http://localhost:9200"));

        if (!client.indices().exists(e -> e.index("courses")).value()) {
            CreateIndexRequest request = new CreateIndexRequest.Builder()
                    .index("courses")
                    .mappings(m -> m
                            .properties("id", p -> p.keyword(k -> k))
                            .properties("imageSource", p -> p.keyword(k -> k))
                            .properties("title", p -> p.text(t -> t))
                            .properties("description", p -> p.text(t -> t))
                            .properties("teacherId", p -> p.keyword(k -> k))
                    ).build();
            client.indices().create(request);
        }

        if (!client.indices().exists(e -> e.index("teachers")).value()) {
            CreateIndexRequest request = new CreateIndexRequest.Builder()
                    .index("teachers")
                    .mappings(m -> m
                            .properties("id", p -> p.keyword(k -> k))
                            .properties("name", p -> p.keyword(k -> k))
                            .properties("email", p -> p.keyword(k -> k))
                            .properties("role", p -> p.keyword(k -> k))
                            .properties("courseIds", p -> p.keyword(k -> k))
                    ).build();
            client.indices().create(request);
        }
        return client;
    }
}
