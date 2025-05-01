package udemy.clone.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import udemy.clone.model.Course;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
}
