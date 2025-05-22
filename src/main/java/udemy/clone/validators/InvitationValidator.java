package udemy.clone.validators;

import org.springframework.stereotype.Component;
import udemy.clone.model.Course;
import udemy.clone.model.Invitation;
import udemy.clone.model.User;
import udemy.clone.model.enums.InvitationStatus;

@Component
public class InvitationValidator {

    public void validateForSendInvitation(Invitation invitation, Course course, User sender, User recipient) {
        if (course.getStudentsIds().contains(recipient.getId())) {
            throw new IllegalArgumentException("You cannot send invitations to participants participating in the course.");
        }
        if (!sender.getRole().equals(User.Role.TEACHER)) {
            throw new IllegalArgumentException("You are not a teacher to send an invitation.");
        }
    }

    public void validateForAcceptOrRejectInvitation(Invitation invitation, User recipient) {
        if (!invitation.getRecipient().getId().equals(recipient.getId())) {
            throw new IllegalArgumentException("You are not the recipient of the invitation!");
        }
        if(invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation status " + invitation.getStatus() + " is not pending");
        }
    }

    public void validateForCancelInvitation(Invitation invitation, User sender) {
        if (!invitation.getSender().equals(sender.getId())) {
            throw new IllegalArgumentException("You cannot cancel the invitation!");
        }
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation status " + invitation.getStatus() + " is not pending");
        }
    }
}
