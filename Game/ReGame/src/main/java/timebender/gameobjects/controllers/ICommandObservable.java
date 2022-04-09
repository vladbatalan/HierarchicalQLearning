package timebender.gameobjects.controllers;

public interface ICommandObservable {
    void attachObserver(ICommandObserver observer);
    void detachObserver(ICommandObserver observer);
    void clearObservers();
}
