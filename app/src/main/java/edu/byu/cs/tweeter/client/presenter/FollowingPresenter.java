package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> {
    public FollowingPresenter(PagedView<User> view) {
        super(view);
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        followService.loadMoreItemsFollowing(authToken, targetUser, pageSize, lastItem, new GetItemsObserver());
    }

    @Override
    protected String getDescription(boolean errOrEx) {
        if(errOrEx){
            return "Failed to get following: ";
        }
        else{
            return "Failed to get following because of exception: ";
        }
    }
}
