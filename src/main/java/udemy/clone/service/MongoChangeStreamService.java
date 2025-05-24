package udemy.clone.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MongoChangeStreamService {
    private final MongoClient mongoClient;
    private final ElasticService esSyncService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final List<String> collections = List.of("user", "courses");

    @PostConstruct
    public void init() {
        for (String collectionName : collections) {
            executorService.submit(() -> watchChanges(collectionName));
        }
    }

    private void watchChanges(String collectionName) {
        log.info("I AM HERE 0");
        MongoDatabase database = mongoClient.getDatabase("studybel");
        MongoCollection<Document> collection = database.getCollection(collectionName);
        var a = collection.watch();
        try (MongoCursor<ChangeStreamDocument<Document>> cursor = a.iterator()) {
            log.info("I AM HERE 1");
            while (cursor.hasNext()) {
                ChangeStreamDocument<Document> change = cursor.next();
                log.info("I AM HERE 2");
                executorService.submit(() -> handleSync(change.getFullDocument(), collectionName));
            }
        }
    }

    private void handleSync(Document change, String indexName) {
        try {
            if ("delete".equals(change.getString("operationType"))) {
                esSyncService.deleteDocument(change, indexName);
            } else {
                esSyncService.indexDocument(change, indexName);
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e.getCause(), e.getStackTrace());
        }
    }
}
