package se.curtrunebylund.projects.activities;

import static logger.CRBLogger.log;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import classes.Project;
import classes.Task;
import classes.projects.Assignment;
import classes.projects.Lap;
import gson.GsonEasy;
import item.State;
import item.Type;
import persist.DB1Result;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.adapters.LapAdapter;
import se.curtrunebylund.projects.classes.Session;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.db.PersistSQLite;
import se.curtrunebylund.projects.fragments.AddAssignmentFragment;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.projects.Grade;
import se.curtrunebylund.projects.threads.InsertThread;
import se.curtrunebylund.projects.threads.SelectThread;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.util.Kronos;
import util.Converter;

public class MusicSessionActivity extends AppCompatActivity implements
        Kronos.Callback,
        InsertThread.Callback,
        SelectThread.Callback,
        AddAssignmentFragment.Callback {
    private EditText editText_heading;
    private EditText  editText_comment;
    private EditText editText_assignment;
    private Button button_repetitions;
    private CheckBox checkBox_assignment;
    private Button button_timer;
    private  Button button_lap;
    private TextView textView_timer;
    private Integer n_repetitions = 0;
    private Assignment currentAssignment;
    private List<Assignment> assignments = new ArrayList<>();

    @Override
    public void onItemInserted(DB1Result result) {
        log("MusicSessionActivity.onItemInsert(DB1Result)");
        if( !result.isOK()){
            Toast.makeText(this, "error inserting assignment", Toast.LENGTH_LONG).show();
            log(result);
        }
    }

    @Override
    public void onRequestSelectError(String errMessage) {
        Toast.makeText(this, errMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSelectDone(String json) {
        log("MusicSessionActivity.onRequestSelectDone(String json)");
        log("json", json);
        Assignment[] assignmentsArray = GsonEasy.getGson().fromJson(json, Assignment[].class);
        List<Assignment> assignments1 = Arrays.asList(assignmentsArray);
        log("size of list", assignments1.size());
        //assignments = new ArrayList<>(Arrays.asList(GsonEasy.getGson().fromJson(json, Assignment[].class)));
    }
    private final boolean VERBOSE = true;
    private boolean lap_running = false;
    private TextView textView_lap;
    private Kronos kronos;
    private Task task;
    private final Grade grade = Grade.PENDING;
    private Session session;

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

        //find controls, ui elements or whatever
        TextView textView_task_heading = findViewById(R.id.textView_musicSession_task_heading);
        editText_heading = findViewById(R.id.editText_musicSession_heading);
        textView_timer = findViewById(R.id.textView_musicSession_timer);
        textView_lap = findViewById(R.id.textView_musicSession_current_lap);
        editText_comment = findViewById(R.id.editText_musicSession_comment);
        recyclerView_laps = findViewById(R.id.recyclerView_musicSession);
        button_lap = findViewById(R.id.button_musicSession_lap);
        button_lap.setEnabled(false);
        button_timer = findViewById(R.id.button_musicSession_timer);
        Button button_saveAssignment = findViewById(R.id.musicSession_button_saveAssignment);
        editText_assignment = findViewById(R.id.editText_musicSession_assignment);
        button_repetitions = findViewById(R.id.musicSession_button_repetitions);
        checkBox_assignment = findViewById(R.id.musicSession_checkBox_assignment);

        //set listeners
        editText_assignment.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                log("editText_assignment.onDrag()");
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        log("DragEvent.ACTION_DRAG_STARTED");
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        log("DragEvent.ACTION_DRAG_ENDED");
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        log("DragEvent.ACTION_DRAG_EXITED");
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        log("DragEvent.ACTION_DRAG_ENTERED");
                        break;
                }
                return false;
            }
        });
        button_saveAssignment.setOnClickListener(view -> saveAssignment());
        button_lap.setOnClickListener(view -> onButtonLap());
        button_timer.setOnClickListener(view -> {
            switch(kronos.getState()){
                case  STOPPED:
                    kronos.start();
                    button_timer.setText(R.string.ui_pause);
                    button_lap.setEnabled(true);
                    break;
                case RUNNING:
                    kronos.pause();
                    button_timer.setText(R.string.ui_resume);
                    button_lap.setEnabled(false);
                    if( current_lap != null && current_lap.isRunning()){
                        stopLap();
                    }
                    break;
                case PAUSED:
                    kronos.resume();
                    button_lap.setEnabled(true);
                    button_timer.setText(R.string.ui_pause);
            }
        });
        button_timer.setOnLongClickListener(view -> {
            kronos.stop();
            textView_timer.setText(R.string.hhmmss);
            button_timer.setText(R.string.ui_start);
            stopLap();
            return true;
        });
        checkBox_assignment.setOnClickListener(view -> {
            if(checkBox_assignment.isChecked()){
                saveAssignment();
            }
        });

        button_repetitions.setOnClickListener(view -> {
            n_repetitions++;
            String str = String.format(Locale.getDefault(), "%d", n_repetitions);
            button_repetitions.setText(str);
        });

        button_repetitions.setOnLongClickListener(view -> {
            n_repetitions = 0;
            button_repetitions.setText(R.string.ui_just_do_it);
            return  true;
        });

        Intent intent = getIntent();
        if( intent.getBooleanExtra(Constants.INTENT_MUSIC_SESSION, false)){
            task = (Task) intent.getSerializableExtra(Constants.INTENT_TASK);
            textView_task_heading.setText(task.getHeading());
            session = (Session) intent.getSerializableExtra(Constants.INTENT_SERIALIZED_ATTEMPT);
            //setTitle(String.format(Locale.getDefault(), "%s, (%d)", session.getHeading(), session.getId()));
            editText_heading.setText(session.getHeading());
            project = (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);

        }else if( intent.getBooleanExtra(Constants.INTENT_BLANK_MUSIC_SESSION, false)){
            log("INTENT_BLANK_MUSIC_SESSION");
            session  = new Session();
            setTitle("new session");
        }

        //init stuff that needs to be initialized
        kronos = Kronos.getInstance(this);
        initRecycler();
        getAssignments();
    }

    private void getAssignments() {
        if( VERBOSE) log("MusicSessionActivity.getAssignments()");
        PersistDBOne.getAssignments(this);
    }

    private String repsToString(){
        return String.format(Locale.getDefault(), "number of repitition: %d", n_repetitions );
    }


    private void saveAssignment() {
        log("MusicSessionActivity.saveAssignment()");
        //session.addAssignment(currentAssignment);
        currentAssignment.setDone(true);
        currentAssignment.appendContent(repsToString());
        PersistDBOne.persist(currentAssignment, this);
        if(assignments.size() > 0){
            //TODO, set next assignment as current, but
            //stack or queue or whatever
            //think about it
        }
        currentAssignment = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        kronos.removeCallback();
        if( VERBOSE) log("MusicSessionActivity.onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreUI();
        kronos.setCallback(this);
        if( VERBOSE) log("MusicSessionActivity.onResume()");
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        kronos.setCallback(this);
        if( VERBOSE) log("MusicSessionActivity.onRestart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        kronos.removeCallback();
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

    @Override
    public void onAddAssignment(String heading) {
        log("MusicSessionActivity.onAddAssignment(String heading)");
        editText_assignment.setText(heading);
        //Toast.makeText(this, "type " + type.toString(), Toast.LENGTH_LONG).show();
        currentAssignment = new Assignment(heading);
        assignments.add(currentAssignment);
        PersistDBOne.persist(currentAssignment, this);
    }

    private void onButtonLap() {
        if( VERBOSE)log("MusicSessionActivity.onButtonLap()");
        if(lap_running){
            stopLap();
            currentAssignment.addLap(current_lap);
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
                saveAssignment();
                break;
            case R.id.musicSession_reload_kronos:
                //timer.reLoadTimer(this);
                textView_timer.setText("hello");
                break;
            case R.id.musicSession_home:
                Intent intent = new Intent(this, SessionListActivity.class);
                intent.putExtra(Constants.INTENT_LIST_SESSIONS, true);
                intent.putExtra(Constants.INTENT_PARENT_TASK_SERIALIZED, task);
                startActivity(intent);

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
        textView_timer.setText(Converter.formatSecondsWithHours(secs));
        if( current_lap != null && current_lap.isRunning()) {
            textView_lap.setText(Converter.formatSecondsWithHours((int) current_lap.getElapsedTime()));
        }
    }

    /**
     * bottomfragment, callback onAddAssignment(...)
     * menu icon add
     */
    private void newAssignment() {
        if (VERBOSE) log("MusicSessionActivity.newAssignment()");
        new AddAssignmentFragment().show(getSupportFragmentManager(), "new assignment");
    }

    private void restoreUI() {
        if(VERBOSE) log("MusicSessionActivity.restoreUI()");
        Kronos.State state = kronos.getState();
        switch (state){
            case RUNNING:
                button_timer.setText(R.string.ui_pause);
                button_lap.setEnabled(true);
                break;
            case PAUSED:
                button_timer.setText(R.string.ui_resume);
                button_lap.setEnabled(false);
                break;
            case STOPPED:
                button_timer.setText(R.string.ui_start);
                button_lap.setEnabled(false);
                break;

        }
        if( state.equals(Kronos.State.RUNNING)){

        }
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
        session.setDuration(kronos.getElapsedTime());
        session.setType(Type.MUSIC);
        session.setState(State.DONE);
        editText_heading.setHint("heading");
        editText_comment.setHint("comment");
        PersistSQLite.update(session, this);
        PersistDBOne.touch(project, task);

        Intent intent = new Intent(this, SessionListActivity.class);
        intent.putExtra(Constants.INTENT_TASK, task);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        startActivity(intent);
    }
    private void stopLap(){
        if( VERBOSE) log("MusicSessionActivity.stopLap()");
        if( current_lap == null){
            log("...current_lap is null, returning, no worries");
            return;
        }
        current_lap.stop();
        lap_running = false;
        button_lap.setText(R.string.ui_start_lap);
        laps.add(0, current_lap);
        currentAssignment.addLap(current_lap);
        lapAdapter.notifyItemInserted(0);
        textView_lap.setText("00:00:00");
    }
}