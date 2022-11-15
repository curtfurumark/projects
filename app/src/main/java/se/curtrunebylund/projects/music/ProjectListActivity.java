package se.curtrunebylund.projects.music;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.activities.SplashActivity;
import se.curtrunebylund.projects.db.GetProjectsByTagThread;
import se.curtrunebylund.projects.db.GetProjectsThread;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.projects.Project;
import se.curtrunebylund.projects.db.Result;
import se.curtrunebylund.projects.help.Constants;

public class ProjectListActivity extends AppCompatActivity implements
        ProjectAdapter.Callback,
        GetProjectsThread.Callback,
        GetProjectsByTagThread.Callback {
    private android.widget.EditText editText_search;
    private RecyclerView recyclerView_projectList;
    private List<Project> projectList;
    private ProjectAdapter projectAdapter;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_list_activity);
        setTitle("project list");
        Debug.log("MusicProjectListActivity.onCreate();");
        editText_search = findViewById(R.id.editText_project_search);
        recyclerView_projectList = findViewById(R.id.recyclerView_project_list);
        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        PersistDBOne.getProjects(this, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filter(String text) {
        Debug.log("ProjectListActivity.filter() " + text);
        if ( projectList == null){
            Debug.log("projectList is null returning");
            return;
        }
        List<Project> filteredProjects = new ArrayList<>();
        for (Project project: projectList){
            if( project.contains(text)){
                filteredProjects.add(project);
            }
        }
        projectAdapter.setList(filteredProjects);
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.music_project_list_menu, menu);
        return true;
    }
    private void getMusicProjects() {
        Debug.log("getMusicProjects()");
        PersistDBOne.getProjectsByTag("bass", this, this);
    }

    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        Debug.log("ProjectListActivity.onOptionsItemSelected()");
        switch (item.getItemId()) {
            case R.id.icon_home:
                startActivity(new Intent(this, SplashActivity.class));
                return true;
            case R.id.load_from_mysql:
                getMusicProjects();
                return true;
            case R.id.icon_add_project:
                Intent intent = new Intent(this, ProjectEditorActivity.class);
                intent.putExtra(Constants.CREATE_PROJECT, true);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProjectList(java.util.List<Project> musicProjects) {
        Debug.log("showProjectList()");
        projectAdapter = new ProjectAdapter(musicProjects, this, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_projectList.setLayoutManager(layoutManager);
        recyclerView_projectList.setItemAnimator(new DefaultItemAnimator());
        recyclerView_projectList.setAdapter(projectAdapter);
    }

    @Override
    public void onProjectsLoaded(List<Project> projectList, Result result) {
        Debug.log("MusicProjectListActivity.onProjectsLoaded()");
        if( result.isOK()){
            this.projectList = projectList;
            showProjectList(projectList);
        }
        else{
            Debug.showMessage(this, "error getting projects " + result);
        }
    }

    @Override
    public void onItemClick(Project project) {
        Debug.log("MusicProjectListActivity.onItemClick(Project)");
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra(Constants.INTENT_SHOW_TASKS, true);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        startActivity(intent);
    }

    @Override
    public void onGetProjectsDone(List<Project> projectList, Result result) {
        Debug.log("MusicProjectListActivity.onGetProjectsDone()");
        if( result.isOK()){
            this.projectList = projectList;
            showProjectList(projectList);
        }
        else{
            Debug.showMessage(this, "error getting projects " + result);
        }
    }
}