package udemy.clone.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import udemy.clone.messaging.MessageBuilder;
import udemy.clone.model.event.AcceptInvitationEvent;
import udemy.clone.notification.NotificationService;
import udemy.clone.service.LessonProgressService;
import udemy.clone.service.UserService;

import java.util.List;
@Component
public class InvitationAcceptListener extends AbstractEventListener<AcceptInvitationEvent> {

    private final LessonProgressService lessonProgressService;

    public InvitationAcceptListener(UserService userService, LessonProgressService lessonProgressService,
                                    List<NotificationService> notificationServices,
                                    List<MessageBuilder<AcceptInvitationEvent>> messageBuilders) {
        super(userService, notificationServices, messageBuilders);
        this.lessonProgressService = lessonProgressService;
    }

    @KafkaListener(topics = "${spring.kafka.topics.invitation-accept.topic}")
    public void onMessage(AcceptInvitationEvent event) {
        lessonProgressService.addLessonProgress(event);
        String message = super.getMessage(event);
        super.sendNotification(event.getTeacherId(), message);
    }
}