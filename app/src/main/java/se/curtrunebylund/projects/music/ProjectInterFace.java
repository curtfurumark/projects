package se.curtrunebylund.projects.music;

public interface ProjectInterFace {
    String getUser();
    String getParentId();
    String getLastUpdate();
    void setLastUpdate(String lastUpdate);
    void setUser(String user);

    String getTag();

    void setTag(String tag);
    void setPriority(String priority);
    String getPriority();

    int getId();


    void setId(int id);

    String getComment();
    void setComment(String comment);


    @Override
    String toString();

    String getHeading();

    void setHeading(String description);
    void setDescription(String description);
    String getDescription();

    String getAdded();

    void setAdded(String added);

    String getTargetDate();

    void setTargetDate(String target_date);

    String getState();

    void setState(String status);

    void setUserName(String user_name);
}
