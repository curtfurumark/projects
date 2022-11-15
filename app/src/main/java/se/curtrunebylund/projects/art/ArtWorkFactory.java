package se.curtrunebylund.projects.art;

import android.content.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;

import se.curtrunebylund.projects.Debug;

public class ArtWorkFactory {
    /**
     * image path for fullsize bitmap
     * @param id
     * @return
     */
    public static String getImagePath(Integer id, Context context){
        return  context.getFilesDir().toString() + String.format("/%s", getImageFileName(id));
    }

    public static String getImageFileName(int id){
        return String.format("fullImage_%d.png", id);
    }

    public static String  getThumbNailPath(Integer id, Context context) {
        Debug.log("PicHelper.getThumbNailPath()");
        return  context.getFilesDir().toString() + String.format("/%s", getThumbNailFileName(id));
    }
    public static ArtWork  newArtWork(Context context){
        Debug.log("ArtWorkFactory.newArtWork(Context)");
        PicHelper picHelper= PicHelper.getInstance(context);
        ArtWork artWork = new ArtWork();
        artWork.setId(ArtWorkManager.getInstance(context).getNextID());
        artWork.setUpdated(LocalDateTime.now());
        artWork.setThumbNailPath(getThumbNailPath(artWork.getId(), context));
        artWork.setImageDirectory(getImagePath(artWork.getId(), context));
        artWork.setThumbNailFileName(getThumbNailFileName(artWork.getId()));;
        artWork.setImageFileName(getImageFileName(artWork.getId()));
        artWork.setCreated(LocalDate.now());
        artWork.setImagePath(getImagePath(artWork.getId(), context));
        return artWork;
    }

    private static String getThumbNailFileName(int id) {
        return String.format("thumbnail_%d.png", id);
    }
}
