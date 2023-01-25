package se.curtrunebylund.projects.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import logger.CRBLogger;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.help.Constants;


@RequiresApi(api = Build.VERSION_CODES.O)
public class CounterActivity extends AppCompatActivity {

    private Integer n_repetitions = 0;
    private EditText editText_attempt_heading;
    private EditText editText_score;
    private TextView textView_nrepetions;
    private EditText editText_assignment;
    private Button button_repetition;
    //private Attempt current_attempt;
    //private List<Listable> attemptList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter_activity);
        setTitle("counter");
        CRBLogger.log("CounterActivity.onCreate()");
        editText_assignment = findViewById(R.id.editText_counterActivity_assignment);
        button_repetition = findViewById(R.id.button_counterActivity_repetion);
        textView_nrepetions = findViewById(R.id.textView_counterActivity_nrepetitions);
        button_repetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n_repetitions++;
                textView_nrepetions.setText(n_repetitions.toString());
            }
        });
        Intent intent = getIntent();
        if( intent.getBooleanExtra(Constants.INTENT_ASSIGNMENT, false)){


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.counter_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.icon_home:
                Intent intent = new Intent(this, SessionListActivity.class);
                //intent.putExtra(ProjectManager.INTENT_PROJECT_ID, task.getProjectID());
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }



    public void onButtonReset(View view){
        n_repetitions = 0;

    }

}