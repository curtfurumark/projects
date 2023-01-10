package se.curtrunebylund.projects.util;

import android.content.Context;

import java.util.List;

import se.curtrunebylund.projects.db.PersistSQLite;

/**
 * stuff that dont belong to the app
 * but makes it easier for me to develop the
 */
public class DevHelper {
    public static void listDBSQLiteTables(Context context){
        System.out.println("listDBSQLiteTables");
        List<String> tables = PersistSQLite.getTableNames(context);

    }
}
