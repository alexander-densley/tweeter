package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.CountFollowHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PageNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.CountFollowObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.IsFollowObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.UnfollowTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends Service{


    public void loadMoreItemsFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, PagedObserver<User> observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollower, new PageNotificationHandler(observer));
        runTask(getFollowersTask);
    }
    public void unfollow(AuthToken currUserAuthToken, User user, SimpleNotificationObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken,
                user, new SimpleNotificationHandler(observer));
        runTask(unfollowTask);
    }
    public void follow(AuthToken currUserAuthToken, User user, SimpleNotificationObserver observer){
        FollowTask followTask = new FollowTask(currUserAuthToken,
                user, new SimpleNotificationHandler(observer));
        runTask(followTask);
    }

    public void updateSelectedUserFollowingAndFollowers(AuthToken currUserAuthToken, User user, CountFollowObserver followerCountObserver, CountFollowObserver followingCountObserver) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(currUserAuthToken,
                user, new CountFollowHandler(followerCountObserver));
        runTask(followersCountTask);

        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new CountFollowHandler(followingCountObserver));
        runTask(followingCountTask);
    }

    public void isFollower(AuthToken currUserAuthToken, User currUser, User user, IsFollowObserver isFollowerObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken,
                currUser, user, new IsFollowerHandler(isFollowerObserver));
        runTask(isFollowerTask);
    }

    public void loadMoreItemsFollowing(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver getFollowingObserver) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new PageNotificationHandler<User>(getFollowingObserver));
        runTask(getFollowingTask);
    }


}
