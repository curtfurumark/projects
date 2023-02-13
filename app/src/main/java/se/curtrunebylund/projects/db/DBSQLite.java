package se.curtrunebylund.projects.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static se.curtrunebylund.projects.util.ProjectsLogger.*;


import classes.Task;
import se.curtrunebylund.projects.classes.Session;
import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.art.ArtWork;
import se.curtrunebylund.projects.infinity.ListItem;
import se.curtrunebylund.projects.util.ProjectsLogger;

public class DBSQLite extends SQLiteOpenHelper {
    private static final String TABLE_ATTEMPTS = "attempts";
    private static final String TABLE_INFINITY = "infinity";
    //private Context context;
    private SQLiteDatabase db;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "projects.db";

    public DBSQLite(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        //this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        log("DBSQLite.onCreate()");
        sqLiteDatabase.execSQL(DBAdmin.CREATE_TABLE_ATTEMPTS);
        sqLiteDatabase.execSQL(DBAdmin.CREATE_TABLE_INFINITY);
        log("...after create table attempts");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }



    public ListItem add(ListItem item) {
        log("DBSQLite.add(ListItem)");
        db = this.getWritableDatabase();
        long id = db.insert(TABLE_INFINITY, null, item.getContentValues());
        item.setID(id);
        Debug.log(item);
        db.close();
        return item;
    }



    public void delete(Session session) {
        log("DBProjects.delete(Attempt) ");
        Debug.log(session);
        String where_clause = String.format(Locale.getDefault(),"id = %d", session.getId());
        log("...where_clause: ", where_clause);
        db = this.getWritableDatabase();
        int rows_deleted = db.delete(TABLE_ATTEMPTS, where_clause, null);
        log("...rows deleted: " + rows_deleted);
    }

    public int delete(ListItem item) {
        log("DBProjects.delete(ListItem) ");
        //Debug.log(item);
        String where_clause = String.format("id = %d", item.getID());
        Debug.log("...where_clause: " + where_clause);
        db = this.getWritableDatabase();
        int rows_deleted = db.delete(TABLE_INFINITY, where_clause, null);
        Debug.log("...rows deleted: " + rows_deleted);
        db.close();
        return rows_deleted;
    }

    public void deleteAttempts(Task task) {
        Debug.log("DBSQlite.deleteAttempts(Task)");
        db = this.getWritableDatabase();
        String whereClause = String.format(Locale.getDefault(), "parent_id = %d", task.getId());
        int rows_deleted = db.delete(TABLE_ATTEMPTS, whereClause, null);
        Debug.log("...rows deleted: " + rows_deleted);
    }

    public void dropTable(String table_name) throws Exception {
        throw new Exception("not implemented");
    }

    public void executeSQL(String sql) {
        log("DBSQLite.executeSQL(String query) ",sql);
        db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
        db = null;
        log("...end of executeSQL()");
    }

    public List<Session> getAttempts() {
        Debug.log("DBProjects.getAttempts()");
        db = this.getReadableDatabase();
        String query = "SELECT * from attempts";
        return queryAttempts(query);
    }

    /**
     * @param parent_id id of parent task (db one table comments)
     * @return list of attempts, believe it or not
     */

    public List<Session> getAttempts(long parent_id) {
        Debug.log("DBProjects.getAttempts(long parent_id) parent_id: " + parent_id);
        String query = String.format(Locale.getDefault(),"SELECT * from attempts  where parent_id = %d", parent_id);
        return queryAttempts(query);
    }

