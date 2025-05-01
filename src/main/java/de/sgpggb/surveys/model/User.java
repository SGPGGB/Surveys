package de.sgpggb.surveys.model;

import java.util.List;
import java.util.UUID;

public class User {

    int id;
    UUID uuid;
    int currentGroup;
    int currentQuestion;
    List<Integer> completedGroups;

    public User(int id, UUID uuid, int currentQuestion, int currentGroup, List<Integer> completedGroups) {
        this.id = id;
        this.uuid = uuid;
        this.currentQuestion = currentQuestion;
        this.currentGroup = currentGroup;
        this.completedGroups = completedGroups;
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

    public int getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(int currentGroup) {
        this.currentGroup = currentGroup;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public List<Integer> getCompletedGroups() {
        return completedGroups;
    }

    public void setCompletedGroups(List<Integer> completedGroups) {
        this.completedGroups = completedGroups;
    }
}
