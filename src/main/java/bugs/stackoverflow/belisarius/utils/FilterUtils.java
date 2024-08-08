package bugs.stackoverflow.belisarius.utils;

public class FilterUtils {
    public static String pluralise(String word, int count) {
        return count != 1 ? word + "s" : word;
    }
}
