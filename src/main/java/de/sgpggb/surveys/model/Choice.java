package de.sgpggb.surveys.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Choice {

    String text = "";
    String color = "white";

    public Choice(String string) {
        if (string == null || string.isEmpty()) {
            return;
        }

        if (string.startsWith("<") && string.contains(">")) {
            int end = string.indexOf('>');
            if (end > 1) {
                this.color = string.substring(1, end);
                this.text = string.substring(end + 1);
                return;
            }
        }

        // Fallback, wenn keine gültige Farbe angegeben ist
        this.color = "white";
        this.text = string;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static List<Choice> createList(String dbString) {
        List<Choice> list = new ArrayList<>();

        if (dbString == null || dbString.isEmpty()) {
            return list;
        }

        String[] parts = dbString.split(";");
        for (String part : parts) {
            list.add(new Choice(part.trim()));
        }

        return list;
    }

    public static String createString(List<Choice> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        StringJoiner joiner = new StringJoiner(";");
        for (Choice ct : list) {
            String entry = "<" + ct.getColor() + ">" + ct.getText();
            joiner.add(entry);
        }

        return joiner.toString();
    }
}
