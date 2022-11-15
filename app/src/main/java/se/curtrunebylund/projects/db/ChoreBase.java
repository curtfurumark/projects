package se.curtrunebylund.projects.db;

import android.content.ContentValues;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import se.curtrunebylund.projects.Debug;


@RequiresApi(api = Build.VERSION_CODES.O)
public abstract class ChoreBase {
    protected LocalTime target_time;
    protected LocalDate target_date;
    protected int project_id;
    protected State state;
    protected boolean tmp = false;
    protected int energy;
    protected int id = -1;
    protected int parent_id = -1;
    protected LocalTime notification_time;
    protected LocalDateTime updated;
    protected LocalDateTime created;
    protected List<ChoreBase> childList = new ArrayList<>();

    protected String heading;
    protected String description;
    protected String comment;
    protected String tags;
    protected ChoreType choreType;

    //public  int  getProjectID(){
    //    return Jeeves.DEFAULT_ARCHIVE_CHORES_PROJECT_ID;
    //}

    public boolean contains(String text) {
        return (heading + description + comment + tags).contains(text);
    }


    public enum State{
        TODO, DONE, WIP;
        public static  String[] toArray(){
            String[] array = new String[State.values().length];
            for(int i = 0; i < State.values().length; i++) {
                array[i] = State.values()[i].toString();
            }
            return array;
        }
    }
    public enum ChoreType{
        DAILY, WEEKLY, MONTHLY, YEARLY, APPOINTMENT, SHOPPING, TODO, REMINDER, EVERYTHING, TASK, URL;
    }
    //@SerializedName("state")


    public ChoreBase(){
        Debug.log("ChoreBase() default ctor", Debug.DebugLevel.HIGH);
        created = LocalDateTime.now();
        updated = created;
        state = State.TODO;
        tmp = false;
    }
    public ChoreBase(ContentValues contentValues){
        Debug.log("ChoreBase(ContentValues) ctor", Debug.DebugLevel.HIGH);
        this.id =contentValues.getAsInteger("id");
        this.parent_id = contentValues.getAsInteger("parent_id");
        this.description = contentValues.getAsString("description");
        this.heading = contentValues.getAsString("heading");
        this.comment = contentValues.getAsString("comment");
        this.tags = contentValues.getAsString("tags");
        this.setCreated(contentValues.getAsInteger("created"));
        this.setUpdated(contentValues.getAsInteger("updated"));
        this.setTargetTime(contentValues.getAsInteger("target_time"));
        this.setTargetDate(contentValues.getAsInteger("target_date"));
        this.setTmp(contentValues.getAsInteger("tmp"));
        this.setType(contentValues.getAsInteger("type"));
        this.setState(contentValues.getAsInteger("state"));
    }
    public ChoreBase addChild(ChoreBase child) {
        if(null == childList){
            childList = new ArrayList<>();
        }
        child.setParentID(id);
        childList.add(child);
        return child;
    }
    public  void addChildren(List<ChoreBase> children){
        childList.addAll(children);
    }
    public abstract long compare();
    public abstract String debug();
    public boolean allChildrenDone(){
        for ( ChoreBase child: childList){
            if (!child.isDone()){
                return false;
            }
        }
        return true;
    }

    public String getComment(){
        return this.comment;
    }
    /**
     * map used by SQLite, which fields, and corresponding values
     * @return
     */
    public  ContentValues getContentValues() {
        Debug.log("ChoreBase.getContentValues()", Debug.DebugLevel.MEDIUM);
        ContentValues contentValues = new ContentValues();
        //contentValues.put("id", id);
        contentValues.put("parent_id", parent_id);
        contentValues.put("description", description);
        contentValues.put("comment", comment);
        contentValues.put("tags", tags);
        contentValues.put("created", created.toEpochSecond(ZoneOffset.UTC));
        contentValues.put("updated", updated.toEpochSecond(ZoneOffset.UTC));
        contentValues.put("target_time", target_time != null? target_time.toSecondOfDay():-1);
        contentValues.put("target_date", target_date != null? target_date.toEpochDay(): -1);
        contentValues.put("tmp", (tmp? 0: 1));
        contentValues.put("type", this.getType().ordinal());
        contentValues.put("state", this.getState().ordinal());
        return contentValues;
    }

    public LocalDateTime getCreated(){
        return created;
    }
    public String getDescription() {
        return description;
    }

    public abstract Class<?> getEditorClass();
    public State getState() {
        return state;
    }
    public abstract ChoreType getType();

    public String getTags(){
        return this.tags;
    }


    public LocalTime getNotificationTime() {
        return notification_time;
    }
    public  long getParentID(){
        return parent_id;
    }
    public Integer getID(){
        return id;
    }
    public abstract String getHeading();
    public LocalDate getTargetDate(){
        return this.target_date;
    }
    public LocalTime getTargetTime(){
        return target_time;
    }
    public abstract String getInfo();

