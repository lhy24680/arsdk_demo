package map.baidu.ar.utils;

/**
 * 用于记录表示坐标系中的某一点坐标
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
     * @param x 经度lng(墨卡托横坐标)
     * @param y 纬度lat(墨卡托纵坐标)
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(int x, int y) {
        this.y = (double) y;
        this.x = (double) x;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public String toString() {
        return "GeoPoint: Latitude: " + this.y + ", Longitude: " + this.x;
    }

    public boolean equals(Object obj) {
        return obj == null ? false : obj.getClass() == this.getClass() && Math.abs(this.y - ((Point) obj).y) <= 1.0E-6D
                && Math.abs(this.x - ((Point) obj).x) <= 1.0E-6D;
    }
}
