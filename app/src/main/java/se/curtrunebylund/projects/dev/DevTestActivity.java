package se.curtrunebylund.projects.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.art.ArtWork;
import se.curtrunebylund.projects.art.ArtWorkManager;
import se.curtrunebylund.projects.art.PicHelper;

public class DevTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_test_activity);
        Debug.log("DevTestActivity.onCreate()");
        Intent intent = getIntent();
        ArtWork artWork = (ArtWork) intent.getSerializableExtra(PicHelper.INTENT_SERIALIZABLE_ARTWORK);
        ArtWork artWork2 = ArtWorkManager.getInstance(this).getArtWorkID(artWork.getId());
    }
}