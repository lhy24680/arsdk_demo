package map.baidu.ar.utils;

/**
 * Created by kanglichen on 2018/3/7.
 */

public class DistanceByMcUtils {

    /**
     * 墨卡托计算
     * @param pt1
     * @param pt2
     * @return
     */
    public static double getDistanceByMc(Point pt1, Point pt2) {
        return Math.sqrt(Math.pow(pt1.x - pt2.x, 2) + Math.pow(pt1.y - pt2.y, 2));
    }

    /**
     * 墨卡托转经纬度后计算距离
     *
     * @param pt1
     * @param pt2
     *
     * @return
     */
    public static double getDistanceByLL(Point pt1, Point pt2) {
        Point point1 = CoordinateConverter.convertMC2LLp(pt1.getY(), pt1.getX());
        Point point2 = CoordinateConverter.convertMC2LLp(pt2.getY(), pt2.getX());
        return getDistanceByLL(point1.getY(),point1.getX(), point2.getY(), point2.getX());
    }

    /**
     * 墨卡托转经纬度后计算距离
     *
     * @param pt1
     * @param pt2
     *
     * @return
     */
    public static double getDistanceByLLToText(Point pt1, Point pt2) {
        Point point1 = CoordinateConverter.convertMC2LLpToText(pt1.getY(), pt1.getX());
        Point point2 = CoordinateConverter.convertMC2LLpToText(pt2.getY(), pt2.getX());
        return getDistanceByLL(point1.getY(),point1.getX(), point2.getY(), point2.getX());
    }

    private static double EARTH_RADIUS = 6378137;//地球半径 米

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 用标准的球面距离计算公式
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistanceByLL(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
//        s = Math.round(s * 10000) / 10000;
        return s;
    }

}
