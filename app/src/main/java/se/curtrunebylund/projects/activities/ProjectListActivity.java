package se.curtrunebylund.projects.activities;

import static logger.CRBLogger.log;
import static logger.CRBLogger.logError;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.adapters.ProjectAdapter;
import se.curtrunebylund.projects.classes.Project;
import se.curtrunebylund.projects.db.PersistDBOne;
import se.curtrunebylund.projects.gson.GsonEasy;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.threads.SelectThread;
import se.curtrunebylund.projects.util.Debug;

public class ProjectListActivity extends AppCompatActivity implements
        ProjectAdapter.Callback,
        SelectThread.Callback{
    private RecyclerView recyclerView_projectList;
    private List<Project> projectList;
    private ProjectAdapter projectAdapter;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_list_activity);
        setTitle("project list");
        log("ProjectListActivity.onCreate();");
        android.widget.EditText editText_search = findViewById(R.id.editText_project_search);
        recyclerView_projectList = findViewById(R.id.recyclerView_project_list);
        progressBar = findViewById(R.id.progressBar_projectList);
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
        projectList = new ArrayList<>();
        initRecycler();
        PersistDBOne.getProjects(this);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void initRecycler() {
        log("initRecycler()");
        projectAdapter = new ProjectAdapter(projectList, this, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_projectList.setLayoutManager(layoutManager);
        recyclerView_projectList.setItemAnimator(new DefaultItemAnimator());
        recyclerView_projectList.setAdapter(projectAdapter);
    }

    private void filter(String text) {

        if ( projectList == null){
            logError("projectList is null, i surrender returning");
            return;
        }
        projectAdapter.setList(projectList.stream().filter(project -> project.contains(text)).collect(Collectors.toList()));
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.music_project_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        Debug.log("ProjectListActivity.onOptionsItemSelected()");
        switch (item.getItemId()) {
            case R.id.icon_home:
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            case R.id.load_from_mysql:
                //getMusicProjects();
                return true;
            case R.id.icon_add_project:
                Intent intent = new Intent(this, ProjectEditorActivity.class);
                intent.putExtra(Constants.CREATE_PROJECT, true);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Project project) {
        log("ProjectListActivity.onItemClick(Project)");
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra(Constants.INTENT_SHOW_TASKS, true);
        intent.putExtra(Constants.INTENT_PROJECT, project);
        startActivity(intent);
    }
    @Override
    public void onRequestSelectError(String errMessage) {
        log("onRequestSelectError(String errMessage)", errMessage);
    }

    @Override
    public void onRequestSelectDone(String json) {
        log("onRequestSelectDone(String json)");
        progressBar.setVisibility(View.GONE);
        try {
            projectList = Arrays.asList(GsonEasy.getGson().fromJson(json, Project[].class));
            projectAdapter.setList(projectList);
        }catch (JsonSyntaxException e){
            log("json", json);
            e.printStackTrace();
        }
    }
}