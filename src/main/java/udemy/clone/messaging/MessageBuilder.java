package udemy.clone.messaging;

import org.springframework.stereotype.Component;

@Component
public interface MessageBuilder<T> {

    Class<?> getInstance();

    String buildMessage(T event);

}