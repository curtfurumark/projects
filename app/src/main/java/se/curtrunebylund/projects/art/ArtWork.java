package se.curtrunebylund.projects.art;

import android.content.ContentValues;
import android.content.Context;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import se.curtrunebylund.projects.Debug;

public class ArtWork implements Serializable {
    private String url;
    private String imagePath;
    private String image_directory;
    private String imageFileName;
    private String thumbNailFileName;
    private String thumbNailPath;
    private String tags;
    private String description;
    private List<Task> tasks;
    private List<String> pictures_paths;
    private int id;
    private long created;
    private long updated;
    private long source_date;

    public LocalDate getSourceDate() {
        return LocalDate.ofEpochDay(source_date);
    }

    public ArtWork() {

        this.tasks = new ArrayList<>();
        this.pictures_paths = new ArrayList<>();
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public void addRefPicturePath(String imagePath) {
        Debug.log("Artwork.addRefPicturePath(String): " + imagePath);
        this.pictures_paths.add(imagePath);
    }

    public long compare() {
        return updated * -1;
    }
    public String debug() {
        return "ArtWork{" +
                " imagePath='" + imagePath + '\'' +
                ", thumbNailPath=" + thumbNailPath + '\'' +
                ", description='" + description + '\'' +
                ", ref pic paths: " + pictures_paths +
                ", id=" + id +
                '}';
    }

    public boolean contains(String string) {
        return (description + tags).toLowerCase(Locale.ROOT).contains(string.toLowerCase(Locale.ROOT));
    }
    public LocalDate getCreated(){
        return LocalDate.ofEpochDay(created);
    }
    public void setCreated(LocalDate localDate){
        this.created = localDate.toEpochDay();
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    /**
     *
     * @return image path to full size picture or empty string if not availble
     */
    public String getImagePath() {
        return imagePath;
    }
    public String getImageDirectory(){
        return image_directory;
    }

    public List<String> getRefPicturePaths() {
        return this.pictures_paths;
    }

    public String getRefPictureFilePath(Context context) {
        Debug.log("ArtWork.getRefPicFilePath(Context)");
        String path = String.format("%s/aw_%d_refpic_%d.png", context.getFilesDir().toString(), id, this.pictures_paths.size() );
        return  path;
    }

    /**
     *
     * @return the name of the file, excluding path
     */
    public String getImageFileName(){
        return imageFileName;
    }
    public String getTags(){
        return tags;
    }

    /**
     *
     * @return the fully qualified path + file name
     * TODO, check out the correct way to name the various parts of a path + filename thingy
     */
    public String getThumbNailPath(){
        return thumbNailPath;
    }
    public String getThumbNailFileName(){
        return thumbNailFileName;
    }

    public List<Task> getTasks(){
        sortTasks();
        return  tasks;
    }

    public LocalDateTime getUpdated(){
        return LocalDateTime.ofEpochSecond(updated, 0 , ZoneOffset.UTC);
    }

    public boolean hasRefPictures(){
        return this.pictures_paths.size() > 0;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setUpdated(LocalDateTime updated){
        Debug.log("ArtWork.setUpdated(LocalDateTime)");
        this.updated = updated.toEpochSecond(ZoneOffset.UTC);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImageDirectory(String directory){
        this.image_directory = directory;
    }
    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public void setSourceDate(LocalDate sourceDate) {
        this.source_date = sourceDate.toEpochDay();
    }
    public void setTags(String tags){
        this.tags = tags;
    }
    public void setThumbNailFileName(String thumbNailFileName) {
        this.thumbNailFileName = thumbNailFileName;
    }
    public void setThumbNailPath(String thumbNailPath){
        this.thumbNailPath = thumbNailPath;
    }
    public void setURL(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ArtWork{" +
                "imagePath='" + imagePath + '\'' +
                "thumbNailPath" + thumbNailPath + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                '}';
    }

    /**
     * descending order
     */
    private void sortTasks(){
        Debug.log("ArtWorkd.sortTasks()");
        Collections.sort(tasks, new Comparator<Task>() {
            public int compare(Task task1, Task task2){
                return Long.compare(task2.compare(), task1.compare());
            }
        });
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        //private String url;
       // private String imagePath;
        cv.put("imagePath", imagePath);
        cv.put("imageFileName", imageFileName);
        cv.put("thumbNailFileName", thumbNailFileName);
        cv.put("thumbNailPath", thumbNailPath);
        cv.put("description", tags);
        //cv.put("tasks", tags);
        //cv.put("picture_paths", tags);
        cv.put("id", id);
        cv.put("created", created);
        cv.put("updated", updated);
        cv.put("source_date",source_date);
        return cv;
    }
}
