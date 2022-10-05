package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> {
    public FollowersPresenter(PagedView<User> view) {
        super(view);
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        followService.loadMoreItemsFollowers(authToken, targetUser, pageSize, lastItem, new GetItemsObserver());
    }

    @Override
    protected String getDescription(boolean errOrEx) {
        if(errOrEx){
            return "Failed to get followers: ";
        }
        else{
            return "Failed to get followers because of exception: ";
        }
    }
}
