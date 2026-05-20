package client.ui.observer;

@FunctionalInterface
public interface Observer<T> {
    void update(T event);
}