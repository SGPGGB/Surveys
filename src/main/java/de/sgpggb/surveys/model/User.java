package de.sgpggb.surveys.model;

import de.sgpggb.surveys.SurveysPlugin;

import java.util.List;
import java.util.UUID;

public class User {

    int id;
    UUID uuid;
    int currentGroup;
    int currentQuestion;
    List<Integer> completedGroups;

    String name;

    public User(int id, UUID uuid, int currentQuestion, int currentGroup, List<Integer> completedGroups) {
        this.id = id;
        this.uuid = uuid;
        this.currentQuestion = currentQuestion;
        this.currentGroup = currentGroup;
        this.completedGroups = completedGroups;
    }

    public void save() {
        SurveysPlugin.getInstance().getDbAdapter().saveUser(this);
    }

    public Question getCurrentQuestion() {
        return SurveysPlugin.getInstance().getManager().getQuestion(currentQuestion);
    }

    public Group getCurrentGroup() {
        return SurveysPlugin.getInstance().getManager().getGroup(currentGroup);
    }

    public void addGroupCompleted(Group group) {
        List<Integer> list = getCompletedGroups();
        list.add(group.getId());
        setCompletedGroups(list);
        setCurrentGroupID(-1);
    }

    public boolean hasCompletedGroup(int id) {
        return completedGroups.contains(id);
    }

    public boolean hasCompletedGroup(Group group) {
        return hasCompletedGroup(group.getId());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getCurrentGroupID() {
        return currentGroup;
    }

    public void setCurrentGroupID(int currentGroup) {
        this.currentGroup = currentGroup;
    }

    public int getCurrentQuestionID() {
        return currentQuestion;
    }

    public void setCurrentQuestionID(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public List<Integer> getCompletedGroups() {
        return completedGroups;
    }

    public void setCompletedGroups(List<Integer> completedGroups) {
        this.completedGroups = completedGroups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
