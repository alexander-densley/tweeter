package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticateUserObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter implements AuthenticateUserObserver {
    // the methods that the preseneter can call on the view
    public interface LoginView{
        void displayErrorMessage(String message);
        void clearErrorMessage();
        void displayInfoMessage(String message);
        void clearInfoMessage();

        void navigateToUser(User user);

    }
    private LoginView view;
    public LoginPresenter(LoginView view){
        this.view = view;
    }
    // the methods that the view can call on the presenter
    public void login(String username, String password){
        String errorMessage = validateLogin(username, password);
        if(errorMessage == null){
            view.clearErrorMessage();
            view.displayInfoMessage("Logging In...");
            new UserService().login(username, password, this);

        }else{
            view.displayErrorMessage(errorMessage);
        }
    }

    public String validateLogin(String alias, String password) {
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

    @Override
    public void handleSuccess(User user, AuthToken authToken) {
        view.clearInfoMessage();
        view.clearErrorMessage();

        view.displayInfoMessage("Hello " + Cache.getInstance().getCurrUser().getName());
        view.navigateToUser(user);
    }

    @Override
    public void handleFailure(String message) {
        view.displayInfoMessage("Failed to login: " + message);
    }

    @Override
    public void handleException(Exception ex) {
        view.displayInfoMessage("Failed to login because of exception: " + ex.getMessage());

    }




    // methods related to observing the model layer

}
