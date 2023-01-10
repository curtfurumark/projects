package se.curtrunebylund.projects.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import logger.CRBLogger;
import se.curtrunebylund.projects.db.DBSQLite;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.db.DBAdmin;
import se.curtrunebylund.projects.db.GetAllGrandChildrenThread;
import se.curtrunebylund.projects.db.Result;
import se.curtrunebylund.projects.classes.Task;
import se.curtrunebylund.projects.art.ArtWorkListActivity;
import se.curtrunebylund.projects.util.ProjectsLogger;

public class SplashActivity extends AppCompatActivity implements GetAllGrandChildrenThread.Callback {
    private TextView textView_art;
    private TextView textView_projects;
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
        Debug.log("SplashActivity.onCreate() infinity branch");
        textView_art = findViewById(R.id.textView_splash_art);
        textView_projects = findViewById(R.id.textView_splash_projects);
        textView_infinity = findViewById(R.id.textView_splash_infinity);
        textView_art.setOnClickListener(view -> startActivity(new Intent(this, ArtWorkListActivity.class)));
        textView_infinity.setOnClickListener(view -> startActivity(new Intent(this, InfinityActivity.class)));
        textView_projects.setOnClickListener(view -> startActivity(new Intent(this, ProjectListActivity.class)));
        openDb();


    }
    private void forOnceInMyLife(){
        ProjectsLogger.log("SplashActivity.forOnceInMyLife()");
        DBAdmin.createLogTable(this);

/*        DBSQLite dbsqLite = new DBSQLite(this);
        dbsqLite.printInfo();
        try {
            DBAdmin.addColumnsToAttempts(this);
        }catch(Exception e){
            CRBLogger.log(e.toString());
        }*/
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

        }
        return super.onOptionsItemSelected(item);
    }

    private void openDb() {
        DBSQLite db = new DBSQLite(this);
        SQLiteDatabase database = db.getReadableDatabase();
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
        DBAdmin.copyTasksToDBLocal(tasks, this);
        Debug.showMessage(this, "done copying");
    }
}