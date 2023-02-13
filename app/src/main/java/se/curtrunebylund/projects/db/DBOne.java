package se.curtrunebylund.projects.db;

import static logger.CRBLogger.log;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import classes.Project;
import classes.Task;
import gson.GsonEasy;
import persist.Urls;
import se.curtrunebylund.projects.classes.Session;
import se.curtrunebylund.projects.util.Debug;
import util.Converter;


@RequiresApi(api = Build.VERSION_CODES.O)
public class DBOne {
    private static final String ADD_APPOINTMENT_URL = "https://curtfurumark.se/projects/add_appointment.php";
    private static final String ADD_PROJECT_URL ="https://curtfurumark.se/projects/add_project.php" ;
    public static final String ADD_TASK_URL = "https://curtfurumark.se/projects/add_task.php";
    public static final String ADD_DIARY_URL = "https://curtfurumark.se/diary/add_diary_android.php";
    private static final String DELETE_TASK_URL = "https://curtfurumark.se/projects/delete_task.php";
    public static final String DELETE_PROJECT_URL = "https://curtfurumark.se/projects/delete_project.php";
    //TODO, check and rename
    private static final String GET_APPOINTMENT_URL ="https://curtfurumark.se/projects/get_project.php";
    public static final String GET_PROJECTS_URL = "https://curtfurumark.se/projects/get_projects.php";
    public static final String GET_TASKS_URL = "https://curtfurumark.se/projects/get_tasks.php";
    public static final String UPDATE_PROJECT_URL = "https://curtfurumark.se/projects/update_project.php";
    private static final String UPDATE_PROJECT_LAST_UPDATE_URL = "https://curtfurumark.se/projects/update_project_last_update.php";
    public static final String UPDATE_TASK_URL = "https://curtfurumark.se/projects/update_task.php";
    private static final String GET_PROJECTS_BY_TAG_URL = "https://curtfurumark.se/projects/get_projects_by_tag.php";
    private static final String GET_TASK_CHILDREN_URL = "https://curtfurumark.se/projects/get_task_children.php";
    private static final String GET_ALL_TASK_CHILDREN_URL = "https://curtfurumark.se/projects/get_all_task_children.php";
    private static final String GET_TASK_URL = "https://curtfurumark.se/projects/get_task.php";
    //private static final String UPDATED_PROJECT_TASK_UPDATED_URL ="https://curtfurumark.se/projects/update_date_time_project_task.php" ;


