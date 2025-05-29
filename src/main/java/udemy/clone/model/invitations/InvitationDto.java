package udemy.clone.model.invitations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import udemy.clone.model.enums.InvitationStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InvitationDto {
    private String id;
    @NotBlank(message = "You have not specified the course you want to invite")
    private String courseId;

    private InvitationStatus status;
    @NotNull
    @NotBlank
    private String senderId;
    @NotNull
    @NotBlank
    private String recipientId;
    private LocalDateTime createdAt;
}
