package se.curtrunebylund.projects.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import logger.CRBLogger;
import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.db.DBStuff;
import se.curtrunebylund.projects.db.GetAllGrandChildrenThread;
import se.curtrunebylund.projects.db.Result;
import se.curtrunebylund.projects.infinity.InfinityActivity;
import se.curtrunebylund.projects.projects.Task;
import se.curtrunebylund.projects.music.ProjectListActivity;
import se.curtrunebylund.projects.art.ArtWorkListActivity;

public class SplashActivity extends AppCompatActivity implements GetAllGrandChildrenThread.Callback {
    private TextView textView_art;
    private TextView textView_music;
    private TextView textView_infinity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        try {
            CRBLogger.startLogging(getFilesDir().toString() + "projects.log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Debug.log("SplashActivity.onCreate()");
        textView_art = findViewById(R.id.textView_splash_art);
        textView_music = findViewById(R.id.textView_splash_music);
        textView_infinity = findViewById(R.id.textView_splash_infinity);
        textView_art.setOnClickListener(view -> startActivity(new Intent(this, ArtWorkListActivity.class)));
        textView_music.setOnClickListener(view -> startActivity(new Intent(this, ProjectListActivity.class)));
        textView_infinity.setOnClickListener(view -> startActivity(new Intent(SplashActivity.this, InfinityActivity.class)));
    }
    private void forOnceInMyLife(){
        Debug.log("SplashActivity.forOnceInMyLife()");
        //List<Attempt> attempts = PersistDBProjects.getAttempts(this);
        //Debug.logAttempts(attempts, false);

        //PersistDBOne.getGrandChildrenTasks(this, this);
        //DBStuff.createMusicTable(this);
        //PersistSQLite.getTableNames(this);
        //PersistInfinity.createTable(this);
        DBStuff.addItem2InfinityTable("a heading", "dev", this);
        DBStuff.addItem2InfinityTable("beheading", "dev", this);
        DBStuff.addItem2InfinityTable("headlong", "dev", this);
        //List<Attempt> attempts = PersistDBProjects.getAttempts(this);
        //Debug.logAttempts(attempts, true);
        //DBProjects db = new DBProjects(this);
        //db.executeSQL("DROP TABLE attempts");
        //db.executeSQL(DBStuff.CREATE_ATTEMPTS_TABLE);
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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * TEMP, converting dbone.commments,tasks (grand children) to local.sqlite.attemmpts
     * @param tasks
     * @param result
     */
    @Override
    public void onGetAllGrandChildrenDone(List<Task> tasks, Result result) {
        Debug.log("SplashActivity.onGetAllGrandChildrenDone(List<Task>, Result");
        if( !result.isOK()) {
            Debug.showMessage(this, result.toString());
            return;
        }
        Debug.showMessage(this, "will copy tasks to attempts");
        DBStuff.copyTasksToDBLocal(tasks, this);
        Debug.showMessage(this, "done copying");
    }
}