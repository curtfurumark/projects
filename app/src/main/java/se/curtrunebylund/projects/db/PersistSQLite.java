package se.curtrunebylund.projects.db;

import android.content.Context;

import java.util.List;

import static logger.CRBLogger.*;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.classes.Attempt;
import se.curtrunebylund.projects.classes.Task;

public class PersistSQLite {
    public static void delete(Attempt attempt, Context context) {
        Debug.log("PersistSQLite.delete(Attempt, Context)");
        DBSQLite db = new DBSQLite(context);
        db.delete(attempt);
    }

    public static void deleteAttempts(Task task, Context context) {
        Debug.log("PersistSQLite.deleteAttempts(Task)");
        DBSQLite db = new DBSQLite(context);
        db.deleteAttempts(task);
    }

    public static List<Attempt> getAttempts(long parent_id, Context context) {
        Debug.log("PersistDBProjects.getAttempts(long parent_id, Context");
        DBSQLite DBSQLite = new DBSQLite(context);
        return DBSQLite.getAttempts(parent_id);
    }

    public static List<Attempt> getAttempts(Context context) {
        Debug.log("PersistDBProjects.getAttempts(Context)");
        DBSQLite DBSQLite = new DBSQLite(context);
        return DBSQLite.getAttempts();
    }

    public static List<String> getTableNames(Context context) {
        Debug.log("PersistSQLite.getTableNames()");
        DBSQLite db = new DBSQLite(context);
        List<String> names = db.getTableNames();
        Debug.log("...number of tables: " + names.size());
        for (String name : names) {
            Debug.log("...name: " + name);
        }
        System.out.println(names);
        return names;
    }

    public static Attempt insert(Attempt attempt, Context context) {
        Debug.log("DBSQLite.insert(Attempt, Context)");
        DBSQLite db = new DBSQLite(context);
        return db.insert(attempt);
    }

    public static List<Attempt> insertAttempts(List<Attempt> attempts, Context context) {
        log("PersistDBProjects.insertAttempts(List<Attempt>");
        DBSQLite DBSQLite = new DBSQLite(context);
        return DBSQLite.insertAttempts(attempts);
    }

    public static void update(Attempt attempt, Context context) {
        log("PersistLocal(Attempt, Context");
        DBSQLite db = new DBSQLite(context);
        db.update(attempt);
        db.close();
    }

}

