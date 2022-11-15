package se.curtrunebylund.projects.music;

import java.util.List;

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
