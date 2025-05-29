package udemy.clone.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import udemy.clone.model.event.AcceptInvitationEvent;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class InvitationAcceptMessageBuilder implements MessageBuilder<AcceptInvitationEvent> {

    private final MessageSource messageSource;

    @Override
    public Class<?> getInstance() {
        return AcceptInvitationEvent.class;
    }

    @Override
    public String buildMessage(AcceptInvitationEvent event) {
        return messageSource.getMessage("invitation.accept", new Object[]{event.getTeacherName(),
                event.getInvitedName(), event.getCourseId()}, Locale.UK);
    }
}