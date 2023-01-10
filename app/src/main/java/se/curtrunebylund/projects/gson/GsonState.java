package se.curtrunebylund.projects.gson;


import static logger.CRBLogger.log;
import static logger.CRBLogger.logException;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import item.State;


public class GsonState implements JsonDeserializer<State> {
    public boolean verbose = false;
    @Override
    public State deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if( verbose) log("GsonState.deserialize: jsonElement: " , jsonElement.getAsString());

        String stateString = jsonElement.getAsString();
        if (stateString == null || stateString.isEmpty() || stateString.equalsIgnoreCase("ALL" )||stateString.equalsIgnoreCase("high")) {
            stateString = "DONE";
        }
        return State.valueOf(stateString.toUpperCase());
    }
}
