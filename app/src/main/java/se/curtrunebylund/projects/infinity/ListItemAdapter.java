package se.curtrunebylund.projects.infinity;

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

import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.R;
import util.Converter;


public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.MyViewHolder>{
    private List<ListItem> items;
    private Context context;

    public void setFilteredList(List<ListItem> items) {
        Debug.log("ListItemAdapter.setFilteredList(List<Attempts>) size; " + items.size());
        this.items = items;
        notifyDataSetChanged();
    }

    public interface Callback{
        public void onItemClick(ListItem item);
    }
    private final Callback callback;

    public ListItemAdapter(List<ListItem> items, Context context, Callback callback) {
        System.out.println("ListItemAdapter() ctor, items.size() = " + items.size());
        if (items == null){
            System.out.println("taskList is null");
        }
        this.items = items;
        this.context = context;
        this.callback = callback;
    }

    @androidx.annotation.NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        System.out.println("ListItemAdapter.onCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull final MyViewHolder holder, int position) {
        //Debug.log("onBindViewHolder();");
        final  ListItem item = items.get(position);
        //Debug.log(item);
        String heading = String.format("%s (id:%d, pid: %d)", item.getHeading(), item.getID(), item.getParentID());
        holder.textView_heading.setText(heading);
        holder.textView_grade.setText(item.getState().toString());
        holder.textView_updated.setText(Converter.formatUI(item.getUpdated()));
        //holder.checkBox_done.setChecked(item.isDone());
    }


    @Override
    public int getItemCount() {
        if (items == null){
            return 0;
        }
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_heading;
        private CheckBox checkBox_done;
        private TextView textView_updated;
        private TextView textView_grade;
        private ConstraintLayout parentLayout;

        public MyViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            textView_heading = itemView.findViewById(R.id.textView_listItemAdapter_heading);
            checkBox_done = itemView.findViewById(R.id.checkBox_listItemAdapter);
            textView_grade = itemView.findViewById(R.id.textView_listItemAdapter_grade);
            textView_updated = itemView.findViewById(R.id.textView_listItemAdapter_updated);
            parentLayout = itemView.findViewById(R.id.constrainLayout_listItem_adapter);
            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( callback != null){
                        callback.onItemClick(items.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

}
