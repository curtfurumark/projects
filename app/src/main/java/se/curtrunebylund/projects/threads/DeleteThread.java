package se.curtrunebylund.projects.threads;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;

import persist.DB1Result;
import persist.DBOneBasic;
import persist.HTTPRequest;
import persist.HttpMethod;


public class DeleteThread extends Thread{
    public interface Callback{
        void onDeleteDone(DB1Result result);
    }
    private Callback callback;
    private String sql;

    public DeleteThread(String sql, Callback callback) {
        this.callback = callback;
        this.sql = sql;
    }
    private void callback(DB1Result res) {
        if (callback != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    callback.onDeleteDone(res);
                }
            });
        }
    }
    @Override
    public void run() {
        HTTPRequest request = new HTTPRequest(HttpMethod.POST);
        request.setUrl(DBOneBasic.DELETE_URL);
        request.add("sql", sql);
        try {
            String json = DBOneBasic.send(request);
            DB1Result result = new Gson().fromJson(json, DB1Result.class);
            callback(result);
        } catch (IOException e) {
            callback(new DB1Result(e));
        }
    }
}
