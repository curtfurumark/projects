package se.curtrunebylund.projects.classes;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;

import item.State;
import item.Type;

public class ListItem implements Serializable {
    private long id;
    private long parent_id;
    private long duration;
    private Type type = Type.PENDING;
    private State state = State.PENDING;

    private String description;
    private String heading;
    private String tags;
    private long created;
    private long updated;

    public Type getType() {
        return type;
    }





    public ListItem(Cursor cursor) {
        this.id = cursor.getLong(0);
        this.parent_id = cursor.getLong(1);
        this.heading = cursor.getString(2);
        this.description = cursor.getString(3);
        this.tags = cursor.getString(4);
        this.duration = cursor.getLong(5);
        this.created = cursor.getLong(6);
        this.updated = cursor.getLong(7);
        this.state = State.values()[cursor.getInt(8)];
        this.type = Type.values()[cursor.getInt(9)];
    }

    public ListItem(){
        this.updated = this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        this.state = State.TODO;
        this.parent_id = 0;
        this.duration = 0;
        this.type = Type.PENDING;
    }

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put("parent_id", parent_id);
        cv.put("heading", heading);
        cv.put("description", description);
        cv.put("tags", tags);
        cv.put("updated", updated);
        cv.put("created", created);
        cv.put("type", type.ordinal());
        cv.put("state", state.ordinal());
        return cv;
    }
    public LocalDateTime getCreated() {
        return LocalDateTime.ofEpochSecond(created,0, ZoneOffset.UTC);
    }

    public String getHeading() {
        return heading;
    }

    public Long getParentID() {
        return parent_id;
    }

    public Long getID() {
        return id;
    }
    public String getInfo() {
        return String.format(Locale.getDefault(),"%s id = %d", heading, id);
    }
    public String getDescription() {
        return description;
    }
    public String getTags(){
        return tags;
    }
    public long getCreatedEpoch() {
        return created;
    }
    public State getState(){
        return state;
    }

    public LocalDateTime getUpdated() {
        return LocalDateTime.ofEpochSecond(updated,0, ZoneOffset.UTC);
    }
    public void setHeading(String heading) {
        this.heading = heading;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setParentID(long parent_id) {
        this.parent_id = parent_id;
    }
    public void setID(long id) {
        this.id = id;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    public void setUpdated(long updated) {
        this.updated = updated;
    }
    public void setUpdated(LocalDateTime localDateTime){
        this.updated = localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setType(Type type){
        this.type = type;
    }
    @Override
    public String toString() {
        return "ListItem{" +
                "  id= " + id +
                " parent_id= " + parent_id +
                ", description='" + description + '\'' +
                ", heading='" + heading + '\'' +
                ", tags='" + tags + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", state=" + state +
                ", type= " + type +

                '}';
    }
    public void update() {
        this.updated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    public boolean contains(String text) {
        return (heading + description + tags).toLowerCase().contains(text.toLowerCase());
    }
}
