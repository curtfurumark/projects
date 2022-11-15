package se.curtrunebylund.projects.help;

import android.os.CountDownTimer;

import se.curtrunebylund.projects.Debug;


public class CRBCountdownTimer {
    //private long msecs_remaining;
    private long initial_msecs;
    private long msecs;
    private CountDownTimer countDownTimer;
    private static CRBCountdownTimer instance;
    private CRBCountdownTimer(Callback callback){
        this.callback = callback;
    }



    private enum TimerState{
        STOPPED, RUNNING, PAUSED
    }
    private TimerState timerState = TimerState.STOPPED;

    public interface Callback{
        public void onTick(long msecs);
        public void onFinish();
    }
    private Callback callback;
    public static CRBCountdownTimer getInstance( Callback callback){
        if( instance == null){
            instance = new CRBCountdownTimer(callback);
        }
        return instance;
    }



    public void pause(){
        Debug.log("CRBCountdownTimer.pause()");
        countDownTimer.cancel();
        timerState = TimerState.PAUSED;
    }
    public void reset() {
        Debug.log("CRBCountdownTimer.reset()");
        countDownTimer.cancel();
        msecs = initial_msecs;
    }
    public void resume(){
        if( initial_msecs == 0){
            Debug.log("...dont call resume if you havent started");
        }
        Debug.log("CRBCountdownTimer.resume()");
        start();
    }
    public void start(){
        Debug.log("CRBCountdownTimer.start()");
        countDownTimer = new CountDownTimer(msecs, 1000) {
            @Override
            public void onTick(long remaining) {
                msecs = remaining;
                callback.onTick(msecs);
            }

            @Override
            public void onFinish () {
                timerState = TimerState.STOPPED;
                callback.onFinish();
            }
        }.start();
        timerState = TimerState.RUNNING;
    }

    public void start(long msecs){
        Debug.log("CRBCountdownTimer.start(long msecs) " + msecs);
        this.initial_msecs = msecs;
        this.msecs= msecs;
        start();
    }
    public void stop(){
        Debug.log("CRBCountdownTimer.stop()");
        countDownTimer.cancel();
        timerState = TimerState.STOPPED;
        msecs = initial_msecs;
    }

}