    public LocalDateTime getTimeFinished() {
        return state.equals(State.DONE)? updated: null;
    }
    public LocalDateTime getUpdated(){
        return updated;
    }
    public  boolean hasNotification(){
        return null != notification_time;
    }
    public boolean isChild(){
        return parent_id > 0;
    }
    public boolean isDone(){
        Debug.log("ChoreBase.isDone() state = "  + state.toString(), Debug.DebugLevel.HIGH);
        return state.equals(State.DONE);
    }
    public  boolean isTemp(){
        return tmp;
    }
    public void setComment(String comment){
        this.comment = comment;
    }
    public void setCreated(LocalDateTime created){
        this.created = created;
    }
    public  void setCreated(int created){
        this.created = LocalDateTime.ofEpochSecond(created, 0 , ZoneOffset.UTC);
    }

    public void setTargetDate(long epoch){
        this.target_date = LocalDate.ofEpochDay(epoch);
    }
    public void setTargetDate(LocalDate target_date){
        this.target_date = target_date;
    }
    public void setTargetTime(LocalTime target_time){
        this.target_time = target_time;
    }
    public void setTargetTime(long epoch){
        if ( epoch == -1){
            this.target_time = null;
        }else {
            this.target_time = LocalTime.ofSecondOfDay(epoch);
        }
    }
    public void setTargetTime(int hour, int minute) {
        this.target_time = LocalTime.of(hour, minute);
    }
    public  void setDone(boolean done){
        Debug.log("ChoreBase.setDone() " + (done? "DONE": "FALSE"), Debug.DebugLevel.HIGH);
        if (done) {
            this.state = State.DONE;
        }else{
            this.state = State.TODO;
        }
    }

    public  void setParentID(int parent_id){
        this.parent_id = parent_id;
    }


    protected  void setState(Integer state_index){
        Debug.log("ChoreBase.setState(Integer)" + state_index, Debug.DebugLevel.HIGH);
        this.state = State.values()[state_index];

    }
    public  void setTags(String tags){
        this.tags = tags;
    }
    public  void setTmp(int tmp){
        this.tmp = tmp != 0;
    }
    public void setTmp(boolean tmp){
        this.tmp = tmp;
    }
    protected  void setType(Integer type_index){
        Debug.log("ChoreBase.setType(Integer)" + type_index, Debug.DebugLevel.HIGH);
        this.choreType = ChoreType.values()[type_index];
    }
    public  void setType(ChoreType choreType){
        this.choreType = choreType;
    }
    public void setDescription(String description) {
        Debug.log("ChoreBase.setDescripton()", Debug.DebugLevel.HIGH);
        this.description = description;
    }
    public void setState(State state){
        Debug.log("ChoreBase.setState() state= " + state, Debug.DebugLevel.HIGH);
        this.state = state;
        //this.updated = LocalDateTime.now();
    }
    public  void setID(int id){
        Debug.log("ChoreBase.setID() ID= " + id, Debug.DebugLevel.HIGH);
        this.id = id;
    }
    public void setNotificationTime(LocalTime notificationTime) {
        this.notification_time = notificationTime;
    }

    public void setUpdated(LocalDateTime updated){
        Debug.log("ChoreBase.setUpdated(): " + updated.toString(), Debug.DebugLevel.HIGH);
        this.updated = updated;
    }

    public void setUpdated(int epochDateTime){
        Debug.log("ChoreBase.setUpdated(epochDateTime)"  + epochDateTime, Debug.DebugLevel.HIGH);
        this.updated = LocalDateTime.ofEpochSecond(epochDateTime, 0, ZoneOffset.UTC);
        //Debug.log("\tupdated: " + updated.toString());
    }
    public boolean isPersist() {
        return tmp;
    }

    public void setPersist(boolean persist) {
        this.tmp = persist;
    }


    public void setTimeFinished(LocalDateTime timeFinished) {
        this.updated = timeFinished;
    }

    public void setEnergyLevel(int energy) {
        Debug.log("ChoreBase.setEnergyLevel() " + energy, Debug.DebugLevel.HIGH);
        this.energy = energy;
    }

    public int getEnergyLevel() {
        if (null != childList && childList.size() > 0){
            for(ChoreBase child: childList){
                energy += child.energy;
            }
        }
        return energy;
    }

    public boolean hasChildren() {
        return (null != childList && 0 < childList.size());
    }

    public List<ChoreBase> getChildren() {
        return childList;
    }
    public boolean hasUnfinishedChildren(){
        Debug.log("ChoreBase.hasUnfinishedChildren()", Debug.DebugLevel.HIGH);
        if ( null == childList){
            return false;
        }
        for( ChoreBase child: childList){
            if ( !child.getState().equals(State.DONE)){
                return true;
            }
        }
        return true;
    }
}
