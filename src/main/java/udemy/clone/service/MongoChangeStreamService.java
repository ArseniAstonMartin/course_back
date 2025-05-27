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
import udemy.clone.model.User;
import udemy.clone.model.elastic.CourseDocument;
import udemy.clone.model.elastic.UserDocument;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class MongoChangeStreamService {
    private final MongoClient mongoClient;
    private final ElasticService esSyncService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final Map<String, Consumer<Document>> collections = Map.of(
            "users", this::processUserDocument,
            "courses", this::processCourseDocument
    );

    @PostConstruct
    public void init() {
        for (Map.Entry<String, Consumer<Document>> entry : collections.entrySet()) {
            executorService.submit(() -> watchChanges(entry.getKey(), entry.getValue()));
        }
    }

    private void watchChanges(String collectionName, Consumer<Document> documentProcessor) {
        MongoDatabase database = mongoClient.getDatabase("studybel");
        MongoCollection<Document> collection = database.getCollection(collectionName);
        try (MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator()) {
            while (cursor.hasNext()) {
                ChangeStreamDocument<Document> change = cursor.next();
                executorService.submit(() -> documentProcessor.accept(change.getFullDocument()));
            }
        }
    }

    private void processUserDocument(Document user) {
        if (user.get("role").equals(User.Role.STUDENT.toString())) {
            return;
        }
        if ("delete".equals(user.getString("operationType"))) {
            esSyncService.deleteDocument(user, "courses");
        }
        log.info("I AM HERE");
        UserDocument userDocument = UserDocument.builder()
                .id(user.get("_id").toString())
                .name(user.getString("name"))
                .email(user.getString("email"))
                .role(User.Role.valueOf(user.getString("role")))
                .courseIds(user.getList("courseIds", String.class))
                .build();
        esSyncService.indexTeacher(userDocument);
    }

    private void processCourseDocument(Document course) {
        log.info("Processing document {}", course.toJson());
        if ("delete".equals(course.getString("operationType"))) {
            esSyncService.deleteDocument(course, "courses");
        }
        String courseId = course.get("_id").toString();
        CourseDocument courseDocument = CourseDocument.builder()
                .id(courseId)
                .imageSource("minio-source-for-image") //TO-DO
                .title(course.getString("title"))
                .description(course.getString("description"))
                .teacherId(course.getString("teacherId"))
                .build();
        esSyncService.indexCourse(courseDocument);
    }
}
