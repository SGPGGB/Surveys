package de.sgpggb.surveys.models;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Survey {
    private int id;
    private UUID uuidCreator;
    private String nameCreator;
    private String text;
    private Timestamp timefrom;
    private Timestamp timeto;
    
    private Map<Integer, Answer> answers;

    public Survey(int id, UUID uuidCreator, String nameCreator, String text, Timestamp timefrom, Timestamp timeto, Map<Integer, Answer> answers) {
        this.id = id;
        this.uuidCreator = uuidCreator;
        this.nameCreator = nameCreator;
        this.text = text;
        this.timefrom = timefrom;
        this.timeto = timeto;
        this.answers = answers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getUuidCreator() {
        return uuidCreator;
    }

    public void setUuidCreator(UUID uuidCreator) {
        this.uuidCreator = uuidCreator;
    }

    public String getNameCreator() {
        return nameCreator;
    }

    public void setNameCreator(String nameCreator) {
        this.nameCreator = nameCreator;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimefrom() {
        return timefrom;
    }

    public void setTimefrom(Timestamp timefrom) {
        this.timefrom = timefrom;
    }

    public Timestamp getTimeto() {
        return timeto;
    }

    public void setTimeto(Timestamp timeto) {
        this.timeto = timeto;
    }

    public Map<Integer, Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, Answer> answers) {
        this.answers = answers;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.id;
        hash = 41 * hash + Objects.hashCode(this.uuidCreator);
        hash = 41 * hash + Objects.hashCode(this.nameCreator);
        hash = 41 * hash + Objects.hashCode(this.text);
        hash = 41 * hash + Objects.hashCode(this.timefrom);
        hash = 41 * hash + Objects.hashCode(this.timeto);
        hash = 41 * hash + Objects.hashCode(this.answers);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Survey other = (Survey) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.nameCreator, other.nameCreator)) {
            return false;
        }
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        if (!Objects.equals(this.uuidCreator, other.uuidCreator)) {
            return false;
        }
        if (!Objects.equals(this.timefrom, other.timefrom)) {
            return false;
        }
        if (!Objects.equals(this.timeto, other.timeto)) {
            return false;
        }
        if (!Objects.equals(this.answers, other.answers)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Survey{" + "id=" + id + ", uuidCreator=" + uuidCreator + ", nameCreator=" + nameCreator + ", text=" + text + ", timefrom=" + timefrom + ", timeto=" + timeto + ", answers=" + answers + '}';
    }
    
    
}
