package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundTaskUtils {
    public static void runTask(Runnable task){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }
}
