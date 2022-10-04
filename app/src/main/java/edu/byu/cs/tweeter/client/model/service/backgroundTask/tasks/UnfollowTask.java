package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {
    private static final String LOG_TAG = "UnfollowTask";



    /**
     * The user that is being followed.
     */
    private final User followee;


    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(messageHandler,authToken);
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        sendSuccessMessage();
    }


}
