package udemy.clone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import udemy.clone.model.enums.InvitationStatus;

import java.time.LocalDateTime;
@Document(collection = "invitations")
@Data
@Builder
@AllArgsConstructor
public class Invitation {
    @Id
    private String id;
    private Course course;
    private InvitationStatus status;
    private User sender;
    private User recipient;
    private LocalDateTime createdAt;

}
