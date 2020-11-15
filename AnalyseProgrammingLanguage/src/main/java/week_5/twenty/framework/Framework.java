package week_5.twenty.framework;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Scanner;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * framework
 */
class Framework {
    /**
     * main entrance
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static void main(String[] args)
            throws IOException, ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter class name to use:");
        String className = scanner.nextLine();
//                "Application1";
        System.out.println("Loading and instantiating " + className + "...");

        Class aClass = new URLClassLoader(new URL[]{new URL(
                "file:///home/runner/SWE212/week_5/twenty/deploy/app.jar")}).loadClass(className);

        if (aClass != null) {
            System.out.println("Enter the file path to use:");
            wordListInterface wordListInterface = (wordListInterface) aClass.getDeclaredConstructor().newInstance();
            List<String> wordAfterFilter = wordListInterface.getArrayListOfAllWordAfterFilter(scanner.nextLine());
            wordListInterface.output(wordAfterFilter).forEach(entry -> {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            });
        }

        scanner.close();
    }
}