package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver {



    public interface RegisterView{
        void displayErrorMessage(String message);
        void clearErrorMessage();
        void displayInfoMessage(String message);
        void clearInfoMessage();

        void navigateToUser(User user);
    }

    private RegisterView view;
    public RegisterPresenter(RegisterView view){
        this.view = view;
    }

    public void register(String firstName, String lastName, String alias, String password, ImageView imageToUpload){
        String errorMessage = validateRegistration(firstName, lastName, alias, password, imageToUpload);
        if(errorMessage == null){
            view.clearErrorMessage();
            view.displayInfoMessage("Registering...");
            new UserService().register(firstName, lastName, alias, password, imageToUpload, this);
        }
    }


    public String validateRegistration(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
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
    public void handleRegisterSuccess(User user, AuthToken authToken) {
        view.clearInfoMessage();
        view.clearErrorMessage();

        view.displayInfoMessage("Hello " + Cache.getInstance().getCurrUser().getName());
        view.navigateToUser(user);
    }

    @Override
    public void handleRegisterFailure(String message) {
        view.displayInfoMessage("Failed to register: " + message);
    }

    @Override
    public void handleRegisterThrewException(Exception ex) {
        view.displayInfoMessage("Failed to register because of exception: " + ex.getMessage());
    }
}
