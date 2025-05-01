package de.sgpggb.surveys.model;

public enum RewardType {

    MONEY,
    REWARD,
    ;

    public static RewardType fromString(String value) {
        for (RewardType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }

}
