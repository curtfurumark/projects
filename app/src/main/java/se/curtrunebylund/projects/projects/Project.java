package se.curtrunebylund.projects.projects;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import se.curtrunebylund.projects.Debug;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Project implements Serializable {

    private int id;
    private String heading;
    private LocalDateTime added;
    private LocalDate target_date;
    private State state;
    private String tags;
    private String comment;
    private LocalDateTime last_update;
    private String description = "just do it";

    public long compare(){
        return last_update.toEpochSecond(ZoneOffset.UTC);
    }
    public boolean contains(String text){
        String everything = heading + description + comment + tags;
        return everything.toLowerCase().contains(text.toLowerCase());
    }

    public LocalDateTime getAdded() {
        return added;
    }
    public String getComment() {
        return comment;
    }
    public String getDescription() {
        return description;
    }
    public int getId() {
        return id;
    }
    public String getInfo(){
        return String.format("%s updated: %s", state.toString(), last_update.format(DateTimeFormatter.ofPattern("yyMMdd HH:mm")));
    }

    public String getHeading() {
        return heading;
    }
    public LocalDateTime getUpdated(){
        return last_update;
    }

    public String getParentId() {
        return "";
    }
    public State getState() {
        return state;
    }

    public String getTags() {
        Debug.log("Project.getTags(): " + tags );
        if( null == tags){
            String info = String.format("id: %d, %s", id, heading);
            Debug.log(info);
            tags = "";
        }
        return tags;
    }

    public void setTags(String tags) {
        this.tags = (tags == null ? "": tags);
    }

    public Project() {
        tags = comment = heading = description = "";

    }


    public void setId(int id) {
        this.id = id;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }





    public void setHeading(String heading) {
        this.heading = heading;
    }


    public void setDescription(String description) {
        this.description = description;
    }






    public void setAdded(LocalDateTime added) {
        this.added = added;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setState(State state) {
        //Debug.log("Project.setState(): " + state.toString());
        this.state = state;
    }

    public LocalDate getTargetDate() {
        return target_date;
    }
    public void setUpdated(LocalDateTime lastUpdate) {
        this.last_update = lastUpdate;
    }

    public void setCreated(LocalDateTime created) {
        this.added = created;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s, %s, %s, %s, %s", last_update.toString(), heading, description,  state, tags);
    }
}
