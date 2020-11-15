package week_5.twenty.app;
import week_5.twenty.framework.wordListInterface;

import java.util.Map;
import java.util.Comparator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * application 2
 */
public class Application2 implements wordListInterface {
    /**
     * get the array list
     * @param string
     * @return
     * @throws IOException
     */
    public List<String> getArrayListOfAllWordAfterFilter(String string) throws IOException {
        List<String> stopWordList = new ArrayList<>(Arrays.asList(new String(
                Files.readAllBytes(Paths.get("../../../stop_words.txt"))).split(",")));
        for (char c = 'a'; c <= 'z'; ++c) {
            stopWordList.add(Character.toString(c));
        }
        List<String> wordList = new ArrayList<> (Arrays.asList( new String(Files.readAllBytes(
                Paths.get(string))).replaceAll("[\\W_]+", " ").toLowerCase().split(" ")));
        wordList.removeAll(stopWordList);
        return wordList;
    }

    /**
     * sort and get the output list
     * @param wordList
     * @return
     */
    public Stream<Map.Entry<String, Integer>> output(List<String> wordList){
        HashMap<String, Integer> frequency = new HashMap<>();
        for (int index = 0; index < wordList.size(); ++index) {
            if (frequency.containsKey(wordList.get(index))) {
                frequency.put(wordList.get(index), frequency.get(wordList.get(index)) + 1);
            } else {
                frequency.put(wordList.get(index), 1);
            }
        }
        Stream<Map.Entry<String, Integer>> stream = frequency.entrySet().stream();
        return stream.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(25);
    }
}