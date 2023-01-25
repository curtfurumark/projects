package se.curtrunebylund.projects.classes;

import java.util.ArrayList;
import java.util.List;

public class Assignment {
    private String heading;
    private int reps;
    private long total_time;
    private  List<Lap> laps = new ArrayList<>();

    public Assignment(String heading) {
        this.heading = heading;
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
}
