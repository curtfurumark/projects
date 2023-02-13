package se.curtrunebylund.projects.db;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;

import classes.Project;
import se.curtrunebylund.projects.util.Debug;


@RequiresApi(api = Build.VERSION_CODES.O)

public class UpdateProjectThread extends Thread{
    private final Project project;
    private final Activity activity;
    public interface Callback{
        void onProjectUpdated(Result res);
    }
    private final Callback callback;


    public UpdateProjectThread(Project project, Callback callback, Activity activity) {
        Debug.log(  "UpdateProjectThread ctor: " + project.toString());
        this.project = project;
        this.callback = callback;
        this.activity = activity;
    }

    @Override
    public void run() {
        final Result res;
        try {
            res = DBOne.update(project);
            if (callback != null && activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onProjectUpdated(res);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}