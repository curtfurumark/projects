package se.curtrunebylund.projects.util;

import static logger.CRBLogger.log;
import static logger.CRBLogger.logError;

import android.os.Handler;
import android.os.Looper;
import android.text.style.TtsSpan;

import java.util.Timer;
import java.util.TimerTask;

import se.curtrunebylund.projects.activities.MusicSessionActivity;


public class Kronos {
    private Timer timer;
    private TimerTask timerTask;
    private static final boolean VERBOSE = true;
    //private boolean ACTIVITY_VISIBLE = true;

    private int seconds;




    public enum State{
        STOPPED, RUNNING, PAUSED
    }
    private State timerState = State.STOPPED;
    public interface Callback{
        void onTimerTick(int secs);
    }
    private Callback callback;


    private static Kronos instance;
    private Kronos(Callback callback){
        this.callback = callback;
    }
    public  static Kronos getInstance(Callback callback){
        if ( instance == null){
            instance = new Kronos(callback);
        }
        return instance;
    }
    public int getElapsedTime(){
        return seconds;
    }
    public State getState(){
        return timerState;
    }

    public void pause(){
        if( VERBOSE ) log("Kronos.pause()");
        timer.cancel();
        timerTask.cancel();
        timerState = State.PAUSED;
    }

    public void reLoadTimer(Callback callback){
        if( VERBOSE) log("Kronos.reLoadTimer(Callback)");
        instance = new Kronos(callback);


    }
    public void resume(){
        if(VERBOSE) log("Kronos.resume()");
        start();
    }

    public void removeCallback(){
        if( VERBOSE) log("Kronos.removeCallback()");
        this.callback = null;
    }

    public void reset(){
        if( VERBOSE) log("Kronos.reset()");
        timer.cancel();
        timerTask.cancel();
        seconds = 0 ;
    }

    public void setCallback(Kronos.Callback callback) {
        if( VERBOSE) log("Kronos.setCallback(Callback)");
        this.callback = callback;
    }
    public void start(){
        if( VERBOSE) log("Kronos.start()");
        timerState = State.RUNNING;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                seconds++;
                if ( callback != null){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onTimerTick(seconds);
                        }
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0 , 1000);
    }

    /**
     * stops the timer, and resets it, maybe there is a better way
     */
    public void stop(){
        if( VERBOSE) log("Kronos.stop()");
        timerState = State.STOPPED;
        seconds = 0;
        timer.cancel();
        timerTask.cancel();
    }


}
