package se.curtrunebylund.projects.activities;


import static se.curtrunebylund.projects.util.ProjectsLogger.log;

import android.content.Intent;
import android.os.Build;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import item.State;
import item.Type;
import logger.CRBLogger;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.classes.Attempt;
import se.curtrunebylund.projects.classes.Project;
import se.curtrunebylund.projects.classes.Task;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.db.PersistSQLite;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.util.MyTimer;
import se.curtrunebylund.projects.util.ProjectsLogger;
import util.Converter;

public class ItemEditorActivity extends AppCompatActivity implements MyTimer.Callback {

    private EditText editText_heading;
    private EditText editText_description;
    private EditText editText_comment;
    private EditText editText_tags;
    private EditText textView_created;
    private TextView textView_updated;

    private TextView textView_id;
    private TextView textView_parent_id;
    private TextView textView_duration;
    private Button button_timer;
    private Spinner spinner_type;
    private Spinner spinner_state;
    private Type type = Type.PENDING;
    private State state = State.PENDING;
    private Task task;
    private Project project;
    private List<Attempt> attempts = new ArrayList<>();
    private Attempt item;

    @Override
    public void onTimerTick(int secs) {
        log("onTimerTick, secs", secs);
    }

    private enum Mode{
        CREATE, EDIT, DELETE
    }
    private enum TimerState{
        STOPPED, RUNNING, PAUSED

    }
    private TimerState timer_state = TimerState.STOPPED;
    private Mode mode = Mode.CREATE;
    //private TimerState timerState = TimerState.STOPPED;
    MyTimer timer;


    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_editor);
        setTitle("item editor");
        CRBLogger.log("ItemEditorActivity.onCreate()");
        editText_heading = findViewById(R.id.editText_itemEditor_heading);
        editText_description = findViewById(R.id.editText_itemEditor_description);
        editText_comment = findViewById(R.id.editText_itemEditor_comment);
        editText_tags = findViewById(R.id.editText_itemEditor_tags);
        textView_created = findViewById(R.id.editText_itemEditor_created);
        textView_updated = findViewById(R.id.editText_itemEditor_updated);
        textView_id = findViewById(R.id.textView_itemEditor_id);
        textView_parent_id = findViewById(R.id.textView_itemEditor_parent_id);
        textView_duration = findViewById(R.id.textView_itemEditor_duration);
        spinner_type = findViewById(R.id.spinner_itemEditor_type);
        spinner_state = findViewById(R.id.spinner_itemEditor_state);
        button_timer = findViewById(R.id.button_itemEditor_timer);
        button_timer.setOnClickListener(view -> handleTimer());
        button_timer.setOnLongClickListener(view -> {
            timer_state = TimerState.STOPPED;
            button_timer.setText("start");
            textView_duration.setText("00:00:00");
            timer.reset();
            return true;
        });
        initSpinnerType(Type.PENDING);
        initSpinnerState(State.PENDING);
        timer = MyTimer.getInstance(this, this);
        timer.setTextView(textView_duration);

        Intent intent = getIntent();
        if( intent.getBooleanExtra(Constants.INTENT_EDIT_ITEM, false)){
            log("ItemEditorActivity INTENT_EDIT_ITEM");
            item = (Attempt) intent.getSerializableExtra(Constants.INTENT_SERIALIZED_ATTEMPT);
            ProjectsLogger.log(item);
            task = (Task) intent.getSerializableExtra(Constants.INTENT_TASK);
            project = (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);
            setUI(item);
        }
    }

    private void handleTimer() {
        log("handleTimer()");
        switch(timer_state){
            case STOPPED:
                timer.start();
                button_timer.setText("PAUSE");
                timer_state = TimerState.RUNNING;
                break;
            case RUNNING:
                timer.pause();
                timer_state = TimerState.PAUSED;
                button_timer.setText("resume");
                break;
        }
    }

    private void initSpinnerType(Type type) {
        log("InfinityEditorActivity.initSpinnerType()");
        String[] types  = Type.toArray();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_type.setAdapter(arrayAdapter);
        spinner_type.setSelection(type.ordinal());
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ItemEditorActivity.this.type = Type.values()[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void initSpinnerState(State state) {
        log("InfinityEditorActivity.initSpinnerState()");
        String[] states  = State.toArray();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, states);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_state.setAdapter(arrayAdapter);
        spinner_state.setSelection(state.ordinal());
        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ItemEditorActivity.this.state = State.values()[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUI(Attempt item) {
        log("ItemEditorActivity.setUI(Item)");
        editText_heading.setText(item.getHeading());
        editText_description.setText(item.getDescription());
        editText_comment.setText(item.getComment());
        editText_tags.setText(item.getTags());
        textView_created.setText(Converter.epochToFormattedDateTime(item.getCreated()));
        textView_updated.setText(Converter.epochToFormattedDateTime(item.getUpdated()));
        textView_id.setText(String.valueOf(item.getId()));
        textView_parent_id.setText(String.valueOf(item.getParent_id()));
        textView_duration.setText(Converter.formatSeconds(item.getDuration()));
        spinner_type.setSelection(item.getType().ordinal());
        spinner_state.setSelection(item.getState().ordinal());
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
                //newAttempt();
                return true;
            case R.id.intent_counter:
                startActivity(new Intent(this, CounterActivity.class));
                break;
            case R.id.delete_attempt:
                //PersistSQLite.delete(current_attempt, this);
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


    private void saveAttempt() {
        CRBLogger.log("ItemEditorActivity.saveAttempt()");
        item.setComment(editText_comment.getText().toString());
        item.setHeading(editText_heading.getText().toString());
        item.setUpdated(LocalDateTime.now());
        item.setDescription(editText_description.getText().toString());
        item.setType(type);
        item.setState(state);
        item.setDuration(timer.getElapsedTime());
        PersistSQLite.update(item, this);
        PersistDBOne.touch(project, task);
        Intent intent = new Intent(this, AttemptListActivity.class);
        intent.putExtra(Constants.INTENT_TASK, task);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        startActivity(intent);
    }
}