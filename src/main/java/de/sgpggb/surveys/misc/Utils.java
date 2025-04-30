package de.sgpggb.surveys.misc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static List<Integer> stringToList(String s) {
        return Arrays.stream(s.split(";")).map(String::trim).map(Integer::parseInt).collect(Collectors.toList());
    }

    public static String listToString(List<Integer> list) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(";"));
    }
}
