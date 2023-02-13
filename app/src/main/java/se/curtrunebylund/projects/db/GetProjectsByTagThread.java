package se.curtrunebylund.projects.db;

import android.app.Activity;

import java.util.List;

import classes.Project;

public class GetProjectsByTagThread extends Thread{
    private String tag;
    private Activity activity;
    public interface Callback {
        public void onProjectsLoaded(List<Project> projectList, Result result);
    }
    private Callback callback;
    public GetProjectsByTagThread(String tag, Callback callback, Activity activity) {
        this.tag = tag;
        this.activity = activity;
        this.callback = callback;

    }

    @Override
    public void run() {
        try {
            List<Project> projectList = DBOne.getProjectsByTag(tag);
            if( callback != null && activity != null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onProjectsLoaded(projectList, new Result("OK:projects by tag|"));
                    }
                });

            }
        } catch (Exception e) {
            if( callback != null && activity != null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onProjectsLoaded(null, new Result("ERROR:" + e.toString() + "|"));
                    }
                });
            }
            //e.printStackTrace();
        }
    }
}
