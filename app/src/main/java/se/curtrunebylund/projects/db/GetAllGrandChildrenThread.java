package se.curtrunebylund.projects.db;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.projects.Task;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GetAllGrandChildrenThread extends Thread {
    private Activity activity;
    //private Integer parent_task_id;
    private Callback callback;
    public interface Callback{
        public void onGetAllGrandChildrenDone(List<Task> tasks, Result result);
    }

    public GetAllGrandChildrenThread(Callback callback, Activity activity) {
        this.activity = activity;
        this.callback = callback;
    }


    @Override
    public void run() {
        Debug.log("GetTaskChildrenThread.run()");
        //List<Task> tasks = null;
        try {
            final List<Task> tasks = DBOne.getAllGrandChildren();
            if ( null != callback && activity != null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onGetAllGrandChildrenDone(tasks, new Result("OK:got all grandchildren|"));
                    }
                });

            }
        } catch (Exception exception) {
            Debug.log("GetTaskChildrenThread...Exception: " + exception.toString());
            if ( null != callback && activity != null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onGetAllGrandChildrenDone(null, new Result( exception.toString()));
                    }
                });

            }
        }
    }
}
