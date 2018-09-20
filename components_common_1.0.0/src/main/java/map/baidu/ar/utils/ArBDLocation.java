package map.baidu.ar.utils;

import android.location.Location;

/**
 * 坐标类
 */
public class ArBDLocation {
    double xLongitude; // 经度
    double xLatitude; // 纬度
    double xMLongitude; // 墨卡托经度
    double xMLatitude; // 墨卡托纬度

    public ArBDLocation() {
    }

    public ArBDLocation(Location location) {
        xLongitude = location.getLongitude();
        xLatitude = location.getLatitude();
        xMLongitude = CoordinateConverter.convertLL2MC(xLongitude, xLatitude).get("x");
        xMLatitude = CoordinateConverter.convertLL2MC(xLongitude, xLatitude).get("y");
    }

    public double getLongitude() {
        return xMLongitude;
    }

    public double getLatitude() {
        return xMLatitude;
    }

    public void setLatitude(double lat) {
        this.xMLatitude = lat;
    }

    public void setLongitude(double lon) {
        this.xMLongitude = lon;
    }
}
