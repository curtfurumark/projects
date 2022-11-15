package se.curtrunebylund.projects.music;

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

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.help.Converter;


public class AttemptsAdapter extends RecyclerView.Adapter<AttemptsAdapter.MyViewHolder>{
    private java.util.List<Attempt> attempts;
    private Context context;

    public void setFilteredList(List<Attempt> attempts) {
        Debug.log("SessionAdapter.setFilteredList(List<Attempts>) size; " + attempts.size());
        this.attempts = attempts;
        notifyDataSetChanged();
    }

    public interface Callback{
        public void onItemClick(Attempt attempt);
    }
    private final Callback callback;

    public AttemptsAdapter(java.util.List<Attempt> attempts, Context context, Callback callback) {
        System.out.println("SessionAdapter ctor, attempts.size() = " + attempts.size());
        if (attempts == null){
            System.out.println("taskList is null");
        }
        this.attempts = attempts;
        this.context = context;
        this.callback = callback;
    }

    @androidx.annotation.NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        System.out.println("onCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attempt_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull final MyViewHolder holder, int position) {
        Debug.log("onBindViewHolder();");
        final  Attempt attempt = attempts.get(position);
        Debug.log(attempt);
        holder.textView_description.setText(attempt.getHeading());
        holder.textView_grade.setText(attempt.getGrade().toString());
        holder.textView_updated.setText(Converter.formatUI(attempt.getUpdatedAsLocalDateTime()));
        holder.checkBox_done.setChecked(attempt.isDone());
    }


    @Override
    public int getItemCount() {
        if (attempts == null){
            return 0;
        }
        return attempts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_description;
        private CheckBox checkBox_done;
        private TextView textView_updated;
        private TextView textView_grade;
        private ConstraintLayout parentLayout;

        public MyViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            textView_description = itemView.findViewById(R.id.textView_sessionAdapter_heading);
            checkBox_done = itemView.findViewById(R.id.checkBox_sessionAdapter);
            textView_grade = itemView.findViewById(R.id.textView_sessionAdapter_grade);
            textView_updated = itemView.findViewById(R.id.textView_sessionAdapter_updated);
            parentLayout = itemView.findViewById(R.id.constrainLayout_session_adapter);
            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( callback != null){
                        callback.onItemClick(attempts.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

}
