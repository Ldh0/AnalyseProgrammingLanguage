package week_3;
import java.io.*;
import java.util.*;
import java.util.function.*;

/**
 * To run the program
 * javac Nine.java
 * java Nine pride-and-prejudice.txt
 */

/**
 * Solve the problem
 */
public class Nine {
    /**
     * template
     * @param f
     * @param <T>
     * @param <B>
     * @return
     */
    private static <T, B> BiConsumer<T, BiConsumer> simple(BiConsumer<T, B> f) { return (BiConsumer<T, BiConsumer>) f; }

    /**
     * template
     * @param t
     * @param <T>
     */
    private static <T> void functionWithNothing(T t) {}

    /**
     * output the result
     * @param list
     * @param lastFunction
     */
    private static void printResult(List<Map.Entry<String, Integer>> list, Consumer<?> lastFunction) {
        System.out.println(" ");
        System.out.println("------------Result of Nine.java: ------------");
        for (int index = 0; index < Math.min(25, list.size()); ++index) {
            System.out.println(list.get(index).getKey() + " - " + list.get(index).getValue());
        }
        lastFunction.accept(null);
    }

    /**
     * sort frequency
     *
     * @param frequencyMap
     * @param function
     */
    private static void sortFrequency(Map<String, Integer> frequencyMap,
                                      BiConsumer<List<Map.Entry<String, Integer>>, Consumer<?>> function) {

        List<Map.Entry<String, Integer>> wordList = new ArrayList<>(frequencyMap.entrySet());
        Collections.sort(wordList, new Comparator<Map.Entry<String, Integer>>() { // self-define sorting function
            public int compare(Map.Entry<String, Integer> EntryA, Map.Entry<String, Integer> EntryB) {
                return EntryB.getValue().compareTo(EntryA.getValue());
            }
        });
        function.accept(wordList, Nine::functionWithNothing);
    }

    /**
     * count frequency
     * @param words
     * @param function
     */
    private static void countFrequency(List<String> words, BiConsumer<Map<String, Integer>, BiConsumer> function) {
        Map<String, Integer> frequencyMap = new HashMap<>(); // count the frequency
        for (int index = 0; index < words.size(); index++) {
            String word = words.get(index);
            if (frequencyMap.containsKey(word)) { // have met this word
                frequencyMap.put(word, frequencyMap.get(word) + 1);
            } else { // never met this word
                frequencyMap.put(word, 1);
            }
        }
        function.accept(frequencyMap, simple(Nine::printResult));
    }

    /**
     * gather stop words, ignore them and single characters when gather words for frequency count
     *
     * @param wordStringArray
     * @param function
     */
    private static void readWordsAndIgnoreStopWord(String[] wordStringArray,
                                                   BiConsumer<List<String>, BiConsumer> function) {
        try {
            List<String> wordList = new ArrayList<>();
            // read the stop words and put into an array
            String[] stopWordArray = (new StringBuffer()).append(new BufferedReader(
                    new FileReader("stop_words.txt")).readLine()).toString().split(",");

            Set<String> stopWordSet = new HashSet<>(); // put the stop words into a set
            for (int index = 0; index < stopWordArray.length; ++index) {
                stopWordSet.add(stopWordArray[index]);
            }

            // gather the words that needed to count frequency
            for (int index = 0; index < wordStringArray.length; ++index) {
                String splitWord = wordStringArray[index];
                if (!stopWordSet.contains(splitWord) && splitWord.length() >= 2) {
                    wordList.add(wordStringArray[index]);
                }
            }

            function.accept(wordList, simple(Nine::sortFrequency));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     *
     * @param string
     * @param function
     */
    private static void splitTheText(String string, BiConsumer<String[], BiConsumer> function) {
        function.accept(string.split("[^a-z0-9]+"), simple(Nine::countFrequency));
    }

    /**
     *
     * @param string
     * @param function
     */
    private static void deleteStopWord(String string, BiConsumer<String, BiConsumer> function) {
        function.accept(string.toLowerCase(), simple(Nine::readWordsAndIgnoreStopWord));
    }

    /**
     * read the text
     * @param fileName the file
     * @param function
     */
    private static void readTextFile(String fileName, BiConsumer<String, BiConsumer> function) {
        try {
            String string;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            StringBuffer stringBuffer = new StringBuffer();
            while ((string = bufferedReader.readLine()) != null) { // read the file line by line
                stringBuffer.append(string + ",");
            }
            function.accept(stringBuffer.toString(), simple(Nine::splitTheText));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * The entrance of the program
     */
    public static void main(String[] args) { readTextFile("pride-and-prejudice.txt", Nine::deleteStopWord); }
}
