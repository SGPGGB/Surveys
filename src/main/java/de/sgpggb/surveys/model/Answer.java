package de.sgpggb.surveys.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Answer {

    int id;
    int questionID;
    UUID uuid;
    String answer;
    Timestamp timestamp;

    public Answer(int id, int questionID, UUID uuid, String answer, Timestamp timestamp) {
        this.id = id;
        this.questionID = questionID;
        this.uuid = uuid;
        this.answer = answer;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
