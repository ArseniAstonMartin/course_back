package udemy.clone.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import udemy.clone.messaging.MessageBuilder;
import udemy.clone.model.Course;
import udemy.clone.model.User;
import udemy.clone.model.event.AcceptInvitationEvent;
import udemy.clone.notification.NotificationService;
import udemy.clone.service.CourseService;
import udemy.clone.service.LessonService;
import udemy.clone.service.UserService;

import java.util.List;
@Component
public class InvitationAcceptListener extends AbstractEventListener<AcceptInvitationEvent> {

    private final UserService userService;
    private final CourseService courseService;
    private final LessonService lessonService;

    public InvitationAcceptListener(UserService userService,CourseService courseService,
                                    LessonService lessonService,
                                    List<NotificationService> notificationServices,
                                    List<MessageBuilder<AcceptInvitationEvent>> messageBuilders) {
        super(userService, notificationServices, messageBuilders);
        this.userService = userService;
        this.courseService = courseService;
        this.lessonService = lessonService;
    }

    @KafkaListener(topics = "${spring.kafka.topics.invitation-accept.topic}")
    public void onMessage(AcceptInvitationEvent event) {
        Course course = courseService.findCourseById(event.getCourseId());

        User invitedUser = userService.findUserById(event.getInvitedId());
        User teacherUser = userService.findUserById(event.getTeacherId());
        invitedUser.setCourseIds(List.of(event.getCourseId()));
        //TODO
        String message = super.getMessage(event);
        super.sendNotification(event.getTeacherId(), message);
    }

}
