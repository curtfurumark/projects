package se.curtrunebylund.projects.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.Map;
import java.util.Set;

import item.State;
import item.Type;
import logger.CRBLogger;
import se.curtrunebylund.projects.classes.Attempt;

public class ProjectsLogger extends CRBLogger{
    public static void log(SQLiteDatabase db){
        log("SQLiteDatabase");
        if( db == null){
            log("db is null returning");
        }
        log("path",db.getPath());
        log("version",db.getVersion());
        log("maximum size",db.getMaximumSize());
    }

    public static void log(ContentValues contentValues) {
        Debug.log("Debug.log(ContentValues");
        System.out.println("...size: " + contentValues.size());
        //Map<Map.Entry<String, Object>> valueSet = contentValues.valueSet();
        Set<Map.Entry<String, Object>> valueSet = contentValues.valueSet();
        for(Map.Entry<String, Object> entry: valueSet){
            log(String.format("key: %s, value: %s\n", entry.getKey(), entry.getValue()));
        }
    }

    public static void log(Attempt item) {
        log("log Attempt...");
        log("id", item.getId());
        log("heading", item.getHeading());
        log("description", item.getDescription());
        log("comment", item.getComment());
        log("tags", item.getTags());
        logEpochDateTime("created", item.getCreated());
        logEpochDateTime("updated", item.getUpdated());
        log("state", item.getState().toString());
        log("type", item.getType().toString());
        log("parent_id", item.getParent_id());
    }


}
