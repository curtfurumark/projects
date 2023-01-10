package se.curtrunebylund.projects.art;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.R;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    private List<Task> tasks;
    private Context context;
    public interface Callback{
        public void onTaskCheckBoxClick(Task task, boolean checked);
    }
    private Callback callback;

    public TaskAdapter(List<Task> tasks, Context context, Callback callback) {
        Debug.log("TaskAdapter ctor: number of tasks: " + tasks.size());
        this.tasks = tasks;
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Debug.log("TaskAdapter.onBindViewHolder");
        Task task= tasks.get(position);
        Debug.log(task.toString());
        holder.textView_heading.setText(task.getDescription());
        holder.checkBox_state.setChecked(task.getState().equals(Task.State.DONE));
        holder.textView_updated.setText(task.getUpdatedAsLocalDateTime().toString());

    }



    @Override
    public int getItemCount() {
        return tasks.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private CheckBox checkBox_state;
        private TextView textView_heading;
        private TextView textView_updated;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox_state = itemView.findViewById(R.id.checkBox_taskAdapter_state);
            textView_heading = itemView.findViewById(R.id.textView_taskAdapter_heading);
            textView_updated = itemView.findViewById(R.id.textView_taskAdapter_updated);
            checkBox_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Task task = tasks.get(getAdapterPosition());
                    if(null != callback){
                        callback.onTaskCheckBoxClick(task, checkBox_state.isChecked());
                    }
                    if (checkBox_state.isChecked()){
                        task.setState(Task.State.DONE);
                    }else{
                        task.setState(Task.State.TODO);
                    }
                    //tasks.get(getAdapterPosition()).toggleState();
                }
            });
        }
    }
}
