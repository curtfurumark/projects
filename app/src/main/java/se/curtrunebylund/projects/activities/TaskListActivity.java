package se.curtrunebylund.projects.activities;

import static logger.CRBLogger.log;
import static logger.CRBLogger.logError;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import classes.Project;
import classes.Task;
import item.State;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.adapters.TaskAdapter;
import se.curtrunebylund.projects.db.GetTasksThread;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.db.Result;
import se.curtrunebylund.projects.db.UpdateTaskThread;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.util.Debug;

public class TaskListActivity extends AppCompatActivity implements
        TaskAdapter.Callback,
        UpdateTaskThread.Callback,
        GetTasksThread.Callback {
    private RecyclerView recyclerView_todo;
    private java.util.List<Task> taskList = new ArrayList<>();
    private Project project;
    private TaskAdapter taskAdapter;
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list_activity);
        setTitle("taskListActivity");
        log("TaskListActivity.onCreate(Bundle)");
        TextView textView_project_heading = findViewById(R.id.textView_taskListActivity_heading);
        recyclerView_todo = findViewById(R.id.recyclerView_taskListActivity);
        textView_project_heading.setOnClickListener(view -> {
            Intent intent = new Intent(TaskListActivity.this, ProjectEditorActivity.class);
            intent.putExtra(Constants.INTENT_EDIT_PROJECT, true);
            intent.putExtra(Constants.INTENT_PROJECT, project);
            startActivity(intent);
        });
        Intent intent = getIntent();
        if( intent.getBooleanExtra(Constants.INTENT_SHOW_TASKS, false)){
            log("INTENT_SHOW_TASKS");
            project =  (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);
            String heading = String.format(Locale.getDefault(),"%s (id: %d)", project.getHeading(), project.getId());
            textView_project_heading.setText(heading);
            initRecycler(taskList);
            PersistDBOne.getTasks(project, this, this);
        }
    }

    private void initRecycler(List<Task> tasks){
        log("TaskListActivity.initRecycler(List<Task>)");
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
        log("TaskListActivity.onGetTasksDone(List<Task>, Result);");
        if(taskList == null){
            logError("\ttasklist is null??? wtf");
        }
        Debug.log(result);
        if( result.isOK() && taskList != null){
            this.taskList = taskList;
            sortTaskListUpdated();
            taskAdapter.setList(taskList);
        }else{
            Debug.showMessage(this, result.getPHPResult());
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_home:
                Intent intent1 = new Intent(this, ProjectListActivity.class);
                startActivity(intent1);
                return true;
            case R.id.icon_add_task:
                //TODO, a bottom fragment please
                Intent intent_add = new Intent(this, TaskAddActivity.class);
                intent_add.putExtra(Constants.INTENT_PROJECT, project);
                intent_add.putExtra(Constants.INTENT_ADD_TASK, true);
                startActivity(intent_add);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Task task) {
        log("TaskListActivity.onItemClick(Task() ");
        log(task);
        Intent intent = new Intent(this, SessionListActivity.class);
        intent.putExtra(Constants.INTENT_SHOW_SESSIONS, true);
        intent.putExtra(Constants.INTENT_TASK, task);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        startActivity(intent);
    }

    @Override
    public void onCheckBoxChecked(Task task, boolean checked) {
        log("TaskListActivity.onCheckBoxChecked()");
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
        log("TaskListActivity.sortTaskList()");
        taskList.sort((task1, task2) -> Long.compare(task2.compare(), task1.compare()));
    }
}