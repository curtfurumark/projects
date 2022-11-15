package se.curtrunebylund.projects.music;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.db.GetChildrenToTaskThread;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.db.PersistSQLite;
import se.curtrunebylund.projects.db.Result;
import se.curtrunebylund.projects.help.AttemptBottomSheet;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.help.Converter;
import se.curtrunebylund.projects.projects.Project;
import se.curtrunebylund.projects.projects.Task;

public class AttemptListActivity extends AppCompatActivity implements
        AttemptsAdapter.Callback,
        AttemptBottomSheet.Listener,
        View.OnClickListener,
        GetChildrenToTaskThread.Callback {
    private static final long FIVE_MINUTES = 5  * 60 * 1000;

    private TextView textView_task_heading;
    private RecyclerView recyclerView;
    private List<Attempt> attempts = new ArrayList<>();
    private AttemptsAdapter attemptsAdapter;
    private Task task;
    private Project project;

    private enum Mode{
        CREATE, EDIT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attempt_list_activity);
        setTitle("Attempt List");
        Debug.log("AttemmptListActivity.onCreate()");
        textView_task_heading = findViewById(R.id.textView_attemptList_task_heading);
        recyclerView = findViewById(R.id.recyclerView_attemptlist);

        attemptsAdapter = new AttemptsAdapter(attempts, this, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(attemptsAdapter);
        Intent intent = getIntent();
        task = (Task)intent.getSerializableExtra(Constants.INTENT_TASK);
        project = (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);
        if (task != null && project != null) {
            String task_heading = String.format(Locale.getDefault(), "%s (%d)", task.getHeading(), task.getId());
            textView_task_heading.setText(task_heading);
            attempts = PersistSQLite.getAttempts(task.getId(),this);
            sortAttempts();
            attemptsAdapter.setFilteredList(attempts);
        }
        textView_task_heading.setOnClickListener(this);
    }

    private void getAttempts(Task task) {
        Debug.log("SessionActivity.getAttempts(Task)");
        PersistDBOne.getAttempts(task, this, this);
    }
    /**
     * callback for AttemptBottomSheet
     * @param heading
     * @param number TODO, not implemented
     */
    @Override
    public void onAttemptSave(String heading, int number) {
        Debug.log("SessionActivity.onAttemptSave(String heading, int number)");
        Debug.log(task);
        int offset = Calculator.getOffset(attempts);
        for( int i = 1; i <= number; i++) {
            Attempt attempt = new Attempt();
            attempt.setHeading(String.format(Locale.getDefault(),"%s, %d", heading, i + offset));
            attempt.setParent_id(task.getId());
            attempt = PersistSQLite.insert(attempt, this);
            attempts.add(attempt);
        }
        sortAttempts();
        attemptsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.attempt_list_activity, menu);
        return true;
    }

    /**
     * on parent task textview click
     * @param view
     */
    @Override
    public void onClick(View view) {
        Debug.log("AttemptList.OnClick(View)");
        Intent intent= new Intent(this, TaskAddActivity.class);
        intent.putExtra(Constants.INTENT_EDIT_TASK, true);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        intent.putExtra(Constants.INTENT_TASK, task);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_home:
                Intent intent =  new Intent(this, TaskListActivity.class);
                intent.putExtra(Constants.INTENT_TASK, task);
                intent.putExtra(Constants.INTENT_SHOW_TASKS, true);
                intent.putExtra(Constants.INTENT_PROJECT, project);
                startActivity(intent);
                return true;
            case R.id.new_attempt:
                new AttemptBottomSheet().show(getSupportFragmentManager(), "hello");
                return true;
            case R.id.delete_attempts:
                PersistSQLite.deleteAttempts(task, this);
            case R.id.get_tasks_from_dbone:
                getAttempts(task);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGetChildrenToTaskDone(List<Task> tasks, Result result) {
        Debug.log("SessionActivity.onGetChildrenToTaskDone(List<Task> Result)");
        Debug.logTaskList(tasks);
        Debug.log(result);
        attempts = Converter.convertTasks(tasks);
        attemptsAdapter.setFilteredList(attempts);
        PersistSQLite.insertAttempts(attempts, this);
    }

    @Override
    public void onItemClick(Attempt attempt) {
        Intent intent = new Intent(this, AttemptEditActivity.class);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        intent.putExtra(Constants.INTENT_TASK, task);
        intent.putExtra(Constants.INTENT_SERIALIZED_ATTEMPT, attempt);
        startActivity(intent);
    }

    private void sortAttempts(){
        Collections.sort(attempts, new Comparator<Attempt>() {
            @Override
            public int compare(Attempt a1, Attempt a2) {
                return Long.compare(a2.compare(), a1.compare());
            }
        });
    }
}