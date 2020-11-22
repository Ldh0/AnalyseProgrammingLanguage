package week_6;

import java.io.*;
import java.util.*;

public class ThirtyTwo {
    private static class ValueCountPair {
        private String value;
        private Integer count;

        public ValueCountPair(String value, Integer count) {
            setValue(value);
            setCount(count);
        }

        public Integer getCount() {
            return count;
        }

        public String getValue() {
            return value;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private static void regroup(SplitWord splitWord, Reducer[] reducers) {
        for (ValueCountPair valueCountPair : splitWord.counts) {
            if (valueCountPair.getValue() == null || valueCountPair.getValue().length() == 0) {
                continue;
            }
            char c = valueCountPair.getValue().charAt(0);
            if (c >= 'a' && c <= 'e') {
                reducers[0].list.add(valueCountPair);
            } else if (c >= 'f' && c <= 'j') {
                reducers[1].list.add(valueCountPair);
            } else if (c >= 'k' && c <= 'o') {
                reducers[2].list.add(valueCountPair);
            } else if (c >= 'p' && c <= 't') {
                reducers[3].list.add(valueCountPair);
            } else {
                reducers[4].list.add(valueCountPair);
            }
        }
    }

    private static class SplitWord {
        public List<ValueCountPair> counts;
        public Set<String> stopWords;

        public void parseWord(String[] lines) {
            for (int index = 0; index < lines.length; ++index) {
                String[] words = lines[index].split("\\s+|-|--|\'");
                for (String word : words) {
                    if (word != null && word.length() > 2) {
                        word = preProcess(word);
                        if (!stopWords.contains(word)) {
                            counts.add(new ValueCountPair(word, 1));
                        }
                    }
                }
            }
        }

        private String preProcess(String string) {
            StringBuilder stringBuilder = new StringBuilder();
            for (char ch : string.toCharArray()) {
                if (Character.isLetter(ch)) {
                    stringBuilder.append(Character.toLowerCase(ch));
                }
            }
            return stringBuilder.toString();
        }

        public SplitWord() {
            counts = new ArrayList<>();
            stopWords = new HashSet<>();

            try {
                String[] words = new BufferedReader(new InputStreamReader(
                        new FileInputStream("stop_words.txt"))).readLine().split(",");
                for(String word: words) {
                    stopWords.add(word);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Reducer implements Runnable {
        public List<ValueCountPair> list = new ArrayList<>();
        public Map<String, Integer> partialFreq = new HashMap<>();
        public void run() {
            for (int index = 0; index < list.size(); ++index) {
                partialFreq.put(list.get(index).getValue(), partialFreq.getOrDefault(list.get(index).getValue(), 0) + 1);
            }
        }
    }

    private static List<String[]> loadText(String pathToFile, int lineNum) {
        List<String[]> textList = new ArrayList<>();
        List<String> lineList = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(pathToFile)));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                lineList.add(line);
            }
            String[] lines = lineList.toArray(new String[0]);
            int paragraphNum = lines.length / lineNum;
            for (int index = 0; index < paragraphNum; ++index) {
                textList.add(Arrays.copyOfRange(lines, index * lineNum, index * lineNum + lineNum));
            }
            if (lines.length > paragraphNum * lineNum) {
                textList.add(Arrays.copyOfRange(lines, paragraphNum * lineNum, lines.length));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textList;
    }

    private static void merge(Reducer[] reducers) {
        List<ValueCountPair> valueCountPairList = new ArrayList<>();
        for (Reducer reducer: reducers) {
            for (String key: reducer.partialFreq.keySet()) {
                valueCountPairList.add(new ValueCountPair(key, reducer.partialFreq.get(key)));
            }
        }
        Collections.sort(valueCountPairList, new Comparator<ValueCountPair>() {
            @Override
            public int compare(ValueCountPair valueCountPair1, ValueCountPair valueCountPair2) {
                return valueCountPair2.getCount().compareTo(valueCountPair1.getCount());
            }
        });
        int count = 0;
        for (ValueCountPair valueCountPair : valueCountPairList) {
            System.out.println(valueCountPair.getValue() + " - " + valueCountPair.getCount());
            ++count;
            if (count >= 25) {
                break;
            }
        }
    }

    private static void mapping(SplitWord splitWord, List<String[]> paragraphs) {
        for (String[] paragraph : paragraphs) {
            splitWord.parseWord(paragraph);
        }
    }

    public static void main(String[] args) {
        System.out.println(" ");
        System.out.println("--------------------Output of 32------------------");

        SplitWord splitWord = new SplitWord();
        mapping(splitWord, loadText("pride-and-prejudice.txt", 200));
        Reducer[] reducer = new Reducer[5];
        Thread[] thread = new Thread[5];

        try {
            for (int index = 0; index < 5; ++index) {
                reducer[index] = new Reducer();
            }
            regroup(splitWord, reducer);
            for (int index = 0; index < 5; ++index) {
                thread[index] = new Thread(reducer[index]);
                thread[index].start();
            }
            for (int index = 0; index < 5; ++index) {
                thread[index].join();
            }
            merge(reducer);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }

        System.out.println(" ");
    }
}