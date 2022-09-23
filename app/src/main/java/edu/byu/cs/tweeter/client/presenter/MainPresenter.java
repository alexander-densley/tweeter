package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter {

    private final MainView view;
    private final FollowService followService;
    private final StatusService statusService;
    private final UserService userService;
    private User user;

    private static final String LOG_TAG = "MainActivity";


    public interface MainView{
        void displayMessage(String message);
        void setFollowersCount(int count);
        void setFollowingCount(int count);
        void setFollowButton(boolean isFollower);
        void enableFollowButton(boolean status);
        void updateFollowButton(boolean removed);
        void showPostingToast(boolean toastStatus);
        void logoutUser();
    }

    public MainPresenter(MainView view){
        this.view = view;
        followService = new FollowService();
        statusService = new StatusService();
        userService = new UserService();
    }

    public void statusPost(String post){
        try {
            statusService.poastStatus(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(),
                    post, getFormattedDateTime(), parseURLs(post), parseMentions(post), new PostStatusObserver());
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    public void logoutUser(){
        userService.logout(Cache.getInstance().getCurrUserAuthToken(),new LogoutObserver());
    }

    private class LogoutObserver implements UserService.LogoutObserver{

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to logout: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to logout because of exception: " + ex.getMessage());
        }

        @Override
        public void loggedOutUser() {
            Cache.getInstance().clearCache();
            view.logoutUser();

        }
    }
    private class PostStatusObserver implements StatusService.PostStatusObserver{

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to post status: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to post status because of exception: " + ex.getMessage());
        }

        @Override
        public void displayPosting() {
            view.showPostingToast(false);
            view.displayMessage("Successfully Posted!");
        }
    }

    public void unfollow(User user){
        this.user = user;
        followService.unfollow(Cache.getInstance().getCurrUserAuthToken(),user, new UnfollowObserver());
    }
    public void follow(User user){
        this.user = user;
        followService.follow(Cache.getInstance().getCurrUserAuthToken(),user, new FollowObserver());

    }
    public void isFollower(User user){
        this.user = user;
        followService.isFollower(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser()
        , user, new IsFollowerObserver());

    }

    private class IsFollowerObserver implements FollowService.IsFollowersObserver{

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to determine following relationship: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to determine following relationship because of exception: " + ex.getMessage());
        }

        @Override
        public void setFollowButton(boolean isFollower) {
            view.setFollowButton(isFollower);
        }
    }

    private class FollowObserver implements FollowService.FollowUserObserver{

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to unfollow: " + message);
            view.enableFollowButton(true);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to unfollow because of exception: " + ex.getMessage());
            view.enableFollowButton(true);
        }

        @Override
        public void updateFollowButton(boolean status) {
            updateSelectedUserFollowingAndFollowers(user);
            view.updateFollowButton(status);
            view.enableFollowButton(true);
        }
    }

    private class UnfollowObserver implements FollowService.UnfollowObserver{

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to unfollow: " + message);
            view.enableFollowButton(true);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to unfollow because of exception: " + ex.getMessage());
            view.enableFollowButton(true);
        }

        @Override
        public void updateFollowButton(boolean status) {
            updateSelectedUserFollowingAndFollowers(user);
            view.updateFollowButton(status);
            view.enableFollowButton(true);

        }
    }
    private class GetFollowingCountObserver implements FollowService.GetFollowingCountObserver{

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to get following count: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get following count because of exception: " + ex.getMessage());
        }

        @Override
        public void setFollowingCount(int count) {
            view.setFollowingCount(count);
        }
    }

    private class GetFollowerCountObserver implements FollowService.GetFollowersCountObserver{

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to get followers count: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get followers count because of exception: " + ex.getMessage());
        }

        @Override
        public void setFollowersCount(int count) {
            view.setFollowersCount(count);
        }
    }


    //todo move to presenter
    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    //todo move to presenter
    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    // todo move to presenter
    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    //todo move to presenter
    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    //todo move
    public void updateSelectedUserFollowingAndFollowers(User user) {

        this.user = user;
        followService.updateSelectedUserFollowingAndFollowers(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetFollowerCountObserver(), new GetFollowingCountObserver());

    }
}
