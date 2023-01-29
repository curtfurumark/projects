package se.curtrunebylund.projects.classes;

import java.util.ArrayList;
import java.util.List;

public class Assignment {
    private String heading;
    private int reps;
    private long total_time;
    public enum Type{
        PENDING, LAPS, REPS
    }
    private Type type = Type.PENDING;
    private  List<Lap> laps = new ArrayList<>();

    public Assignment(String heading, Type type) {
        this.heading = heading;
        this.type = type;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public List<Lap> getLaps() {
        return laps;
    }

    public void setLaps(List<Lap> laps) {
        this.laps = laps;
    }

    public void addLap(Lap lap) {
        laps.add(lap);
    }

    public long getTotal_time() {
        return total_time;
    }

    public void setTotal_time(long total_time) {
        this.total_time = total_time;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
