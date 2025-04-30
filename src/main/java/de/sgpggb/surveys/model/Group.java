package de.sgpggb.surveys.model;

import de.sgpggb.pluginutilitieslibbungee.Logging;
import de.sgpggb.surveys.Manager;
import de.sgpggb.surveys.SurveysPlugin;

import java.util.ArrayList;
import java.util.List;

public class Group {

    int id;
    String name;
    int order;
    String permission;
    List<Integer> questions;
    int rewardID;

    public Group() {
        this.id = -1;
        this.name = "";
        this.order = -1;
        this.permission = "";
        this.questions = new ArrayList<>();
        this.rewardID = -1;
    }

    public Group(int id, String name, int order, String permission, List<Integer> questions, int rewardID) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.permission = permission;
        this.questions = questions;
        this.rewardID = rewardID;
    }

    Manager manager = SurveysPlugin.getInstance().getManager();
    Logging log = SurveysPlugin.getInstance().getLog();

    public Question getNextQuestion(Question current) {
        Question next = manager.getQuestion(current.getNextID());
        if (next.getGroupID() != this.id) {
            log.error("found next question " + next.getId() + " but it is not in group " + this.id);
            return null;
        }
        return next;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<Integer> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Integer> questions) {
        this.questions = questions;
    }

    public int getRewardID() {
        return rewardID;
    }

    public void setRewardID(int rewardID) {
        this.rewardID = rewardID;
    }
}
