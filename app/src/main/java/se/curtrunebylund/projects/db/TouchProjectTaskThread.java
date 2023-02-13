package se.curtrunebylund.projects.db;

import static logger.CRBLogger.log;
import static logger.CRBLogger.logError;

import classes.Project;
import classes.Task;
import persist.DB1Result;
import persist.PersistDB1;
import se.curtrunebylund.projects.util.Debug;

public class TouchProjectTaskThread extends Thread{
    private Project project;
    public boolean VERBOSE = true;
    private Task task;

    public TouchProjectTaskThread(Project project, Task task) {
        if(VERBOSE) log("TouchProjectTaskThread(Project, Task");
        this.project = project;
        this.task = task;
    }

    @Override
    public void run() {
        DB1Result res = PersistDB1.touchTaskProject(project, task);
        if( !res.isOK()){
            logError("error touching task or project, check log for details");
            log(res);
        }
    }
}
