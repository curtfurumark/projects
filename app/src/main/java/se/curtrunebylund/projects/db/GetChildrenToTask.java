package se.curtrunebylund.projects.db;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.projects.Task;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GetChildrenToTask extends Thread {
    private Activity activity;
    private Integer parent_task_id;
    private Callback callback;
    public interface Callback{

        //public void onGetChildTasksDone(List<Task> tasks, Result result);
        public void onGetChildrenToTaskDone(List<Task> tasks, Result result);

    }

    public GetChildrenToTask(Integer parent_task_id, Activity activity, Callback callback) {
        this.activity = activity;
        this.parent_task_id = parent_task_id;
        this.callback = callback;
    }


    @Override
    public void run() {
        Debug.log("GetChildrenToTask.run() parent_task_id: " + parent_task_id);
        //List<Task> tasks = null;
        try {
            final  List<Task> tasks = DBOne.getTaskChildren(parent_task_id);
            if ( null != callback && activity != null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onGetChildrenToTaskDone(tasks, new Result("OK:"));
                    }
                });

            }
        } catch (Exception exception) {
            Debug.log("GetGrandChildrenThread...Exception: " + exception.toString());
            if ( null != callback && activity != null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onGetChildrenToTaskDone(null, new Result("ERROR:" + exception.toString()));
                    }
                });
            }
        }
    }
}
