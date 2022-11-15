package se.curtrunebylund.projects.db;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import java.util.List;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.projects.Task;

public class GetTasksThread extends Thread{
        private final String parent_id;
        private Activity activity;
        private final Handler handler = new Handler();
        public interface Callback{
            public void onGetTasksDone(List<Task> taskList, Result result);
        }
        private final Callback callback;


        public GetTasksThread(Integer id, Callback callback, Activity activity) {
            this.parent_id = id.toString();
            this.callback = callback;
            this.activity = activity;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            Debug.log("GetTasksThread.run()");
            try {
                final List<Task> taskList = DBOne.getTasks(parent_id);
                if( callback != null && activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onGetTasksDone(taskList, new Result("OK:tasklist for project id=" + parent_id));

                        }
                    });
                }
            }catch (final Exception e){
                Debug.log("exception getting tasks: " + e.toString());
                if( activity != null && callback != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onGetTasksDone(null, new Result(e.toString()));
                        }
                    });
                }
            }
        }
}

