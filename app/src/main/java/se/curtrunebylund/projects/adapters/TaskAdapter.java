package se.curtrunebylund.projects.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import item.State;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.classes.Task;
import util.Converter;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>{
    private java.util.List<Task> taskList;
    private Context context;

    public void setList(List<Task> taskList) {
        Debug.log("TaskAdapter.setList(List<Task>) " + taskList.size());
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    public interface Callback{
        public void onItemClick(Task task);
        public void onCheckBoxChecked(Task task, boolean checked);
    }
    private Callback callback;

    public TaskAdapter(java.util.List<Task> taskList, Context context, Callback callback) {
        System.out.println("TaskAdapter(List<Task>, Context, Callback");
        if (taskList == null){
            Debug.log("...taskList is null");
        }
        this.taskList = taskList;
        this.context = context;
        this.callback = callback;
    }

    @androidx.annotation.NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        System.out.println("TaskListAdapter.onCreateViewHolder");
        android.view.View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull final MyViewHolder holder, int position) {
        Debug.log("TaskListAdapter.onBindViewHolder();");
        final Task task = taskList.get(position);
        holder.textView_updated.setText(Converter.formatUI(task.getUpdated()));
        holder.textView_description.setText(task.getHeading());
        boolean checked = false;
        if( task.getState() == null){
            Debug.log("...state of emergency...state is null");
        }else{
            Debug.log("...state is: " + task.getState().toString());
            if( task.getState().equals(State.DONE)){
                checked = true;
            }
        }
        holder.checkBox_state.setChecked(checked);
        Debug.log(task);
    }


    @Override
    public int getItemCount() {
        if (taskList == null){
            return 0;
        }
        return taskList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private android.widget.TextView textView_description;
        private CheckBox checkBox_state;
        private TextView textView_updated;
        private TextView textView_state;
        private ConstraintLayout parentLayout;

        public MyViewHolder(@androidx.annotation.NonNull android.view.View itemView) {
            super(itemView);
            textView_description = itemView.findViewById(R.id.textView_taskAdapter_heading);
            textView_updated = itemView.findViewById(R.id.textView_taskAdapter_updated);
            checkBox_state = itemView.findViewById(R.id.checkBox_taskAdapter_state);
            checkBox_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onCheckBoxChecked(taskList.get(getAdapterPosition()), checkBox_state.isChecked());
                }
            });
            parentLayout = itemView.findViewById(R.id.constraintLayout_taskAdapter);
            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( callback != null){
                        callback.onItemClick(taskList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

}
