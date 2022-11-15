package se.curtrunebylund.projects.gson;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.db.ChoreBase;


public class GsonChoreType implements JsonDeserializer<ChoreBase.ChoreType> {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public ChoreBase.ChoreType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Debug.log("GsonChoreType.deserialize()");
        String string = json.getAsString().toUpperCase();
        int ordinal = json.getAsInt();
        Debug.log("...json string : " + string);
        Debug.log("...json as int, ie ordinal: " + ordinal);
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
        //ChoreBase.ChoreType choreType = ChoreBase.ChoreType.valueOf(string);
        ChoreBase.ChoreType choreType = ChoreBase.ChoreType.values()[ordinal];
        return choreType;
    }
}
