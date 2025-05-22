package udemy.clone.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import udemy.clone.model.event.InvitationEvent;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class InvitationMessageBuilder implements MessageBuilder<InvitationEvent> {

    private final MessageSource messageSource;


    @Override
    public Class<?> getInstance() {
        return InvitationEvent.class;
    }

    @Override
    public String buildMessage(InvitationEvent event) {
        return messageSource.getMessage("invitation.send", new Object[] {event.getInvitedName(),
                event.getTeacherName(), event.getCourseId()}, Locale.UK);
    }
}
