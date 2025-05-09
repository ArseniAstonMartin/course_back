package udemy.clone.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import udemy.clone.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

}
