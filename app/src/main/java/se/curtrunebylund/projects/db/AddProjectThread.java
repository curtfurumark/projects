package se.curtrunebylund.projects.db;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import classes.Project;
import se.curtrunebylund.projects.util.Debug;

public class AddProjectThread extends Thread{
    private final Project project;
    private Activity activity;
    private Callback callback;
    public interface Callback{
        public void onAddProjectThreadDone(Project project, Result result);
    }

    public AddProjectThread(Project project, Callback callback, Activity activity) {
        this.project = project;
        this.activity = activity;
        this.callback = callback;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        final Result result = DBOne.add(project);
        Debug.log(result);
        if( null != callback && null != activity){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onAddProjectThreadDone(project, result);
                }
            });
        }
    }
}
