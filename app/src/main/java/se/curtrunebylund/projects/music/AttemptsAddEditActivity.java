package se.curtrunebylund.projects.music;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalDateTime;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.R;

public class AttemptsAddEditActivity extends AppCompatActivity {
    private EditText editText_description;
    private TextView textView_state;
    private TextView textView_created;
    private TextView textView_updated;
    private TextView textView_id;
    private EditText editText_tags;
    private int project_id;
    private boolean edit_project = false;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attempts_add_edit_activity);
        Debug.log("AttemptsAddEdit.onCreate()");
        setTitle("AttemptsAddEditActivity");
        editText_description = findViewById(R.id.editText_description);
        textView_created = findViewById(R.id.textView_project_created);
        textView_id = findViewById(R.id.textView_project_id);
        textView_state = findViewById(R.id.textView_project_state);
        textView_updated = findViewById(R.id.textView_project_updated);
        editText_tags= findViewById(R.id.editText_project_tags);
        Intent intent = getIntent();
        project_id = intent.getIntExtra("project_id", -1);
        if ( project_id > 0 ){
            Debug.log("edit project: " + project_id);

        }else{
            textView_updated.setText(LocalDateTime.now().toString());
            textView_created.setText(LocalDate.now().toString());
        }
        //Debug.showMessage(this, "ProjectAddEditActivity");
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.project_add_edit, menu);
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_save_project:
                //save();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}