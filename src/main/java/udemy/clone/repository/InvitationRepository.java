package udemy.clone.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import udemy.clone.model.Invitation;
@Repository
public interface InvitationRepository extends MongoRepository<Invitation, String> {
}
