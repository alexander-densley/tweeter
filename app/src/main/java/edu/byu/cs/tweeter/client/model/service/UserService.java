package edu.byu.cs.tweeter.client.model.service;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.AuthenticateUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticateUserObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service {


    public void logout(AuthToken authToken, SimpleNotificationObserver logoutObserver){
        LogoutTask logoutTask = new LogoutTask(authToken, new SimpleNotificationHandler(logoutObserver));
        runTask(logoutTask);
    }

    public void register(String firstName, String lastName, String alias, String password, ImageView imageToUpload, AuthenticateUserObserver observer){
        Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

        RegisterTask registerTask = new RegisterTask(firstName, lastName,
                alias, password, imageBytesBase64, new AuthenticateUserHandler(observer));

        runTask(registerTask);

    }

    public void login(String username, String password, AuthenticateUserObserver observer){
        LoginTask loginTask = new LoginTask(username, password, new AuthenticateUserHandler(observer));
        runTask(loginTask);
    }


    public void getUserProfile(AuthToken currUserAuthToken, String toString, UserObserver getUserObserver) {
        GetUserTask getUserTask = new GetUserTask(currUserAuthToken, toString, new GetUserHandler(getUserObserver));
        runTask(getUserTask);
    }


}
