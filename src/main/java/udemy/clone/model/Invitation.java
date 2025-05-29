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
    private String courseId;
    private InvitationStatus status;
    private String senderId;
    private String recipientId;
    private LocalDateTime createdAt;

}
