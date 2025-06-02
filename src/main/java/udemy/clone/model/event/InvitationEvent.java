package udemy.clone.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationEvent {
    private String courseId;
    private String courseTitle;
    private String invitedId;
    private String invitedName;
    private String teacherId;
    private String teacherName;
}