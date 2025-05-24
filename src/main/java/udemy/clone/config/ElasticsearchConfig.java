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
                                    .properties("name", p -> p.keyword(k -> k))
                                    .properties("", p -> p.nested(k -> k))
                                    .properties("", p -> p.double_(d -> d))
                                    .properties("", p -> p.text(t -> t))
                            // TO-DO: Define all fields
                    ).build();
            client.indices().create(request);
        }

        if (!client.indices().exists(e -> e.index("users")).value()) {
            CreateIndexRequest request = new CreateIndexRequest.Builder()
                    .index("users")
                    .mappings(m -> m
                                    .properties("name", p -> p.keyword(k -> k))
                                    .properties("", p -> p.nested(k -> k))
                            // TO-DO: Define all fields
                    ).build();
            client.indices().create(request);
        }

        return client;
    }
}
