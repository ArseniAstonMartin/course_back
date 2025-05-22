package udemy.clone.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ElasticsearchConfig {
    @Bean
    public ElasticsearchClient elasticsearchClient() throws IOException {
        return ElasticsearchClient.of(b -> b.host("http://localhost:9200"));
    }
}
