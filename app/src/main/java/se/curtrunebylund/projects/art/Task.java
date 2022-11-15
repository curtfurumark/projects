package se.curtrunebylund.projects.art;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Task implements Serializable {
    private String heading;
    private String description;
    private long created;
    private long updated;
    private int state;


    public enum State{
        TODO, WIP, DONE, ABORTED;
    }


    public long compare() {
        return updated;
    }

    public LocalDate getCreated(){
        return LocalDate.ofEpochDay(created);
    }
    public String getDescription() {
        return description;
    }
    public String getHeading() {
        return heading;
    }




    public State getState() {
        return State.values()[state];
    }
    public long getUpdated() {
        return updated;
    }
    public LocalDateTime getUpdatedAsLocalDateTime() {
        return LocalDateTime.ofEpochSecond(updated,0, ZoneOffset.UTC);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setState(State state) {
        this.state = state.ordinal();
    }
    public void setUpdated(LocalDateTime localDateTime) {
        updated =localDateTime.toEpochSecond(ZoneOffset.UTC);
    }
    @Override
    public String toString() {
        return "Task{" +
                "heading='" + heading + '\'' +
                ", description='" + description + '\'' +
                ", state=" + state +
                '}';
    }



}
