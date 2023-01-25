package se.curtrunebylund.projects.classes;

import static logger.CRBLogger.log;

import java.util.Locale;

import util.Converter;

public class Lap {
    private final long start_time;
    private long stop_time;
    private boolean running;
    public Lap(){
        start_time = System.currentTimeMillis();
        running = true;
    }
    public long getElapsedTime(){
        log("...getElapsedTime()");
        return running ? ((System.currentTimeMillis()) - start_time) /1000 : (stop_time - start_time) / 1000;
    }
    public String getInfo(){
        return Converter.formatSeconds((int) ((stop_time - start_time)/1000));
    }
    public void stop(){
        running = false;
        stop_time = System.currentTimeMillis();

    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Lap, elapased time %d", getElapsedTime());
    }
}
