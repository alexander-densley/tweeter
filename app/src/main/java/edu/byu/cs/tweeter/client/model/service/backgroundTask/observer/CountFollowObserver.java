package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

public interface CountFollowObserver extends ServiceObserver{
    void handleSuccess(int count);
}
