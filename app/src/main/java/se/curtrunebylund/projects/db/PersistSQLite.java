package se.curtrunebylund.projects.db;

import android.content.Context;

import java.util.List;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.music.Attempt;
import se.curtrunebylund.projects.projects.Task;

public class PersistSQLite {
    public static void delete(Attempt attempt, Context context){
            Debug.log("PersistSQLite.delete(Attempt, Context)");
            DBSQLite db = new DBSQLite(context);
            db.delete(attempt);
    }
    public static void deleteAttempts(Task task, Context context) {
        Debug.log("PersistSQLite.deleteAttempts(Task)");
        DBSQLite db = new DBSQLite(context);
        db.deleteAttempts(task);
    }
    public static List<Attempt> getAttempts(long parent_id, Context context){
        Debug.log("PersistDBProjects.getAttempts(long parent_id, Context");
        DBSQLite DBSQLite = new DBSQLite(context);
        return DBSQLite.getAttempts(parent_id);
    }

    public static List<Attempt> getAttempts(Context context){
        Debug.log("PersistDBProjects.getAttempts(Context)");
        DBSQLite DBSQLite = new DBSQLite(context);
        return DBSQLite.getAttempts();
    }
    public static void getTableNames(Context context) {
        Debug.log("PersistSQLite.getTableNames()");
        DBSQLite db = new DBSQLite(context);
        List<String> names = db.getTableNames();
        System.out.println("...size: " + names.size());
        for(String name: names){
            System.out.println("...name: " + name);
        }
        System.out.println(names);
    }

    public static Attempt insert(Attempt attempt, Context context){
        Debug.log("DBSQLite.insert(Attempt, Context)");
        DBSQLite db = new DBSQLite(context);
        return db.insert(attempt);
    }
    public static List<Attempt> insertAttempts(List<Attempt> attempts, Context context) {
        Debug.log("PersistDBProjects.insertAttempts(List<Attempt>");
        DBSQLite DBSQLite = new DBSQLite( context);
        return DBSQLite.insertAttempts(attempts);
    }
    public static void update(Attempt attempt, Context context) {
        Debug.log("PersistDBLocal(Attempt, Context");
        Debug.log(attempt);
        DBSQLite db = new DBSQLite(context);
        db.update(attempt);
    }




}
