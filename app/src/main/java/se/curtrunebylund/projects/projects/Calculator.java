package se.curtrunebylund.projects.projects;

import java.util.List;

import se.curtrunebylund.projects.classes.Attempt;

public class Calculator {
    public static int getOffset(List<Attempt> attemps){
        int offset = 0;
        for(Attempt attempt: attemps){
            if( attempt.getHeading().toLowerCase().contains("attempt")){
                offset++;
            }
        }
        return offset;
    }
}
