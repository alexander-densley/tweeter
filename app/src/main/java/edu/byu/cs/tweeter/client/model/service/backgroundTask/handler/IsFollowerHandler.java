package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.IsFollowObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.IsFollowerTask;

public class IsFollowerHandler extends BackgroundTaskHandler<IsFollowObserver> {
    public IsFollowerHandler(IsFollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, IsFollowObserver observer) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.handleSuccess(isFollower);
    }
}
