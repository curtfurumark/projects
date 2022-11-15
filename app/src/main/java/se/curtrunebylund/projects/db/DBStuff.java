package se.curtrunebylund.projects.db;

import android.content.ContentValues;
import android.content.Context;

import java.util.List;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.art.ArtWork;
import se.curtrunebylund.projects.art.ArtWorkManager;
import se.curtrunebylund.projects.help.Converter;
import se.curtrunebylund.projects.infinity.ListItem;
import se.curtrunebylund.projects.music.Attempt;
import se.curtrunebylund.projects.projects.Task;

public class DBStuff {
    public static final String CREATE_MUSIC_TABLE = "CREATE TABLE attempts" +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "parent_id INTEGER, " +
            "heading TEXT," +
            "description TEXT,"+
            "comment TEXT, " +
            "created INTEGER," +
            "updated INTEGER," +
            "grade INTEGER," +
            ")";
    public static final String CREATE_ARTWORKS_TABLE = "CREATE TABLE artworks" +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "description TEXT, " +
            "comment TEXT, " +
            "tags TEXT, " +
            "created INTEGER, " +
            "updated INTEGER, " +
            "source_date INTEGER, " +
            "state INTEGER, " +
            "private INTEGER)";

    public static final String CREATE_REF_PICS_TABLE = "CREATE TABLE ref_pics" +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "parent_id INTEGER," +
            "path TEXT)";

    public static void addItem2InfinityTable(String heading, String tags, Context context) {
        Debug.log("DBStuff.addItem2InfinityTable");
        ListItem item = new ListItem();
        item.setHeading(heading);
        item.setTags(tags);
        item.setType(ListItem.Type.DEV);
        item = PersistInfinity.add(item, context);
        Debug.log(item);
    }


    public void createArtworkTable(Context context){
        Debug.log("DBStuff.createArtworkTable()");
        DBSQLite dbsqLite = new DBSQLite(context);
        dbsqLite.executeSQL(CREATE_ARTWORKS_TABLE);
        dbsqLite.executeSQL(CREATE_REF_PICTURES_TABLE);
    }
    public static void createMusicTable(Context context){
        Debug.log("DBStuff.createMusicTable()");
        DBSQLite dbsqLite = new DBSQLite(context);
        dbsqLite.executeSQL(CREATE_MUSIC_TABLE);
        //dbsqLite.executeSQL(CREATE_REF_PICTURES_TABLE);
    }
    public static final String  CREATE_REF_PICTURES_TABLE = "CREATE TABLE ref_pics" +
            "( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "parent_id INTEGER, " +
            "full_file_name text)";

    public static void copyTasksToDBLocal(List<Task> tasks, Context context){
        Debug.log("DBAdmin.copyTasksToDBLocal(List<Task>)");
        if( tasks == null){
            Debug.log("...tasks == null");
            return;
        }
        List<Attempt> attempts = Converter.convertTasks(tasks);
        Debug.logAttempts(attempts, true);
        DBSQLite db = new DBSQLite(context);
        db.insertAttempts(attempts);
        Debug.log("...attempts inserted");
    }
    public static void migrateArtworksToDBLocal(Context context){
        Debug.log("DBStuff.migrateArtworksToDBLocal()");
        List<ArtWork> artWorks = ArtWorkManager.getInstance(context).getWorks();
        DBSQLite dbsqLite = new DBSQLite(context);
        for (ArtWork artWork: artWorks){
            artWork = dbsqLite.insert(artWork);
            if (artWork.getRefPicturePaths().size() > 0 ){
                for (String path: artWork.getRefPicturePaths()){
                    //dbsqLite.insertRefPicPath(path);
                }
            }
        }
        dbsqLite.insert(artWorks.get(0));

    }
}
