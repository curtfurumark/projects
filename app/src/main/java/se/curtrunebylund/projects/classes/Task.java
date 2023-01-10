package se.curtrunebylund.projects.classes;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import item.State;
import se.curtrunebylund.projects.db.ChoreBase;
import util.Converter;


@RequiresApi(api = Build.VERSION_CODES.O)
public class Task implements Serializable {

    private ChoreBase.ChoreType type = ChoreBase.ChoreType.TASK;
    private int id;
    private int parent_id;
    private int parent_task_id = -1;
    private String comment;
    private String heading = "N/A";
    private State state = State.TODO;
    private LocalDate created;
    private LocalDateTime updated;
    private LocalDate target_date;
    private String tags;
    private String description;
    private LocalTime target_time;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Task(String heading, String description, int project_id) {
        this.heading = heading;
        this.description = description;
        this.comment = "";
        this.tags = "";
        this.state = State.TODO;
        this.id = -1;
        this.parent_id = project_id;
        updated = LocalDateTime.now();
        target_date = created = LocalDate.now();
    }

    public Task() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long compare() {
        if (state == null) {
            state = State.DONE;
        }
        if( state.equals(State.DONE)){
            return Long.MIN_VALUE + updated.toEpochSecond(ZoneOffset.UTC);
        }
        return updated.toEpochSecond(ZoneOffset.UTC);
        //return state.equals(State.DONE) ? updated.toEpochSecond(ZoneOffset.UTC) :
        //        updated.plusDays(100).toEpochSecond(ZoneOffset.UTC);
    }
    public boolean contains(String text) {
        return ( (description + heading + comment + tags).contains(text));
    }

    public String debug() {
        return "Task{" +
                "id=" + id +
                ", project_id=" + parent_id +
                ", parent_task_id='" + parent_task_id + '\'' +
                ", heading='" + heading + '\'' +
                ", description='" + description + '\'' +
                ", comment='" + comment + '\'' +
                ", type='" + type.toString() + '\'' +
                ", tags='" + tags + '\'' +
                ", state=" + state +
                ", updated='" + updated + '\'' +
                ", created='" + created + '\'' +
                ", target date='" + target_date + '\'' +
                '}';
    }

    public String getDescription() {
        return description;
    }
    public int getProjectID() {
        return parent_id;
    }

    public int getParentId() {
        return parent_task_id;
    }

    //date_time in db format yyyy-MM-dd HH:mm:ss
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime getUpdated() {
        return updated;
    }




    //date_added yyyy-MM-dd
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDate getDateCreated() {
        return created;
    }

    public String getInfo() {
        boolean is_today = updated.toLocalDate().equals(LocalDate.now());
        String finished_string = "";
        if ( state.equals(State.DONE)){
            finished_string = String.format("%s", target_date.toString());
        }
        String updated_string = String.format("%s updated: %s",
                state.toString(), is_today?
                updated.toLocalTime().format(DateTimeFormatter.ofPattern(Converter.TIME_FORMAT_PATTERN)):
                updated.toLocalDate().format(DateTimeFormatter.ofPattern(Converter.DATE_FORMAT_PATTERN)));

        return String.format("%s, %s", finished_string,  updated_string);
    }
    public State getState() {
        return state;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }




    public String getTags() {
        return tags;
    }

    public ChoreBase.ChoreType getType() {
        return type;
    }
    public String getComment() {
        return comment;
    }

    public String getHeading() {
        return heading;
    }

    public LocalDate getTargetDate() {
        return target_date;
    }

    public int getId() {
        return id;
    }
    public LocalTime getTargetTime(){
        return target_time;
    }
    public boolean hasTags() {
        return !tags.isEmpty();
    }

    public void setCreated(LocalDate created){
        this.created = created;
        this.target_date = created;
    }
    public void setState(State state) {
        this.state = state;
        if ( state.equals(State.DONE)){
            this.target_date= LocalDate.now();
        }
        this.updated = LocalDateTime.now();
    }

    public void setType(ChoreBase.ChoreType type){
        this.type = type;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setProjectID(int project_id){
        this.parent_id = project_id;
    }
    public void setParentTaskID(int parent_task_id){
        this.parent_task_id = parent_task_id;

    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setTargetDate(LocalDate target_date) {
        //Debug2.log("Task.setTargetDate: " + target_date.toString());
        this.target_date = target_date;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String toString() {
        //state == "TODO"?
        if (state == null) {
            //Debug.log("state is null...");
            //Debug.log("comment/task id: " + this.id);
            state = State.DONE;
        }
        String date_or_time = "";
        if (updated.toLocalDate().equals(LocalDate.now())) {
            date_or_time = updated.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            //Debug.log("Task.. date_or_time: " + date_or_time);
        } else {
            date_or_time = updated.toLocalDate().toString();
        }
        return String.format("%s, %s", state.equals(State.DONE) ? date_or_time : state.toString(), heading);
    }


    public void setTargetTime(LocalTime target_time) {
        this.target_time = target_time;
    }


}

