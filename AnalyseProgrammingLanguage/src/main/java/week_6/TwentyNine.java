package week_6;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class TwentyNine {
    private class Message {
        private String string;
        private Object object;

        public Message(String string, Object object) {
            setString(string);
            setObject(object);
        }

        public String getString() {
            return string;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public void setString(String string) {
            this.string = string;
        }
    }

    public static void main(String[] args) {
        System.out.println(" ");
        System.out.println("--------------------Output of 29------------------");
        new TwentyNine("pride-and-prejudice.txt");
        System.out.println(" ");
    }

    private WordFrequencyController wordFrequencyController;
    private WordFrequencyObject wordFrequencyObject;
    private StopWordObject stopWordObject;
    private DataStorageObject dataStorageObject;

    TwentyNine(String file) {
        wordFrequencyController = new WordFrequencyController();
        wordFrequencyObject = new WordFrequencyObject(wordFrequencyController);
        stopWordObject = new StopWordObject(wordFrequencyObject);
        dataStorageObject = new DataStorageObject(file, stopWordObject);
        dataStorageObject.concurrentLinkedQueue.add(new Message("send_word_freqs", ""));
    }

    private class WordFrequencyController extends ThreadObject {
        public WordFrequencyController() {
            (new Thread(this)).start();
        }

        @Override
        public void dispatch(Message message) {
            super.dispatch(message);
            if (message.string.equals("top25")) {
                for(String wordFreq: (ArrayList<String>) message.getObject()) {
                    System.out.println(wordFreq);
                }
                send(this, new Message("die", ""));
            }
        }
    }

    private class ThreadObject implements Runnable {
        public String name;
        public ConcurrentLinkedQueue<Message> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
        public boolean stopFlag = false;

        @Override
        public void run() {
            while (!stopFlag) {
                if (!concurrentLinkedQueue.isEmpty()) {
                    Message message = concurrentLinkedQueue.poll();
                    dispatch(message);
                } else {
                    try { Thread.sleep(500); } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void send(ThreadObject receiver, Message message) {
            receiver.concurrentLinkedQueue.offer(message);
        }

        public void dispatch(Message message) {
            if (message.string.equals("die")) {
                this.stopFlag = true;
            }
        }
    }

    private class StopWordObject extends ThreadObject {
        private Set<String> stopWords;
        private WordFrequencyObject wordFrequencyObject;

        public StopWordObject(WordFrequencyObject wordFrequencyObject) {
            stopWords = new HashSet<>();
            this.wordFrequencyObject = wordFrequencyObject;
            try {
                String[] words = new BufferedReader(new InputStreamReader(new FileInputStream("../stop_words.txt")))
                        .readLine().split(",");
                for (String word : words) {
                    this.stopWords.add(word);
                }
                (new Thread(this)).start();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void dispatch(Message message) {
            super.dispatch(message);
            if (message.string.equals("init")) {
                return;
            }
            if (message.string.equals("filter")) {
                String word = (String) message.object;
                if (!stopWords.contains(word)) {
                    send(wordFrequencyObject, new Message("word", word));
                }
            } else {
                send(wordFrequencyObject, message);
                send(this, new Message("die", ""));
            }
        }
    }

    private class DataStorageObject extends ThreadObject {
        private String data;
        private StopWordObject stopWordObject;

        public DataStorageObject(String file, StopWordObject stopWordObject) {
            this.stopWordObject = stopWordObject;
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String string;
                StringBuilder stringBuilder = new StringBuilder();
                while((string = bufferedReader.readLine()) != null) {
                    String[] words = string.split("\\s+|-|--|\'");
                    for(String word: words) {
                        if (word != null && word.length() > 2) {
                            stringBuilder.append(preprocess(word) + " ");
                        }
                    }
                }
                data = stringBuilder.toString();
                (new Thread(this)).start();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
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

        @Override
        public void dispatch(Message message) {
            super.dispatch(message);
            if (message.string.equals("send_word_freqs")) {
                for (String string : data.toLowerCase().split("[^a-z]+")) {
                    send(stopWordObject, new Message("filter", string));
                }
                send(stopWordObject, new Message("top25", ""));
                send(this, new Message("die", ""));
            }
        }
    }

    private class WordFrequencyObject extends ThreadObject {
        private Map<String, Integer> wordCountMap;
        private WordFrequencyController wordFrequencyController;

        public WordFrequencyObject(WordFrequencyController wordFrequencyController) {
            wordCountMap = new HashMap<>();
            this.wordFrequencyController = wordFrequencyController;
            (new Thread(this)).start();
        }

        @Override
        public void dispatch(Message message) {
            super.dispatch(message);
            if (message.string.equals("word")) {
                String key = (String) message.getObject();
                wordCountMap.put(key, wordCountMap.containsKey(key) ? wordCountMap.get(key) + 1 : 1);
            }
            if (message.string.equals("top25")) {
                int outputNum = 25;
                Queue<String> minFrequencyHeap = new PriorityQueue<>(outputNum + 1, new Comparator<String>() {
                    @Override
                    public int compare(String stringA, String stringB) {
                        return wordCountMap.get(stringA) - wordCountMap.get(stringB);
                    }
                });
                Iterator<Map.Entry<String, Integer>> iterator = wordCountMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> mapEntry = iterator.next();
                    if (minFrequencyHeap.size() < outputNum || mapEntry.getValue() > wordCountMap.get(minFrequencyHeap.peek())) {
                        minFrequencyHeap.offer(mapEntry.getKey());
                        if (minFrequencyHeap.size() > outputNum) {
                            minFrequencyHeap.poll();
                        }
                    }
                }
                String[] topFrequencyWords = new String[minFrequencyHeap.size()];
                for (int index = topFrequencyWords.length - 1; index >= 0;) {
                    topFrequencyWords[index--] = minFrequencyHeap.poll();
                }
                List<String> wordWithFrequency = new ArrayList<>(outputNum);
                for(String topFrequencyWord: topFrequencyWords) {
                    wordWithFrequency.add(topFrequencyWord + "  -  " + wordCountMap.get(topFrequencyWord));
                }
                send(wordFrequencyController, new Message("top25", wordWithFrequency));
                send(this, new Message("die", ""));
            }
        }
    }
}

