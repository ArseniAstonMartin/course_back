package udemy.clone.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import udemy.clone.messaging.MessageBuilder;
import udemy.clone.model.event.InvitationEvent;
import udemy.clone.notification.NotificationService;
import udemy.clone.service.UserService;

import java.util.List;
@Component
public class InvitationSendListener extends AbstractEventListener<InvitationEvent> {
    public InvitationSendListener(UserService userService, List<NotificationService> notificationServices,
                                  List<MessageBuilder<InvitationEvent>> messageBuilders) {
        super(userService, notificationServices, messageBuilders);
    }

    @KafkaListener(topics = "${spring.kafka.topics.invitation-send.name}")
    public void onMessage(InvitationEvent event) {
        String message = super.getMessage(event);
        super.sendNotification(event.getInvitedName(), message);
    }
}
