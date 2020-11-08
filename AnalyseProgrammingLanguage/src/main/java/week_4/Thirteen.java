package week_4;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Solution of 13
 */
public class Thirteen {
    /**
     * my map
     * @param <K>
     * @param <V>
     */
    private static class SelfDefineMap<K, V> extends HashMap<K, V> {protected SelfDefineMap<K, V> selfDefineMap = this;}

    /**
     * my pair
     * @param <T1>
     * @param <T2>
     */
    private static class SelfDefinePair<T1, T2> {
        private T1 t1;
        private T2 t2;

        /**
         *
         * @param t1
         * @param t2
         */
        SelfDefinePair(T1 t1, T2 t2) {
            setFirst(t1);
            setSecond(t2);
        }

        /**
         *
         * @return
         */
        public T1 getFirst() {return t1;}

        /**
         *
         * @return
         */
        public T2 getSecond() {return t2;}

        /**
         *
         * @param t1
         */
        public void setFirst(T1 t1) {this.t1 = t1;}

        /**
         *
         * @param t2
         */
        public void setSecond(T2 t2) {this.t2 = t2;}
    }

    /**
     * dataStorageObject
     */
    private static SelfDefineMap<String, Object> dataStorageObject = new SelfDefineMap<String, Object>() {{
            selfDefineMap.put("init", (Consumer<String>) (file) -> {
                try {
                    selfDefineMap.put("data",
                            Files.lines(Paths.get(file)).map(line -> line.toLowerCase()
                            .replaceAll("[^a-z0-9]", " "))
                            .collect(Collectors.joining(" ")).split("\\s+"));
                    selfDefineMap.put("words", (Supplier<String[]>) () -> (String[]) selfDefineMap.get("data"));
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            });
        }
    };

    /**
     * stopWordObject
     */
    private static SelfDefineMap<String, Object> stopWordObject = new SelfDefineMap<String, Object>() {{
            selfDefineMap.put("init", (Runnable) () -> {
                try {
                    selfDefineMap.put("stop_words", Files.lines(Paths.get("stop_words.txt"))
                            .map(line -> line.split(",")).flatMap(Arrays::stream)
                            .collect(Collectors.toCollection(HashSet::new)));
                    selfDefineMap.put("is_stop_word",
                            (Function<String, Boolean>) word -> word.length() < 2
                                    || ((Set<String>) selfDefineMap.get("stop_words")).contains(word));
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            });
        }
    };

    /**
     * frequencyObject
     */
    private static SelfDefineMap<String, Object> frequencyObject = new SelfDefineMap<String, Object>() {{
            selfDefineMap.put("frequency", new HashMap<String, Integer>());

            //count frequency
            selfDefineMap.put("calculate", (Consumer<String>) word -> ((HashMap<String, Integer>) selfDefineMap
                    .get("frequency")).merge(word, 1, 
                    (oldOne, newOne) -> oldOne + newOne));

            //sort
            selfDefineMap.put("sort_frequency", (Supplier<List<SelfDefinePair<String, Integer>>>) () ->
                    ((HashMap<String, Integer>) selfDefineMap.get("frequency")).entrySet().stream()
                            .map(entry -> new SelfDefinePair<>(entry.getKey(), entry.getValue()))
                            .sorted((entryA, entryB) -> (entryA.getSecond().compareTo(entryB.getSecond())) * (-1))
                            .collect(Collectors.toList()));
        }
    };

    /**
     * main entrance of the problem
     * @param args
     */
    public static void main(String[] args) {
        String file = "pride-and-prejudice.txt";
        ((Consumer<String>) dataStorageObject.get("init")).accept(file);
        ((Runnable) stopWordObject.get("init")).run();

        String[] wordArray = ((Supplier<String[]>) dataStorageObject.get("words")).get();
        for (int index = 0; index < wordArray.length; ++index) {
            if (((Function<String, Boolean>) stopWordObject.get("is_stop_word")).apply(wordArray[index])) {
                continue;
            }
            ((Consumer<String>) frequencyObject.get("calculate")).accept(wordArray[index]);
        }

        // 13.3
        frequencyObject.put("output", (Consumer<Map<String, Object>>) (map) -> {
            List<SelfDefinePair<String, Integer>> outputList =
                    ((Supplier<List<SelfDefinePair<String, Integer>>>) map.get("sort_frequency")).get();
            for (int index = 0; index < 25; ++index) {
                System.out.println(outputList.get(index).getFirst() + " - " + outputList.get(index).getSecond());
            }
        });

        ((Consumer<Map<String, Object>>) frequencyObject.get("output")).accept(frequencyObject);
    }
}