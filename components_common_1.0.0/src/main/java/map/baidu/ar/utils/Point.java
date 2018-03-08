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
    //    public String toString() {
//        return "Point [x=" + this.getDoubleX() + ", y=" + this.getDoubleY() + "]";
//    }
//
//    public int hashCode() {
//        int prime = true;
//        int result = 1;
//        int result = 31 * result + this.getIntX();
//        result = 31 * result + this.getIntY();
//        return result;
//    }
//
//    public boolean equals(Object obj) {
//        if(this == obj) {
//            return true;
//        } else if(obj == null) {
//            return false;
//        } else if(this.getClass() != obj.getClass()) {
//            return false;
//        } else {
//            com.baidu.platform.comapi.basestruct.Point other = (com.baidu.platform.comapi.basestruct.Point)obj;
//            return this.getDoubleX() != other.getDoubleX()?false:this.getDoubleY() == other.getDoubleY();
//        }
//    }
//
//    public String toQuery() {
//        return String.format("(%d,%d)", new Object[]{Integer.valueOf(this.getIntX()), Integer.valueOf(this.getIntY())});
//    }
}
