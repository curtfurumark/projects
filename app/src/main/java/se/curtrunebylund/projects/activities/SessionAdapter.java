package se.curtrunebylund.projects.activities;

import static logger.CRBLogger.log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.classes.Session;
import se.curtrunebylund.projects.util.Debug;


public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.MyViewHolder>{
    private java.util.List<Session> sessions;
    private static final boolean VERBOSE = false;

    public void setFilteredList(List<Session> sessions) {
        Debug.log("SessionAdapter.setFilteredList(List<Attempts>) size; " + sessions.size());
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    public interface Callback{
        void onItemClick(Session session);
        void onEditClick(Session session);
    }
    private final Callback callback;

    public SessionAdapter(java.util.List<Session> sessions, Callback callback) {
        if( VERBOSE) log("SessionAdapter ctor, attempts.size() = " , sessions.size());
        this.sessions = sessions;
        this.callback = callback;
    }

    @androidx.annotation.NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        if( VERBOSE) log("SessionAdapter.onCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attempt_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull final MyViewHolder holder, int position) {
        if( VERBOSE) log("SessionAdapter.onBindViewHolder();");
        final Session session = sessions.get(position);
        Debug.log(session);
        holder.textView_heading.setText(session.getHeading());
        holder.textView_info.setText(session.getInfo());
    }


    @Override
    public int getItemCount() {
        if (sessions == null){
            return 0;
        }
        return sessions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView_heading;
        private final TextView textView_info;

        public MyViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            textView_heading = itemView.findViewById(R.id.textView_sessionAdapter_heading);
            textView_info = itemView.findViewById(R.id.textView_sessionAdapter_info);
            ImageView imageView_edit = itemView.findViewById(R.id.imageView_itemAdapter_edit);
            ConstraintLayout parentLayout = itemView.findViewById(R.id.constrainLayout_session_adapter);
            parentLayout.setOnClickListener(view -> {
                if( callback != null){
                    callback.onItemClick(sessions.get(getAdapterPosition()));
                }
            });
            imageView_edit.setOnClickListener(view -> {
                if( callback != null){
                    callback.onEditClick(sessions.get(getAdapterPosition()));
                }
            });
        }
    }

}
