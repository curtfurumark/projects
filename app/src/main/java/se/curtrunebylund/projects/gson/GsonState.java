package se.curtrunebylund.projects.gson;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.projects.State;


public class GsonState implements JsonDeserializer<State> {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public State deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Debug.log("GsonState.deserialize: jsonElement: " + jsonElement.getAsString() , Debug.DebugLevel.HIGH);

        String stateString = jsonElement.getAsString();
        try {
            int stateIndex = jsonElement.getAsInt();
            Debug.log("deserialize State, stateIndex: " + stateIndex, Debug.DebugLevel.HIGH);
        }catch (Exception e){
            Debug.log("exception deserialize State " + e.toString() + " dont worry", Debug.DebugLevel.HIGH);
        }
        if (stateString == null || stateString.isEmpty() || stateString.equalsIgnoreCase("ALL" )||stateString.equalsIgnoreCase("high")) {
            stateString = "DONE";
        }
        return State.valueOf(stateString.toUpperCase());
    }
}
