package se.curtrunebylund.projects.music;

import androidx.annotation.NonNull;
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

import java.time.LocalDateTime;
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
import se.curtrunebylund.projects.help.MyTimer;
import se.curtrunebylund.projects.projects.Project;
import se.curtrunebylund.projects.projects.State;
import se.curtrunebylund.projects.projects.Task;

public class AttemptEditActivity extends AppCompatActivity implements
        AttemptsAdapter.Callback,
        AttemptBottomSheet.Listener,
        MyTimer.Callback,
        GetChildrenToTaskThread.Callback {
    private static final long FIVE_MINUTES = 5  * 60 * 1000;
    private TextView textView_task_heading;
    private EditText editText_heading;
    private EditText editText_comment;
    private Button button_timer;
    private Button button_reset;
    private TextView textView_timer;
    private TextView textView_attempt_id;
    private Task task;
    private Project project;
    private List<Attempt> attempts = new ArrayList<>();
    private Attempt current_attempt;
    private Grade grade = Grade.PENDING;
    /*
    private enum TimerState{
        STOPPED, RUNNING, PAUSED
    }

     */
    private enum Mode{
        CREATE, EDIT
    }
    private Mode mode = Mode.CREATE;
    //private TimerState timerState = TimerState.STOPPED;
    MyTimer timer;


    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attempt_edit_activity);
        setTitle("Edit attempt");
        Debug.log("AttemptEditActivity.onCreate()");
        timer = MyTimer.getInstance(this, this);
        //timer.setActivity(this);
        textView_task_heading = findViewById(R.id.textView_sessionActivity_task_heading);
        editText_heading = findViewById(R.id.editText_sessionActivity_heading);
        editText_comment = findViewById(R.id.editText_sessionActivity_comment);
        button_timer = findViewById(R.id.button_sessionActivity_timer);
        button_reset = findViewById(R.id.button_sessionActivity_reset);
        textView_timer = findViewById(R.id.textView_sessionActivity_timer);
        timer.setTextView(textView_timer);
        textView_attempt_id = findViewById(R.id.textView_attemptEdit_id);
        button_timer.setText("start");
        textView_timer.setText(Converter.formatMilliSeconds(0));
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
                //Debug.log("Timer.onClick() " + timerState.toString());
                switch(timer.getState()){
                    case  STOPPED:
                        timer.start();
                        //timerState = TimerState.RUNNING;
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
        Intent intent = getIntent();
        task = (Task)intent.getSerializableExtra(Constants.INTENT_TASK);
        project = (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);
        current_attempt=  (Attempt) intent.getSerializableExtra(Constants.INTENT_SERIALIZED_ATTEMPT);
        if (task != null && project != null) {
            String heading = String.format(Locale.getDefault(),"%s (id=%d)", task.getHeading(), task.getId());
            textView_task_heading.setText(heading);
            editText_heading.setText(current_attempt.getHeading());
            editText_comment.setText(current_attempt.getComment());
            String sz_id = String.format(Locale.getDefault(), "id: %d", current_attempt.getId());
            textView_attempt_id.setText(String.valueOf(sz_id));
        }
        setTimerButtons();
    }


    private void getAttempts(Task task) {
        Debug.log("SessionActivity.getAttempts(Task)");
        PersistDBOne.getAttempts(task, this, this);
    }
    /**
     * callback for AttemptBottomSheet
     * @param heading
     * @param number
     */
    @Override
    public void onAttemptSave(String heading, int number) {
        Debug.log("SessionActivity.onAttemptSave(String heading, int number)");
        Debug.log(task);
        Attempt attempt = new Attempt();
        attempt.setHeading(heading);
        attempt.setParent_id(task.getId());
        attempt = PersistSQLite.insert(attempt, this);
        attempts.add(attempt);
        sortAttempts();

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.attempt_edit_activity, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_home:
                Intent intent =  new Intent(this, AttemptListActivity.class);
                intent.putExtra(Constants.INTENT_TASK, task);
                intent.putExtra(Constants.INTENT_SHOW_TASKS, true);
                intent.putExtra(Constants.INTENT_PROJECT, project);
                startActivity(intent);
                return true;
            case R.id.new_attempt:
                newAttempt();
                return true;
            case R.id.delete_attempt:
                PersistSQLite.delete(current_attempt, this);
                Intent intent1 =  new Intent(this, AttemptListActivity.class);
                intent1.putExtra(Constants.INTENT_TASK, task);
                intent1.putExtra(Constants.INTENT_SHOW_TASKS, true);
                intent1.putExtra(Constants.INTENT_PROJECT, project);
                startActivity(intent1);
                return true;
            case R.id.icon_save_attempt:
                saveAttempt();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Debug.log("...onSaveInstanceState");
        outState.putInt("timerState", timer.getState().ordinal());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Debug.log("...onRestoreInstanceState()");
        //timerState = TimerState.values()[savedInstanceState.getInt("timerState")];
        //timer.setActivity(this);
        setTimerButtons();
    }

    @Override
    public void onGetChildrenToTaskDone(List<Task> tasks, Result result) {
        Debug.log("SessionActivity.onGetChildrenToTaskDone(List<Task> Result)");
        Debug.logTaskList(tasks);
        Debug.log(result);
        attempts = Converter.convertTasks(tasks);
    }

    public void onRadioButtonClick(View view) {
        Debug.log("SessionActivity.onRadioButtonClick(View)");
        int id = view.getId();
        RadioButton radioButton = findViewById(id);
        String str_grade = radioButton.getText().toString();
        grade = Grade.valueOf(str_grade.toUpperCase());
        if ( mode.equals(Mode.EDIT)) {
            current_attempt.setGrade(Grade.valueOf(str_grade.toUpperCase()));
            current_attempt.setUpdated(LocalDateTime.now());
            current_attempt.setState(State.DONE);
            PersistSQLite.update(current_attempt, this); }

    }

    @Override
    public void onItemClick(Attempt attempt) {
        Debug.showMessage(this, attempt.getHeading());
        setTitle("edit attempt");
        mode = Mode.EDIT;
        current_attempt = attempt;
        editText_heading.setText(attempt.getHeading());
        editText_comment.setText(attempt.getComment());
    }

    @Override
    public void onTimerTick(int secs) {
        Debug.log("AttemptEditActivity.onTimerClick() " + secs);
        Debug.log("...formatted: " + Converter.formatSeconds(secs));
        textView_timer.setText(Converter.formatSeconds(secs));
    }

    private void newAttempt() {
        Debug.log("AttemptEditActivity.newAttempt");
        setTitle("new attempt");
        mode = Mode.CREATE;
        editText_comment.setHint("comment here");
        editText_heading.setHint("heading here");
    }
    private void setTimerButtons() {
        Debug.log("AttemptEditActivity.setTimerButtons() " + timer.getState().toString());
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

    private void sortAttempts(){
        Collections.sort(attempts, new Comparator<Attempt>() {
            @Override
            public int compare(Attempt a1, Attempt a2) {
                return Long.compare(a2.compare(), a1.compare());
            }
        });
    }
    private void saveAttempt() {
        Debug.log("SessionActivity.saveAttempt()");
        String heading = editText_heading.getText().toString();
        if ( heading.isEmpty()){
            Debug.showMessage(this, "a heading please");
            return;
        }
        String comment = editText_comment.getText().toString();
        current_attempt.setHeading(heading);
        current_attempt.setUpdated(LocalDateTime.now());
        current_attempt.setComment(comment);
        current_attempt.setGrade(grade);
        current_attempt.setParent_id(task.getId());
        current_attempt.setDuration(timer.getElapsedTime());
        PersistDBOne.touch(project, task);
        PersistSQLite.update(current_attempt, this);

        editText_heading.setHint("heading");
        editText_comment.setHint("comment");
        Intent intent = new Intent(this, AttemptListActivity.class);
        intent.putExtra(Constants.INTENT_TASK, task);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        startActivity(intent);
    }

    private void updateAttempt() {
        Debug.log("SessionActivity.updateAttempt()");
        PersistSQLite.update(current_attempt, this);
    }
}