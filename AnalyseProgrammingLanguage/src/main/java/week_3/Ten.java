package week_3;
import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.function.*;

/**
 * To run the program
 * javac Ten.java
 * java Ten pride-and-prejudice.txt
 */

/**
 * Solve the problem
 */
public class Ten {
    private Object object;

    /**
     * default constructor
     * @param string
     */
    private Ten (String string) {
        this.object = string;
    }

    /**
     * sort the frequency
     * @param wordFrequency
     * @return wordFrequencyAfterSorted
     */
    private static Object sort(Object wordFrequency) {
        List<Map.Entry<String, Integer>> wordFrequencyAfterSorted = new ArrayList<>
                (((HashMap) wordFrequency).entrySet());
        Collections.sort(wordFrequencyAfterSorted, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> entryA, Entry<String, Integer> entryB) {
                return (entryB.getValue().compareTo(entryA.getValue()));
            }
        });
        return wordFrequencyAfterSorted;
    }

    /**
     * count the word frequency
     * @param wordList
     * @return wordFrequency
     */
    private static Object countFrequency(Object wordList) {
        HashMap<String, Integer> wordFrequency = new HashMap<>();
        for (int index = 0; index < ((List<String>) wordList).size(); ++index) {
            String string = ((List<String>) wordList).get(index);
            if (wordFrequency.containsKey(string)) { // have met this word
                wordFrequency.put(string, wordFrequency.get(string) + 1);
            } else { // never met this word
                wordFrequency.put(string, 1);
            }
        }
        return wordFrequency;
    }

    /**
     * read the word from file and filter out stop words
     * @param fileName
     * @return
     */
    private static Object readTextFile(Object fileName) {
        List<String> wordList = new ArrayList<>(); // word list for the text
        Set<String> stopWordSet = new HashSet<>(); // set for stop words
        try {
            // gather stop words
            String []stopWordArray = new Scanner(new FileReader("stop_words.txt"))
                    .nextLine().split(",");
            for (int index = 0; index < stopWordArray.length; ++index) {
                stopWordSet.add(stopWordArray[index]);
            }

            // read the file
            String currentString = ""; // record current string just read from file
            FileInputStream fileInputStream = new FileInputStream((String) fileName);
            while (fileInputStream.available() > 0) { // read character one by one
                char currentChar = (char) fileInputStream.read();
                if (Character.isLetterOrDigit(currentChar)) { // none a-z or 0-9
                    currentString += currentChar;
                } else {
                    if (!stopWordSet.contains(currentString.toLowerCase()) && currentString.length() > 1) {
                        wordList.add(currentString.toLowerCase()); // not stop words or single character
                    }
                    currentString = "";
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return wordList;
    }

    /**
     * print the top 25 result
     */
    private void printResult() {
        System.out.println(" ");
        System.out.println("------------Result of Ten.java: ------------");
        List<Map.Entry<String, Integer>> mapEntryList = (List<Entry<String, Integer>>) this.object;
        for (int index = 1; index <= 25; ++index) {
            Map.Entry<String, Integer> entry = mapEntryList.get(index - 1);
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }

    /**
     * bind the function together
     * @param function
     * @return
     */
    private Ten bind(Function<Object, Object> function) {
        this.object = function.apply(this.object);
        return this;
    }

    /**
     * main entrance of the program
     * @param args
     */
    public static void main(String[] args) {
        new Ten("pride-and-prejudice.txt")
                .bind(Ten::readTextFile)
                .bind(Ten::countFrequency)
                .bind(Ten::sort)
                .printResult();
    }
}
