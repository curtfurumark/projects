package se.curtrunebylund.projects.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import item.State;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.db.AddTaskThread;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.db.Result;
import se.curtrunebylund.projects.db.UpdateTaskThread;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.classes.Project;
import se.curtrunebylund.projects.classes.Task;


@RequiresApi(api = Build.VERSION_CODES.O)
public class TaskAddActivity extends AppCompatActivity implements
        AddTaskThread.Callback,
        UpdateTaskThread.Callback{

    private TextView label_project_heading;
    private EditText editText_task_heading;
    private EditText editText_description;
    private EditText editText_tags;
    private TextView textView_target_date;
    private Spinner spinner_state;
    private Switch switch_is_done;
    private Project project;
    private Task task;
    private LocalDate target_date = LocalDate.now();
    private State state = State.TODO;



    enum Mode {
        CREATE, EDIT
    }
    private Mode mode = Mode.CREATE;
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_add_activity);
        Debug.log("TaskAddActivity.onCreate()");
        setTitle("add task");
        //Debug.log("TaskAddActivity.onCreate()");
        //on_create = true;
        label_project_heading = findViewById(R.id.textView_taskAdd_project_heading);
        editText_task_heading = findViewById(R.id.editText_taskAdd_heading);
        editText_description = findViewById(R.id.editText_taskAdd_description);
        editText_tags = findViewById(R.id.editText_taskAdd_tags);
        textView_target_date = findViewById(R.id.textView_taskAdd_target_date);

        spinner_state = findViewById(R.id.spinner_taskAdd_state);
        initStateSpinner(State.TODO);
        textView_target_date.setText(target_date.toString());
        textView_target_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowDateDialog();
            }
        });
        switch_is_done = findViewById(R.id.switch_taskEdit_done);
        switch_is_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Debug.showMessage(TaskAddActivity.this, "onclick");
                //saveTask();
            }
        });
        Intent intent = getIntent();
        if( intent.getBooleanExtra(Constants.INTENT_ADD_TASK, false)){
            setTitle("add task");
            mode = Mode.CREATE;
            project = (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);
            if( null == project ){
                Debug.log("trouble, task or project is null;");
            }else{
                label_project_heading.setText(project.getHeading());
            }
        }
        if( intent.getBooleanExtra(Constants.INTENT_EDIT_TASK, false)){
            setTitle("edit task");
            mode = Mode.EDIT;
            project = (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);
            if( project == null){
                Debug.showMessage(this, "project is null");
            }
            task = (Task) intent.getSerializableExtra(Constants.INTENT_TASK);
            editText_task_heading.setText(task.getHeading());
            editText_description.setText(task.getDescription());
            editText_tags.setText(task.getTags());
            switch_is_done.setChecked(task.getState().equals(State.DONE));
        }
    }
    private void initStateSpinner(State initial_state) {
        Debug.log("TaskAddActivity.initStateSpinner()");
        String[] states = State.toArray();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, states);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_state.setAdapter(arrayAdapter);
        spinner_state.setSelection(initial_state.ordinal());
        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = State.values()[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_edit_task, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_save_task:
                saveTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTask() {
        Debug.log("TaskAddActivity.saveTask()");
        if( mode.equals(Mode.EDIT)){
            saveEditedTask();;
        }else {
            saveNewTask();
        }
    }

    private void saveNewTask() {
        Debug.log("TaskAddActivity.saveNewTask()");
        String heading = editText_task_heading.getText().toString();
        if ( heading.isEmpty()){
            Debug.showMessage(this, "missing heading");
            return;
        }
        String description = editText_description.getText().toString();
        task = new Task( heading, description, project.getId());
        if (switch_is_done.isChecked()){
            task.setState(State.DONE);
        }else{
            task.setState(state);
        }
        task.setTargetDate( target_date);
        task.setTags(editText_tags.getText().toString());
        try {
            PersistDBOne.add(task, this, this);
        }catch (Exception e){
            Debug.log("error saving task to database");
            Debug.showMessage(this, e.toString());
        }
    }

    private void saveEditedTask() {
        Debug.log("TaskAddActivity.saveEditedTask()");
        task.setHeading(editText_task_heading.getText().toString());
        task.setDescription(editText_description.getText().toString());
        task.setTags(editText_tags.getText().toString());
        task.setUpdated(LocalDateTime.now());
        task.setState(switch_is_done.isChecked()?State.DONE: State.TODO);
        PersistDBOne.update(task, this, this);
    }

    public void onShowDateDialog(){
        Debug.log("TaskAddActivity.onShowDateDialog()");
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                textView_target_date.setText(String.format("%d-%d-%d", year, month + 1 , dayOfMonth));
                target_date = LocalDate.of(year, month + 1, dayOfMonth);
            }
        });
        datePickerDialog.show();
    }
    @Override
    public void onTaskAdded(Result result, Task task) {
        Debug.log("TaskAddActivity.onTaskAdded() res: " + result.toString());
        if ( result.isOK()) {
            task.setId(result.getID());
            Debug.log(task);
            project.setUpdated(LocalDateTime.now());
            //PersistDBOne.update(project, null, null);
            Intent intent = new Intent(this, TaskListActivity.class);
            intent.putExtra(Constants.INTENT_SHOW_TASKS, true);
            intent.putExtra(Constants.INTENT_PROJECT, project);
            startActivity(intent);
        }else{
            Debug.showMessage(this, result.toString());
        }
    }

    @Override
    public void onTaskUpdated(Result result) {
        Debug.log("TaskAddActivity.onTaskUpdated(ResultV2)");
        if( !result.isOK()){
            Debug.showMessage(this, result.getPHPResult());
        }
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        intent.putExtra(Constants.INTENT_SHOW_TASKS, true);
        startActivity(intent);
    }
}