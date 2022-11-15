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


public class GsonChoreState implements JsonDeserializer<ChoreBase.State> {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public ChoreBase.State deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Debug.log("GsonChoreState.deserialize()");
        int state_index = 0;
        try{
            state_index = json.getAsInt();
            Debug.log("state_index " + state_index);
        }catch(Exception e){
            Debug.log("GsonChoreState.deserialize exception: " + e.toString());
        }
        return ChoreBase.State.values()[state_index];
    }
}
