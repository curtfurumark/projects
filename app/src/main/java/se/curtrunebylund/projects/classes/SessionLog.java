package se.curtrunebylund.projects.classes;

import com.google.gson.Gson;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SessionLog {
    private long created;
    private long total_time;
    private long session_id;
    private List<Assignment> assignments = new ArrayList<>();

    public SessionLog(long session_id) {
        this.session_id = session_id;
        this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
    public void add(Assignment assignment){
        assignments.add(assignment);
    }
    public String toJson(){
        return new Gson().toJson(this);
    }
}
