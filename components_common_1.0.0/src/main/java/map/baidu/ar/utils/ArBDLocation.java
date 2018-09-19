/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package map.baidu.ar.utils;

import android.location.Location;

public class ArBDLocation {
    Double xLongitude; // 经度
    Double xLatitude; // 纬度
    Double xMLongitude; // 墨卡托经度
    Double xMLatitude; // 墨卡托纬度

    public ArBDLocation() {

    }

    public ArBDLocation(Location location) {
        xLongitude = location.getLongitude();
        xLatitude = location.getLatitude();
        xMLongitude = CoordinateConverter.convertLL2MC(xLongitude, xLatitude).get("x");
        xMLatitude = CoordinateConverter.convertLL2MC(xLongitude, xLatitude).get("y");
    }

    public Double getLongitude() {
        return xMLongitude;
    }

    public Double getLatitude() {
        return xMLatitude;
    }
}
