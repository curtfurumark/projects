package se.curtrunebylund.projects.gson;

import static logger.CRBLogger.log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import se.curtrunebylund.projects.db.ChoreBase;
import se.curtrunebylund.projects.util.Debug;


public class GsonChoreType implements JsonDeserializer<ChoreBase.ChoreType> {
    public boolean verbose = false;
    @Override
    public ChoreBase.ChoreType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if( verbose) log("GsonChoreType.deserialize()");
        String string = json.getAsString().toUpperCase();
        int ordinal = json.getAsInt();
        if( verbose) log("...json string : " , string);
        if( verbose) log("...json as int, ie ordinal: " + ordinal);
        if(string.equals("-1")){
            string = "TASK";
            ordinal = 9;
        }
        if ( string.equals("9")){
            string = "TASK";
        }
        if( string.equals("10")){
            string = "URL";
        }
        return ChoreBase.ChoreType.values()[ordinal];
    }
}
