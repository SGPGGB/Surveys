package de.sgpggb.surveys.model;

public enum AnswerType {

    NUMERICAL,
    FREE_TEXT,
    SINGLE_CHOICE,
    MULTIPLE_CHOICE,
    ;

    public static AnswerType fromString(String value) {
        for (AnswerType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
