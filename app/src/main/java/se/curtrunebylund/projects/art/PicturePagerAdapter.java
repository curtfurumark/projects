package se.curtrunebylund.projects.art;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;
import java.util.Objects;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.R;

public class PicturePagerAdapter extends PagerAdapter {
    private List<String> image_paths;
    private Context context;
    private LayoutInflater layoutInflater;
    private int current_index = -1;
    private ImageView imageView;
    private Bitmap bitmap;
    public interface Callback{
        public void onImageClick(String path);
    }
    private Callback callback;

    public PicturePagerAdapter(List<String> image_paths, Context context, Callback callback) {
        this.image_paths = image_paths;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callback = callback;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        current_index = position;
        View itemView = layoutInflater.inflate(R.layout.picture_pager_item, container, false);
        imageView = itemView.findViewById(R.id.imageView_picturePagerAdapter);
        bitmap = BitmapFactory.decodeFile(image_paths.get(position));
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( null != callback){
                    callback.onImageClick(image_paths.get(position));
                }
            }
        });
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public int getCount() {
        return image_paths.size();
    }
    public String getCurrentImagePath(){
        return image_paths.get(current_index);
    }
    public Drawable getDrawable(){
        return imageView.getDrawable();
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout)object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

    public void rotateImage() {
        Debug.log("PicturePagerAdapter()");
        String path = image_paths.get(current_index);
        PicHelper picHelper = PicHelper.getInstance(context);
        try {
            bitmap = picHelper.rotateBitmap(bitmap, 90);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
    }
}
