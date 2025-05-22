package udemy.clone.producer;

public interface EventPublisher<T> {

    void publish(T event);
}
