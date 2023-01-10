package se.curtrunebylund.projects.db;

import android.content.Context;

import java.sql.SQLException;
import java.util.List;

import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.infinity.ListItem;

public class PersistInfinity {

    public static void createTable(Context context){
        System.out.println("PersistInfinity.createTable");
        String sql =
                "CREATE TABLE IF NOT EXISTS infinity ("
                        + "	id integer PRIMARY KEY autoincrement,"
                        + " parent_id INTEGER ,"
                        + " heading TEXT ,"
                        + "	description text ,"
                        + " tags TEXT, "
                        + " duration INTEGER, "
                        + " created INTEGER, "
                        + " updated INTEGER ,"
                        + "	state INTEGER , "
                        + " type INTEGER"
                        + ");";
        DBSQLite db = new DBSQLite(context);
        db.executeSQL(sql);

    }

    public static int delete(ListItem item, Context context) throws SQLException {
        System.out.println("PersistInfinity.delete(): " + item.getHeading());
        System.out.println("say what");
        DBSQLite db = new DBSQLite(context);
        return db.delete(item);

    }
    public static void dropTable(String table_name, Context context) throws Exception {
        System.out.printf("PersistInfinity.dropTable(%s)\n", table_name);
        DBSQLite db = new DBSQLite(context);
        db.dropTable(table_name);
    }

    public static ListItem add(ListItem item, Context context) {
        System.out.println("PersistInfinity.add(ListItem)" + item.getHeading());
        DBSQLite db = new DBSQLite(context);
        return db.add(item);
    }


    public static List<ListItem> getChildren(Long parent_id, Context context) {
        System.out.printf("PersistInfinity.getChildren(ListItem)\n");
        DBSQLite db = new DBSQLite(context);
        return db.getChildren(parent_id);
    }

    public static List<ListItem> getChildren(ListItem item, Context context) {
        System.out.printf("PersistInfinity.getChildren(ListItem)\n");
        DBSQLite db = new DBSQLite(context);
        return db.getChildren(item.getID());
    }
    public static ListItem getItem(long id, Context context) throws SQLException {
        System.out.printf("PersistInfinity.getItem(%d)\n", id);
        DBSQLite db = new DBSQLite(context);
        return db.getItem(id);
    }

    public static boolean hasChild(ListItem item, Context context){
        Debug.log("PersistInfinity.hasChild(ListItem id:%d)");
        DBSQLite db = new DBSQLite(context);
        return db.hasChild(item.getID());
    }

    public static List<ListItem> getListItems(Context context) {
        System.out.printf("PersistInfinity.getListItems()\n");
        DBSQLite db = new DBSQLite(context);
        String query = String.format("SELECT * FROM infinity");
        //Debug.log("...query: " + query);
        return db.getListItems(query);
    }
    public static List<ListItem> getRootItems(Context context){
        Debug.log("PersistInfinity.getRootItems(Context)");
        DBSQLite db = new DBSQLite(context);
        String sql = "SELECT * FROM infinity WHERE parent_id = 0";
        return db.getListItems(sql);

    }

    /**
     *
     * @param item
     * @param context
     * @return -1 for failure, otherwise och
     * @throws Exception
     */
    public static long update(ListItem item, Context context) throws Exception {
        if( item == null){
            Debug.log("PersistInfinity.updateListItem() called with null ListItem");
            return -1;
        }
        System.out.printf("PersistInfinity.update(ListItem id = %d)\n", item.getID());
        DBSQLite db = new DBSQLite(context);
        long res =  db.update(item);
        if( res == -1){
            throw new Exception(String.format("updated item%d failed", item.getID()));
        }
        return res;
    }


}
