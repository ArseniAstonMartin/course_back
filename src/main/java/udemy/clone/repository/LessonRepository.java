package udemy.clone.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import udemy.clone.model.Lesson;
import udemy.clone.model.User;

import java.util.List;

@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {
    @Query(value = "{ 'id': ?0, 'lessonProgress.status': ?1 }", fields = "{ 'lessonIds': 1}")
    List<Lesson> findStudentsLessonsByProgress(String userId, String lessonStatus);

    @Query(value = "{ 'lessonProgress.lessonId': ?0, 'lessonProgress.status': ?1 }")
    List<User> findLessonsUsersByProgress(String lessonId, String lessonStatus);
}
