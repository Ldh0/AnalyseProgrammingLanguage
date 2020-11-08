package week_4;
import java.io.*;
import java.util.*;
import java.util.function.*;

/**
 * Solution of 15
 */
public class Fifteen {
    /**
     * framework
     */
    private static class WordFrequencyFramework {
        private ArrayList<Consumer<String>> loadEventHandlers = new ArrayList<>();
        private ArrayList<Runnable> doworkEventHandlers = new ArrayList<>();
        private ArrayList<Runnable> endEventHandlers = new ArrayList<>();

        /**
         * run functions
         * @param fileName
         */
        public void run(String fileName) {
            for(Consumer<String> h : loadEventHandlers) {
                h.accept(fileName);
            }
            for(Runnable h : doworkEventHandlers) {
                h.run();
            }
            for(Runnable h : endEventHandlers) {
                h.run();
            }
        }

        /**
         * registerForLoadEvent
         * @param handler
         */
        public void registerForLoadEvent(Consumer<String> handler) {
            loadEventHandlers.add(handler);
        }

        /**
         * registerForEndEvent
         * @param handler
         */
        public void registerForEndEvent(Runnable handler) {
            endEventHandlers.add(handler);
        }

        /**
         * registerForDoworkEvent
         * @param handler
         */
        public void registerForDoworkEvent(Runnable handler) {
            doworkEventHandlers.add(handler);
        }
    }

    /**
     * count the z words
     */
    private static class countZ {
        private StopWords stopWords;
        private HashSet<String> zWord = new HashSet<>();

        /**
         *
         * @param stopWords
         */
        private countZ(StopWords stopWords) {
            this.stopWords = stopWords;
        }

        /**
         * output the z words
         */
        private void output() {
            System.out.println("Number of words that contain 'z' is: " + zWord.size());
        }

        /**
         * find z words
         * @param word
         */
        private void findZWord(String word) {
            if(!stopWords.isStopWord(word) && word.contains("z")) {
                zWord.add(word);
            }
        }
    }

    /**
     * stop words
     */
    private static class StopWords {
        private HashSet<String> stopWord = new HashSet<>();

        /**
         * constructor
         * @param wordFrequencyFramework
         */
        private StopWords(WordFrequencyFramework wordFrequencyFramework) {
            wordFrequencyFramework.registerForLoadEvent(this::readStopWord);
        }

        /**
         * if it is stop words
         * @param word
         * @return
         */
        private boolean isStopWord(String word) {
            return (stopWord.contains(word) && word.length() > 1);
        }

        /**
         *
         * @param s won't be used
         */
        private void readStopWord(String s) {
            try {
                String[] stopWordArray = new BufferedReader(new FileReader("stop_words.txt"))
                        .readLine().split(",");
                for (int index = 0; index < stopWordArray.length; index++) {
                    stopWord.add(stopWordArray[index]);
                }
            }catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * read words
     */
    private static class DataStorage {
        private ArrayList<Consumer<String>> wordEventHandlers = new ArrayList<>();
        private StopWords stopWords;
        private String[] wordArray;

        /**
         * read words
         */
        private void setWordFromArray() {
            for (int index = 0; index < wordArray.length; ++index) {
                if (!stopWords.isStopWord(wordArray[index]) && wordArray[index].length() > 1) {
                    for (Consumer<String> consumer : wordEventHandlers) {
                        consumer.accept(wordArray[index]);
                    }
                }
            }
        }

        /**
         * read words from file
         * @param filePath
         */
        private void readWords(String filePath) {
            String string;
            StringBuffer stringBuffer = new StringBuffer();
            try{
                BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                while((string = bufferedReader.readLine()) != null) {
                    stringBuffer.append(string.toLowerCase().replaceAll("[^a-z0-9]", " ") + " ");
                }
                wordArray = stringBuffer.toString().split("\\s+");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        /**
         *
         * @param handler
         */
        private void registerWordEventHandler(Consumer<String> handler) {
            wordEventHandlers.add(handler);
        }

        /**
         *
         * @param wordFrequencyFramework
         * @param stopWords
         */
        private DataStorage(WordFrequencyFramework wordFrequencyFramework, StopWords stopWords) {
            wordFrequencyFramework.registerForLoadEvent(this::readWords);
            wordFrequencyFramework.registerForDoworkEvent(this::setWordFromArray);
            this.stopWords = stopWords;
        }
    }

    /**
     * count frequency
     */
    private static class WordFrequencyCounter {
        private HashMap<String, Integer> hashMap = new HashMap<>();

        /**
         * sort and output the words
         */
        private void sortAndOutput() {
            List<Map.Entry<String, Integer>> words = new ArrayList<>(hashMap.entrySet());
            Collections.sort(words, (entryA, entryB) ->
                    entryB.getValue().compareTo(entryA.getValue())
            );

            for (int index = 0; index < words.size() && index < 25; ++index) {
                System.out.println(words.get(index).getKey() + " - " + words.get(index).getValue());
            }
        }

        /**
         *
         * @param wordFrequencyFramework
         * @param dataStorage
         */
        private WordFrequencyCounter(WordFrequencyFramework wordFrequencyFramework, DataStorage dataStorage) {
            dataStorage.registerWordEventHandler(this::countFrequency);
            wordFrequencyFramework.registerForEndEvent(this::sortAndOutput);
        }

        /**
         * count the frequency
         * @param key
         */
        private void countFrequency(String key) {
            hashMap.put(key, hashMap.containsKey(key) ? hashMap.get(key) + 1 : 1);
        }
    }

    /**
     * main entrance of the program
     * @param args
     */
    public static void main(String[] args) {
        WordFrequencyFramework wordFrequencyFramework = new WordFrequencyFramework();
        StopWords stopWords = new StopWords(wordFrequencyFramework);
        DataStorage dataStorage = new DataStorage(wordFrequencyFramework, stopWords);

        WordFrequencyCounter wordFrequencyCounter = new WordFrequencyCounter(wordFrequencyFramework, dataStorage);

        countZ countZ = new countZ(stopWords);
        dataStorage.registerWordEventHandler(countZ::findZWord);
        wordFrequencyFramework.registerForEndEvent(countZ::output);

        String fileName = "pride-and-prejudice.txt";
        wordFrequencyFramework.run(fileName);
    }
}