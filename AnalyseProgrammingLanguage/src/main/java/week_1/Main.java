package week_1;

import java.util.*;
import java.io.*;
import java.util.Map.*;

/**
 * To run the program
 * javac Main.java
 * java Main pride-and-prejudice.txt
 */

/**
 * Solve the problem
 */
class Main {
    private static HashMap<String, Integer> hashMap; // record the frequency of word
    private static Set<String> stopWordSet; // set of stop words
    private static List<Map.Entry<String, Integer>> hashList; // for sorting the word by frequency

    /**
     * Read file and return content in string buffer, use delimiter to cut long string
     * @Param fileName
     * @Param delemiter
     * @Return string buffer
     */
    private static StringBuffer getStringBufferFromFile(String fileName, String delimiter) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader bufferedReader =
                    new BufferedReader(new FileReader(fileName));
            String string;
            while ((string = bufferedReader.readLine()) != null) { // read file line by line
                stringBuffer.append(string + delimiter);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return stringBuffer;
    }

    /**
     * Mark down all stop words
     */
    private static void readStopWord(String fileName, String delimiter, String splitWay) {
        String []stopWordArray = getStringBufferFromFile(fileName, delimiter)
                .toString().split(splitWay);

        for (int index = 0; index < stopWordArray.length;
             ++index) { // Mark down words (split by splitWay)
            stopWordSet.add(stopWordArray[index]);
        }
    }

    /**
     * Count frequency
     * @Param fileName
     */
    private static void countFrequency(String fileName, String delimiter, String splitWay) {
        String[] words = getStringBufferFromFile(fileName, delimiter)
                .toString().toLowerCase().split(splitWay);

        for (int index = 0; index < words.length; ++index) { // Count the frequency word by word
            if (stopWordSet.contains(words[index])) { // don't count stop words
                continue;
            }

            int result;
            if (hashMap.containsKey(words[index])) {
                result = hashMap.get(words[index]) + 1;
            } else {
                result = 1;
            }
            hashMap.put(words[index], result);
        }
    }

    /**
     * Sort the frequency
     */
    private static void sortFrequency() {
        hashList = new ArrayList<Map.Entry<String, Integer>> (hashMap.entrySet());

        Collections.sort(hashList,
                new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(Entry<String, Integer> entryA,
                                       Entry<String, Integer> entryB) {
                        return entryB.getValue().compareTo(entryA.getValue());
                    }
                });
    }

    /**
     * Output the result
     */
    private static void outputResult(int maxItem, int minLength) {
        int number = 0;
        for (int index = 0; index < hashList.size() && number < maxItem; ++index) {
            if (hashList.get(index).getKey().length() >= minLength) {
                String output = hashList.get(index).getKey() + " - " +
                        Integer.toString(hashList.get(index).getValue());
                System.out.println(output);
                ++number;
            }
        }
    }

    /**
     * The entrance of the program
     */
    public static void main(String[] args) {
        hashMap = new HashMap<String, Integer>();
        stopWordSet = new HashSet<String>();
        readStopWord("stop_words.txt", ",", ",");
        countFrequency("pride-and-prejudice.txt", ",", "[^a-z0-9]+");
        sortFrequency();
        outputResult(25, 2);
    }
}