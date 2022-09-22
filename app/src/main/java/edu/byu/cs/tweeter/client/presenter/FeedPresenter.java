package edu.byu.cs.tweeter.client.presenter;

import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter {
    private FeedView view;
    private StatusService statusService;
    private UserService userService;
    private static final int PAGE_SIZE = 10;
    private Status lastStatus;
    private boolean hasMorePages;

    private boolean isLoading = false;




    public interface FeedView {
        void displayMessage(String message);
        void setLoadingFooter(boolean value);
        void addFeed(List<Status> statuses);

        void displayUserInfo(User user);
    }

    public FeedPresenter(FeedView view){
        this.view = view;
        statusService = new StatusService();
        userService = new UserService();
    }

    public boolean isLoading() {
        return isLoading;
    }
    public boolean HasMorePages() {
        return hasMorePages;
    }


    private class GetFeedObserver implements StatusService.GetFeedObserver{

        @Override
        public void addFeed(List<Status> statuses, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
            view.addFeed(statuses);
            FeedPresenter.this.hasMorePages = hasMorePages;

        }

        @Override
        public void displayErrorMessage(String message) {
            isLoading = false;
            view.displayMessage("Failed to get feed: " + message);
            view.setLoadingFooter(false);
        }
        @Override
        public void displayException(Exception ex) {
            isLoading = false;
            view.displayMessage("Failed to get feed because of exception: " + ex.getMessage());
            view.setLoadingFooter(false);

        }
    }
    public void loadMoreItems(User user) {
        isLoading = true;
        view.setLoadingFooter(true);
        statusService.loadMoreItemsFeed(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastStatus, new GetFeedObserver());
    }

    private class GetUserObserver implements UserService.GetUserObserver{

        @Override
        public void displayErrorMessage(String message) {
            isLoading = false;
            view.displayMessage("Failed to get user's profile: " +  message);
            view.setLoadingFooter(false);
        }

        @Override
        public void displayException(Exception ex) {
            isLoading = false;
            view.displayMessage("Failed to get user's profile because of exception: " + ex.getMessage());
            view.setLoadingFooter(false);
        }

        @Override
        public void returnUser(User user) {
            view.displayUserInfo(user);

        }
    }
    public void getUserProfile(TextView userAlias) {
        userService.getUserProfile(Cache.getInstance().getCurrUserAuthToken(), userAlias.getText().toString(), new GetUserObserver());
    }

}
