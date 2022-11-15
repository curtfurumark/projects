package se.curtrunebylund.projects.db;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.help.Converter;
import se.curtrunebylund.projects.projects.State;


@RequiresApi(api = Build.VERSION_CODES.O)
public class HTTPPost {
    private Map<String, String> postPairs = new HashMap<>();
    private String url;

    @Override
    public String toString() {
        return "HTTPPost{" +
                "postPairs=" + postPairs +
                ", url='" + url + '\'' +
                '}';
    }

    public HTTPPost(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * if value is null, it is translated into an empty string ""
     * @param key
     * @param value
     */
    public void add(String key, String value) {
        if (value == null){
            value = "";
        }
        postPairs.put(key, value);
    }
    public void add(String name, float value){
        postPairs.put(name, String.valueOf(value));
    }
    public void add(String name, int value){
        postPairs.put(name, String.valueOf(value));
    }
    public void add(String key, long value){postPairs.put(key, String.valueOf(value));}

    public String toPostString() throws UnsupportedEncodingException {
        //Debug.log(  "HTTPPost.toPostString()");
        return urlEncode(postPairs);
    }

    public static String urlEncode(Map<String, String> map) throws UnsupportedEncodingException {
        int i = 0;
        String postValues = "";
        for (Map.Entry<String, String> pair : map.entrySet()) {
            //System.out.println(pair.getKey() + " " + pair.getValue());
            String key = URLEncoder.encode(pair.getKey(), "UTF-8");
            String value = URLEncoder.encode(pair.getValue(), "UTF-8");
            postValues += key + "=" + value;
            if (map.size() > ++i) {
                postValues += "&";
            }
        }
        Debug.log("HTTPPost.urlEncode()...postString: " + postValues);
        return postValues;
    }

    public void add(String key, LocalTime targetTime) {
            postPairs.put(key, targetTime != null? targetTime.format(DateTimeFormatter.ofPattern(Converter.TIME_FORMAT_PATTERN)):"");
    }

    public void add(String key, LocalDate target_date) {
        if( target_date == null){
            Debug.log("HTTPPost.add(String, LocalDate) called with null date");
        }
        postPairs.put(key, target_date != null? target_date.format(DateTimeFormatter.ofPattern(Converter.DATE__FORMAT_PATTERN)): "");
    }

    public void add(String key, State state1) {

        postPairs.put(key, state1 != null ? state1.toString(): "");
    }

    public void add(String key, LocalDateTime date_time) {
        postPairs.put(key, Converter.format(date_time));
    }
}
