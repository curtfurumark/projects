package se.curtrunebylund.projects.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import static logger.CRBLogger.*;

import java.time.LocalDateTime;

import item.State;
import item.Type;
import se.curtrunebylund.projects.classes.Attempt;
import se.curtrunebylund.projects.classes.Project;
import se.curtrunebylund.projects.classes.Task;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.db.PersistSQLite;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.util.MyTimer;
import se.curtrunebylund.projects.projects.Grade;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.R;
import util.Converter;

public class MusicSessionActivity extends AppCompatActivity implements MyTimer.Callback {
    //private EditText editText_task_heading;
    private TextView textView_task_heading;
    private TextView textView_heading;
    private TextView textView_updated;
    private EditText   editText_comment;
    private EditText editText_assignment;
    private TextView textView_repetitions;
    private Button button_repetitions;
    private Button button_timer;
    private Button button_reset;
    private TextView textView_timer;
    private Integer n_repetions = 0;
    private TextView textView_id;
    private MyTimer timer;
    private Task task;
    private Grade grade = Grade.PENDING;
    private Attempt session;
    private Project project;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_session_activity);
        Debug.log("MusicSessionActivity.onCreate()");
        setTitle("music session");

        textView_task_heading = findViewById(R.id.textView_musicSession_task_heading);
        textView_heading = findViewById(R.id.textView_musicSession_heading);
        textView_timer = findViewById(R.id.textView_musicSession_timer);
        editText_comment = findViewById(R.id.editText_musicSession_comment);
        button_reset = findViewById(R.id.button_musicSession_reset);
        button_timer = findViewById(R.id.button_musicSession_timer);
        editText_assignment = findViewById(R.id.editText_musicSession_assignment);
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.stop();
                textView_timer.setText(Converter.formatMilliSeconds(0));
                button_timer.setText("start");
                setTimerButtons();
            }
        });
        button_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(timer.getState()){
                    case  STOPPED:
                        timer.start();
                        button_timer.setText("pause");
                        button_reset.setEnabled(false);
                        break;
                    case RUNNING:
                        timer.pause();
                        //timerState = TimerState.PAUSED;
                        button_reset.setEnabled(true);
                        button_timer.setText("resume");
                        break;
                    case PAUSED:
                        timer.resume();
                        button_timer.setText("pause");
                        button_reset.setEnabled(false);
                }
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
            textView_heading.setText(session.getHeading());
            project = (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);

        }
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
        String heading = textView_heading.getText().toString();
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
        textView_heading.setHint("heading");
        editText_comment.setHint("comment");
        PersistSQLite.update(session, this);
        PersistDBOne.touch(project, task);

        Intent intent = new Intent(this, AttemptListActivity.class);
        intent.putExtra(Constants.INTENT_TASK, task);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        startActivity(intent);
    }
    public void onRadioButtonClick(View view) {
        Debug.log("MusicSessionActivity.onRadioButtonClick(View)");
        int id = view.getId();
        RadioButton radioButton = findViewById(id);
        String str_grade = radioButton.getText().toString();
        grade = Grade.valueOf(str_grade.toUpperCase());
/*        if ( mode.equals(SessionActivity.Mode.EDIT)) {
            current_attempt.setGrade(Grade.valueOf(str_grade.toUpperCase()));
            current_attempt.setUpdated(LocalDateTime.now());
            current_attempt.setState(State.DONE);
            PersistSQLite.update(current_attempt, this);
        }*/
    }

    private void setTimerButtons() {
        log("MusicSesssionActivity.setTimerButtons() " , timer.getState().toString());
        switch(timer.getState()){
            case RUNNING:
                button_reset.setEnabled(false);
                button_timer.setText("pause");
                break;
            case PAUSED:
                button_reset.setEnabled(true);
                button_timer.setText("resume");
                break;
            case STOPPED:
                button_reset.setEnabled(false);
                button_timer.setText("start");
        }
    }

    @Override
    public void onTimerTick(int secs) {
        textView_timer.setText(Converter.formatSeconds(secs));
    }
}