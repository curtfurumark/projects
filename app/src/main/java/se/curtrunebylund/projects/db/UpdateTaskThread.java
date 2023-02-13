package se.curtrunebylund.projects.db;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import classes.Task;
import se.curtrunebylund.projects.util.Debug;


public class UpdateTaskThread extends Thread{
    private Task task;
    private Activity activity;
    public interface Callback{
        void onTaskUpdated(Result result);
    }
    private Callback callback;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public UpdateTaskThread(Task task, Callback callback, Activity activity) {
        Debug.log("UpdateTaskThread() ctor, task= " + task.toString());
        this.activity = activity;
        this.callback = callback;
        this.task = task;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        final Result result = DBOne.update(task);
        if ( !result.isOK()){
            Debug.log("error updating task " + result.toString());
        }else{
            Debug.log("task updated? res: " + result.toString());
        }
        if ( null != callback && activity != null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onTaskUpdated(result);
                }
            });

        }

    }
}
