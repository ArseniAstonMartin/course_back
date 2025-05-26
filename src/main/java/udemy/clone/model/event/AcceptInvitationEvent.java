package udemy.clone.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcceptInvitationEvent {
    private String courseId;
    private String courseTitle;
    private String invitedName;
    private String invitedId;
    private String teacherName;
    private String teacherId;
}