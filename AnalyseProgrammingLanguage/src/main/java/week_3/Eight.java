package week_3;
import java.util.*;
import java.io.*;
import java.util.Map.*;

/**
 * To run the program
 * javac Eight.java
 * java Eight pride-and-prejudice.txt
 */

/**
 * Solve the problem
 */
class Eight {
    private static HashMap<String, Integer> hashMap; // record the frequency of word
    private static Set<String> stopWordSet; // set of stop words
    private static List<Map.Entry<String, Integer>> hashList; // for sorting the word by frequency
    private static List<Map.Entry<String, Integer>> sortedHashList;
    private static StringBuffer stringBuffer;

    /**
     * Read file and return content in string buffer, use delimiter to cut long string
     * @Param fileName
     * @Param delemiter
     */
    private static void setStringBufferFromFile(String fileName, String delimiter) {
        stringBuffer = new StringBuffer();
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
    }

    /**
     * Mark down all stop words
     */
    private static void readStopWord(String fileName, String delimiter, String splitWay) {
        setStringBufferFromFile(fileName, delimiter);
        String []stopWordArray = stringBuffer.toString().split(splitWay);

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
        setStringBufferFromFile(fileName, delimiter);
        String[] words = stringBuffer.toString().toLowerCase().split(splitWay);

        for (int index = 0; index < words.length; ++index) { // Count the frequency word by word
            if (stopWordSet.contains(words[index])) { // don't count stop words
                continue;
            }

            int result;  // mark down the count number
            if (hashMap.containsKey(words[index])) {
                result = hashMap.get(words[index]) + 1;
            } else {
                result = 1;
            }
            hashMap.put(words[index], result);
        }
    }

    /**
     * merge sort for recursion
     * @param left left endpoint of the sector
     * @param right right endpoint of the sector
     */
    private static void mergeSort(int left, int right) {
        if (left + 1 >= right) { // if there are less than 3 entry
            if (hashList.get(left).getValue() < hashList.get(right).getValue()) {
                Entry<String, Integer> entry = hashList.get(left);
                hashList.set(left, hashList.get(right));
                hashList.set(right, entry);
            }
            return;
        }
        int middle = (left + right) / 2;
        mergeSort(left, middle); // recursion
        mergeSort(middle + 1, right); // recursion
        int pointerInLeft = left, pointerInRight = middle + 1, index = left;

        // merge two lists
        while (index <= right && (pointerInLeft <= middle || pointerInRight <= right)) {
            if (pointerInLeft > middle) {//left pointer overflow
                sortedHashList.set(index++, hashList.get(pointerInRight++));
            } else if (pointerInRight > right) {//right pointer overflow
                sortedHashList.set(index++, hashList.get(pointerInLeft++));
            } else { // none of left or right is overflow
                if (hashList.get(pointerInLeft).getValue() > hashList.get(pointerInRight).getValue()) {
                    sortedHashList.set(index++, hashList.get(pointerInLeft++));
                } else {
                    sortedHashList.set(index++, hashList.get(pointerInRight++));
                }
            }
        }
        for (index = left; index <= right; ++index) {
            hashList.set(index, sortedHashList.get(index));
        }
    }

    /**
     * Sort the frequency
     */
    private static void sortFrequency() {
        hashList = new ArrayList<> (hashMap.entrySet());
        sortedHashList = new ArrayList<>(hashList);
        mergeSort(0, hashList.size() - 1);
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
        hashMap = new HashMap<>();
        stopWordSet = new HashSet<String>();
        readStopWord("stop_words.txt", ",", ",");
        countFrequency("pride-and-prejudice.txt", ",", "[^a-z0-9]+");
        sortFrequency();
        System.out.println(" ");
        System.out.println("------------Result of Eight.java: ------------");
        outputResult(25, 2);
    }
}