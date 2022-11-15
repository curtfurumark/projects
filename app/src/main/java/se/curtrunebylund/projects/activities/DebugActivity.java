package se.curtrunebylund.projects.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.R;

public class DebugActivity extends AppCompatActivity {

    public static final String INTENT_ERR_MESSAGE = "intent_err_message";
    private TextView textView_err_message;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_activity);
        Debug.log("DebugActivity.onCreate()");
        textView_err_message = findViewById(R.id.textView_debugActivity_errMesage);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String message = intent.getStringExtra(INTENT_ERR_MESSAGE);
        message = message.isEmpty() ? "no message...hmm": message;
        Debug.showMessage(this, message);
        textView_err_message.setText(message);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

         */
    }
}