    public static Task getTask(long id) throws Exception {
        Debug.log("DBOne.getTask(long id) id ="+  id);
        HTTPPost httpPost = new HTTPPost(GET_TASK_URL);
        httpPost.add("task_id", id);
        String res = post(httpPost);
        Debug.log("res: " + res);
        if( !res.startsWith("{")){
            throw new Exception(res);
        }
        Gson gson = GsonEasy.getGson();
        Task task = gson.fromJson(res, Task.class);
        return task;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Result add(Project project)  {
        Debug.log(  "MySQL.add(Project)" + ADD_PROJECT_URL);
        HTTPPost httpPost = new HTTPPost(ADD_PROJECT_URL);
        httpPost.add("heading", project.getHeading());
        httpPost.add("description", project.getDescription());
        httpPost.add("comment", project.getComment());
        httpPost.add("state", project.getState());
        httpPost.add("target_date", project.getTargetDate());
        httpPost.add("tags", project.getTags());
        Debug.log(  httpPost.toString());
        return new Result( post(httpPost));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    /**
     * php updates parent project "last_update"
     */
    public static Result add(Task task) {
        Debug.log(  "DBOne.add(Task)");
        HTTPPost httpPost = new HTTPPost(ADD_TASK_URL);
        httpPost.add("parent_id",task.getProjectID());
        httpPost.add("parent_task_id", task.getParentId());
        httpPost.add("updated", task.getUpdated());
        httpPost.add("updated", task.getUpdated());
        httpPost.add("target_date", task.getTargetDate());
        httpPost.add("target_time", task.getTargetTime());
        httpPost.add("state", task.getState().toString());
        httpPost.add("tags", task.getTags());
        httpPost.add("created", task.getDateCreated());
        httpPost.add("type", task.getType().ordinal());
        httpPost.add("heading", task.getHeading());
        httpPost.add("description", task.getDescription());
        httpPost.add("comment", task.getComment());
        //return new Result("OK:" + httpPost.toString());
        return new Result(post(httpPost));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Result deleteTask(int id) {
        Debug.log("MySQLDB.deleteTask(): id=" + id);
        HTTPPost httpPost = new HTTPPost(DELETE_TASK_URL);
        httpPost.add("id", String.valueOf(id));
        return new Result( post(httpPost));
    }



    public static Result delete(Project project, boolean delete_child_tasks) throws IOException {
        Debug.log(  "DBOne.deleteProject id:" + project.getId() +  ", delete child tasks" + delete_child_tasks);
        HTTPPost httpPost = new HTTPPost(DELETE_PROJECT_URL);
        httpPost.add("id",project.getId());
        httpPost.add("delete_tasks", delete_child_tasks ? 1: 0);
        return new Result(post(httpPost));
    }

    /**
     * gets all tasks that are children to a task ie grandchildren
     * @return
     * @throws Exception
     */
    public static List<Task> getAllGrandChildren() throws Exception {
        Debug.log("DBOne.getAllGrandChildren()");
        HTTPPost httpPost = new HTTPPost(GET_ALL_TASK_CHILDREN_URL);
        String res = post((httpPost));
        Gson gson = GsonEasy.getGson();
        if ( !res.startsWith("[")){
            throw new Exception(res);
        }
        Task[] taskArray = gson.fromJson(res, Task[].class);
        return new ArrayList<>(Arrays.asList(taskArray));
        
    }

    public static Project getProject(Integer id) throws Exception {
        Debug.log("DBOne.getProject(id)");
        Result result = null;
        HTTPPost httpPost = new HTTPPost(GET_APPOINTMENT_URL);
        httpPost.add("id", id.toString());
        String res = post(httpPost);
        if ( !res.startsWith("{")){
            throw new Exception(res);
        }
        Gson gson = GsonEasy.getGson();
        return gson.fromJson(res, Project.class);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Project> getProjects() throws Exception {
        Debug.log("DBOne.getProjects()");
        HTTPPost httpPost = new HTTPPost(GET_PROJECTS_URL);
        httpPost.add("state", "all");
        httpPost.add("tags", "all");
        String res = post(httpPost);
        if (! res.startsWith("[{")){
            Debug.log("res not json, throwing an exception");
            throw new Exception(res);
        }
        Gson gson = GsonEasy.getGson();
        Project[] projectsArray = gson.fromJson(res, Project[].class);
        return new ArrayList<>(Arrays.asList(projectsArray));
    }
    public static List<Project>getProjectsByTag(String tag) throws Exception {
        Debug.log("DBOne.getProjectsByTag(String)");
        HTTPPost httpPost = new HTTPPost(GET_PROJECTS_BY_TAG_URL);
        httpPost.add("tag", tag);
        String res = post(httpPost);
        if( !res.startsWith("[")){
            System.out.println("...res: " + res);
            throw new Exception(res);
        }
        Gson gson = GsonEasy.getGson();
        Project[] project_array = gson.fromJson(res, Project[].class);
        return new ArrayList<>(Arrays.asList(project_array));
        //return null;
    }

    /**
     * gets all children to specified parentTask
     * @param parent_task_id
     * @return
     * @throws Exception
     */
    public static List<Task> getTaskChildren(Integer parent_task_id) throws Exception {
        Debug.log("DBOne.getTaskChildren() parent_task_id: " + parent_task_id);
        HTTPPost httpPost = new HTTPPost(GET_TASK_CHILDREN_URL);
        httpPost.add("parent_task_id", parent_task_id.toString());
        String json = post(httpPost);
        if (! json.startsWith("[")){
            Debug.log("res not json, throwing an exception");
            throw new Exception(json);
        }
        Gson gson = GsonEasy.getGson();
        Task[] taskArray = gson.fromJson(json, Task[].class);
        return new ArrayList<>(Arrays.asList(taskArray));
    }
    public static List<Task>  getTasks(String parent_id) throws Exception {
        Debug.log(String.format("DataBaseOne.getTasks(%s)", parent_id));
        HTTPPost httpPost = new HTTPPost(GET_TASKS_URL);
        httpPost.add("parent_id", parent_id);
        String result = post(httpPost);
        if (!result.startsWith("[")){
            Debug.log("result not json throwing an exception");
            throw new Exception("ERROR:" + result);
        }
        Debug.log( result);
        Gson gson = GsonEasy.getGson();
        Task[]  task_array = gson.fromJson(result, Task[].class);
        Debug.log("getTask, task_list size" + task_array.length);
        return new ArrayList<Task>(Arrays.asList(task_array));
    }

    public static void persistSessions(List<Session> sessions){
        //TODO implement in intellij onedotcom
    }

    /**
     *
     * @param project
     * @param task
     * @return
     */
    public static String touch(Project project, Task task)  {
        log("DBOne.touch(Project project, Task task)");
        HTTPPost httpPost = new HTTPPost(Urls.PROJECTS_TOUCH_PROJ_AND_TASK);
        httpPost.add("project_id", project.getId());
        httpPost.add("task_id", task.getId());
        httpPost.add("updated", LocalDateTime.now());
        return post(httpPost);
    }

    public static String post(HTTPPost httpPost){
        Debug.log(  "DBOne.post() " + httpPost.toString());
        String result = "";
        try {
            URL url = new URL(httpPost.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(httpPost.toPostString());
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = e.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            result = e.toString();
        } catch (ProtocolException e) {
            e.printStackTrace();
            result = e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            result = e.toString();
        }
        Debug.log(  "res (mysqld.post(): " + result);
        return result;
    }



    public static Result update(Task task) {
        Debug.log(  "DBOne.update(Task) id = " + task.getId());
        HTTPPost httpPost = new HTTPPost(UPDATE_TASK_URL);
        httpPost.add("description", task.getDescription());
        httpPost.add("comment", task.getComment());
        httpPost.add("heading", task.getHeading());
        httpPost.add("tags", task.getTags());
        httpPost.add("state", task.getState().toString());
        httpPost.add("updated", task.getUpdated().toString());
        httpPost.add("target_date", task.getTargetDate().format(DateTimeFormatter.ofPattern(Converter.DATE_FORMAT_PATTERN)));
        httpPost.add("parent_id", task.getProjectID());
        httpPost.add("parent_task_id", task.getParentId());
        httpPost.add("id", String.valueOf(task.getId()));
        return new Result(post(httpPost));
    }


    public static Result update(Project project) throws IOException {
        Debug.log("DBOne.update(Project) id = " + project.getId());
        HTTPPost httpPost = new HTTPPost(UPDATE_PROJECT_URL);
        httpPost.add("id", String.valueOf( project.getId()) );
        httpPost.add("state", project.getState().toString());
        httpPost.add("heading", project.getHeading());
        httpPost.add("description", project.getDescription());
        httpPost.add("last_update", project.getUpdated().format(DateTimeFormatter.ofPattern(Converter.DATE_TIME_FORMAT_PATTERN)));
        httpPost.add("comment", project.getComment());
        httpPost.add("target_date", project.getTargetDate());
        //httpPost.add("target_date", project.getTargetDate().format(DateTimeFormatter.ofPattern(Converter.DATE__FORMAT_PATTERN)));
        httpPost.add("tags", project.getTags());
        return new Result(post(httpPost));
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String updateProject(int project_id, LocalDateTime localDateTime){
        Debug.log("DbOne.updateProject()" + project_id);
        HTTPPost httpPost = new HTTPPost(DBOne.UPDATE_PROJECT_LAST_UPDATE_URL);
        httpPost.add("project_id", String.valueOf(project_id));
        httpPost.add("last_update", localDateTime.format(DateTimeFormatter.ofPattern(Converter.DATE_TIME_FORMAT_PATTERN)));
        return post(httpPost);
    }
}
