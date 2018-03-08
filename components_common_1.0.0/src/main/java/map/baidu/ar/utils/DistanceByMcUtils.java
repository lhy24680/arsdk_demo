package map.baidu.ar.utils;


/**
 * Created by kanglichen on 2018/3/7.
 */

public class DistanceByMcUtils {

    public static double getDistanceByMc(Point pt1, Point pt2) {
        return Math.sqrt(Math.pow(pt1.x-pt2.x,2) + Math.pow(pt1.y-pt2.y,2));
    }
}
