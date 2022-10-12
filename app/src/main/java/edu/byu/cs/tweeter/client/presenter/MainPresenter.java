package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.CountFollowObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.IsFollowObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter {

    private final MainView view;
    private final FollowService followService;
    private StatusService statusService;
    private final UserService userService;
    private User user;

    private static final String LOG_TAG = "MainActivity";


    public interface MainView extends Presenter.View{
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
        statusService = getStatusService();
        userService = new UserService();
    }

    protected StatusService getStatusService(){
        if(statusService == null){
            statusService = new StatusService();
        }
        return new StatusService();
    }

    public void unfollow(User user){
        this.user = user;
        followService.unfollow(Cache.getInstance().getCurrUserAuthToken(),user, new UnfollowObserver());
    }

    public void updateSelectedUserFollowingAndFollowers(User user) {
        this.user = user;
        followService.updateSelectedUserFollowingAndFollowers(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetFollowerCountObserver(), new GetFollowingCountObserver());
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

    public void logoutUser(){
        userService.logout(Cache.getInstance().getCurrUserAuthToken(),new LogoutObserver());
    }

    public void statusPost(String post){
        try {
            getStatusService().postStatus(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(),
                    post, getFormattedDateTime(), parseURLs(post), parseMentions(post), new PostStatusObserver());
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

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


    private abstract class BaseCountObserver implements CountFollowObserver{

        @Override
        public void handleSuccess(int count) {
            handleCountSuccess(count);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get " + getObserverName() + ": " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get " + getObserverName() + " because of exception: " + ex.getMessage());
        }
        public abstract void handleCountSuccess(int count);
        protected abstract String getObserverName();
    }

    private class GetFollowingCountObserver extends BaseCountObserver {
        @Override
        public void handleCountSuccess(int count) {
            view.setFollowingCount(count);
        }

        @Override
        protected String getObserverName() {
            return "following count";
        }
    }

    private class GetFollowerCountObserver extends BaseCountObserver {
        @Override
        public void handleCountSuccess(int count) {
            view.setFollowersCount(count);
        }

        @Override
        protected String getObserverName() {
            return "followers count";
        }
    }

    public abstract class BaseSimpleNotificationObserver implements SimpleNotificationObserver {
        @Override
        public void handleSuccess() {
            handleSimpleSuccess();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get " + getObserverName() + ": " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get " + getObserverName() + " because of exception: " + exception.getMessage());
        }

        public abstract void handleSimpleSuccess();
        protected abstract String getObserverName();
    }

    private class LogoutObserver extends BaseSimpleNotificationObserver {
        @Override
        public void handleSimpleSuccess() {
            Cache.getInstance().clearCache();
            view.logoutUser();
        }

        @Override
        protected String getObserverName() {
            return "logout";
        }
    }
    private class PostStatusObserver extends BaseSimpleNotificationObserver {

        @Override
        public void handleSimpleSuccess() {
            view.showPostingToast(false);
            view.displayMessage("Successfully Posted!");
        }
        @Override
        protected String getObserverName() {
            return "post status";
        }
    }

    private class FollowObserver extends BaseSimpleNotificationObserver {
        @Override
        public void handleSimpleSuccess() {
            updateSelectedUserFollowingAndFollowers(user);
            view.updateFollowButton(false);
            view.enableFollowButton(true);
        }
        @Override
        protected String getObserverName() {
            return "follow";
        }
    }

    private class UnfollowObserver extends BaseSimpleNotificationObserver {
        @Override
        public void handleSimpleSuccess() {
            updateSelectedUserFollowingAndFollowers(user);
            view.updateFollowButton(true);
            view.enableFollowButton(true);
        }
        @Override
        protected String getObserverName() {
            return "unfollow";
        }
    }

    private class IsFollowerObserver implements IsFollowObserver {

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to determine following relationship: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to determine following relationship because of exception: " + ex.getMessage());
        }

        @Override
        public void handleSuccess(boolean isFollower) {
            view.setFollowButton(isFollower);
        }
    }
}
