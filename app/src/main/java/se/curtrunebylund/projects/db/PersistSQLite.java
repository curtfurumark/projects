package se.curtrunebylund.projects.db;

import android.content.Context;

import java.util.List;

import static logger.CRBLogger.*;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.classes.Session;
import se.curtrunebylund.projects.classes.Task;

public class PersistSQLite {
    public static void delete(Session session, Context context) {
        Debug.log("PersistSQLite.delete(Attempt, Context)");
        DBSQLite db = new DBSQLite(context);
        db.delete(session);
    }

    public static void deleteAttempts(Task task, Context context) {
        Debug.log("PersistSQLite.deleteAttempts(Task)");
        DBSQLite db = new DBSQLite(context);
        db.deleteAttempts(task);
    }

    public static List<Session> getAttempts(long parent_id, Context context) {
        Debug.log("PersistDBProjects.getAttempts(long parent_id, Context");
        DBSQLite DBSQLite = new DBSQLite(context);
        return DBSQLite.getAttempts(parent_id);
    }

    public static List<Session> getAttempts(Context context) {
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

    public static Session insert(Session session, Context context) {
        Debug.log("DBSQLite.insert(Attempt, Context)");
        DBSQLite db = new DBSQLite(context);
        return db.insert(session);
    }

    public static List<Session> insertAttempts(List<Session> sessions, Context context) {
        log("PersistDBProjects.insertAttempts(List<Attempt>");
        DBSQLite DBSQLite = new DBSQLite(context);
        return DBSQLite.insertAttempts(sessions);
    }

    public static void update(Session session, Context context) {
        log("PersistLocal(Attempt, Context");
        DBSQLite db = new DBSQLite(context);
        db.update(session);
        db.close();
    }

}

