package se.curtrunebylund.projects.activities;

import static logger.CRBLogger.log;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import item.State;
import item.Type;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.adapters.LapAdapter;
import se.curtrunebylund.projects.classes.Assignment;
import se.curtrunebylund.projects.classes.Lap;
import se.curtrunebylund.projects.classes.Project;
import se.curtrunebylund.projects.classes.Session;
import se.curtrunebylund.projects.classes.SessionLog;
import se.curtrunebylund.projects.classes.Task;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.db.PersistSQLite;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.projects.Grade;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.util.Kronos;
import util.Converter;

public class MusicSessionActivity extends AppCompatActivity implements Kronos.Callback {
    private EditText editText_heading;
    private EditText  editText_comment;
    private EditText editText_assignment;
    private Button button_repetitions;
    private Button button_timer;
    private  Button button_lap;
    private TextView textView_timer;
    private Integer n_repetitions = 0;
    private Assignment currentAssignment;
    private enum AssignmentMode{
        REPS, LAPS, DONE,  PENDING
    }
    private AssignmentMode mode = AssignmentMode.PENDING;
    private final boolean VERBOSE = true;
    private boolean lap_running = false;
    private TextView textView_lap;
    private Kronos timer;
    private Task task;
    private final Grade grade = Grade.PENDING;
    private Session session;
    private SessionLog sessionLog;
    private RecyclerView recyclerView_laps;
    private LapAdapter lapAdapter;
    private final List<Lap> laps = new ArrayList<>();
    private Project project;
    private Lap current_lap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_session_activity);
        log("MusicSessionActivity.onCreate()");
        setTitle("music session");

        //private EditText editText_task_heading;
        TextView textView_task_heading = findViewById(R.id.textView_musicSession_task_heading);
        editText_heading = findViewById(R.id.editText_musicSession_heading);
        textView_timer = findViewById(R.id.textView_musicSession_timer);
        textView_lap = findViewById(R.id.textView_musicSession_current_lap);
        editText_comment = findViewById(R.id.editText_musicSession_comment);
        recyclerView_laps = findViewById(R.id.recyclerView_musicSession);
        button_lap = findViewById(R.id.button_musicSession_lap);
        button_lap.setEnabled(false);
        button_timer = findViewById(R.id.button_musicSession_timer);
        editText_assignment = findViewById(R.id.editText_musicSession_assignment);
        button_lap.setOnClickListener(view -> onButtonLap());
        button_timer.setOnClickListener(view -> {
            switch(timer.getState()){
                case  STOPPED:
                    timer.start();
                    button_timer.setText(R.string.ui_pause);
                    break;
                case RUNNING:
                    timer.pause();
                    button_timer.setText(R.string.ui_resume);
                    button_lap.setEnabled(false);
                    if( current_lap != null && current_lap.isRunning()){
                        stopLap();
                    }
                    break;
                case PAUSED:
                    timer.resume();
                    button_lap.setEnabled(true);
                    button_timer.setText(R.string.ui_pause);
            }
        });
        button_timer.setOnLongClickListener(view -> {
            timer.stop();
            textView_timer.setText(R.string.hhmmss);
            button_timer.setText(R.string.ui_start);
            stopLap();
            return true;
        });

        button_repetitions = findViewById(R.id.button_musicSession_repitition);
        button_repetitions.setOnClickListener(view -> {
            n_repetitions++;
            String str = String.format(Locale.getDefault(), "%d", n_repetitions);
            button_repetitions.setText(str);
            updateAssignment(AssignmentMode.REPS);
        });

        button_repetitions.setOnLongClickListener(view -> {
            n_repetitions = 0;
            button_repetitions.setText(R.string.ui_just_do_it);
            return  true;
        });
        timer = Kronos.getInstance(this);
        Intent intent = getIntent();
        if( intent.getBooleanExtra(Constants.INTENT_MUSIC_SESSION, false)){
            task = (Task) intent.getSerializableExtra(Constants.INTENT_TASK);
            textView_task_heading.setText(task.getHeading());
            session = (Session) intent.getSerializableExtra(Constants.INTENT_SERIALIZED_ATTEMPT);
            sessionLog = session.getSessionLog();
            //setTitle(String.format(Locale.getDefault(), "%s, (%d)", session.getHeading(), session.getId()));
            editText_heading.setText(session.getHeading());
            project = (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);

        }else if( intent.getBooleanExtra(Constants.INTENT_BLANK_MUSIC_SESSION, false)){
            log("INTENT_BLANK_MUSIC_SESSION");
            session  = new Session();
            sessionLog = session.getSessionLog();
            setTitle("new session");
        }
        sessionLog = new SessionLog(session.getId());
        initRecycler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.setActivityVisible(false);
        timer.removeCallback();
        if( VERBOSE) log("MusicSessionActivity.onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.setActivityVisible(true);
        timer.setCallback(this);
        if( VERBOSE) log("MusicSessionActivity.onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        timer.setActivityVisible(true);
        timer.setCallback(this);
        if( VERBOSE) log("MusicSessionActivity.onRestart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.setActivityVisible(false);
        timer.removeCallback();
        if( VERBOSE) log("MusicSessionActivity.onStop()");
    }

    private void initRecycler() {
        if( VERBOSE) log("MusicSessionActivity.initRecycler()");
        lapAdapter = new LapAdapter(laps);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_laps.setLayoutManager(layoutManager);
        recyclerView_laps.setItemAnimator(new DefaultItemAnimator());
        recyclerView_laps.setAdapter(lapAdapter);
    }


    private void onButtonLap() {
        if( VERBOSE)log("MusicSessionActivity.onButtonLap()");
        if(lap_running){
            stopLap();
            updateAssignment(AssignmentMode.LAPS);
        }else {
            current_lap = new Lap();
            lap_running = true;
            button_lap.setText(R.string.ui_stop_lap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.music_session_menu, menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_save_project:
                saveSession();
                return true;
            case R.id.musicSession_new_assignment:
                newAssignment();
                break;
            case R.id.musicSession_assignment_check:
                updateAssignment(AssignmentMode.DONE);
                break;
            case R.id.musicSession_reload_kronos:
                //timer.reLoadTimer(this);
                textView_timer.setText("hello");
                break;
            case R.id.musicSession_home:
                startActivity(new Intent(this, TaskListActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * callback for Kronos, called once every second whenever Kronos is running
     * @param secs, number of seconds elapsed since start of timer
     */
    @Override
    public void onTimerTick(int secs) {
        if(VERBOSE) log("MusicSessionActivity.onTimerClick() secs", secs);
        //textView_timer.
        textView_timer.setText(Converter.formatSeconds(secs));
        if( current_lap != null && current_lap.isRunning()) {
            textView_lap.setText(Converter.formatSeconds((int) current_lap.getElapsedTime()));
        }
    }

    private void newAssignment() {
        if (VERBOSE) log("MusicSessionActivity.newAssignment()");
        String description = editText_assignment.getText().toString();
        if( description.isEmpty()){
            Toast.makeText(this, "please enter a description of your assignment", Toast.LENGTH_LONG).show();
            return;
        }
        currentAssignment = new Assignment(description);
    }
    private void saveSession() {
        if(VERBOSE) log("MusicSessionActivity.saveSession()");
        String heading = editText_heading.getText().toString();
        if ( heading.isEmpty()){
            Debug.showMessage(this, "a heading please");
            return;
        }
        String comment = editText_comment.getText().toString();
        session.setUpdated(LocalDateTime.now());
        session.setComment(comment);
        session.setGrade(grade);
        session.setParent_id(task.getId());
        session.setDuration(timer.getElapsedTime());
        session.setType(Type.MUSIC);
        session.setState(State.DONE);
        editText_heading.setHint("heading");
        editText_comment.setHint("comment");
        session.set(sessionLog);
        PersistSQLite.update(session, this);
        PersistDBOne.touch(project, task);

        Intent intent = new Intent(this, SessionListActivity.class);
        intent.putExtra(Constants.INTENT_TASK, task);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        startActivity(intent);
    }
    private void stopLap(){
        if( VERBOSE) log("MusicSessionActivity.stopLap()");
        current_lap.stop();
        lap_running = false;
        button_lap.setText(R.string.ui_start_lap);
        laps.add(0, current_lap);
        lapAdapter.notifyItemInserted(0);
        textView_lap.setText("00:00:00");
    }
    private void updateAssignment(AssignmentMode mode){
        if( VERBOSE) log("MusicSessionActivity.updateAssignment(AssignmentMode)", mode.toString());
        if( currentAssignment != null) {
            switch (mode) {
                case LAPS:
                    currentAssignment.addLap(current_lap);
                    break;
                case REPS:
                    if( VERBOSE) log("REPS", n_repetitions);
                    currentAssignment.setReps(n_repetitions);
                    break;
                case DONE:
                    sessionLog.add(currentAssignment);
                    currentAssignment = null;
                    break;
            }
        }
    }
}