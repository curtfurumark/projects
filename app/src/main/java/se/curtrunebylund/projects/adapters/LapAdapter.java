package se.curtrunebylund.projects.adapters;

import static logger.CRBLogger.log;
import static logger.CRBLogger.logError;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import classes.projects.Lap;
import se.curtrunebylund.projects.R;

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.MyViewHolder>  {

    private List<Lap> lapList;

    public LapAdapter(List<Lap> lapList) {
        log("LapAdapter constructor");
        if( lapList == null){
            logError("LapAdapter call ctor with null list, i surrender");
            return;
        }
        this.lapList = lapList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lap_adapter, parent, false);
        return new MyViewHolder(itemView);
    }
    public void setList(List<Lap> lapList){
        if( lapList == null){
            logError("EnigmaAdapter call with null list, i surrender");
            return;
        }
        this.lapList = lapList;
        log("length of lapList", this.lapList.size());
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull LapAdapter.MyViewHolder holder, final int position) {
        log("LapAdapter.onBindViewHolder()");
        Lap lap = lapList.get(position);
        holder.textView_info.setText(lap.getInfo());
    }

    @Override
    public int getItemCount() {
        return lapList != null? lapList.size(): 0;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView_info;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_info = itemView.findViewById(R.id.lapAdapter_info);

        }
    }
}
