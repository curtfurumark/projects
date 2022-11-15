package se.curtrunebylund.projects.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.art.ArtWork;
import se.curtrunebylund.projects.infinity.ListItem;
import se.curtrunebylund.projects.music.Attempt;
import se.curtrunebylund.projects.projects.Task;

public class DBSQLite extends SQLiteOpenHelper {
    private static final String TABLE_ATTEMPTS = "attempts";
    private static final String TABLE_INFINITY = "infinity";
    private Context context;
    private SQLiteDatabase db;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "projects.db";
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

    public DBSQLite(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public ListItem add(ListItem item) {
        Debug.log("DBSQLite.add(ListItem)");
        db = this.getWritableDatabase();
        long id = db.insert(TABLE_INFINITY, null, item.getContentValues());
        item.setID(id);
        Debug.log(item);
        db.close();
        return item;
    }

    public void delete(Attempt attempt) {
        Debug.log("DBProjects.delete(Attempt) ");
        Debug.log(attempt);
        String where_clause = String.format(Locale.getDefault(),"id = %d", attempt.getId());
        Debug.log("...where_clause: " + where_clause);
        db = this.getWritableDatabase();
        int rows_deleted = db.delete(TABLE_ATTEMPTS, where_clause, null);
        Debug.log("...rows deleted: " + rows_deleted);
    }

    public int delete(ListItem item) {
        Debug.log("DBProjects.delete(ListItem) ");
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
        Debug.log("DBSQLite.executeSQL(String query) " + sql);
        db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
        db = null;
        Debug.log("");
    }

    public List<Attempt> getAttempts() {
        Debug.log("DBProjects.getAttempts()");
        db = this.getReadableDatabase();
        String query = "SELECT * from attempts";
        return queryAttempts(query);
    }

    /**
     * @param parent_id id of parent task (db one table comments)
     * @return list of attempts, believe it or not
     */

    public List<Attempt> getAttempts(long parent_id) {
        Debug.log("DBProjects.getAttempts(long parent_id) parent_id: " + parent_id);
        String query = String.format("SELECT * from attempts  where parent_id = %d", parent_id);
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

    public ArtWork insert(ArtWork artWork) {
        Debug.log("DBSQLite.insert(ArtWork");
        db = this.getWritableDatabase();
        long id = db.insert("artworks", null, artWork.getContentValues());
        artWork.setId((int) id);
        db.close();
        return artWork;
    }

    public Attempt insert(Attempt attempt) {
        Debug.log("DBProjects.insert(Attempt) " + attempt.getHeading());
        db = this.getWritableDatabase();
        long id = db.insert(TABLE_ATTEMPTS, null, attempt.getContentValues());
        attempt.setId(id);
        db.close();
        return attempt;
    }

    public List<Attempt> insertAttempts(List<Attempt> attempts) {
        Debug.log("DBSQLite.insertAttempts(List<Attempt>) size: " + attempts.size());
        db = this.getWritableDatabase();
        //db.execSQL("DELETE FROM attempts");
        for (Attempt attempt : attempts) {
            long id = db.insert(TABLE_ATTEMPTS, null, attempt.getContentValues());
            attempt.setId(id);
            Debug.log(attempt);
        }
        db.close();
        return attempts;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Debug.log("DBProjects.onCreate()");
        // sqLiteDatabase.execSQL(DBStuff.CREATE_MUSIC_TABLE);
        Debug.log("...after create table attempts");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public List<Attempt> queryAttempts(String query) {
        Debug.log("DBProjects.queryAttempts(String query ) " + query);
        List<Attempt> attempts = new ArrayList<>();
        db = this.getReadableDatabase();
        //String query = String.format("SELECT * from attempts  where parent_id = %d", parent_id);
        Debug.log("...query: " + query);
        Cursor cursor = db.rawQuery(query, null);
        Debug.log(cursor);
        if (cursor.moveToFirst()) {
            Debug.log("...cursor move to first");
            do {
                Attempt attempt = new Attempt(cursor);
                attempts.add(attempt);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return attempts;
    }

    public void update(Attempt attempt) {
        Debug.log("DBProjects.update(Attempt)");
        String whereClause = String.format("id = %d", attempt.getId());
        db = this.getWritableDatabase();
        Debug.log(attempt.getContentValues());
        int res = db.update(TABLE_ATTEMPTS, attempt.getContentValues(), whereClause, null);
        Debug.log("...res: " + res);
        db.close();
    }

    public long update(ListItem item) throws Exception {
        System.out.printf("DBSQLite.update(ListItem) id = %d\n", item.getID());
        String where_clause = String.format(Locale.getDefault(), "id = %d", item.getID());
        Debug.log(item.getContentValues());
        db = this.getWritableDatabase();
        long res = db.update(TABLE_INFINITY, item.getContentValues(), where_clause, null);
        Debug.log("\tres: " + res);
        db.close();
        db = null;
        return res;
    }

    public boolean hasChild(long id) {
        Debug.log("DBSQLite.hasChild()");
        String query = String.format(Locale.getDefault(),"SELECT count() where parent_id = %d", id);
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Debug.log(cursor);
        return false;
    }
}

