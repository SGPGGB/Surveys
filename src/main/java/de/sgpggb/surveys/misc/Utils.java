package de.sgpggb.surveys.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static List<Integer> stringToList(String s) {
        if (s == null || s.isEmpty())
            return new ArrayList<>();
        return Arrays.stream(s.split(";")).map(String::trim).map(Integer::parseInt).collect(Collectors.toList());
    }




    public static String listToString(List<Integer> list) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(";"));
    }

    public static String listToString2(List<Integer> list) {
        return String.join(";", list.stream().map(String::valueOf).toArray(String[]::new));
    }
}
