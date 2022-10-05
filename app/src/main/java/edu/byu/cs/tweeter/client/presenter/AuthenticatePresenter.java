package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticateUserObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticatePresenter extends Presenter {
    private final AuthenticateView view;
    protected UserService userService;

    public AuthenticatePresenter(AuthenticateView view) {
        this.view = view;
        userService = new UserService();
    }

    public interface AuthenticateView extends View {
        void displayErrorMessage(String message);
        void clearErrorMessage();
        void clearInfoMessage();
        void navigateToUser(User user);
    }

    protected abstract String getDescription(boolean errOrEx);

    protected abstract String validateUser(String alias, String password);

    protected abstract String getAction();

    protected abstract void run(String alias, String password, AuthenticateUserObserver observer);

    public void authenticate(String alias, String password){
        String errorMessage = validateUser(alias, password);
        if(errorMessage == null){
            view.clearErrorMessage();
            view.displayMessage(getAction());
            run(alias, password, new ValidateUserObserver());
        }else{
            view.displayErrorMessage(errorMessage);
        }
    }

    protected class ValidateUserObserver implements AuthenticateUserObserver{

        @Override
        public void handleSuccess(User user, AuthToken authToken) {
            view.clearInfoMessage();
            view.clearErrorMessage();

            view.displayMessage("Hello " + Cache.getInstance().getCurrUser().getName());
            view.navigateToUser(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage(getDescription(true) + message);

        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage(getDescription(false) + ex.getMessage());
        }
    }
}
