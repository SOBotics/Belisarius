package bugs.stackoverflow.belisarius.utils;

class ClassUtils {

    static String getClassName(String className) {
        return className.substring(className.lastIndexOf('.') + 1).trim();
    }
}
