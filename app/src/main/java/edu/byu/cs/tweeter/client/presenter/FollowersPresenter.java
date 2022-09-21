package edu.byu.cs.tweeter.client.presenter;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter {
    private final View view;
    private final FollowService followService;
    private static final int PAGE_SIZE = 10;
    private User lastFollower;



    private boolean hasMorePages;
    private boolean isLoading = false;



    public interface View {
        void displayMessage(String message);
        void setLoadingFooter(boolean value);
        void addFollowers(List<User> followers);
    }

    public FollowersPresenter(View view){
        this.view = view;
        followService = new FollowService();
    }

    public boolean isLoading() {
        return isLoading;
    }
    public boolean HasMorePages() {
        return hasMorePages;
    }

    public void loadMoreItems(User user) {
        isLoading = true;
        view.setLoadingFooter(true);

        followService.loadMoreItemsFollowers(Cache.getInstance().getCurrUserAuthToken(),user,PAGE_SIZE,lastFollower,new GetFollowersObserver());
    }

    private class GetFollowersObserver implements FollowService.GetFollowersObserver{

        @Override
        public void addFollowers(List<User> followers, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
            view.addFollowers(followers);
            FollowersPresenter.this.hasMorePages = hasMorePages;
        }

        @Override
        public void displayErrorMessage(String message) {
            isLoading = false;
            view.displayMessage("Failed to get followers: " + message);
            view.setLoadingFooter(false);

        }

        @Override
        public void displayException(Exception ex) {
            isLoading = false;
            view.displayMessage("Failed to get followers because of exception: " + ex.getMessage());
            view.setLoadingFooter(false);
        }
    }
}
