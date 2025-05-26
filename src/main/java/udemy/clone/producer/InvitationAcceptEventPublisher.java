package udemy.clone.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import udemy.clone.model.event.AcceptInvitationEvent;
@Slf4j
@Component
@RequiredArgsConstructor
public class InvitationAcceptEventPublisher implements EventPublisher<AcceptInvitationEvent> {
    @Value("${spring.kafka.producer.invitation-accept.topic}")
    private String TOPIC;

    private final KafkaTemplate<String, Object> invitationAcceptProducer;
    @Override
    public void publish(AcceptInvitationEvent event) {
        invitationAcceptProducer.send(TOPIC, event);
        log.info("Published invitation event: {}", event.getClass().getName());
    }

    @Override
    public Class<?> getInstance() {
        return AcceptInvitationEvent.class;
    }
}
