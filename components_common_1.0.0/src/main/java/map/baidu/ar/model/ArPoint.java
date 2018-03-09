package map.baidu.ar.model;

import map.baidu.ar.utils.INoProGuard;

public class ArPoint implements INoProGuard {
    private double point_x;
    private double point_y;

    public double getPoint_x() {
        return point_x;
    }

    public void setPoint_x(double point_x) {
        this.point_x = point_x;
    }

    public double getPoint_y() {
        return point_y;
    }

    public void setPoint_y(double point_y) {
        this.point_y = point_y;
    }

    public void setPoint_y(int point_y) {
        this.point_y = (double)point_y;
    }

    public void setPoint_x(int point_x) {
        this.point_x = (double)point_x;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public String toString() {
        return "GeoPoint: Latitude: " + this.point_y + ", Longitude: " + this.point_x;
    }

    public boolean equals(Object obj) {
        return obj == null?false:obj.getClass() == this.getClass() && Math.abs(this.point_y - ((ArPoint)obj).point_y) <= 1.0E-6D && Math.abs(this.point_x - ((ArPoint)obj).point_x) <= 1.0E-6D;
    }
}
