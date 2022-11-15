package se.curtrunebylund.projects.art;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Locale;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.activities.DebugActivity;
import se.curtrunebylund.projects.R;


public class PictureEditActivity extends AppCompatActivity {
    public static final String INTENT_LOAD_URI = "intent_load_uri";
    public static final String INTENT_LOAD_BITMAP = "load_bitmap";
    private enum Action{
        NO_ACTION, LOAD_BITMAP, EDIT_ARTWORK, ADD_REF_PICTURE, NEW_ARTWORK, SHARE_PICTURE
    }
    private Action action = Action.NO_ACTION;
   // private static final String FILE_NAME ="/file_name.png";
    public static final String INTENT_ARTWORK_DESCRIPTION ="intent_artwork_description" ;
    private static final int REQUEST_IMAGE_FROM_GALLERY = 3;
    private static final int REQUEST_IMAGE_FROM_CAMERA = 1;
    private static boolean CAPTURE_FROM_CAMERA = false;
    private boolean UPDATE_IMAGE_USE_CAMERA = false;
    private boolean UPDATE_IMAGE_USE_GALLERY = false;
    //private boolean EDIT_EXISTING_ARTWORK = false;
    private ImageView imageView;
    private TextView textView_heading;
    private TextView editText_picture_data;
    private TextView textView_picture_path;
    private Bitmap bitmap;
    //private Bitmap bitmap_fullSize;
    private ArtWorkManager artWorkManager;
    private ArtWork current_artwork;
    private Uri imageUri;
    private String bitmapPath;
    //private String ;
    private PicHelper picHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_edit_activity);
        setTitle("picture edit");
        Debug.log("PictureEditActivity.onCreate()");
        picHelper = PicHelper.getInstance(this);
        artWorkManager = ArtWorkManager.getInstance(this);

        imageView = findViewById(R.id.imageView_make_it_funky);
        textView_heading = findViewById(R.id.textView_pictureEditActivity_heading);
        editText_picture_data = findViewById(R.id.textView_pictureEdit_picture_data);
        textView_picture_path = findViewById(R.id.textView_pictureEdit_picture_path);
        textView_heading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( current_artwork != null){
                    Intent intent = new Intent(PictureEditActivity.this, ArtworkEditActivity.class);
                    intent.putExtra(PicHelper.INTENT_ARTWORK_ID, current_artwork.getId());
                    intent.putExtra(PicHelper.INTENT_EDIT_ARTWORK, true);
                    startActivity(intent);
                }
            }
        });


        Intent intent = getIntent();
        if( intent.getBooleanExtra(PicHelper.INTENT_LOAD_BITMAP, false)) {
            setTitle("load bitmap");
            action = Action.EDIT_ARTWORK;
            Debug.log("...INTENT_LOAD_BITMAP");
            try {
                bitmapPath = intent.getStringExtra(PicHelper.INTENT_BITMAP_PATH);
                textView_picture_path.setText(bitmapPath);
                current_artwork = (ArtWork) intent.getSerializableExtra(PicHelper.INTENT_SERIALIZABLE_ARTWORK);
                if( current_artwork == null){
                    Debug.log("...artwork is null");
                    startActivity(new Intent(this, DebugActivity.class));

                }
                //a hack of sorts,  dont want no copies, just the original artwork
                current_artwork  = artWorkManager.getArtWorkID(current_artwork.getId());
                String heading = String.format(Locale.getDefault(),"%s,id=%d", current_artwork.getDescription(), current_artwork.getId());
                textView_heading.setText(heading);
                if (bitmapPath == null || bitmapPath.isEmpty()) {
                    Debug.showMessage(this, "...missing path (load bitmap mode)");
                    startActivity(new Intent(this, DebugActivity.class));
                }
                bitmap = picHelper.bitmapFromFilePath(bitmapPath);
                String pictureData = String.format(Locale.getDefault(),"height: %d, width %d", bitmap.getHeight(), bitmap.getWidth());
                editText_picture_data.setText(pictureData);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Debug.log("an exception occured " + e);
                Debug.showMessage(this, e.toString());
            }
        }else if( intent.getBooleanExtra(PicHelper.INTENT_ADD_REFERENCE_PICTURE, false)){
            action = Action.ADD_REF_PICTURE;
            Debug.log("...INTENT_ADD_REFERENCE_PICTURE");
            try {
                setTitle("add ref pic");
                current_artwork= (ArtWork) intent.getSerializableExtra(PicHelper.INTENT_SERIALIZABLE_ARTWORK);
                if ( current_artwork == null){
                    Debug.log("...artwork_index is null");
                    startActivity(new Intent(this, DebugActivity.class));
                }
                textView_heading.setText(current_artwork.getDescription());

            }catch (Exception e){
                Debug.log("...exception: " + e.toString());
            }

        }else if (Intent.ACTION_SEND.equals(intent.getAction()) && null != intent.getType()){
            setTitle("share image");
            if( intent.getType().startsWith("image")){
                action = Action.SHARE_PICTURE;
                handleShareImage(intent);
            }else{
                Debug.showMessage(this, "no handler for " + intent.getType());
            }
        }else {
            action = Action.NEW_ARTWORK;
           Debug.log("...new picture");
           textView_heading.setText("choose picture from camera or gallery");
           editText_picture_data.setText("");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.picture_edit_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void createThumbnail() {
        Debug.log("PictureEditActivity.createThumbnail()");
        if( null != bitmap && null != current_artwork){
            try {
                Bitmap thumbnail = picHelper.scale(bitmap, 300);
                picHelper.save(thumbnail, current_artwork.getThumbNailPath());
            } catch (Exception e) {
                e.printStackTrace();
                Debug.showMessage(this, e.toString());
                Intent intent = new Intent(this, DebugActivity.class);
                intent.putExtra(DebugActivity.INTENT_ERR_MESSAGE, e.toString());
                startActivity(intent);
            }
        }
    }

    private void deletePicture() {
        Debug.log("PictureEditActivity.deletePicture()");
        if(current_artwork != null){
            boolean pic_is_thumbnail = bitmapPath.contains("thumbnail");
            boolean deleted = picHelper.deleteImage(bitmapPath);
            String message = deleted ? "file deleted": "error deleting file";
            Debug.showMessage(this, message);
            if( deleted) {
                if (pic_is_thumbnail) {
                    current_artwork.setThumbNailPath("");
                } else {
                    current_artwork.setImagePath("");
                }
                current_artwork.setUpdated(LocalDateTime.now());
                artWorkManager.save();
                imageView.setImageBitmap(null);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ( item.getItemId()){
            case R.id.icon_home:
                startActivity(new Intent(this, ArtWorkListActivity.class));
                return true;
            case R.id.show_black_and_white:
                setImageViewBW(imageView);
                return true;
            case R.id.create_new_artwork:
                saveNewArtWork();
                return true;
            case R.id.picture_from_camera:
                captureImageFromCamera();
                return true;
            case R.id.get_pic_from_gallery:
                getPictureFromGallery();
                return true;
            case R.id.move_pic_to_archive:
                try {
                    movePictureToRefPictures();
                } catch (Exception e) {
                    Debug.showMessage(this, e.toString());
                }
                return true;
            case R.id.rotate_minus_90:
                try {
                    bitmap = picHelper.rotateBitmap(bitmap, -90);
                    imageView.setImageBitmap(bitmap);
                    picHelper.save(bitmap, bitmapPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.rotate_plus_90:
                try {
                    bitmap = picHelper.rotateBitmap(bitmap, 90);
                    imageView.setImageBitmap(bitmap);
                    picHelper.save(bitmap, bitmapPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.delete_picture:
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("confirm")
                        .setMessage("delete: " + bitmapPath + "?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePicture();
                            }
                        })
                        .setNegativeButton("no", null)
                        .show();
                return true;
            case R.id.create_thumbnail:
                createThumbnail(); //for current bitmap
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void movePictureToRefPictures() throws Exception {
        Debug.log("PictureEditActivity.movePictureToRefPictures()");
        //copy file to new location
        //delete old file
        String source = bitmapPath;
        String destination = current_artwork.getRefPictureFilePath(this);
        picHelper.copyFile(source, destination);
        current_artwork.addRefPicturePath(destination);
        artWorkManager.save();
    }



    private void captureImageFromCamera() {
        Debug.log("PictureEditActivity.captureImageFromCamera()");
        CAPTURE_FROM_CAMERA = true;
        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            if (action.equals(Action.NEW_ARTWORK)) {
                Debug.log("...action: " + action);
                current_artwork = ArtWorkFactory.newArtWork(this);
            }
            if( action.equals(Action.EDIT_ARTWORK)){

            }
            File cameraFile = picHelper.createImageFile(this, ".jpg");
            //
            Debug.log("...cameraFile path" + cameraFile.getAbsolutePath());
            current_artwork.setImagePath(cameraFile.getAbsolutePath());
            imageUri = FileProvider.getUriForFile(this, "se.curtrunebylund.projects", cameraFile);
            Debug.log(imageUri);
            bitmapPath = cameraFile.getAbsolutePath();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_FROM_CAMERA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * dispatch to gallery
     * reply: onActivityResult
     */
    private void getPictureFromGallery() {
        Debug.log("PictureThisActivity.getPictureFromGallery()");
        if(action.equals(Action.NEW_ARTWORK)) {
            current_artwork = ArtWorkFactory.newArtWork(this);
        }
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_FROM_GALLERY);
    }
    private void handleCameraRequest() {
        Debug.log("PictureEditActivity.handleCameraRequest()");
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            if (null == bitmap){
                Debug.log("...bitmap is null");
                startActivity(new Intent(this, DebugActivity.class));
            }
            Bitmap thumb_nail = picHelper.scale(bitmap, 300);
            String bitmap_info = String.format("height %s, width %s", bitmap.getHeight(), bitmap.getWidth());
            picHelper.save(thumb_nail, current_artwork.getThumbNailPath());
            if ( imageUri != null) {
                imageView.setImageURI(imageUri);
            }else{
                Debug.log("...imageUri is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Intent intent = new Intent(this, DebugActivity.class);
            intent.putExtra(DebugActivity.INTENT_ERR_MESSAGE, e.toString());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void handleGalleryRequest() {
        Debug.log("PictureEditActivity.handleGalleryRequest()");
        String imagePath = "";
        if (imageUri == null){
            Debug.showMessage(this, "no picture chosen!?");
            return;
        }
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            bitmap =BitmapFactory.decodeStream(imageStream);
            switch(action){
                case EDIT_ARTWORK:
                case NEW_ARTWORK:
                case SHARE_PICTURE:
                    Bitmap thumbnail = picHelper.scale(bitmap, 300);
                    picHelper.save(thumbnail, current_artwork.getThumbNailPath());
                    if( current_artwork.getImagePath().isEmpty()){
                        current_artwork.setImagePath(ArtWorkFactory.getImagePath(current_artwork.getId(),this));
                    }
                    picHelper.save(bitmap, current_artwork.getImagePath());
                    break;
                case ADD_REF_PICTURE:
                    imagePath = current_artwork.getRefPictureFilePath(this);
                    current_artwork.addRefPicturePath(imagePath);
                    picHelper.save(bitmap, imagePath);
                    break;
            }
            current_artwork.setUpdated(LocalDateTime.now());
            //artWorkManager.update(current_artwork);
            artWorkManager.save();
        } catch (Exception e) {
            Debug.showMessage(this, "exception: " + e);
            e.printStackTrace();
        }
    }

    private void handleShareImage(Intent intent) {
        Debug.log("PictureEditActivity.handleShareImage()");
        //Debug.showMessage(this, "got an image");
        //ArtWorkManager.uri =
        imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        //String path = uri.getPath();
        textView_picture_path.setText(imageUri.getPath());
        imageView.setImageURI(imageUri);
        current_artwork = ArtWorkFactory.newArtWork(this);
        handleGalleryRequest();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Debug.log("PictureEditActivity.onActivityResult() requestCode " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode != RESULT_OK){
            Debug.log("...RESULT NOT OK");
            Debug.showMessage(this, "result not ok, no picture chosen mate?");
            return;
        }
        if( requestCode== REQUEST_IMAGE_FROM_CAMERA){
            Debug.log("...REQUEST_IMAGE_FROM_CAMERA");
            bitmap = BitmapFactory.decodeFile(imageUri.getPath());
            imageView.setImageBitmap(bitmap);
            handleCameraRequest();
        }else if (requestCode == REQUEST_IMAGE_FROM_GALLERY) {
            Debug.log("...REQUEST_IMAGE_FROM_GALLERY");
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            handleGalleryRequest();
        }
    }

    /**
     * if not edit
     */
    private void saveNewArtWork() {
        Debug.log("PictureEditActivity.saveNewArtWork()");
        Intent intent = new Intent(this, ArtworkEditActivity.class);
        intent.putExtra(PicHelper.INTENT_NEW_ARTWORK_FROM_GALLERY, true);
        intent.putExtra(PicHelper.INTENT_SERIALIZABLE_ARTWORK, current_artwork);
        startActivity(intent);
    }

    private void setImageViewBW(ImageView iv){
        Debug.log("PictureEditActivity.setImageViewBW()");

        float brightness = 10; // change values to suite your need

        float[] colorMatrix = {
                0.33f, 0.33f, 0.33f, 0, brightness,
                0.33f, 0.33f, 0.33f, 0, brightness,
                0.33f, 0.33f, 0.33f, 0, brightness,
                0, 0, 0, 1, 0
        };

        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        iv.setColorFilter(colorFilter);
    }
}