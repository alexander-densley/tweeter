package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.PagedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;

public class PageNotificationHandler<T> extends BackgroundTaskHandler<PagedObserver<T>> {
    public PageNotificationHandler(PagedObserver<T> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, PagedObserver<T> observer) {
        List<T> items = (List<T>) data.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(PagedTask.MORE_PAGES_KEY);
        observer.handleSuccess(items, hasMorePages);
    }
}
