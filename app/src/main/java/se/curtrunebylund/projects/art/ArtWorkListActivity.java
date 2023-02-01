package se.curtrunebylund.projects.art;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.activities.HomeActivity;
import se.curtrunebylund.projects.projects.DevStuff;


public class ArtWorkListActivity extends AppCompatActivity {
    private EditText editText_heading;
    private RecyclerView recyclerView;
    private ArtworkAdapter artworkAdapter;
    private ArtWorkManager artWorkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artwork_list_activity);
        setTitle("picture this");
        Debug.log("PictureThisActivity.onCreate()");
        artWorkManager = ArtWorkManager.getInstance(this);
        //artWorkManager.getArtWork(1).setThumbNailPath("/data/user/0/se.curtrunebylund.projects/files/pic_2.png");
        //artWorkManager.save();
        recyclerView = findViewById(R.id.recyclerView_pictureThis_art_works);
        editText_heading = findViewById(R.id.editText_pictureAct_heading);
        //editText_heading.setText("number of works in list: " + artWorkManager.getCount());
        artworkAdapter = new ArtworkAdapter(artWorkManager.getWorks(), this, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(artworkAdapter);

        editText_heading.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterArtWorks(editable.toString());
            }
        });

        Intent intent= getIntent();
        if( intent.getBooleanExtra(ArtWorkManager.INTENT_NEW_ARTWORK, false)){
            addArtWork(intent);
        }
        DevStuff.testCreateFileForCamera(this);
      //  Glide.with(this).load(url2).into(imageView);
    }

    private void filterArtWorks(String string) {
        Debug.log("PictureThis.filterArtWorks() ");
        List<ArtWork> filteredList = new ArrayList<>();
        for( ArtWork artWork: artWorkManager.getWorks()){
            if ( artWork.contains(string)){
                    filteredList.add(artWork);
            }
        }
        artworkAdapter.setFilteredList(filteredList);
    }

    private void addArtWork(Intent intent) {
        Debug.log("PictureThisActivity.addArtWork(Intent)");
        String description = intent.getStringExtra(ArtWorkManager.INTENT_ARTWORK_DESCRIPTON);
        Uri uri = intent.getParcelableExtra(ArtWorkManager.INTENT_URI);
        ArtWork artWork = new ArtWork();
        artWork.setDescription(description);
        Integer id = artWorkManager.getNextID();
        String imagePath = null;
        try {
            imagePath = getImagePath(uri, id);
            artWork.setId(id);
            artWork.setCreated(LocalDate.now());
            artWork.setUpdated(LocalDateTime.now());
            artWork.setImagePath(imagePath);
            artWorkManager.add(artWork);
            artWorkManager.save();

        } catch (IOException e) {
            e.printStackTrace();
        }
     }

    private String getImagePath(Uri uri, Integer id) throws IOException {
        Debug.log("PictureThisActivity.getImagePath(Uri)");
        final InputStream imageStream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
        String file_name = getFilesDir().toString() + String.format("/pic_%s.png", id.toString());
        FileOutputStream fileOutputStream = new FileOutputStream(file_name);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        return file_name;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.artwork_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Debug.log("PictureThisActivity.onOptionsItemSelected");
        switch(item.getItemId()){
            case R.id.icon_home:
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            case R.id.new_artwork:
                Intent intent = new Intent(this, ArtworkEditActivity.class);
                intent.putExtra(ArtWorkManager.INTENT_NEW_ARTWORK, true);
                startActivity(intent);
                return true;
            case R.id.picture_pager_activity:
                startActivity(new Intent(this, PicturePagerActivity.class));
                return true;
            case R.id.picture_edit_activity:
                startActivity(new Intent(this, PictureEditActivity.class));
                return true;
            case R.id.sort_descending_update:
                artWorkManager.sort(ArtWorkManager.SortOrder.DESCENDING_UPDATE);
                artworkAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_title:
                artWorkManager.sort(ArtWorkManager.SortOrder.ASCENDING_TITLE);
                artworkAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_ascending_update:
                artWorkManager.sort(ArtWorkManager.SortOrder.ASCENDING_UPDATE);
                artworkAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Debug.log("PictureThisActivity.onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data){
            Debug.log("...result ok ");
            Uri pictureUri = data.getData();
            Intent intent = new Intent(this, PictureEditActivity.class);
            intent.putExtra(PicHelper.INTENT_LOAD_IMAGE_URI, true);
            intent.putExtra(PicHelper.INTENT_IMAGE_URI, pictureUri);
            startActivity(intent);
        }else{
            Debug.log("result code not ok or data null" );
        }
    }
}