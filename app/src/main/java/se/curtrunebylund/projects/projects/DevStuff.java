package se.curtrunebylund.projects.projects;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.curtrunebylund.projects.classes.Session;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.art.ArtWorkFactory;
import se.curtrunebylund.projects.art.PicHelper;

public class DevStuff {


    public static void testCreateFileForCamera(Context context){
        Debug.log("DevStuff.textCreateFileForCamera()");
        try {
            String path = ArtWorkFactory.getImagePath(123, context);
            File cameraFile = PicHelper.getInstance(context).createImageFile(path);
            Debug.log("...got camerafile ?" + cameraFile.getAbsolutePath());
            boolean deleted = cameraFile.delete();
            Debug.log("...file deleted? " + deleted);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Session> getAttempts(int number, long parent_id){
        Debug.log("DevStuff.getAttempts(int)  " + number);
        List<Session> sessions = new ArrayList<>();
        for ( int i = 0;  i < number; i++){
            Session session = new Session();
            session.setHeading("attempt " + i);
            session.setParent_id(parent_id);
            sessions.add(session);
        }
        return sessions;
    }
}
