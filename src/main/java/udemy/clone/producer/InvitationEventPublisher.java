package udemy.clone.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import udemy.clone.model.event.InvitationEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvitationEventPublisher implements EventPublisher<InvitationEvent> {

    @Value("${spring.kafka.producer.invitation-send.topic}")
    private String TOPIC;

    private final KafkaTemplate<String, Object> invitationKafkaTemplate;

    @Override
    public void publish(InvitationEvent event) {
        invitationKafkaTemplate.send(TOPIC, event);
        log.info("Published invitation event: {}", event.getClass().getName());
    }

    @Override
    public Class<?> getInstance() {
        return InvitationEvent.class;
    }
}
