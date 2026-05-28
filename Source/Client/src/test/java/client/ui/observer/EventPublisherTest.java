package client.ui.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

class EventPublisherTest {

    @Test
    void notifyObservers_callsRegisteredObserver() {
        EventPublisher<String> publisher = new EventPublisher<>();
        AtomicReference<String> receivedEvent = new AtomicReference<>();

        publisher.addObserver(receivedEvent::set);

        publisher.notifyObservers("game state updated");

        assertEquals("game state updated", receivedEvent.get());
    }

    @Test
    void notifyObservers_callsAllRegisteredObservers() {
        EventPublisher<String> publisher = new EventPublisher<>();
        AtomicInteger callCounter = new AtomicInteger(0);

        publisher.addObserver(event -> callCounter.incrementAndGet());
        publisher.addObserver(event -> callCounter.incrementAndGet());

        publisher.notifyObservers("event");

        assertEquals(2, callCounter.get());
    }

    @Test
    void removeObserver_removedObserverIsNotCalled() {
        EventPublisher<String> publisher = new EventPublisher<>();
        AtomicInteger callCounter = new AtomicInteger(0);

        Observer<String> observer = event -> callCounter.incrementAndGet();

        publisher.addObserver(observer);
        publisher.removeObserver(observer);

        publisher.notifyObservers("event");

        assertEquals(0, callCounter.get());
    }

    @Test
    void addObserver_withNullObserver_doesNotFail() {
        EventPublisher<String> publisher = new EventPublisher<>();

        publisher.addObserver(null);
        publisher.notifyObservers("event");

        assertEquals(0, 0);
    }
}