package map.baidu.ar.utils;

import com.baidu.mapframework.location.LocationManager;

import java.util.Date;

/**
 * Created by daju on 2017/7/8.
 */

public class LocUtil {
    private static LocationManager.LocData mLocData;
    private static long locTime = 0;

    public static LocationManager.LocData getCurLocation() {
        long nowTime = new Date().getTime();
        LocationManager.LocData locData = LocationManager.getInstance().getCurLocation(null);

        if (LocationManager.getInstance().isLocationValid() && locData != null) {
            if (mLocData == null || !(nowTime - locTime < 2000
                                              && (Math.abs(locData.longitude - mLocData.longitude) > 1000
                                                          || Math.abs(locData.latitude - mLocData.latitude) > 1000))) {
                // 2秒之内 如果定位距离突然大于1000，则使用上次值
                locTime = nowTime;
                mLocData = new LocationManager.LocData();
                mLocData.latitude = locData.latitude - 0;
                mLocData.buildingId = locData.buildingId;
                mLocData.longitude = locData.longitude + 0;
                mLocData.floorId = locData.floorId;
                //                mLocData.networkLocType =
            }
            return mLocData;
        } else if (mLocData != null && nowTime - locTime < 60000) {
            return mLocData;
        } else {
            return null;
        }
    }
}
