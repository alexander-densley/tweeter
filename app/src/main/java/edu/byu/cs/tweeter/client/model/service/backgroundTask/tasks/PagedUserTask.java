package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedUserTask extends PagedTask<User>{

    protected PagedUserTask(Handler messageHandler, AuthToken authToken, User targetUser, int limit, User lastItem) {
        super(messageHandler, authToken, targetUser, limit, lastItem);
    }

    @Override
    protected List<User> getUsersForItems(List<User> items) {
        return items;
    }
}
