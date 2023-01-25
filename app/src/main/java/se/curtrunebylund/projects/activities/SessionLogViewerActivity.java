package se.curtrunebylund.projects.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.help.Constants;

public class SessionLogViewerActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_log_viewer);
        textView = findViewById(R.id.textView_sessionLogViewer);
        Intent intent = getIntent();
        if( intent.getBooleanExtra(Constants.INTENT_VIEW_SESSION_LOG, false)){
            String json = intent.getStringExtra(Constants.INTENT_SESSION_LOG_JSON);
            textView.setText(json);
        }
    }
}
