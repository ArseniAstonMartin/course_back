package udemy.clone.validators;

import org.springframework.stereotype.Component;
import udemy.clone.model.Course;
import udemy.clone.model.Invitation;
import udemy.clone.model.User;
import udemy.clone.model.enums.InvitationStatus;

import java.util.*;

@Component
public class InvitationValidator {

    public void validateForSendInvitation(Course course, User sender, User recipient) {
        Set<String> studentsIds = course.getStudentsIds() != null ? course.getStudentsIds() : new HashSet<String>();
        if (studentsIds.contains(recipient.getId())) {
            throw new IllegalArgumentException("You cannot send invitations to participants participating in the course.");
        }
    }

    public void validateForAcceptOrRejectInvitation(Invitation invitation, User recipient) {
        List<String> courseIds = recipient.getCourseIds() != null ? recipient.getCourseIds() : new ArrayList<>();
        Collections.sort(courseIds);
        if (Collections.binarySearch(courseIds, invitation.getId()) > 0) {
            throw new IllegalArgumentException("You are already a participant of the course!");
        }
        if(!invitation.getStatus().equals(InvitationStatus.PENDING)) {
            throw new IllegalArgumentException("Invitation status " + invitation.getStatus() + " is not pending");
        }
    }

    public void validateForCancelInvitation(Invitation invitation) {
        if (!invitation.getStatus().equals(InvitationStatus.PENDING)) {
            throw new IllegalArgumentException("Invitation status " + invitation.getStatus() + " is not pending");
        }
    }
}
