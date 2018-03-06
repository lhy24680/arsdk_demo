package map.baidu.ar.utils;

/**
 * 
 * 用于记录表示坐标系中的某一点坐标
 *
 */
public class Point {

    /**
     * 经度longitude(或墨卡托横坐标x)
     */
    public double x;
    /**
     * 纬度latitude(或墨卡托纵坐标y)
     */
    public double y;

    /**
     * 
     * @param x 经度lng(墨卡托横坐标)
     * @param y 纬度lat(墨卡托纵坐标)
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
