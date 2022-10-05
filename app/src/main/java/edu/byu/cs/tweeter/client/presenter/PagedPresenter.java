package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter {
    private final PagedView<T> view;
    protected T lastItem;
    UserService userService;
    StatusService statusService;
    FollowService followService;

    protected static final int PAGE_SIZE = 10;

    protected boolean hasMorePages;
    protected boolean isLoading = false;

    public PagedPresenter(PagedView<T> view) {
        this.view = view;
        userService = new UserService();
        statusService = new StatusService();
        followService = new FollowService();
    }

    public interface PagedView<T> extends View {
        void setLoadingFooter(boolean value);
        void addItems(List<T> items);
        void displayUserInfo(User user);
    }

    public void loadMoreItems(User targetUser) {
        isLoading = true;
        view.setLoadingFooter(true);
        getItems(Cache.getInstance().getCurrUserAuthToken(), targetUser, PAGE_SIZE, lastItem);
    }

    public void getUser(String alias){
        userService.getUserProfile(Cache.getInstance().getCurrUserAuthToken(), alias, new GetUserObserver());
    }

    protected abstract void getItems(AuthToken authToken, User targetUser, int pageSize, T lastItem);

    protected abstract String getDescription(boolean errOrEx);

    protected class GetItemsObserver implements PagedObserver<T>{

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            view.addItems(items);
            PagedPresenter.this.hasMorePages = hasMorePages;
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(getDescription(true) + message);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(getDescription(false) + ex.getMessage());
        }
    }

    protected class GetUserObserver implements UserObserver{

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

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }
}
