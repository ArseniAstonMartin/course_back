package udemy.clone.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import udemy.clone.messaging.MessageBuilder;
import udemy.clone.model.User;
import udemy.clone.model.user.UserDto;
import udemy.clone.notification.NotificationService;
import udemy.clone.service.UserService;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbstractEventListener <T> {
    private final UserService userService;
    private final List<NotificationService> notificationServices;
    private final List<MessageBuilder<T>> messageBuilders;

    public String getMessage(T event) {
        return messageBuilders.stream()
                .filter(messageBuilder -> messageBuilder.getInstance() == event.getClass())
                .findFirst()
                .map(tMessageBuilder -> tMessageBuilder.buildMessage(event))
                .orElseThrow(() -> new IllegalArgumentException("Not message builder found for the given event type: " + event.getClass().getName())).toString();
    }
    public void sendNotification(String id, String message) {
        UserDto user = userService.findUserDtoById(id);
        notificationServices.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Notification service not found"))
                .send(user, message);

    }

}
