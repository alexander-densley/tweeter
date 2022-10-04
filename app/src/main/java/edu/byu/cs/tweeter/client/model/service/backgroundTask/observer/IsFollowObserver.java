package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

import java.util.List;

public interface IsFollowObserver extends ServiceObserver{
    void handleSuccess(boolean isFollower);

}
