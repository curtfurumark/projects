package se.curtrunebylund.projects.threads;

import static logger.CRBLogger.log;
import static logger.CRBLogger.logError;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import persist.DBOneBasic;
import persist.HTTPRequest;
import persist.HttpMethod;


public class SelectThread extends Thread {
    private final String query;
    public static boolean VERBOSE = false;
    public interface Callback{
        void onRequestSelectError(String errMessage);
        void onRequestSelectDone(String json);
    }
    private final Callback callback;
    public SelectThread(String query, Callback callback) {
        log("SelectThread(String query, Callback callback) query", query);
        this.callback = callback;
        this.query = query;
    }
    private void callback(String json){
        if( VERBOSE) log("SelectThread.callback(String json)");
        if( callback != null){
            new Handler(Looper.getMainLooper()).post(() -> callback.onRequestSelectDone(json));

        }else{
            logError("missing callback, not ok");
        }
    }

    @Override
    public void run() {
        if( VERBOSE) log("SelectThread.run()");
        HTTPRequest request= new HTTPRequest(DBOneBasic.SELECT_URL);
        request.add("sql", query);
        request.setMethod(HttpMethod.POST);
        try {
            String json = DBOneBasic.send(request);
            callback(json);
        } catch (IOException e) {
            callback.onRequestSelectError(e.toString());
        }
    }
}
