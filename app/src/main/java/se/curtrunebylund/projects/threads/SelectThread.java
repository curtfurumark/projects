package se.curtrunebylund.projects.threads;

import static logger.CRBLogger.log;
import static logger.CRBLogger.logError;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import classes.UrlItem;
import persist.DB1Result;
import persist.DBOneBasic;
import persist.HTTPRequest;
import persist.HttpMethod;


public class SelectThread extends Thread {
    private String query;
    private HttpMethod method = HttpMethod.POST;
    public interface Callback{
        void onRequestSelectError(String errMessage);
        void onRequestSelectDone(String json);
    }
    private Callback callback;
    public SelectThread(String query, Callback callback) {
        log("DBOneSelectThread() query", query);
        this.callback = callback;
        this.query = query;
    }
    private void callback(String json){
        log("SelectThread.callback(String json)");
        if( callback != null){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    callback.onRequestSelectDone(json);
                }
            });

        }else{
            logError("missing callback, not ok");
        }
    }

    @Override
    public void run() {
        log("SelectThread.run()");
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
