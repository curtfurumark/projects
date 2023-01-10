package se.curtrunebylund.projects.activities;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import se.curtrunebylund.projects.classes.Attempt;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.R;


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
        public void onEditClick(Attempt attempt);
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
        holder.textView_heading.setText(attempt.getHeading());
        holder.textView_info.setText(attempt.getInfo());
    }


    @Override
    public int getItemCount() {
        if (attempts == null){
            return 0;
        }
        return attempts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_heading;
        private TextView textView_info;
        private ImageView imageView_edit;
        private ConstraintLayout parentLayout;

        public MyViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            textView_heading = itemView.findViewById(R.id.textView_sessionAdapter_heading);
            textView_info = itemView.findViewById(R.id.textView_sessionAdapter_info);
            imageView_edit = itemView.findViewById(R.id.imageView_itemAdapter_edit);
            parentLayout = itemView.findViewById(R.id.constrainLayout_session_adapter);
            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( callback != null){
                        callback.onItemClick(attempts.get(getAdapterPosition()));
                    }
                }
            });
            imageView_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( callback != null){
                        callback.onEditClick(attempts.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

}
