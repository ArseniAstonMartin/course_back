package udemy.clone.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import udemy.clone.model.event.SendInvitationEvent;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class InvitationSendMessageBuilder implements MessageBuilder<SendInvitationEvent> {

    private final MessageSource messageSource;


    @Override
    public Class<?> getInstance() {
        return SendInvitationEvent.class;
    }

    @Override
    public String buildMessage(SendInvitationEvent event) {
        return messageSource.getMessage("invitation.send", new Object[] {event.getInvitedName(),
                event.getTeacherName(), event.getCourseId()}, Locale.UK);
    }
}
