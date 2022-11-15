package se.curtrunebylund.projects.help;

import static android.os.Looper.*;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

import se.curtrunebylund.projects.Debug;


public class MyTimer {
    private Timer timer;
    private TimerTask timerTask;
    private TextView textView;
    private int seconds;
    public enum State{
        STOPPED, RUNNING, PAUSED
    }
    private State timerState = State.STOPPED;
    public interface Callback{
        public void onTimerTick(int secs);
    }
    private Callback callback;

    private Activity activity;
    private static MyTimer instance;
    private MyTimer(Callback callback, Activity activity){
        this.activity = activity;
        this.callback = callback;
    }
    public  static MyTimer getInstance(Callback callback, Activity activity){
        if ( instance == null){
            instance = new MyTimer(callback, activity);
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
        Debug.log("MyTimer.pause()");
        timer.cancel();
        timerTask.cancel();
        timerState = State.PAUSED;
    }
    public void resume(){
        Debug.log("MyTimer.resume()");
        start();
    }

    public void reset(){
        Debug.log("MyTimer.reset()");
        timer.cancel();
        timerTask.cancel();
        seconds = 0 ;
    }
    public void start(){
        Debug.log("MyTimer.start()");
        timerState = State.RUNNING;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                seconds++;
                if ( callback != null && activity != null){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //callback.onTimerTick(seconds);
                            textView.setText(Converter.formatSeconds(seconds));
                        }
                    });
                    //Looper.getMainLooper().post(new Runnable(){
                    //activity.runOnUiThread(new Runnable() {
                       /* @Override
                        public void run() {
                            callback.onTimerTick(seconds);
                        }
                    });*/
                }else{
                    Debug.log("...callback or activity is null or both of them");
                    Debug.log("activity " + (activity == null) );
                    Debug.log("callback: " + ( callback == null));

                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0 , 1000);
    }
    public void setActivity(Activity activity){
        this.activity = activity;
    }
    public void setTextView(TextView textView){
        this.textView = textView;
    }
    public void stop(){
        Debug.log("MyTimer.stop()");
        timerState = State.STOPPED;
        seconds = 0;
        timer.cancel();
        timerTask.cancel();
    }


}
