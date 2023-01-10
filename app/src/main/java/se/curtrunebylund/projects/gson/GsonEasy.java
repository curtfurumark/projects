package se.curtrunebylund.projects.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import item.State;
import se.curtrunebylund.projects.db.ChoreBase;


public class GsonEasy {
    public static Gson getGson()        {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new GsonDate());
        gsonBuilder.registerTypeAdapter(ChoreBase.ChoreType.class, new GsonChoreType());
        gsonBuilder.registerTypeAdapter(ChoreBase.State.class, new GsonChoreState());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new GsonDateTime());
        gsonBuilder.registerTypeAdapter(LocalTime.class, new GsonTime());
        gsonBuilder.registerTypeAdapter(State.class, new GsonState());
        return gsonBuilder.create();
    }
}
