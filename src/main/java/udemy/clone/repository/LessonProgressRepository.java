package udemy.clone.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import udemy.clone.model.util.LessonProgress;

@Repository
public interface LessonProgressRepository extends MongoRepository<LessonProgress, String> {
}
