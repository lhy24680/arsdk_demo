package map.baidu.ar.utils;


public class TypeUtils {

    public static <T> T safeCast(Object obj, Class<T> classType) {
        if (obj == null || classType == null || !classType.isInstance(obj)) {
            return null;
        }
        return (T) obj;
    }
}
