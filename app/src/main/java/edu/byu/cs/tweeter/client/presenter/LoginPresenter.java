package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticateUserObserver;

public class LoginPresenter extends AuthenticatePresenter {
    AuthenticateView view;

    public LoginPresenter(AuthenticateView view) {
        super(view);
        this.view = view;
    }

    @Override
    protected String getDescription(boolean errOrEx) {
        if(errOrEx){
            return "Failed to login: ";
        }
        else{
            return "Failed to get login because of exception: ";
        }
    }

    @Override
    public String validateUser(String alias, String password) {
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
    protected String getAction() {
        return "Logging In...";
    }

    @Override
    protected void run(String alias, String password, AuthenticateUserObserver observer) {
        userService.login(alias, password, observer);
    }

}
