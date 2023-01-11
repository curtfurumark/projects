package se.curtrunebylund.projects.activities;

import static logger.CRBLogger.log;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;

import item.State;
import item.Type;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.classes.Attempt;
import se.curtrunebylund.projects.classes.Project;
import se.curtrunebylund.projects.classes.Task;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.db.PersistSQLite;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.projects.Grade;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.util.MyTimer;
import util.Converter;

public class MusicSessionActivity extends AppCompatActivity implements MyTimer.Callback {
    private EditText editText_heading;
    private TextView textView_updated;
    private EditText  editText_comment;
    private EditText editText_assignment;
    private Button button_repetitions;
    private Button button_timer;
    private TextView textView_timer;
    private Integer n_repetions = 0;
    private TextView textView_id;
    private MyTimer timer;
    private Task task;
    private Grade grade = Grade.PENDING;
    private Attempt session;
    private Project project;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_session_activity);
        Debug.log("MusicSessionActivity.onCreate()");
        setTitle("music session");

        //private EditText editText_task_heading;
        TextView textView_task_heading = findViewById(R.id.textView_musicSession_task_heading);
        editText_heading = findViewById(R.id.editText_musicSession_heading);
        textView_timer = findViewById(R.id.textView_musicSession_timer);
        editText_comment = findViewById(R.id.editText_musicSession_comment);
        Button button_lap = findViewById(R.id.button_musicSession_lap);
        button_timer = findViewById(R.id.button_musicSession_timer);
        editText_assignment = findViewById(R.id.editText_musicSession_assignment);
        button_lap.setOnClickListener(view -> {
            //timer.stop();
            startLap();
            //textView_timer.setText(Converter.formatMilliSeconds(0));
            //button_timer.setText("start");
            //setTimerButtons();
        });
        button_timer.setOnClickListener(view -> {
            switch(timer.getState()){
                case  STOPPED:
                    timer.start();
                    button_timer.setText("pause");
                    break;
                case RUNNING:
                    timer.pause();
                    button_timer.setText("resume");
                    break;
                case PAUSED:
                    timer.resume();
                    button_timer.setText("pause");
            }
        });
        button_timer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                timer.stop();
                return true;
            }
        });

        button_repetitions = findViewById(R.id.button_musicSession_repitition);
        button_repetitions.setOnClickListener(view -> {
            n_repetions++;
            button_repetitions.setText(n_repetions.toString());
        });
        button_repetitions.setOnLongClickListener(view -> {
            n_repetions = 0;
            button_repetitions.setText("do it");
            return  true;
        });
        timer = MyTimer.getInstance(this, this);
        timer.setTextView(textView_timer);
        Intent intent = getIntent();
        if( intent.getBooleanExtra(Constants.INTENT_MUSIC_SESSION, false)){
            task = (Task) intent.getSerializableExtra(Constants.INTENT_TASK);
            textView_task_heading.setText(task.getHeading());
            session = (Attempt) intent.getSerializableExtra(Constants.INTENT_SERIALIZED_ATTEMPT);
            setTitle(String.format("%s, (%d)", session.getHeading(), session.getId()));
            editText_heading.setText(session.getHeading());
            project = (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);

        }
    }

    private void startLap() {
        log("MusicSessionActivity.startLap()");
        int startSecs = timer.getElapsedTime();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.project_add_edit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_save_project:
                saveSession();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveSession() {
        Debug.log("MusicSessionActivity.saveSession()");
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
        session.setType(Type.SESSION_MUSIC);
        session.setState(State.DONE);
        editText_heading.setHint("heading");
        editText_comment.setHint("comment");
        PersistSQLite.update(session, this);
        PersistDBOne.touch(project, task);

        Intent intent = new Intent(this, AttemptListActivity.class);
        intent.putExtra(Constants.INTENT_TASK, task);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        startActivity(intent);
    }

    private void setTimerButtons() {
        log("MusicSessionActivity.setTimerButtons() " , timer.getState().toString());
        switch(timer.getState()){
            case RUNNING:
                button_timer.setText("pause");
                break;
            case PAUSED:
                button_timer.setText("resume");
                break;
            case STOPPED:
                button_timer.setText("start");
        }
    }

    @Override
    public void onTimerTick(int secs) {
        textView_timer.setText(Converter.formatSeconds(secs));
    }
}