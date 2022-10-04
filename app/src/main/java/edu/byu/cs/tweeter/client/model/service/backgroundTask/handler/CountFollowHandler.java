package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.CountFollowObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.GetCountTask;

public class CountFollowHandler extends BackgroundTaskHandler<CountFollowObserver> {

    public CountFollowHandler(CountFollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, CountFollowObserver observer) {
        int count = data.getInt(GetCountTask.COUNT_KEY);
        observer.handleSuccess(count);
    }
}
