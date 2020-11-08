package week_4;
import java.io.*;
import java.util.*;

/**
 * Solution of the problem 12
 */
public class Twelve {
    /**
     * FrequencyController
     */
    private static class FrequencyController {
        DataStorage dataStorage = new DataStorage();
        StopWord stopWord = new StopWord();
        Frequency frequency = new Frequency();

        /**
         *
         * @param message
         * @return
         */
        public Object dispatch(String[] message) {
            if ("run".equals(message[0])) {
                return this.run();
            } else {
                return this.init(message[1]);
            }
        }

        /**
         *
         * @return
         */
        private Object run() {
            String[] array = (String[])this.dataStorage.dispatch(new String[]{"words"});
            for (String string : array) {
                if(false == (boolean) stopWord.dispatch(new String[]{"is_stop_word", string})) {
                    frequency.dispatch(new String[]{"increment_count", string});
                }
            }
            List<Map.Entry<String, Integer>> outputList = (List<Map.Entry<String, Integer>>) frequency
                    .dispatch(new String[]{"sorted"});
            for (int index = 0; index < outputList.size() && index < 25; ++index) {
                System.out.println(outputList.get(index).getKey() + " - " + outputList.get(index).getValue());
            }
            return null;
        }

        /**
         *
         * @param file
         * @return
         */
        private Object init(String file) {
            dataStorage.dispatch(new String[]{"init", file});
            stopWord.dispatch(new String[]{"init"});
            return null;
        }
    }

    /**
     * manage frequency
     */
    private static class Frequency {
        HashMap<String, Integer> hashMap = new HashMap<>();

        /**
         * sort the frequency
         * @return
         */
        private List<Map.Entry<String, Integer>> sort() {
            List<Map.Entry<String, Integer>> list = new ArrayList<>(hashMap.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> entryA, Map.Entry<String, Integer> entryB) {
                    return entryB.getValue().compareTo(entryA.getValue());
                }
            });
            return list;
        }

        /**
         *
         * @param key
         * @return
         */
        private Map<String, Integer> countFrequency(String key) {
            hashMap.put(key, hashMap.containsKey(key) ? hashMap.get(key) + 1 : 1);
            return hashMap;
        }

        /**
         *
         * @param message
         * @return
         */
        public Object dispatch(String[] message) {
            if ("sorted".equals(message[0])) {
                return this.sort();
            } else {
                return this.countFrequency(message[1]);
            }
        }
    }

    /**
     * manage stop words
     */
    private static class StopWord {
        HashSet<String> stopWord = new HashSet<>();

        /**
         *
         * @param word
         * @return
         */
        private boolean isStopWord(String word) {
            return stopWord.contains(word) || word.length() < 2;
        }

        /**
         *
         * @return
         */
        private HashSet<String> init() {
            try {
                Collections.addAll(stopWord, new BufferedReader(new FileReader("stop_words.txt"))
                        .readLine().split(","));
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            return stopWord;
        }

        /**
         *
         * @param message
         * @return
         */
        public Object dispatch(String[] message) {
            if (message[0].equals("is_stop_word")) {
                return this.isStopWord(message[1]);
            } else {
                return this.init();
            }
        }
    }

    /**
     *
     */
    private static class DataStorage {
        private String fileContent = null;

        /**
         *
         * @param file
         * @return
         */
        private String init(String file) {
            StringBuilder stringBuilder = new StringBuilder();
            String string;
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                while ((string = bufferedReader.readLine()) != null) {
                    stringBuilder.append(string + " ");
                }
                fileContent = stringBuilder.toString();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            return fileContent;
        }

        /**
         *
         * @param message
         * @return
         */
        public Object dispatch(String[] message) {
            if ("words".equals(message[0])) {
                return this.words();
            } else {
                return this.init(message[1]);
            }
        }

        /**
         *
         * @return
         */
        private String[] words() {
            String[] words = fileContent.toLowerCase().split("[^a-z0-9]+");
            return words;
        }
    }

    /**
     * main entrance of the function
     * @param args
     */
    public static void main(String[] args) {
        FrequencyController frequencyController = new FrequencyController();

        frequencyController.dispatch(new String[]{"init", "pride-and-prejudice.txt"});
        frequencyController.dispatch(new String[]{"run"});
    }
}
