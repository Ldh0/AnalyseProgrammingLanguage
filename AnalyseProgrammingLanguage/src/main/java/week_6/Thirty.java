package week_6;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Thirty {
    private static HashSet<String> stopWords;
    private static ConcurrentHashMap<String, Integer> wordFrequency;
    private static ConcurrentLinkedQueue<String> wordSpace;
    private static ConcurrentLinkedQueue<Map<String, Integer>> freqSpace;

    public Thirty() {
        wordSpace = new ConcurrentLinkedQueue<>();
        freqSpace = new ConcurrentLinkedQueue<>();
        stopWords = new HashSet<>();
        wordFrequency = new ConcurrentHashMap<>();

        try {
            String[] words = new BufferedReader(new InputStreamReader(new FileInputStream("stop_words.txt")))
                    .readLine().split(",");
            for(String word: words) {
                stopWords.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String preprocess(String word) {
        StringBuilder stringBuilder = new StringBuilder();
        for(char ch: word.toCharArray()) {
            if (Character.isLetter(ch)) {
                stringBuilder.append(Character.toLowerCase(ch));
            }
        }
        return stringBuilder.toString();
    }

    private void loadText(String file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String string;
            while((string = bufferedReader.readLine()) != null) {
                String[] words = string.split("\\s+|-|--|\'");
                for(String word: words) {
                    if (word != null && word.length() > 2) {
                        wordSpace.offer(preprocess(word));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectTop25Element() {
        int outputNum = 25;
        Queue<String> minFrequencyHeap = new PriorityQueue<>(outputNum + 1, new Comparator<String>() {
            @Override
            public int compare(String stringA, String stringB) {
                return wordFrequency.get(stringA) - wordFrequency.get(stringB);
            }
        });
        Iterator<Map.Entry<String, Integer>> iterator = wordFrequency.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, Integer> mapEntry = iterator.next();
            if(minFrequencyHeap.size() < outputNum || mapEntry.getValue() > wordFrequency.get(minFrequencyHeap.peek())) {
                minFrequencyHeap.offer(mapEntry.getKey());
                if (minFrequencyHeap.size() > outputNum) {
                    minFrequencyHeap.poll();
                }
            }
        }
        String[] topFrequencyWords = new String[minFrequencyHeap.size()];
        for(int index = topFrequencyWords.length - 1; index >= 0; --index) {
            topFrequencyWords[index] = minFrequencyHeap.poll();
        }
        for(String topFrequencyWord: topFrequencyWords) {
            System.out.println(topFrequencyWord + "  -  " + wordFrequency.get(topFrequencyWord));
        }
    }

    static class Operator1 implements Runnable {
        private Map<String, Integer> wordCountMap;

        public Operator1(Map<String, Integer> wordCountMap) {
            this.wordCountMap = wordCountMap;
        }

        @Override
        public void run() {
            processWords();
        }

        private void processWords() {
            String word;
            while ((word = wordSpace.poll()) != null) {
                if (!stopWords.contains(word)) {
                    wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                }
            }
            freqSpace.offer(wordCountMap);
        }
    }

    static class Operator2 implements Runnable {
        public Operator2() {
            super();
        }

        @Override
        public void run() {
            mergePartialResults();
        }

        private void mergePartialResults() {
            Map<String, Integer> partialFreqs;
            while ((partialFreqs = freqSpace.poll()) != null) {
                List<Map.Entry<String, Integer>> list = new ArrayList<>(partialFreqs.entrySet());
                for(Map.Entry<String, Integer> result : list) {
                    wordFrequency.put(result.getKey(), wordFrequency.containsKey(result.getKey())
                            ? wordFrequency.get(result.getKey()) + result.getValue() : result.getValue());
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(" ");
        System.out.println("--------------------Output of 30------------------");

        Thirty thirty = new Thirty();
        thirty.loadText("pride-and-prejudice.txt");

        Thread[] operator1List = new Thread[5], operator2List = new Thread[5];
        for (int i = 0; i < operator1List.length; ++ i) {
            operator1List[i] = new Thread(new Operator1(new HashMap<>()));
            operator2List[i] = new Thread(new Operator2());
        }
        try {
            for (int index = 0; index < operator1List.length; ++index) {
                operator1List[index].start();
            }
            for (int index = 0; index < operator1List.length; ++index) {
                operator1List[index].join();
            }
            for (int index = 0; index < operator2List.length; ++index) {
                operator2List[index].start();
            }
            for (int index = 0; index < operator2List.length; ++index) {
                operator2List[index].join();
            }
            thirty.selectTop25Element();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
        System.out.println(" ");
    }
}
