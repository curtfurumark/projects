package se.curtrunebylund.projects.threads;

import persist.DB1Result;

public class UpdateThread extends Thread {
    private String query;
    public interface Callback{
        void onUpdateDone(DB1Result result);
    }
    private Callback callback;

    public UpdateThread(String query, Callback callback) {
        this.query = query;
        this.callback = callback;
    }

    @Override
    public void run() {
        super.run();
    }
}