    public List<ListItem> getChildren(Long parent_id) {
        Debug.log("DBProjects.getChildren(ListItem) id: " + parent_id);
        List<ListItem> items = new ArrayList<>();
        db = this.getReadableDatabase();
        String query = String.format("SELECT * from infinity  where parent_id = %d", parent_id);
        Debug.log("...query: " + query);
        Cursor cursor = db.rawQuery(query, null);
        //Debug.log(cursor);
        if (cursor.moveToFirst()) {
            Debug.log("...cursor move to first");
            do {
                items.add(new ListItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }
    public String getDBPath(){
        log("DBSQLite.getDBPath()");
        db = this.getReadableDatabase();
        String path = db.getPath();
        db.close();
        return path;
    }

    public ListItem getItem(long id) {
        System.out.printf("DBSQLite.getItem(%d)\n", id);
        ListItem item = null;
        String query = String.format("SELECT * FROM infinity  WHERE id = %d", id);
        db = this.getReadableDatabase();
        Debug.log("...query: " + query);
        Cursor cursor = db.rawQuery(query, null);
        Debug.log(cursor);
        if (cursor.moveToFirst()) {
            item = new ListItem(cursor);
        }
        cursor.close();
        db.close();
        return item;
    }

    public List<ListItem> getListItems(String query) {
        Debug.log("DBSQLite.getItems()");
        Debug.log("query: " + query);
        List<ListItem> items = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Debug.log(cursor);
        if (cursor.moveToFirst()) {
            Debug.log("...cursor move to first");
            do {
                items.add(new ListItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    public List<String> getTableNames() {
        Debug.log("DBSQLite.getTableNames()");
        List<String> tableNames = new ArrayList<>();
        String query = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name";
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String table_name = cursor.getString(0);
                tableNames.add(table_name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tableNames;
    }
    public boolean hasChild(long id) {
        Debug.log("DBSQLite.hasChild()");
        String query = String.format(Locale.getDefault(),"SELECT count() where parent_id = %d", id);
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Debug.log(cursor);
        cursor.close();
        return false;
    }

    public ArtWork insert(ArtWork artWork) {
        Debug.log("DBSQLite.insert(ArtWork");
        db = this.getWritableDatabase();
        long id = db.insert("artworks", null, artWork.getContentValues());
        artWork.setId((int) id);
        db.close();
        return artWork;
    }

    public Session insert(Session session) {
        Debug.log("DBProjects.insert(Attempt) " + session.getHeading());
        db = this.getWritableDatabase();
        long id = db.insert(TABLE_ATTEMPTS, null, session.getContentValues());
        session.setId(id);
        db.close();
        return session;
    }

    public List<Session> insertAttempts(List<Session> sessions) {
        Debug.log("DBSQLite.insertAttempts(List<Attempt>) size: " + sessions.size());
        db = this.getWritableDatabase();
        for (Session session : sessions) {
            long id = db.insert(TABLE_ATTEMPTS, null, session.getContentValues());
            session.setId(id);
            Debug.log(session);
        }
        db.close();
        return sessions;
    }



    public List<Session> queryAttempts(String query) {
        Debug.log("DBProjects.queryAttempts(String query ) " + query);
        List<Session> sessions = new ArrayList<>();
        db = this.getReadableDatabase();
        Debug.log("...query: " + query);
        Cursor cursor = db.rawQuery(query, null);
        Debug.log(cursor);
        if (cursor.moveToFirst()) {
            Debug.log("...cursor move to first");
            do {
                Session session = new Session(cursor);
                sessions.add(session);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sessions;
    }

    public void update(Session session) {
        log("DBLocal.update(Attempt)");
        String whereClause = String.format("id = %d", session.getId());
        db = this.getWritableDatabase();
        ProjectsLogger.log(session.getContentValues());
        int res = db.update(TABLE_ATTEMPTS, session.getContentValues(), whereClause, null);
        log("...res: " ,res);
        db.close();
    }

    public long update(ListItem item) throws Exception {
        log("DBSQLite.update(ListItem) id ", item.getID());
        String where_clause = String.format(Locale.getDefault(), "id = %d", item.getID());
        log(item.getContentValues());
        db = this.getWritableDatabase();
        long res = db.update(TABLE_INFINITY, item.getContentValues(), where_clause, null);
        Debug.log("\tres: " + res);
        db.close();
        db = null;
        return res;
    }



    public void printInfo() {
        db = this.getReadableDatabase();
        log(db);
    }
}

