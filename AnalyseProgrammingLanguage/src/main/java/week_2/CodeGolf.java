package week_2;

import java.util.*;
import java.io.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CodeGolf {
    private static Scanner getScanner(String fileName) throws FileNotFoundException {return new Scanner(new FileReader(fileName));}
    private static void Output(Map.Entry<String, Long> mapEntry) {System.out.println(mapEntry.getKey() + " - " + mapEntry.getValue());}
    public static void main(String[] args) throws IOException {
        Stream<String> wordListStream = Arrays.asList((getScanner("pride-and-prejudice.txt").useDelimiter("\\Z").next()).toLowerCase().split("[^a-z]")).stream();
        Set<String> stopWordSet = new HashSet(Arrays.asList(getScanner("stop_words.txt").next().split(",")));
        Stream<String> wordStreamAfterFilter = wordListStream.filter(filterWord -> (!stopWordSet.contains(filterWord) && filterWord.length()> 1));
        Set<Map.Entry<String, Long>> wordSet = wordStreamAfterFilter.collect(Collectors.groupingBy(word -> word, Collectors.counting())).entrySet();
        wordSet.stream().sorted((entryA, entryB) -> entryB.getValue().compareTo(entryA.getValue())).limit(25).forEach(mapEntry-> Output(mapEntry));
    }
}
