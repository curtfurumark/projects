package se.curtrunebylund.projects.art;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.activities.DebugActivity;
import se.curtrunebylund.projects.R;

public class PicturePagerActivity extends AppCompatActivity implements PicturePagerAdapter.Callback{
    private ViewPager viewPager;
    private PicturePagerAdapter picturePagerAdapter;
    private ArtWorkManager artWorkManager;
    private ArtWork artWork;
    private List<String> paths;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_pager_activity);
        setTitle("picture pager");
        Debug.log("PicturePagerActivity.onCreate()");
        artWorkManager = ArtWorkManager.getInstance(this);
        viewPager = findViewById(R.id.viewPager_picturePagerActivity);
        Intent intent = getIntent();
        if( intent.getBooleanExtra(PicHelper.INTENT_SHOW_REF_PICTURES, false)){
            Debug.log("...INTENT_SHOW_REF_PICTURES");
            artWork = (ArtWork) intent.getSerializableExtra(PicHelper.INTENT_SERIALIZABLE_ARTWORK);
            if ( artWork == null){
                Debug.log("artWork is null, not good");
                startActivity(new Intent(this, DebugActivity.class));
                Debug.log("...YOU SHOULD NEVER SEE THIS");
            }
            paths = artWork.getRefPicturePaths();
        }else{
            paths = artWorkManager.getImagePaths();
        }
        picturePagerAdapter = new PicturePagerAdapter(paths, this, this);
        viewPager.setAdapter(picturePagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.picture_pager_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.picture_this_activity:
                startActivity(new Intent(this, ArtWorkListActivity.class));
                return true;
            case R.id.rotate_minus_90:
                Debug.showMessage(this, "not implemented");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onImageClick(String path) {
        Debug.log("PicturePagerActivity.onImageClick(String ) path = " + path);
        Debug.showMessage(this, "path: " + path);
        Intent intent = new Intent(this, PictureEditActivity.class);
        intent.putExtra(PicHelper.INTENT_LOAD_BITMAP, true);
        intent.putExtra(PicHelper.INTENT_BITMAP_PATH, path);
        startActivity(intent);
    }
}