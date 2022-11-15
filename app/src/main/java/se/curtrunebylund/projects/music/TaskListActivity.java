package se.curtrunebylund.projects.music;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.db.GetTasksThread;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.db.Result;
import se.curtrunebylund.projects.db.UpdateTaskThread;
import se.curtrunebylund.projects.projects.Project;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.projects.Task;
import se.curtrunebylund.projects.projects.State;

public class TaskListActivity extends AppCompatActivity implements
        TaskAdapter.Callback,
        UpdateTaskThread.Callback,
        GetTasksThread.Callback {
    private TextView textView_project_heading;
    private RecyclerView recyclerView_todo;
    private java.util.List<Task> taskList = new ArrayList<>();
    private Project project;
    private TaskAdapter taskAdapter;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list_activity);
        setTitle("taskListActivity");
        Debug.log("TaskListActivity.onCreate()");
        textView_project_heading = findViewById(R.id.textView_taskListActivity_heading);
        recyclerView_todo = findViewById(R.id.recyclerView_taskListActivity);
        textView_project_heading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskListActivity.this, ProjectEditorActivity.class);
                intent.putExtra(Constants.INTENT_EDIT_PROJECT, true);
                intent.putExtra(Constants.INTENT_PROJECT, project);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        if( intent.getBooleanExtra(Constants.INTENT_SHOW_TASKS, false)){
            Debug.log("INTENT_SHOW_TASKS");
            project =  (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);
            String heading = String.format(Locale.getDefault(),"%s (id: %d)", project.getHeading(), project.getId());
            textView_project_heading.setText(heading);
            initRecycler(taskList);
            PersistDBOne.getTasks(project, this, this);
        }
    }

    private void initRecycler(List<Task> tasks){
        Debug.log("TaskListActivity.initRecycler(List<Task>)");
        taskAdapter = new TaskAdapter(tasks, this, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_todo.setLayoutManager(layoutManager);
        recyclerView_todo.setItemAnimator(new DefaultItemAnimator());
        recyclerView_todo.setAdapter(taskAdapter);
        taskAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.music_task_menu, menu);
        return true;
    }
    @Override
    public void onGetTasksDone(List<Task> taskList, Result result) {
        Debug.log("TaskListActivity.onGetTasksDone(List<Task>, Result);");
        if(taskList == null){
            Debug.log("\ttasklist is null??? wtf");
        }
        Debug.log(result);
        Debug.logTaskList(taskList);
        if( result.isOK() && taskList != null){
            this.taskList = taskList;
            sortTaskListUpdated();
            taskAdapter.setList(taskList);
        }else{
            Debug.showMessage(this, result.getPHPResult());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_home:
                Intent intent1 = new Intent(this, ProjectListActivity.class);
                startActivity(intent1);
                return true;
            case R.id.icon_add_task:
                Intent intent_add = new Intent(this, TaskAddActivity.class);
                intent_add.putExtra(Constants.INTENT_PROJECT, project);
                intent_add.putExtra(Constants.INTENT_ADD_TASK, true);
                startActivity(intent_add);
                return true;
            case R.id.get_comments:
                //this.coupleProjectsAndTasks();
                return true;
            case R.id.clear_tasks:
                //clearTasks();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Task task) {
        Debug.log("TaskListActivity.onItemClick(Task() ");
        Debug.log(task);
        Intent intent = new Intent(this, AttemptListActivity.class);
        intent.putExtra(Constants.INTENT_TASK, task);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        startActivity(intent);
    }

    @Override
    public void onCheckBoxChecked(Task task, boolean checked) {
        Debug.log("TaskListActivity.onCheckBoxChecked()");
        task.setState(checked ? State.DONE: State.TODO);
        PersistDBOne.update(task, this, this);
    }

    @Override
    public void onTaskUpdated(Result result) {
        if( !result.isOK()) {
            Debug.showMessage(this, result.getPHPResult());
        }
    }
    private void sortTaskListUpdated() {
        Debug.log("TaskListActivity.sortTaskList()");
        Collections.sort(taskList, new Comparator<Task>() {
            public int compare(Task task1, Task task2) {
                return Long.compare(task2.compare(), task1.compare());

            }
        });
    }
}