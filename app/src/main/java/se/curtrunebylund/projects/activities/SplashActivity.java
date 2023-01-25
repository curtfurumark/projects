package se.curtrunebylund.projects.activities;

import static logger.CRBLogger.log;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import logger.CRBLogger;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.art.ArtWorkListActivity;
import se.curtrunebylund.projects.db.DBSQLite;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.util.ProjectsLogger;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        try {
            CRBLogger.startLogging(getFilesDir().toString() + "projects.log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Debug.log("SplashActivity.onCreate() infinity branch");
        TextView textView_art = findViewById(R.id.textView_splash_art);
        TextView textView_projects = findViewById(R.id.textView_splash_projects);
        TextView textView_infinity = findViewById(R.id.textView_splash_infinity);
        textView_art.setOnClickListener(view -> startActivity(new Intent(this, ArtWorkListActivity.class)));
        textView_infinity.setOnClickListener(view -> startActivity(new Intent(this, InfinityActivity.class)));
        textView_projects.setOnClickListener(view -> startActivity(new Intent(this, ProjectListActivity.class)));
        openDb();


    }
    private void forOnceInMyLife() {
        log("SplashActivity.forOnceInMyLife()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.splash_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.for_once_in_my_life:
                forOnceInMyLife();
                return true;
            case R.id.splash_open_db:
                openDb();
                break;
            case R.id.splash_start_music_session:
                startMusicSessionActivity();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void startMusicSessionActivity() {
        log("startMusicSessionActivity");
        Intent intent = new Intent(this, MusicSessionActivity.class);
        intent.putExtra(Constants.INTENT_BLANK_MUSIC_SESSION, true);
        startActivity(intent);
    }

    /**
     *  a hack i suppose, android studio needs the database to open to able to inspect it
     *  whatever, it's called from dropdown menu anyways
     */
    private void openDb() {
        DBSQLite db = new DBSQLite(this);
        SQLiteDatabase database = db.getReadableDatabase();
    }

}