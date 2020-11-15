package week_5.twenty.app;
import week_5.twenty.framework.wordListInterface;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * application 1
 */
public class Application1 implements wordListInterface {
    /**
     * output
     * @param wordArrayList
     * @return
     */
    public Stream<Entry<String, Integer>> output(List<String> wordArrayList){
        return wordArrayList.stream().collect(Collectors.groupingBy(String::toString, Collectors.counting()))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().intValue()))
                .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(25);
    }

    /**
     * get the array list
     * @param string
     * @return
     * @throws IOException
     */
    public List<String> getArrayListOfAllWordAfterFilter(String string) throws IOException {
        ArrayList<String> wordArrayList =
                new ArrayList<>(Arrays.asList(
                        new String(
                                Files.readAllBytes(
                                        Paths.get(string)))
                                .toLowerCase()
                                .split("[\\W_]+"))),
                stopWordArrayList =
                        new ArrayList<>(Arrays.asList(
                                new String(
                                        Files.readAllBytes(
                                                Paths.get("../../../stop_words.txt")))
                                        .split(",")));
        for (char c = 'a'; c <= 'z'; ++c) {
            stopWordArrayList.add(Character.toString(c));
        }
        wordArrayList.removeAll(stopWordArrayList);
        return wordArrayList;
    }
}