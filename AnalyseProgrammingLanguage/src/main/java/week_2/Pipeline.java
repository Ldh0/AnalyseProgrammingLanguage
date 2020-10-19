package week_2;

import java.util.*;
import java.io.*;
import java.util.Map.*;

/**
 * To run the program
 * javac Pipeline.java
 * java Pipeline pride-and-prejudice.txt
 */

/**
 * Solve the problem
 */
class Pipeline {
    /**
     * Read file and return content in string buffer, use delimiter to cut long string
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
    private static Set<String> readStopWord(String splitWay, StringBuffer stringBuffer) {
        String []stopWordArray = stringBuffer.toString().split(splitWay);
        Set<String> stopWordSet = new HashSet<String>();
        for (int index = 0; index < stopWordArray.length;
             ++index) { // Mark down words (split by splitWay)
            stopWordSet.add(stopWordArray[index]);
        }
        return stopWordSet;
    }

    /**
     * Count frequency
     */
    private static HashMap<String, Integer> countFrequency(String splitWay, StringBuffer stringBuffer, Set<String> stopWordSet) {
        String[] words = stringBuffer.toString().toLowerCase().split(splitWay);
        HashMap<String, Integer> hashMap = new HashMap<>();
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
        return hashMap;
    }

    /**
     * Sort the frequency
     */
    private static List<Map.Entry<String, Integer>> sortFrequency(HashMap<String, Integer> hashMap) {
        List<Map.Entry<String, Integer>> hashList = new ArrayList<Map.Entry<String, Integer>> (hashMap.entrySet());

        Collections.sort(hashList,
                new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(Entry<String, Integer> entryA,
                                       Entry<String, Integer> entryB) {
                        return entryB.getValue().compareTo(entryA.getValue());
                    }
                });
        return hashList;
    }

    /**
     * Output the result
     */
    private static void outputResult(int maxItem, int minLength, List<Map.Entry<String, Integer>> hashList) {
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
        System.out.println("------------Result of Cookbook.java: ------------");
        outputResult(25, 2, sortFrequency(
                countFrequency("[^a-z0-9]+", getStringBufferFromFile("pride-and-prejudice.txt", ","),
                        readStopWord(",", getStringBufferFromFile("stop_words.txt", ",")))));
    }
}
