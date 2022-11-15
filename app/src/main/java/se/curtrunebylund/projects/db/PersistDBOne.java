package se.curtrunebylund.projects.db;

import android.app.Activity;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.projects.Project;
import se.curtrunebylund.projects.projects.Task;

public class PersistDBOne {
    public static void add(Project project, AddProjectThread.Callback callback, Activity activity){
        Debug.log("PersistDBOne.add(Project, Callback, Activity");
        AddProjectThread thread = new AddProjectThread(project,callback, activity);
        thread.start();
    }
    public static void add(Task task, AddTaskThread.Callback callback, Activity activity) {
        Debug.log("PersistDBOne.add(Task, Callback, Activity");
        Debug.log(task);
        AddTaskThread addTaskThread = new AddTaskThread(task, callback, activity);
        addTaskThread.start();
    }
    public static void getTasks(Project project, GetTasksThread.Callback callback, Activity activity){
        Debug.log("PersistDBOne.getTasks()");
        GetTasksThread getTasksThread = new GetTasksThread(project.getId(), callback, activity);
        getTasksThread.start();
    }
    public static void getProjectsByTag(String tag, GetProjectsByTagThread.Callback callback, Activity activity){
        GetProjectsByTagThread getProjectsByTagThread = new GetProjectsByTagThread(tag, callback, activity);
        getProjectsByTagThread.start();
    }
    public static void getGrandChildrenTasks(GetAllGrandChildrenThread.Callback callback, Activity activity){
        Debug.log("PersistDBOne.getGrandChildrenTasks(Callback, Activity)");
        GetAllGrandChildrenThread getAllGrandChildrenThread = new GetAllGrandChildrenThread(callback, activity);
        getAllGrandChildrenThread.start();
    }

    public static void getAttempts(Task parent, GetChildrenToTaskThread.Callback callback, Activity activity){
        Debug.log("PersistDBOne.getAttempts(Task, Callback, Activity)");
       GetChildrenToTaskThread getChildrenToTaskThread = new GetChildrenToTaskThread(parent.getId(), activity, callback) ;
       getChildrenToTaskThread.start();
    }

    public static void getProjects(GetProjectsThread.Callback callback, Activity activity) {
        Debug.log("PersistDBOne.getProjects()");
        GetProjectsThread getProjectsThread = new GetProjectsThread(callback, activity);
        getProjectsThread.start();

    }

    //TODO do i need to do this
    public static void update(Project project, UpdateProjectThread.Callback callback, Activity activity) {
        Debug.log("PersistDBOne.update(Project, Callback, Activity)");
        UpdateProjectThread thread = new UpdateProjectThread(project, callback, activity);
        thread.start();
    }

    public static void update(Task task, UpdateTaskThread.Callback callback, Activity activity) {
        Debug.log("PersistDBOne.update(Task) id = " + task.getId());
        UpdateTaskThread updateTaskThread = new UpdateTaskThread(task, callback, activity);
        updateTaskThread.start();
    }

    public static void touch(Project project, Task task) {
        Debug.log("PersistDBOne.touch(Project, Task)");
        TouchProjectTaskThread thread = new TouchProjectTaskThread(project, task);
        thread.start();
    }
}
