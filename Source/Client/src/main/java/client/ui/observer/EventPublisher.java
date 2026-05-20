package client.ui.observer;

import java.util.ArrayList;
import java.util.List;

public class EventPublisher<T> implements Observable<T> {
    private final List<Observer<T>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<T> observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    public void notifyObservers(T event) {
        for (Observer<T> observer : observers) {
            observer.update(event);
        }
    }
}