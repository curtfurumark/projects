package se.curtrunebylund.projects.db;

import java.util.HashMap;
import java.util.Map;

public class Result {
    protected String php_result;
    private Map<String,String> entries = new HashMap<>();
    public Result(String php_result) {
        this.php_result = php_result;
        entries = parse(php_result);
    }

    private  String getKeyValuePair(String str, String key){
        String keyValue = "";
        int start_index = str.indexOf(key);
        if (start_index != -1) {
            int end_index = str.indexOf("|", start_index);
            if ( end_index == -1){
                System.out.println("syntax error missing end of value delimiter for key: " + key);
            }else {
                keyValue = str.substring(start_index, end_index);
                System.out.println("keyValue: " + keyValue);
            }
        }else{
            System.out.println("key: " + key + "not found");
        }
        return keyValue;
    }
    private Map<String, String> parse(String enchilada){
        System.out.println("ResultV2.parse() " + enchilada);
        Map<String, String > map = new HashMap<>();
        String[] pairs = enchilada.split("\\|");
        for(String pair: pairs){
            String[] one_entry = pair.split(":");
            String key = one_entry[0];
            String value = one_entry[1];
            if( map.containsKey(key)){
                value = value + ", " +  map.get(key);
            }
            map.put(key, value);
        }
        return map;
    }

    public  String  getValueAsString(String keyValue){
        int start = keyValue.indexOf(":");
        String value = keyValue.substring(start + 1);
        return value;
    }

    public  int getValueAsInt(String keyValue){
        //System.out.println("ResultV2.getValueAsInt");
        String value = getValueAsString(keyValue);
        return Integer.parseInt(value);
    }


    public String getPHPResult(){
        return php_result;
    }

    public void debug(){
        for( String key: entries.keySet()){
            System.out.println("..." +  key + ": " + entries.get(key));
        }
    }
    public boolean isOK(){
        return php_result.contains("OK:");
    }
    public int getID(){
        String keyValue = getKeyValuePair(php_result, "ID:");
        return getValueAsInt(keyValue);
    }
}
