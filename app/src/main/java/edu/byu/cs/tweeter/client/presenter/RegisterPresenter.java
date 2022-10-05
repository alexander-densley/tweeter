package edu.byu.cs.tweeter.client.presenter;

import android.widget.ImageView;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticateUserObserver;

public class RegisterPresenter extends AuthenticatePresenter {
    AuthenticateView view;
    String firstName;
    String lastName;
    ImageView imageToUpload;

    public RegisterPresenter(AuthenticateView view) {
        super(view);
        this.view = view;
    }

    @Override
    protected String getDescription(boolean errOrEx) {
        if(errOrEx){
            return "Failed to register: ";
        }
        else{
            return "Failed to get register because of exception: ";
        }
    }

    @Override
    protected String validateUser(String alias, String password) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (alias.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }

        if (imageToUpload.getDrawable() == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
        return null;
    }

    @Override
    protected String getAction() {
        return "Registering...";
    }

    @Override
    protected void run(String alias, String password, AuthenticateUserObserver observer) {
        userService.register(firstName, lastName, alias, password, imageToUpload, observer);

    }

    public void setRegistrationInfo(String firstName, String lastName, ImageView imageToUpload){
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageToUpload = imageToUpload;
    }


}
