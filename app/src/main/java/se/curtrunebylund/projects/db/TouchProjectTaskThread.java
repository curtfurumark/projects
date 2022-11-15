package se.curtrunebylund.projects.db;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.projects.Project;
import se.curtrunebylund.projects.projects.Task;

public class TouchProjectTaskThread extends Thread{
    private Project project;
    private Task task;

    public TouchProjectTaskThread(Project project, Task task) {
        this.project = project;
        this.task = task;
    }

    @Override
    public void run() {
        String res = DBOne.touch(project, task);
        Debug.log("...res: " + res);
    }
}
