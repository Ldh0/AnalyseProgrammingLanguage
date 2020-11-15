package week_5;

import java.io.IOException;
import java.util.Map.*;
import java.nio.file.*;
import java.lang.reflect.*;
import java.util.stream.*;
import java.util.*;

/**
 * main solution of 17
 */
class Seventeen{
    /**
     * entrance
     * @param args
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args)
            throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {

        System.out.println("Please provide a class name: ");
        String string = //"mainInterface";
        new Scanner(System.in).nextLine();

        ControllerForFrequency controllerForFrequency = new ControllerForFrequency();
        controllerForFrequency.init(args[0]);
        controllerForFrequency.run();

        new Reflection().init(string);
    }
}

/**
 * manage stop words
 */
class StopWord extends mainInterface {
    protected List<String> stopWordList;

    /**
     *
     * @return
     */
    public String info(){
        String result = new mainInterface().info() + ": " + this.stopWordList.getClass().getName();
        return result;
    }

    /**
     * judge stop words
     * @param word
     * @return
     */
    public boolean is_stop_word(String word){
        boolean b = this.stopWordList.contains(word);
        return b;
    }

    /**
     * init
     * @throws IOException
     */
    public void init() throws IOException {
        this.stopWordList = new ArrayList<>(Arrays.asList(new String
                (Files.readAllBytes(Paths.get("../stop_words.txt"))).toLowerCase().split(",")));
        for (char c = 'a'; c <= 'z'; ++c) {
            this.stopWordList.add(Character.toString(c));
        }
    }
}

/**
 * data storage
 */
class DataStorage extends mainInterface {
    protected String string = "";

    /**
     * infor
     * @return
     */
    public String info(){
        mainInterface tf = new mainInterface();
        return tf.info() + ": " + this.string.getClass().getName();
    }

    /**
     * initialize
     * @param pathOfFile
     * @throws IOException
     */
    public void init(String pathOfFile) throws IOException {
        String words = new String(Files.readAllBytes(Paths.get(pathOfFile))).toLowerCase();
        this.string = words.replaceAll("[\\W_]+", " ");
    }

    /**
     *
     * @return
     */
    public List<String> words(){
        return Arrays.asList(this.string.split(" "));
    }
}

/**
 * the main interface for all
 */
class mainInterface { public String info(){ return this.getClass().getName(); }}

/**
 * manage frequency
 */
class Frequency extends mainInterface {
    protected HashMap<String, Integer> wordFrequencyHashMap = new HashMap<>();

    /**
     * sort it
     * @return
     */
    public Stream<Entry<String, Integer>> sorted(){
        return this.wordFrequencyHashMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));
    }

    /**
     * count frequency
     * @param word
     */
    public void increment_count(String word){
        if (wordFrequencyHashMap.containsKey(word)) {
            wordFrequencyHashMap.put(word, wordFrequencyHashMap.get(word) + 1);
        } else {
            wordFrequencyHashMap.put(word, 1);
        }
    }

    /**
     *
     * @return
     */
    public String info(){
        mainInterface tf = new mainInterface();
        return tf.info()  + ": " + this.wordFrequencyHashMap.getClass().getName();
    }
}

/**
 * for reflection
 */
class Reflection {
    /**
     * initialize
     * @param ClassName
     * @throws ClassNotFoundException
     */
    public void init(String ClassName) throws ClassNotFoundException{
        Class aClass = Class.forName(ClassName);

        /**
         * output
         */
        for (Field f: aClass.getFields())
            System.out.println("Field: " + f.getName() + ". Type: " + f.getType());
        for (Field f: aClass.getDeclaredFields())
            System.out.println("Field: " + f.getName() + ". Type: " + f.getType());
        for (Method m: aClass.getMethods())
            System.out.println("Method: " + m.getName());
        for (Method m: aClass.getDeclaredMethods())
            System.out.println("Method: " + m.getName());
        for (Class interfaceIndex: aClass.getInterfaces())
            System.out.println("Interface: " + interfaceIndex.getName());
        while (aClass != null) {
            System.out.println("Super class: " + aClass.getName());
            aClass = aClass.getSuperclass();
        }
    }
}

/**
 * controller for frequency
 */
class ControllerForFrequency extends mainInterface {
    private StopWord stopWord;
    private Method[] methodOfStopWord;
    private DataStorage dataStorage;
    private Method[] methodOfDataStorage;
    private Frequency frequency;
    private Method[] methodOfFrequency;

    /**
     * init
     * @param fileName
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void init(String fileName) throws InvocationTargetException, IllegalAccessException {
        this.dataStorage = new DataStorage();
        this.methodOfDataStorage = dataStorage.getClass().getDeclaredMethods();
        this.methodOfDataStorage[1].invoke(dataStorage, fileName);

        this.stopWord = new StopWord();
        this.methodOfStopWord = stopWord.getClass().getDeclaredMethods();
        this.methodOfStopWord[1].invoke(stopWord);

        this.frequency = new Frequency();
        this.methodOfFrequency = frequency.getClass().getDeclaredMethods();
    }

    /**
     * run
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void run() throws InvocationTargetException, IllegalAccessException {
        for (String string : (List<String>) (methodOfDataStorage[2].invoke(dataStorage))) {
            if (!(boolean) methodOfStopWord[2].invoke(stopWord, string)) {
                methodOfFrequency[2].invoke(frequency, string);
            }
        }
        ((Stream<Entry<String, Integer>>) this.methodOfFrequency[1].invoke(frequency))
                .limit(25).forEach(entry -> {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        });
    }
}