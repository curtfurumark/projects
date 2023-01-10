package se.curtrunebylund.projects.gson;

import static logger.CRBLogger.log;
import static logger.CRBLogger.logException;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import se.curtrunebylund.projects.db.ChoreBase;
import se.curtrunebylund.projects.util.Debug;


public class GsonChoreState implements JsonDeserializer<ChoreBase.State> {
    public boolean verbose = false;
    @Override
    public ChoreBase.State deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (verbose) log("GsonChoreState.deserialize()");
        int state_index = 0;
        try{
            state_index = json.getAsInt();
            if ( verbose) log("state_index " + state_index);
        }catch(Exception e){
            logException(e);
        }
        return ChoreBase.State.values()[state_index];
    }
}
