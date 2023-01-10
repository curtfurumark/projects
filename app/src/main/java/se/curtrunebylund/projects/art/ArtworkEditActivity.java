package se.curtrunebylund.projects.art;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.activities.DebugActivity;
import se.curtrunebylund.projects.R;


public class ArtworkEditActivity extends AppCompatActivity implements TaskAdapter.Callback, TaskBottomSheet.Listener {
    private ArtWorkManager artWorkManager;
    private PicHelper picHelper;
    private Uri imageUri = null;
    private TextView textView_thumbnail_path;
    private TextView textView_image_path;
    private RecyclerView recyclerView_tasks;
    private TaskAdapter taskAdapter;
    private TextView textView_updated;
    private TextView textView_created;
    private TextView textView_source_date;
    private TextView textView_has_ref_pics;
    private EditText editText_tags;
    private EditText editText_description;
    private TextView textView_id;
    private ArtWork current_artWork;
    //private int artWork_index = -1;
    private boolean edit_mode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.artwork_edit_activity);
            Debug.log("ArtWorkEditActivity.onCreate()");
            artWorkManager = ArtWorkManager.getInstance(this);
            picHelper = PicHelper.getInstance(this);
            //imageView = findViewById(R.id.imageView_editArtworkAct);
            recyclerView_tasks = findViewById(R.id.recyclerView_pictureEdit_tasks);
            editText_description = findViewById(R.id.et_editArtworkAct_description);
            //editText_task_description = findViewById(R.id.editText_pictureEditAct_task_description);
            textView_created = findViewById(R.id.tv_editArtworkAct_created);
            textView_updated = findViewById(R.id.tv_editArtworkAct_updated);
            textView_source_date = findViewById(R.id.textView_pictureEditAct_source_date);
            textView_has_ref_pics = findViewById(R.id.textView_artWorkEdit_has_ref_pics);

            textView_thumbnail_path = findViewById(R.id.tv_editArtworkAct_image_path);
            textView_image_path = findViewById(R.id.textView_editPicAct_full_image_path);
            textView_image_path.setTextColor(Color.BLUE);
            //textView_image_path.setTextAppearance(Sty);
            textView_id = findViewById(R.id.tv_editArtworkAct_id);
            editText_tags = findViewById(R.id.et_editArtworkAct_tags);
        }catch(Exception exception){
            Debug.log("...exception caught");
            Debug.log(exception.toString());
            Intent intent = new Intent(this, DebugActivity.class);
            intent.putExtra("INTENT_EXCEPTION", exception.toString());
            startActivity(intent);
        }
        textView_source_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        textView_image_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImage(current_artWork.getImagePath());
            }
        });
        textView_thumbnail_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImage(current_artWork.getThumbNailPath());
            }
        });
        textView_has_ref_pics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Debug.showMessage(ArtworkEditActivity.this, "has ref pics!");
                Debug.log(current_artWork);
                Intent intent = new Intent(ArtworkEditActivity.this, PicturePagerActivity.class);
                intent.putExtra(PicHelper.INTENT_SHOW_REF_PICTURES, true);
                //intent.putExtra(PicHelper.INTENT_ARTWORK_INDEX, artWork_index);
                intent.putExtra(PicHelper.INTENT_ARTWORK_ID, current_artWork.getId());
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        if( intent.getBooleanExtra(PicHelper.INTENT_EDIT_ARTWORK, false)){
            setTitle("edit artwork");
            edit_mode = true;
            //artWork_index = intent.getIntExtra(ArtWorkManager.INTENT_ARTWORK_INDEX, -1);
            int artWork_id = intent.getIntExtra(PicHelper.INTENT_ARTWORK_ID, -1);
            if( artWork_id == -1){
                Debug.showMessage(this, "artWork_id == -1, not good");
                startActivity(new Intent( this, ArtWorkListActivity.class));
            }
            current_artWork = artWorkManager.getArtWorkID(artWork_id);
            Debug.log(current_artWork);
            //Debug.log("...artWork: number of children: " + artWork.getTasks().size());
            editText_description.setText(current_artWork.getDescription());
            textView_id.setText("id: " + current_artWork.getId());

            String fullImagePath = current_artWork.getImagePath().isEmpty()? "full path not available": current_artWork.getImagePath();
            textView_image_path.setText(fullImagePath);

            String thumb_nail_path = current_artWork.getThumbNailPath();
            thumb_nail_path = (thumb_nail_path == null || thumb_nail_path.isEmpty())? "thumbnail not available": thumb_nail_path;
            textView_thumbnail_path.setText(thumb_nail_path);

            textView_source_date.setText("source date: " + current_artWork.getSourceDate().toString());
            textView_updated.setText("updated: " + current_artWork.getUpdated().toString());
            textView_created.setText("created: " + current_artWork.getCreated().toString());
            String ref_pics = String.format("%s", current_artWork.hasRefPictures()? + current_artWork.getRefPicturePaths().size() + "ref pics": "no ref pics");
            textView_has_ref_pics.setText(ref_pics);
            editText_tags.setText(current_artWork.getTags());
        }else if (intent.getBooleanExtra(PicHelper.INTENT_NEW_ARTWORK_FROM_GALLERY, false)){
            Debug.log("...new artwork from gallery");
            setTitle("new artwork");
            //Uri uri = intent.getParcelableExtra(PicHelper.INTENT_IMAGE_URI);
            current_artWork = (ArtWork) intent.getSerializableExtra(PicHelper.INTENT_SERIALIZABLE_ARTWORK);
            try {
                textView_image_path.setText(current_artWork.getImagePath());
                textView_thumbnail_path.setText(current_artWork.getThumbNailPath());
                textView_created.setText(LocalDate.now().toString());
                textView_updated.setText(LocalDateTime.now().toString());
                textView_id.setText("id" + current_artWork.getId());
                textView_has_ref_pics.setText("no ref pics methinks");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        taskAdapter = new TaskAdapter(current_artWork.getTasks(), this, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_tasks.setLayoutManager(layoutManager);
        recyclerView_tasks.setItemAnimator(new DefaultItemAnimator());
        recyclerView_tasks.setAdapter(taskAdapter);
    }

    private void addTask(String description) {
        Debug.log("PictureEditActivity.addTask(String) " + description);
        Task task = new Task();
        //String description = editText_task_description.getText().toString();
        if (!description.isEmpty()) {
            task.setDescription(description);
            task.setUpdated(LocalDateTime.now());
            task.setState(Task.State.TODO);
            current_artWork.setUpdated(LocalDateTime.now());
            current_artWork.add(task);
            artWorkManager.save();
            //editText_task_description.setText("");
            taskAdapter.notifyDataSetChanged();
        }else{
            Debug.showMessage(this, "a description of the task please");
        }
    }
    private ArtWork artWorkFromUri(Uri uri) throws Exception {
        Debug.log("ArtWorkEditActivity.artWorkFromUri()");
        if ( null == uri){
            Debug.log("...uri is null");
            startActivity(new Intent(this, DebugActivity.class));
        }else{
            Debug.log("...uri path: " + uri.getPath());
        }
        ArtWork artWork = ArtWorkFactory.newArtWork(this);
        Bitmap bitmap_fullSize = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        Bitmap thumbNail = picHelper.scale(bitmap_fullSize, 300);
        //String thumbNailPath = getThumbNailPath(artWork.getId());
        //artWork.setThumbNailPath(thumbNailPath);
        picHelper.save(thumbNail, artWork.getThumbNailPath());
        return artWork;

    }

    private void deleteArtwork() {
        Debug.log("PictureEditActivity.deleteArtwork()");
        Debug.showMessage(this, "not implemented");
        boolean res = picHelper.deleteImage(current_artWork.getImagePath());
        Debug.log("...delete image  " + current_artWork.getImagePath() + ", res:  " + res);
        res = picHelper.deleteImage(current_artWork.getThumbNailPath());
        Debug.log("...delete thumbnail " + current_artWork.getThumbNailPath() + " res: " + res);
        if (current_artWork.hasRefPictures()){
            List<String> refPicsList = current_artWork.getRefPicturePaths();
            for(String path: refPicsList){
                res = picHelper.deleteImage(path);
                Debug.log("...delete ref pic " + path + " res: " + res);
            }
        }
        res = artWorkManager.delete(current_artWork);
        Debug.log("...delete artWork res: " + res);
        //artWorkManager.save();
    }

    /**
     * Listener for TaskBottomSheet
     * @param text
     */
    @Override
    public void onButtonSave(String text) {
        addTask(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.artwork_edit_activty, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.icon_save:
                save();
                return true;
            case R.id.icon_home:
                startActivity(new Intent(this, ArtWorkListActivity.class));
                return true;
            case R.id.add_reference_picture:
                 add_reference_picture();
                return true;
            case R.id.delete_artwork:
                deleteArtwork();
                return true;
            case R.id.new_picture:
                Debug.showMessage(this, "deprecated");
                return true;
            case R.id.add_task:
                new TaskBottomSheet().show(getSupportFragmentManager(), "hello");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void add_reference_picture() {
        Debug.log("ArtworkEditActivity.add_reference_picture()");
        Intent intent = new Intent(this, PictureEditActivity.class);
        intent.putExtra(PicHelper.INTENT_ADD_REFERENCE_PICTURE, true);
        //intent.putExtra(PicHelper.INTENT_ARTWORK, artWork);
        //intent.putExtra(PicHelper.INTENT_ARTWORK_INDEX, artWork_index);
        intent.putExtra(PicHelper.INTENT_ARTWORK_ID, current_artWork.getId());
        startActivity(intent);
    }

    private void save() {
        Debug.log("PictureEditActivity.save()");
        try {
            current_artWork.setDescription(editText_description.getText().toString());
            //artWork.setUpdated(LocalDateTime.now());
            current_artWork.setTags(editText_tags.getText().toString());
            if( edit_mode) {
                Debug.log("edit mode do nothing");
            }else{
                //TODO sync from camera and from gallery
                //artWork.setId(artWorkManager.getNextID());
                //String thumbNailPath = picHelper.getThumbNailPath(artWork.getId());
                //artWork.setCreated(LocalDate.now());
                //picHelper.save(bitmap_thumbNail, thumbNailPath);
                //artWork.setThumbNailPath(thumbNailPath);
                artWorkManager.add(current_artWork);
            }
            artWorkManager.save();
            Intent intent = new Intent(this, ArtWorkListActivity.class);
            startActivity(intent);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    public void showDateDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O )
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                textView_source_date.setText(String.format("%d-%d-%d", year, month + 1 , dayOfMonth));
                LocalDate source_date = LocalDate.of(year, month + 1, dayOfMonth);
                current_artWork.setSourceDate(source_date);
                artWorkManager.save();
            }
        });
        datePickerDialog.show();
    }

    @Override
    public void onTaskCheckBoxClick(Task task, boolean checked) {
        Debug.log("PictureEditActivity.onTaskBoxClick(Task, boolean checked) " + checked);
        current_artWork.setUpdated(LocalDateTime.now());
        task.setState(checked ? Task.State.DONE: Task.State.TODO);
        artWorkManager.save();
    }

    public void showImage(String bitmapPath) {
        Debug.log("ArtworkEditActivity.showImage(String bitmapPath) " + bitmapPath);
        Debug.log(current_artWork);
        Intent intent = new Intent(ArtworkEditActivity.this, PictureEditActivity.class);
        intent.putExtra(PicHelper.INTENT_LOAD_BITMAP,true);
        intent.putExtra(PicHelper.INTENT_SERIALIZABLE_ARTWORK, current_artWork);
        intent.putExtra(PicHelper.INTENT_BITMAP_PATH, bitmapPath);
        startActivity(intent);
    }


}