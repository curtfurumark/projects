package se.curtrunebylund.projects.art;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import se.curtrunebylund.projects.util.Debug;

/**
 * not really necessary this one
 */
public class PicHelper {
    public static final String INTENT_IMAGE_URI = "intent_image_uri";
    public static final String INTENT_LOAD_IMAGE_URI = "intent_load_image_uri";
    public static final String INTENT_IMAGE_PATH = "intent_image_path";
    public static final String INTENT_SHOW_FULL_IMAGE = "intent_show_full_image";
    public static final String INTENT_FULL_IMAGE_PATH = "intent_full_image_path";
    public static final String INTENT_LOAD_BITMAP = "intent_load_bitmap" ;
    public static final String INTENT_BITMAP_PATH = "intent_bitmap_path";
    public static final String INTENT_EDIT_ARTWORK = "intent_edit_artwork" ;
    //public static final String INTENT_ARTWORK_INDEX ="intent_artwork_index" ;
    public static final String INTENT_NEW_ARTWORK_FROM_GALLERY = "intent_new_artwork_from_gallery";
    public static final String INTENT_SERIALIZABLE_ARTWORK = "intent_artwork";
    public static final String INTENT_ADD_REFERENCE_PICTURE = "intent_add_reference_picture";
    public static final String INTENT_SHOW_REF_PICTURES = "intent_show_ref_pictures";
    public static final String INTENT_ARTWORK_ID = "intent_artwork_id";
    private Context context;
    private static PicHelper instance;
    private PicHelper(Context context){
        this.context = context;
    }
    public static PicHelper getInstance(Context context){
        if(instance == null){
            instance = new PicHelper(context);
        }
        return instance;
    }
    public Bitmap bitmapFromUri(Uri uri) throws Exception {
        Debug.log("PictureTestActivity.bitmapFromUri()");
        final InputStream imageStream = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        return bitmap;
    }
    public void copyFile(String source, String destination) throws Exception {
        Debug.log("PicHelper.copyFile(String,String)  src: " + source + " dest" + destination);
        Bitmap bitmap = BitmapFactory.decodeFile(source);
        save(bitmap, destination);
    }
    public File createImageFile(String file_name, String suffix) throws IOException {
        Debug.log("PicHelper.createImageFile() file_name: " + file_name + " suffix: " +suffix);
        File directory = context.getFilesDir();//.toString() + String.format("/%s", getImageFileName(id));
        File tmp_file = File.createTempFile(file_name, suffix, directory);
        Debug.log("...tmp_file absolute path: " + tmp_file.getAbsolutePath());
        /*File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File temp_file = File.createTempFile(file_name, suffix, directory);
        Debug.log("...temp_file path " + temp_file.getAbsolutePath());
        return temp_file;

         */
        return tmp_file;
    }
    public File createImageFile(String full_name) throws IOException {
        Debug.log("PicHelper.createImageFile(String) full_name: " + full_name);
        //File directory = context.getFilesDir();//.toString() + String.format("/%s", getImageFileName(id));
        File file = new File(full_name);
        file.createNewFile();
        //File tmp_file = File.createTempFile(file_name, suffix, directory);
        Debug.log("...file: " + file.getAbsolutePath());
        return file;
    }

    public File createImageFile(Context context, String suffix) throws IOException {
        Debug.log("PicHelper.createImageFile(Context) suffix: " + suffix);
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "CRB_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                suffix,         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    /**
     * in the future, the not so distant future? each artwork item will have multiple images associated
     * @param imagePath
     */
    public boolean deleteImage(String imagePath) {
        Debug.log("PicHelper.deleteImage(String path) " + imagePath);
        File file = new File(imagePath);
        return file.delete();
    }

    private Bitmap getBitmap(String file_name) throws IOException {
        Debug.log("PicHelper.getBitmap(");
        //FileInputStream fileInputStream = new FileInputStream(file_name);
        Bitmap bitmap = BitmapFactory.decodeFile(file_name);
        if ( bitmap == null){
            Debug.log("...bitmap is null");
        }
        Debug.log("...end of getBitmap");
        return bitmap;
    }
    private Bitmap getBitmap(Uri uri) {
        Bitmap bitmap = null;
        InputStream imageStream = null;
        try {
            imageStream = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(imageStream);
            imageStream.close();
        }catch (Exception e){
            Debug.log("getBitMap exception " + e.toString());
        }
        return bitmap;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) throws IllegalArgumentException {
        Debug.log("PicHelper.getRealPathFromURI()");
        Debug.log(contentUri);
        String path =null;
        Cursor cursor = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
        int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        if ( index == -1){
            Debug.log("...index = -1");
            return null;
        }
        cursor.moveToFirst();
        path = cursor.getString(index);
        Debug.log("...path path path: " + path);
        /*
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(proj[0]);
        Debug.log("...column_index: " + column_index);
        path =  cursor.getString(column_index);
        Debug.log("...path? : " + path);
        if (cursor != null) {
            cursor.close();
        }
        */
        return path;
    }

    public Bitmap rotateBitmap(Bitmap bitmap, float degrees) throws Exception {
        Debug.log("PictureEditActivity.rotateBitMap()");
        if (null == bitmap){
            throw new Exception("PictureEditActivity.rotateBitMap, bitmap is null");
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);

        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        return rotated;
    }

    /**
     *
     * @param bitmap
     * @param id
     * @return the path to the save bitmap
     * @throws IOException
     */
    public String save(Bitmap bitmap, Integer id, boolean hack) throws Exception {
        Debug.log("PictureThisActivity.getImagePath(Uri)");
        if( bitmap == null){
            throw new Exception("Bitmap is null (PictureEditActivity.save()");
        }
        //final InputStream imageStream = getContentResolver().openInputStream(uri);
        //Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        //bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
        String file_name = context.getFilesDir().toString() + String.format("/pic_%s.png", id.toString());
        FileOutputStream fileOutputStream = new FileOutputStream(file_name);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        return file_name;
    }
    /**
     *
     * @param bitmap
     * @param
     * @return the path to the save and scaled image
     * @throws IOException
     */
    public String save(Bitmap bitmap, String file_name) throws Exception {
        Debug.log("PictureThisActivity.save(Bitmap, String file_name) " + file_name);
        if( bitmap == null){
            throw new Exception("Bitmap is null (PictureEditActivity.save()");
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file_name);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        return file_name;
    }

    public Bitmap scale (Uri uri, int height){
        Debug.log("PicHelper.scale(Uri, int height)");
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
        return scale(bitmap, height);
    }
    public Bitmap scale(Bitmap bitmap, int height){
        Debug.log("PicHelper.scale(Bitmap, int height) height = " + height);
        if ( null == bitmap){
            Debug.log("...bitmap is null");
            return null;
        }
        float currentHeight = bitmap.getHeight();
        float currentWidth = bitmap.getWidth();
        Debug.log(String.format("currentW %f, currentH %f ", currentWidth, currentHeight));
        float scale = height / currentHeight;
        Debug.log("...scale is " + scale);
        int newWidth = (int) (currentWidth * scale);
        Debug.log("...new width: " + newWidth);
        return Bitmap.createScaledBitmap(bitmap , newWidth, height, false);
    }









    public Bitmap bitmapFromFilePath(String path) {
        Debug.log("PicHelper.bitmapFromFilePath(String) " + path);
        Bitmap bitmap =  BitmapFactory.decodeFile(path);
        return bitmap;
    }
}
