package se.curtrunebylund.projects.activities;

import static logger.CRBLogger.logError;
import static se.curtrunebylund.projects.util.ProjectsLogger.log;

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
import java.util.List;
import java.util.Locale;

import item.Type;
import logger.CRBLogger;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.classes.Session;
import se.curtrunebylund.projects.classes.Project;
import se.curtrunebylund.projects.classes.Task;
import se.curtrunebylund.projects.db.GetChildrenToTaskThread;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.db.PersistSQLite;
import se.curtrunebylund.projects.db.Result;
import se.curtrunebylund.projects.fragments.AddSessionFragment;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.util.Debug;

public class SessionListActivity extends AppCompatActivity implements
        SessionAdapter.Callback,
        AddSessionFragment.Callback,
        View.OnClickListener,
        GetChildrenToTaskThread.Callback {
    private static final long FIVE_MINUTES = 5  * 60 * 1000;

    private List<Session> sessions = new ArrayList<>();
    private SessionAdapter sessionAdapter;
    private Task task;
    private Project project;

    @Override
    public void onGetChildrenToTaskDone(List<Task> tasks, Result result) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attempt_list_activity);
        setTitle("Session List");
        log("SessionListActivity.onCreate()");
        TextView textView_task_heading = findViewById(R.id.textView_attemptList_task_heading);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_attemptlist);

        sessionAdapter = new SessionAdapter(sessions, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(sessionAdapter);
        Intent intent = getIntent();
        task = (Task)intent.getSerializableExtra(Constants.INTENT_TASK);
        project = (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);
        if (task != null && project != null) {
            String task_heading = String.format(Locale.getDefault(), "%s (%d)", task.getHeading(), task.getId());
            textView_task_heading.setText(task_heading);
            sessions = PersistSQLite.getAttempts(task.getId(),this);
            sortAttempts();
            sessionAdapter.setFilteredList(sessions);
        }
        if(intent.getBooleanExtra(Constants.INTENT_LIST_SESSIONS, false)){
            task = (Task) intent.getSerializableExtra(Constants.INTENT_PARENT_TASK_SERIALIZED);
            if(task == null){
                logError("SessionListActivity, INTENT_PARENT_TASK_SERIALIZED return null");
            }else{
                sessions = PersistSQLite.getAttempts(task.getId(), this);
                sortAttempts(); //TODO shouldn't have to sort them first thing, edit sql query
                sessionAdapter.setFilteredList(sessions);
            }
        }
        textView_task_heading.setOnClickListener(this);
    }

    private void getAttempts(Task task) {
        Debug.log("SessionActivity.getAttempts(Task)");
        PersistDBOne.getAttempts(task, this, this);
    }
    /**
     * callback for AddSessionFragment
     * @param heading, heading for the new Item/Attempt/Session
     * @param type, type of new attempt, should be session
     */
    @Override
    public void onAttemptSave(String heading, Type type) {
        Debug.log("AttemptList.onAttemptSave(String heading, int number)");
        Session session = new Session(heading);
        session.setType(type);
        session.setParent_id(task.getId());
        sessions.add(session);
        PersistSQLite.insert(session, this);
        PersistDBOne.touch(project, task);
        log(session);
        sortAttempts();
        sessionAdapter.notifyDataSetChanged();
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
        CRBLogger.log("AttemptList.OnClick(View)");
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
                new AddSessionFragment().show(getSupportFragmentManager(), "hello");
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
    public void onItemClick(Session session) {
        Intent intent = new Intent(this, MusicSessionActivity.class);
        if (!Type.MUSIC.equals(session.getType())) {
            onEditClick(session);
        } else {
            intent.putExtra(Constants.INTENT_MUSIC_SESSION, true);
            intent.putExtra(Constants.INTENT_PROJECT, project);
            intent.putExtra(Constants.INTENT_TASK, task);
            intent.putExtra(Constants.INTENT_SERIALIZED_ATTEMPT, session);
            startActivity(intent);
        }
    }

    @Override
    public void onEditClick(Session session) {
        CRBLogger.log("AttemptListActivity.onEditClick(Attempt)");
        Intent intent = new Intent(this, ItemEditorActivity.class);
        intent.putExtra(Constants.INTENT_EDIT_ITEM, true);
        intent.putExtra(Constants.INTENT_SERIALIZED_ATTEMPT, session);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        intent.putExtra(Constants.INTENT_TASK, task);
        startActivity(intent);
    }

    private void sortAttempts(){
        sessions.sort((a1, a2) -> Long.compare(a2.compare(), a1.compare()));
    }
}