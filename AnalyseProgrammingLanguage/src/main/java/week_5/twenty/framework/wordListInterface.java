package week_5.twenty.framework;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * the interface for sorting and outputing
 */
public interface wordListInterface {
    /**
     * output
     * @param list
     * @return
     */
    Stream<Entry<String, Integer>> output(List<String> list);

    /**
     *
     * @param string
     * @return
     * @throws IOException
     */
    List<String> getArrayListOfAllWordAfterFilter(String string) throws IOException;
}

