package se.curtrunebylund.projects.activities;


import static se.curtrunebylund.projects.util.ProjectsLogger.log;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.Locale;

import classes.Project;
import classes.Task;
import item.State;
import item.Type;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.classes.Session;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.db.PersistSQLite;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.util.Kronos;
import util.Converter;

public class ItemEditorActivity extends AppCompatActivity implements Kronos.Callback, TextWatcher {

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
    private static final boolean VERBOSE = true;
    private Project project;

    private Session item;



    private enum Mode{
        CREATE, EDIT, DELETE
    }

    /**
     * as is now, mode will always be edit
     * create item, is a fragment thing
     * so if you want to create a new item, first use fragment add heading then push edit to allow for some finetuning of the item
     */
    private Mode mode = Mode.CREATE;
    //private TimerState timerState = TimerState.STOPPED;
    private Kronos kronos;
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_editor);
        setTitle("item editor");
        log("ItemEditorActivity.onCreate()");
        editText_heading = findViewById(R.id.editText_itemEditor_heading);
        editText_description = findViewById(R.id.editText_itemEditor_description);
        editText_comment = findViewById(R.id.editText_itemEditor_comment);
        editText_tags = findViewById(R.id.editText_itemEditor_tags);
        textView_created = findViewById(R.id.editText_itemEditor_created);
        textView_updated = findViewById(R.id.editText_itemEditor_updated);
        textView_id = findViewById(R.id.textView_itemEditor_id);
        textView_parent_id = findViewById(R.id.textView_itemEditor_parent_id);
        textView_duration = findViewById(R.id.editText_itemEditor_duration);
        textView_duration.addTextChangedListener(this);
        spinner_type = findViewById(R.id.spinner_itemEditor_type);
        spinner_state = findViewById(R.id.spinner_itemEditor_state);
        button_timer = findViewById(R.id.button_itemEditor_timer);

        kronos = Kronos.getInstance(this);
        button_timer.setOnClickListener(view -> setKronos());
        button_timer.setOnLongClickListener(view -> {
            button_timer.setText(R.string.ui_start);
            textView_duration.setText("00:00:00");
            kronos.reset();
            return true;
        });
        initSpinnerType();
        initSpinnerState();

        Intent intent = getIntent();
        if( intent.getBooleanExtra(Constants.INTENT_EDIT_ITEM, false)){
            setTitle("edit");
            log("ItemEditorActivity INTENT_EDIT_ITEM");
            item = (Session) intent.getSerializableExtra(Constants.INTENT_SERIALIZED_ATTEMPT);
            log(item);
            task = (Task) intent.getSerializableExtra(Constants.INTENT_TASK);
            project = (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);
            setUI(item);
        }
    }
    @Override
    public void afterTextChanged(Editable editable) {
        if( VERBOSE) log("ItemEditor.afterTextChanged(Editable)", editable.toString());
        //TODO, do something, just anything
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
    private void initSpinnerState() {
        if( VERBOSE) log("ItemEditorActivity.initSpinnerState()", State.PENDING.toString());
        String[] states  = State.toArray();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, states);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_state.setAdapter(arrayAdapter);
        spinner_state.setSelection(State.PENDING.ordinal());
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
    private void initSpinnerType() {
        if( VERBOSE) log("ItemEditorActivity.initSpinnerType()", Type.PENDING.toString());
        String[] types  = Type.toArray();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_type.setAdapter(arrayAdapter);
        spinner_type.setSelection(Type.PENDING.ordinal());
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
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.attempt_edit_activity, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_home:
            case R.id.delete_attempt:
                //PersistSQLite.delete(current_attempt, this);
                Intent intent =  new Intent(this, SessionListActivity.class);
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
            case R.id.icon_save_attempt:
                saveItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if( VERBOSE) log("ItemEditorActivity.onPause()");
        kronos.removeCallback();
    }

    @Override
    protected void onResume() {
        if( VERBOSE) log("ItemEditorActivity.onResume()");
        super.onResume();
        resetKronos();
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    public void onTimerTick(int secs) {
        if( VERBOSE) log("ItemEditor.onTimerTick, secs", secs);
        textView_duration.setText(Converter.formatSecondsWithHours(secs));
    }
    private void resetKronos() {
        if( VERBOSE) log("ItemEditorActivity.resetKronos()");
        kronos.setCallback(this);
        Kronos.State state = kronos.getState();
        switch (state){
            case RUNNING:
                button_timer.setText(R.string.ui_pause);
                break;
            case PAUSED:
                button_timer.setText(R.string.ui_resume);
                break;
            case STOPPED:
                button_timer.setText(R.string.ui_start);
                break;
        }
        textView_duration.setText(Converter.formatSecondsWithHours(kronos.getElapsedTime()));

    }


    private void saveItem() {
        if(VERBOSE) log("ItemEditorActivity.saveItem()");
        item.setComment(editText_comment.getText().toString());
        item.setHeading(editText_heading.getText().toString());
        item.setUpdated(LocalDateTime.now());
        item.setDescription(editText_description.getText().toString());
        item.setType(type);
        item.setState(state);
        item.setDuration(kronos.getElapsedTime());
        PersistSQLite.update(item, this);
        PersistDBOne.touch(project, task);
        kronos.reset();
        kronos.removeCallback();
        Intent intent = new Intent(this, SessionListActivity.class);
        intent.putExtra(Constants.INTENT_TASK, task);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        startActivity(intent);
    }
    private void setKronos() {
        if( VERBOSE) log("ItemEditorActivity.setKronos()");
        Kronos.State state = kronos.getState();
        switch (state){
            case STOPPED:
                kronos.start();
                button_timer.setText(R.string.ui_pause);
                break;
            case RUNNING:
                kronos.pause();
                button_timer.setText(R.string.ui_resume);
                break;
        }
        kronos.setCallback(this);
    }
    private void setUI(Session item) {
        if( VERBOSE) log("ItemEditorActivity.setUI(Item)");
        editText_heading.setText(item.getHeading());
        editText_description.setText(item.getDescription());
        editText_comment.setText(item.getComment());
        editText_tags.setText(item.getTags());
        String created = String.format(Locale.getDefault(), "created: %s",Converter.epochToFormattedDateTime(item.getCreated()));
        textView_created.setText(created);
        String updated = String.format(Locale.getDefault(), "updated %s",Converter.epochToFormattedDateTime(item.getUpdated()));
        textView_updated.setText(updated);
        textView_id.setText(String.valueOf(item.getId()));
        textView_parent_id.setText(String.valueOf(item.getParent_id()));
        textView_duration.setText(Converter.formatSecondsWithHours(item.getDuration()));
        spinner_type.setSelection(item.getType().ordinal());
        spinner_state.setSelection(item.getState().ordinal());
    }
}