package se.curtrunebylund.projects.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import logger.CRBLogger;
import se.curtrunebylund.projects.classes.Session;
import se.curtrunebylund.projects.db.Result;
import se.curtrunebylund.projects.infinity.ListItem;
import se.curtrunebylund.projects.classes.Task;
import se.curtrunebylund.projects.art.ArtWork;

public class Debug {
    public static final boolean VERBOSE = false;
    public static String CB_TAG = "CURTRUNEBYLUND";
    public static void log(String mess){
        Log.v(CB_TAG, mess);
        CRBLogger.logToFile(mess);
    }
    private static List<String> logList = new ArrayList<>();

    public static void log(ListItem item) {
        log("log(ListItem()");
        if( item == null){
            log("item is null");
            return;
        }
        log("\tid: " + item.getID());
        log("\tparent_id: " + item.getParentID());
        log("\theading: " + item.getHeading());
        log("\tdescription: " + item.getDescription());
        log("\ttags: " + item.getTags());
        log("\tcreated: " + LocalDateTime.ofEpochSecond(item.getCreatedEpoch(),0, ZoneOffset.UTC).toString());
        log(item.toString());
    }

    public enum DebugLevel{
        LOW, MEDIUM, HIGH
    }
    public static DebugLevel debugLevel = DebugLevel.MEDIUM;
    public static void log(String message, DebugLevel level){
        if (level.ordinal() >= debugLevel.ordinal()){
            Log.v(CB_TAG, message);
        }

    }

    public static void log(Result result) {
        log(result.getPHPResult());
    }

    public static void log(Task task) {
        Debug.log("Debug.log(Task)");
        log(task.debug());
    }

    public static void logTaskList(List<Task> tasks) {
        Debug.log("Debug.logTaskList<Task>");
        if( tasks == null){
            Debug.log("...tasks == null");
            return;
        }
        Debug.log("...size: " + tasks.size());
        for( Task task: tasks){
            log(task);
        }
    }
    public static void log(Stack stack) {
        log("Debug.log(Stack)");
        if( null == stack){
            log("stack is null");
            return;
        }
        for(int i =0; i < stack.getSize(); i++){
            System.out.printf("item[%d] = %s\n", i, stack.get(i).toString());
        }
    }

    public static void log(Session session) {
        Debug.log("Debug.log(Attempt)");
        Debug.log(session.toString());
    }

    public static void log(Cursor cursor) {
        Debug.log("Debug.log(Cursor)");
        System.out.println("...columnCount: " + cursor.getColumnCount());
        System.out.println("...row count: ? " +cursor.getCount());
        //System.out.println("...column names: " + cursor.getColumnNames());
        String[] columnNames = cursor.getColumnNames();
        for(String name: columnNames){
            System.out.println("...column name: " + name);
        }
    }

    public static void logAttempts(List<Session> sessions, boolean log_all) {
        Debug.log("Debug.logAttempts(List<Attempt> boolean log_all = " + log_all);
        Debug.log("...number of attempts: " + sessions.size());
        if ( log_all) {
            for (Session session : sessions) {
                log(session);
            }
        }else{
            for (int i = 0; i < sessions.size(); i+= 10){
                log(sessions.get(i));
            }
        }
    }



    //TODO database?
    public static void logError(String s) {
        log(s);
    }



    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        log(message);
    }

    public static void log(Uri uri) {
        Debug.log("Debug.log(Uri)");
        System.out.println("...host: " + uri.getHost());
        System.out.println("...toString: " + uri.toString());
        System.out.println("...path" + uri.getPath());
        System.out.println("...authority: " + uri.getAuthority());
        System.out.println("...encoded path: " + uri.getEncodedPath());
    }

    public static void log(Bitmap bitmap) {
        Debug.log("Debug.log(Bitmap)");
        if ( bitmap == null){
            Debug.log("...bitmap is null");
            return;
        }
        System.out.println("...height: " + bitmap.getHeight());
        System.out.println("...width: " + bitmap.getWidth());
        System.out.println("...toString: " + bitmap.toString());
    }

    public static void log(ArtWork artWork) {
        Debug.log("Debug.log(Artwork) " + artWork.getDescription());
        System.out.println("...id: " + artWork.getId());
        System.out.println("...created: " + artWork.getCreated());
        System.out.println("...thumbnail path: " + artWork.getThumbNailPath());
        System.out.println("...image path: " + artWork.getImagePath());
        System.out.println("...has ref pics: " + artWork.hasRefPictures());
        System.out.println("....ref pics " + artWork.getRefPicturePaths());
        //System.out.println(artWork.debug());
    }
}
