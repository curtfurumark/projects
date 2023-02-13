package se.curtrunebylund.projects.classes;

import static logger.CRBLogger.log;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import classes.projects.Assignment;
import item.State;
import item.Type;
import se.curtrunebylund.projects.projects.Grade;
import se.curtrunebylund.projects.util.Debug;
import util.Converter;

public class Session implements Serializable {
    private long id;
    private long parent_id;
    private String heading;
    private String description;
    private String comment;
    private String tags;
    private long created;
    private long updated;
    private int duration;
    private Type type = Type.PENDING;
    private String json;
    private se.curtrunebylund.projects.projects.Grade grade;
    private State state;

    public Session(){
        updated = created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        grade = se.curtrunebylund.projects.projects.Grade.PENDING;
        state = State.TODO;
    }
    //TODO redundant?
    public Session(ContentValues cv){
        id = cv.getAsLong("id");
        parent_id = cv.getAsLong("parent_id");
        heading = cv.getAsString("heading");
        description = cv.getAsString("description");
        comment = cv.getAsString("comment");
        updated = cv.getAsLong("updated");
        created = cv.getAsLong("created");
        grade = se.curtrunebylund.projects.projects.Grade.values()[cv.getAsInteger("grade")];
        state = State.values()[cv.getAsInteger("state")];
        duration = cv.getAsInteger("duration");
        json = cv.getAsString("json");
    }

    public Session(Cursor cursor) {
        log("Session(Cursor) constructor");
        int col_count = cursor.getColumnCount();
        log("column count", col_count);
        id = cursor.getLong(0);
        parent_id = cursor.getLong(1);
        heading = cursor.getString(2);
        description = cursor.getString(3);
        comment = cursor.getString(4);
        created = cursor.getLong(5);
        updated = cursor.getLong(6);
        grade = se.curtrunebylund.projects.projects.Grade.values()[cursor.getInt(7)];
        state = State.values()[cursor.getInt(8)];
        duration = cursor.getInt(9);
        json = cursor.getString(10);
        int typeIndex = cursor.getInt(11);
        type = typeIndex >= Type.values().length ? Type.PENDING:Type.values()[typeIndex];
        //type = Type.values()[cursor.getInt(11)];
    }

    public Session(String heading) {
        this();
        this.heading = heading;
    }
    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("heading", heading);
        cv.put("parent_id", parent_id);
        cv.put("description", description);
        cv.put("comment", comment);
        cv.put("updated", updated);
        cv.put("created", created);
        cv.put("grade", grade.ordinal());
        cv.put("state", state.ordinal());
        cv.put("type", type.ordinal());
        cv.put("duration", duration);
        cv.put("json", json);
        return cv;
    }

    public long compare() {
        return isDone()? Long.MIN_VALUE + updated: updated;
        //updated;
    }
    public static List<Session> getAttempts(String heading, int number, int parent_id) {
        Debug.log(String.format("Attempt.getAttempts(%s, %d %d)", heading, number, parent_id));
        List<Session> sessions = new ArrayList<>();
        for( int i = 0; i < number; i++){
            Session session = new Session(heading);
            session.setParent_id(parent_id);
            sessions.add(session);
        }
        return sessions;
    }

    public long getId() {
        return id;
    }

    public long getParent_id() {
        return parent_id;
    }

    public String getTags(){
        return "not implemented";
    }

    public Type getType(){
        return type;
    }


    public State getState() {
        return state;
    }
    public boolean isDone() {
        return !grade.equals(Grade.PENDING);
    }
    public void setState(State state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public se.curtrunebylund.projects.projects.Grade getGrade() {
        return grade;
    }

    public String getComment() {
        return comment;
    }

    public long getCreated() {
        return created;
    }
    public int getDuration(){
        return duration;
    }
    public String getHeading() {
        return heading;
    }
    public String getInfo(){
        if( state.equals(State.DONE)){
            return String.format("DONE, %s, %d secs", Converter.epochDateTimeUI(updated), duration);
        }
        return String.format(Locale.getDefault(),"%s, %s", Converter.epochToFormattedDateTime(updated), state.toString());
    }

    public long getUpdated() {
        return updated;
    }

    public LocalDateTime getUpdatedAsLocalDateTime() {
        return LocalDateTime.ofEpochSecond(updated, 0 , ZoneOffset.UTC);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGrade(se.curtrunebylund.projects.projects.Grade grade) {
        this.grade = grade;
    }
    public void setDuration(int duration){
        this.duration = duration;
    }
    public void setHeading(String heading) {
        this.heading = heading;
    }
    public void setId(long id) {
        this.id = id;
    }
    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }
    public void setType(Type type){
        this.type = type;
    }
    public void setUpdated(long updated) {
        this.updated = updated;
    }
    public void setUpdated(LocalDateTime updated){
        this.updated = updated.toEpochSecond(ZoneOffset.UTC);
    }
    @Override
    public String toString() {
        return "Attempt{" +
                "id= " + id +
                ", parent_id= " + parent_id +
                ", heading='" + heading + '\'' +
                ", description='" + description + '\'' +
                ", comment='" + comment + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", grade=" + grade +
                ", state=" + state +
                '}';
    }



    public void addAssignment(Assignment currentAssignment) {
    }
}
