package se.curtrunebylund.projects.db;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.classes.Project;

public class GetProjectsThread extends Thread {

    //private Handler handler = new Handler();
    private Activity activity;
    public interface Callback {
        public void onGetProjectsDone(List<Project> projectList, Result result);
    }
    private Callback callback;

    public GetProjectsThread(Callback callback, Activity activity) {
        this.activity = activity;
        this.callback = callback;
    }
    
    @Override
    public void run() {
        Debug.log("GetProjectsThread().run()");
        try {
            final List<Project> projectList = DBOne.getProjects();
            if( null != activity && null != callback) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Debug.log("got projectList will call afterGetProjectsThread");
                        callback.onGetProjectsDone(projectList, new Result("OK:got project list just for you"));
                    }
                });
            }
        } catch (final Exception e) {
            if( null != activity && null != callback) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Debug.log("exception getting projects " + e.toString());
                        callback.onGetProjectsDone(null, new Result(e.toString()));
                    }
                });
            }
        }
    }
}
