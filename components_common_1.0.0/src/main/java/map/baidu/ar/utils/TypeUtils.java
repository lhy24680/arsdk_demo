package map.baidu.ar.utils;


/**
 * Created by 享 on 2016/8/23.
 */
public class TypeUtils {

    public static <T> T safeCast(Object obj, Class<T> classType) {
        if (obj == null || classType == null || !classType.isInstance(obj)) {
            return null;
        }
        return (T) obj;
    }
}
