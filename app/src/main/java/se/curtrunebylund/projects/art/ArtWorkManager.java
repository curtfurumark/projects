package se.curtrunebylund.projects.art;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.curtrunebylund.projects.Debug;


public class ArtWorkManager {
    public static final String INTENT_NEW_ARTWORK = "intent_new_art_work" ;
    public static final String INTENT_ARTWORK_DESCRIPTON = "intent_artwork_description";
    public static final String INTENT_URI = "intent_uri";

    public static final String INTENT_ARTWORK_INDEX = "intent_artwork_index" ;
    private static final String JSON_FILE_NAME = "/works.json" ;
    private List<ArtWork> works;

    public boolean delete(ArtWork current_artWork) {
        Debug.log("ArtWorkManager.delete(ArtWork)");
        boolean res = works.remove(current_artWork);
        save();
        return res;
    }


    public enum SortOrder{
        ASCENDING_UPDATE, DESCENDING_UPDATE, ASCENDING_TITLE
    }
    private SortOrder sortOrder = SortOrder.ASCENDING_UPDATE;
    public static Uri uri;
    private Context context;
    private static ArtWorkManager instance;
    private ArtWorkManager(Context context){
        Debug.log("ArtWorkManager ctor");
        this.context = context;
        //this.works = deserialize(FILE_NAME, context);
        try {
            this.works = fromJson(JSON_FILE_NAME);
            sort(SortOrder.DESCENDING_UPDATE);
            Debug.log("got works from json " + works.size());
            //Debug.showMessage(context, "got works from json " + works.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ( works == null) {
            this.works = new ArrayList<>();
        }
    }
    public static ArtWorkManager getInstance(Context context){
        if ( instance == null){
            instance = new ArtWorkManager(context);
        }
        return instance;
    }
    public void add(ArtWork artWork){
        Debug.log("ArtWorkManager.add(ArtWork) " + artWork.toString());
        works.add(artWork);
    }
    public void clear() {
        works.clear();
    }

    /**
     *
     * @param artWork_id
     * @return null if not found
     */
    public ArtWork getArtWorkID(int artWork_id) {
        for( ArtWork artWork: works){
            if ( artWork.getId() == artWork_id){
                return artWork;
            }
        }
        return null;
    }
    public List<String> getImagePaths() {
        Debug.log("ArtWorkManager.getImagePaths()");
        List<String> paths = new ArrayList<>();
        for(ArtWork artWork: works){
            paths.add(artWork.getImagePath());
        }
        return paths;
    }

    public List<ArtWork> getWorks(){
        Debug.log("ArtWorkManager.getWorks: size " + works.size());
        /*for(ArtWork work: works){
            String str = String.format("%s path %s", work.getDescription(), work.getImagePath());
        }

         */
        sort(SortOrder.DESCENDING_UPDATE);
        return works;
    }

    public Integer getNextID(){
        return works.size() + 1;
    }



/**
    public static List<ArtWork> deserialize(String file_name, Context context){
        System.out.println("Persist.deserialize(): " + file_name) ;
        List<ArtWork> works;
        try {
            file_name = context.getFilesDir() + "/" + file_name;
            Debug.log("file name: " + file_name);
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file_name));
            works = (List<ArtWork>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException | RuntimeException e  ) {
            Debug.log("exception: " + e.toString());
            works = null;
        }
        return works;
    }

    public void deleteDev() {
    }
*/
    public void delete(int index) {
        works.remove(index);
    }

    private List<ArtWork> fromJson(String file_name) throws IOException {
        Debug.log("ArtWorkManager.fromJson() " + file_name);
        //FileInputStream fileInputStream = new FileInputStream(file_name);
        //fileInputStream.read();
        file_name = context.getFilesDir() +file_name;
        String json = new String(Files.readAllBytes(Paths.get(file_name)));
        Debug.log("JSON FROM FILE " + json);
        Gson gson = new Gson();
        ArtWork[] worksArray = gson.fromJson(json, ArtWork[].class);
        return new ArrayList<>(Arrays.asList(worksArray));
    }

    public int getCount() {
        return works.size();
    }

    public ArtWork getArtWork(int index) {
        return works.get(index);
    }

    private List<ArtWork> load() throws IOException {
        return fromJson(JSON_FILE_NAME);
    }
    public void save() {
        Debug.log("ArtWorkManager.save()");
        //serialize(works, FILE_NAME, context);
        try {
            toJson(works, JSON_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ArtWork> sort(SortOrder sort_order){
        Debug.log(String.format("ArtWorkManager.sort(%s)", sortOrder.toString()));
        switch (sort_order){
            case DESCENDING_UPDATE:
                Collections.sort(works, new Comparator<ArtWork>() {
                    public int compare(ArtWork aw1, ArtWork aw2){
                        return Long.compare(aw1.compare(), aw2.compare());
                    }
                });
                break;
            case ASCENDING_UPDATE:
                Collections.sort(works, new Comparator<ArtWork>() {
                    public int compare(ArtWork aw1, ArtWork aw2){
                        return Long.compare(aw2.compare(), aw1.compare());
                    }
                });
                break;
            case ASCENDING_TITLE:
                Collections.sort(works, new Comparator<ArtWork>() {
                    public int compare(ArtWork aw1, ArtWork aw2){
                        return aw1.getDescription().compareTo(aw2.getDescription());
                    }
                });
                break;
        }
        return works;
    }

    private  void toJson(List<ArtWork> works, String file_name) throws IOException {
        Debug.log("ArtWorkManager.toJson()");
        Gson gson = new Gson();
        String json = gson.toJson(works);
        Debug.log("JSON: " + json);
        file_name = context.getFilesDir() + file_name;
        FileOutputStream fileOutputStream =new FileOutputStream(file_name);
        PrintStream printStream = new PrintStream(fileOutputStream);
        printStream.print(json);
        printStream.close();
        Debug.log("....end of toJson()");
    }




}
