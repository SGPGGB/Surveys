package de.sgpggb.surveys.util;

import java.sql.Timestamp;

public class Util {

    public static String duration(Timestamp tf, Timestamp ts) {
        long tfin = ts.getTime() - tf.getTime();
        tfin = tfin / 1000;
        long day = tfin / 86400;
        tfin = tfin - day * 86400;
        long hour = tfin / 3600;
        tfin = tfin - hour * 3600;
        long min = tfin / 60;
        long sec = tfin - min * 60; 
        
        if (day > 1) return day + " Tage";
        if (day > 0) return day + " Tag";
        if (hour > 1) return hour + " Stunden";
        if (hour > 0) return hour + " Stunde";
        if (min > 1) return min + " Minuten";
        if (min > 0) return min + " Minute";
        if (sec > 1) return sec + " Sekunden";
        if (sec > 0) return sec + " Sekunde";
        throw new IllegalStateException();
    }
}
