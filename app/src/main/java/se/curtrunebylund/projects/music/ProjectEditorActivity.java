package se.curtrunebylund.projects.music;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.db.AddProjectThread;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.db.Result;
import se.curtrunebylund.projects.db.UpdateProjectThread;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.projects.Project;
import se.curtrunebylund.projects.projects.State;


public class ProjectEditorActivity extends AppCompatActivity implements
        AddProjectThread.Callback,
        DatePickerDialog.OnDateSetListener,
        UpdateProjectThread.Callback {
    private EditText editText_description;
    private EditText  editText_heading;
    private EditText editText_comment;
    private EditText editText_tags;
    private TextView textView_date;
    private TextView textView_updated;
    private TextView textView_created;
    private TextView textView_id;
    //private LocalDate target_date = null;
    private Project project;


    private enum Mode{
        CREATE, EDIT
    }
    private Mode mode;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_editor_activity);
        Debug.log("ProjectEditorActivity.onCreate");
        editText_description = findViewById(R.id.editText_addProject_description);
        editText_tags = findViewById(R.id.editText_addProject_tags);
        editText_comment = findViewById(R.id.editText_addProject_comment);
        textView_date = findViewById(R.id.textView_addProject_targetDate);
        textView_date.setText(LocalDate.now().toString());
        editText_heading = findViewById(R.id.editText_addProject_heading);
        textView_created = findViewById(R.id.textView_projectEditor_created);
        textView_updated = findViewById(R.id.textView_projectEditor_updated);
        textView_id = findViewById(R.id.textView_projectEditor_id);
        Intent intent = getIntent();
        if( intent.getBooleanExtra(Constants.INTENT_EDIT_PROJECT, false)){
            mode = Mode.EDIT;
            setTitle("edit project");
            project = (Project) intent.getSerializableExtra(Constants.INTENT_PROJECT);
            if( project == null){
                Debug.showMessage(this, "project is null");
            }
            editText_heading.setText(project.getHeading());
            editText_description.setText(project.getDescription());
            editText_comment.setText(project.getComment());
            String updated = String.format(Locale.getDefault(),"updated: %s", project.getUpdated());
            textView_updated.setText(updated);
            String created = String.format(Locale.getDefault(),"created: %s", project.getAdded());
            textView_created.setText(created);
            String id = String.format(Locale.getDefault(),"id: %d", project.getId());
            textView_id.setText(id);
        }else if(intent.getBooleanExtra(Constants.CREATE_PROJECT, false)){
            Debug.log("CREATE_PROJECT");
            setTitle("create project");
            mode = Mode.CREATE;
            createProject();
        }
    }

    private void createProject() {
        Debug.log("ProjectAddActivity.createProject()");
        project = new Project();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void afterAddProjectThread(Result result){

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveProject(){
        Debug.log( "ProjectAddActivity.onAddProject()");
        project.setHeading(editText_heading.getText().toString());
        project.setComment(editText_comment.getText().toString());
        project.setDescription(editText_description.getText().toString());
        project.setUpdated(LocalDateTime.now());
        project.setTags(editText_tags.getText().toString());

        if( mode.equals(Mode.CREATE)) {
            project.setState(State.TODO);
            project.setCreated(LocalDateTime.now());
            PersistDBOne.add(project, this, this);
        }else{
            PersistDBOne.update(project, this, this);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.project_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Debug.log("onOptionsItemSelected()");
        switch (item.getItemId()) {
            case R.id.icon_home:
                Intent intent1 = new Intent(this, ProjectListActivity.class);
                startActivity(intent1);
                return true;
            case R.id.icon_save:
                saveProject();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    public void onDateClick(View view){
        showDatePickerDialog();
    }


    @Override
    public void onProjectUpdated(Result res) {
        Debug.log("ProjectEditor.onProjectUpdated(Result)");
        if( res.isOK()){
            Intent intent = new Intent(this, ProjectListActivity.class);
            startActivity(intent);

        }else {
            Debug.showMessage(this, "project updated ? " + res.toString());
        }
    }


    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnDateSetListener(this);
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        String date = String.format("%d-%02d-%02d", year, month +1, dayOfMonth);
        //target_date = LocalDate.of(year, month + 1, dayOfMonth);
        textView_date.setText(date);
    }

    @Override
    public void onAddProjectThreadDone(Project project, Result result) {
        Debug.log("Project.onAddProjectThreadDone(): " + result.toString());
        if (result.isOK()){
            project.setId(result.getID());
            Intent intent = new Intent(this, ProjectListActivity.class);
            startActivity(intent);
        }else {
            Debug.showMessage(this, "NOT OK: " + result.toString());
        }
    }
}