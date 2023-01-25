package se.curtrunebylund.projects.projects;

import java.util.List;

import se.curtrunebylund.projects.classes.Session;

public class Calculator {
    public static int getOffset(List<Session> attemps){
        int offset = 0;
        for(Session session : attemps){
            if( session.getHeading().toLowerCase().contains("attempt")){
                offset++;
            }
        }
        return offset;
    }
}
