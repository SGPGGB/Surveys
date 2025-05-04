package de.sgpggb.surveys.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Question {

    int id;
    int nextID;
    String text;
    AnswerType answerType;
    String choices;
    int groupID;

    public Question() {
        this.id = -1;
        this.text = "";
        this.choices = "";
        this.nextID = -1;
        this.answerType = AnswerType.FREE_TEXT;
        this.groupID = -1;
    }

    public Question(int id, String text, String choices, int nextID, AnswerType answerType, int groupID) {
        this.id = id;
        this.text = text;
        this.choices = choices;
        this.nextID = nextID;
        this.answerType = answerType;
        this.groupID = groupID;
    }

    public int getNextID() {
        return nextID;
    }

    public void setNextID(int nextID) {
        this.nextID = nextID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getChoicesList() {
        return Arrays.asList(choices.split(";"));
    }

    public void setChoicesList(List<String> choices) {
        this.choices = choices.stream()
            .filter(s -> !s.isEmpty())
            .collect(Collectors.joining(";"));
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
}
