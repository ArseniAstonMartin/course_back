package udemy.clone.notification;

import org.springframework.stereotype.Component;
import udemy.clone.model.user.UserDto;

@Component
public interface NotificationService {

    void send(UserDto userDro, String message);

}
