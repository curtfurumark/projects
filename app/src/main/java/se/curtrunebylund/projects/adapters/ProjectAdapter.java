package se.curtrunebylund.projects.adapters;

import static logger.CRBLogger.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.classes.Project;
import util.Converter;


public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.MyViewHolder>{
    private List<Project> projectList;
    private Context context;
    public interface Callback{
        public void onItemClick(Project project);
    }
    private Callback callback;
    public ProjectAdapter(java.util.List<Project> musicProjects,Callback callback,  Context context) {
        System.out.println("MusicProjectAdapter ctor");
        if (musicProjects == null){
            System.out.println("todolist is null");
        }
        this.projectList = musicProjects;
        this.context = context;
        this.callback = callback;
    }
    @androidx.annotation.NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        //Debug.log("ProjectAdapter.onCreateViewHolder");
        android.view.View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_adapter, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull final MyViewHolder holder, int position) {
        //Debug.log("ProjectAdapter.onBindViewHolder();");
        final Project project = projectList.get(position);
        System.out.println(project.toString());
        holder.textView_description.setText(project.getHeading());
        holder.textView_id.setText(String.valueOf(project.getId()));
        holder.textView_updated.setText(Converter.formatUI(project.getUpdated()));
    }
    @Override
    public int getItemCount() {
        if (projectList == null){
            return 0;
        }
        return projectList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private android.widget.TextView textView_description;
        //private CheckBox checkBox_done;
        private android.widget.TextView textView_id;
        private TextView textView_updated;
        private ConstraintLayout parentLayout;

        public MyViewHolder(@androidx.annotation.NonNull android.view.View itemView) {
            super(itemView);
            //checkBox_done.setOnClickListener();
            textView_description = itemView.findViewById(R.id.textView_sessionAdapter_heading);
            textView_id = itemView.findViewById(R.id.textView_music_project_id);
            textView_updated = itemView.findViewById(R.id.textView_updated);
            parentLayout = itemView.findViewById(R.id.constrainLayout_music_project_adapter);
            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null){
                        callback.onItemClick(projectList.get(getAdapterPosition()));
                    }
                }
            });
        }

    }
    public void setList(List<Project> projectList){
        log("ProjectAdapter.setList(List<Project>)");
        this.projectList = projectList;
        notifyDataSetChanged();
    }
}
