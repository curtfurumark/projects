package se.curtrunebylund.projects.projects;

public enum State {
    DONE, WIP, TODO, PENDING, FAILED, INFINITE, ABORTED;
    public static int getOrdinal(State state){
        return state.ordinal();
    }

    public static String[] toArray() {
        State[] states = State.values();
        String[] str_states = new String[State.values().length];
        for( int i = 0; i < State.values().length; i++){
            str_states[i] = State.values()[i].toString();
        }
        return str_states;
    }
}
