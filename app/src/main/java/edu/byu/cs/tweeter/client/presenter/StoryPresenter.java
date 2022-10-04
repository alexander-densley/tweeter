package edu.byu.cs.tweeter.client.presenter;


import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter {
    private StoryView view;
    private StatusService statusService;
    private UserService userService;
    private static final int PAGE_SIZE = 10;
    private Status lastStatus;

    public boolean HasMorePages() {
        return hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    private boolean hasMorePages;
    private boolean isLoading = false;

    public interface StoryView{
        void displayMessage(String message);
        void setLoadingFooter(boolean value);
        void addStory(List<Status> statuses);

        void displayUserInfo(User user);
    }

    public StoryPresenter(StoryView view){
        this.view = view;
        statusService = new StatusService();
        userService = new UserService();
    }

    private class GetStoryObserver implements PagedObserver<Status> {

        @Override
        public void handleSuccess(List statuses, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastStatus = (statuses.size() > 0) ? (Status) statuses.get(statuses.size() - 1) : null;
            view.addStory(statuses);
            StoryPresenter.this.hasMorePages = hasMorePages;

        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.displayMessage("Failed to get story: " + message);
            view.setLoadingFooter(false);

        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.displayMessage("Failed to get story because of exception: " + ex.getMessage());
            view.setLoadingFooter(false);
        }
    }
    public void loadMoreItems(User user){
        isLoading = true;
        view.setLoadingFooter(true);
        statusService.loadMoreItemsStory(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastStatus, new GetStoryObserver());
    }
    private class GetUserObserver implements UserObserver {

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.displayMessage("Failed to get user's profile: " +  message);
            view.setLoadingFooter(false);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.displayMessage("Failed to get user's profile because of exception: " + ex.getMessage());
            view.setLoadingFooter(false);
        }

        @Override
        public void handleSuccess(User user) {
            view.displayUserInfo(user);
        }
    }
    public void getUserProfile(String userAlias) {
        userService.getUserProfile(Cache.getInstance().getCurrUserAuthToken(), userAlias, new StoryPresenter.GetUserObserver());
    }

}
