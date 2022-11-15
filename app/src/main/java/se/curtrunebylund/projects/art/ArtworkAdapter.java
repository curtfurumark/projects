package se.curtrunebylund.projects.art;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.help.Converter;


public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkAdapter.MyViewHolder>{
    private List<ArtWork> artWorkList;
    private Context context;
    private Callback callback;

    public void setFilteredList(List<ArtWork> filteredList) {
        artWorkList = filteredList;
        notifyDataSetChanged();
    }

    public interface Callback{
        public void onItemClick(int index, ArtWork artWork);
    }

    public ArtworkAdapter(List<ArtWork> works, Context context, Activity activity ) {
        this.artWorkList = works;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_this_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ArtWork artWork = artWorkList.get(position);
        Debug.log("onBindViewHolder: " + artWork.toString());
        if( holder.editText_description != null) {
            holder.editText_description.setText(artWork.getDescription());
            holder.textView_id.setText(artWork.getId().toString() );
            holder.textView_updated.setText(Converter.format(artWork.getUpdated()));
            holder.image.setImageBitmap(BitmapFactory.decodeFile(artWork.getThumbNailPath()));
        }else{
            Debug.log("edit text is null");
        }
    }

    @Override
    public int getItemCount() {
        return artWorkList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView editText_description;
        private TextView textView_id;
        private TextView textView_updated;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //Debug.log("MyViewHolder ctor");
            image = itemView.findViewById(R.id.image_pictureAdapter);
            editText_description = itemView.findViewById(R.id.editText_pictureAdapter_description);
            textView_id = itemView.findViewById(R.id.textView_pictureAdapter_id);
            textView_updated = itemView.findViewById(R.id.textView_pictureAdapter_updated);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ArtworkEditActivity.class);
                    intent.putExtra(PicHelper.INTENT_EDIT_ARTWORK, true);
                    ArtWork artWork = artWorkList.get(getAdapterPosition());
                    intent.putExtra(ArtWorkManager.INTENT_ARTWORK_INDEX, getAdapterPosition());
                    intent.putExtra(PicHelper.INTENT_ARTWORK_ID, artWork.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
