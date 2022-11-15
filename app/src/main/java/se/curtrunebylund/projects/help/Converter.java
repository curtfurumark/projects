package se.curtrunebylund.projects.help;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import se.curtrunebylund.projects.projects.Task;
import se.curtrunebylund.projects.music.Attempt;
import se.curtrunebylund.projects.music.Grade;

public class Converter {
    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATE__FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String TIME_FORMAT_PATTERN = "HH:mm";


    public static List<Attempt> convertTasks(List<Task> tasks){
        List<Attempt> attempts = new ArrayList<>();
        for( Task task: tasks){
            Attempt attempt = new Attempt(task.getHeading());
            attempt.setDescription(task.getDescription());
            attempt.setComment(task.getComment());
            attempt.setParent_id(task.getParentId());
            attempt.setCreated(task.getDateCreated().atStartOfDay().toEpochSecond(ZoneOffset.UTC));
            attempt.setUpdated(task.getUpdated().toEpochSecond(ZoneOffset.UTC));
            attempt.setState(task.getState());
            attempt.setGrade(Grade.PENDING);
            attempts.add(attempt);
        }
        return attempts;
    }


    public static String format(LocalDateTime updated) {
        if (updated == null) {
            return "";
        }
        return updated.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT_PATTERN));
    }
    /**
     *
     * @param dateTime
     * @return hours and minutes if same day, full format otherwise
     */
    public static String formatUI(LocalDateTime dateTime) {
        if(dateTime.toLocalDate().equals(LocalDate.now())){
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return dateTime.format(DateTimeFormatter.ofPattern("E d/M - y"));
        //return dateTime.format(DateTimeFormatter.ofPattern("yy:MM:dd"));
    }

    public static String formatMilliSeconds(long msecs){
            int secs =(int) (msecs / 1000) / 60;
            int mins =(int) (msecs / 1000) % 60;
            return  String.format("%02d:%02d", secs, mins);
    }
    public static String formatSeconds(int secs) {
        System.out.println("Converter.formatSeconds(int) " +secs);
        int isecs =  secs % 60;
        int mins =   (secs % 3600 )/ 60;
        return  String.format("%02d:%02d", mins , isecs );    }
}
