package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {
    public StoryPresenter(PagedView<Status> view) {
        super(view);
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        statusService.loadMoreItemsStory(authToken, targetUser, pageSize, lastItem, new GetItemsObserver());
    }

    @Override
    protected String getDescription(boolean errOrEx) {
        if(errOrEx){
            return "Failed to get story: ";
        }
        else{
            return "Failed to get story because of exception: ";
        }
    }
}
