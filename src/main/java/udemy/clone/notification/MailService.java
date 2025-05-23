package udemy.clone.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import udemy.clone.model.user.UserDto;

@Service
@RequiredArgsConstructor
public class MailService implements NotificationService {
    public static final String SUBJECT = "Hello from Studybel";

    private final JavaMailSender mailSender;
    @Override
    public void send(UserDto userDro, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setText(message);
        mailMessage.setTo(userDro.getEmail());
        mailMessage.setSubject(SUBJECT);
        mailSender.send(mailMessage);
    }
}
