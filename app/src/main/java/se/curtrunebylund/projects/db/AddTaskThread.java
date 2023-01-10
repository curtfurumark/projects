package se.curtrunebylund.projects.db;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.classes.Task;


@RequiresApi(api = Build.VERSION_CODES.O)
public class AddTaskThread extends Thread{
    private Task task;
    private Callback callback;
    private Activity activity;

    public AddTaskThread(Task task, Callback callback, Activity activity) {
        this.task = task;
        this.callback = callback;
        this.activity = activity;
    }

    public interface Callback{
        public void onTaskAdded(Result result, Task task);
    }

    @Override
    public void run() {
        Debug.log("AddTaskThread.run()");
        final Result result = DBOne.add(task);
        if ( result.isOK()){
            task.setId(result.getID());
            if ( null != callback && activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onTaskAdded(result, task);
                    }
                });

            }
        }
        //Debug.log("result: " + result.toString());
    }
}
