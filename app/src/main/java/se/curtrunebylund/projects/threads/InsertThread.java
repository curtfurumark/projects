package se.curtrunebylund.projects.threads;

import static logger.CRBLogger.log;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;

import persist.DB1Result;
import persist.DBOneBasic;
import persist.HTTPRequest;
import persist.HttpMethod;


public class InsertThread extends Thread{
    private String sql;
    public interface Callback{
        void onItemInserted(DB1Result result);
    }
    private Callback callback;
    public InsertThread(String sql, Callback callback) {
        log("InsertThread() sql", sql);
        this.sql = sql;
        this.callback = callback;
    }

    private void callback(DB1Result result){
        log("InsertThread.callback(DB1Result)");
        if( callback != null){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    callback.onItemInserted(result);
                }
            });

        }else{
            log("missing callback,but thats ok");
        }
    }


    public void run() {
        log("InsertThread.run()");
        HTTPRequest request= new HTTPRequest(DBOneBasic.INSERT_URL);
        request.add("sql", sql);
        request.setMethod(HttpMethod.POST);
        try {
            String json = DBOneBasic.send(request);
            log("json", json);
            Gson gson = new Gson();
            DB1Result result = gson.fromJson(json, DB1Result.class);
            log("result", result.toString());
            callback(result);
        } catch (IOException e) {
            callback(new DB1Result(e));
        }
    }
}
