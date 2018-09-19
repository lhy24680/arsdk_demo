package map.baidu.ar.utils;

import static android.content.Context.LOCATION_SERVICE;

import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import map.baidu.ar.init.ArSdkManager;

/**
 * 手机自带定位工具类
 */

public class LocNativeUtil {

    private static LocNativeUtil mInstance = null;
    // 通过LocationManager来获取位置
    private static LocationManager locationManager;
    // 显示位置提供器的类型
    private static String provider;
    // 判断是否有访问位置的权限
    Location location = null;

    public static synchronized LocNativeUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LocNativeUtil(context);
        }
        return mInstance;
    }

    private LocNativeUtil(Context context) {
        if (locationManager == null || provider == null) {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            // 获取所有可用的位置提供器
            List<String> providerList = locationManager.getProviders(true);
            if (providerList.contains(LocationManager.GPS_PROVIDER)) {
                // 当前位置提供器为GPS
                provider = LocationManager.GPS_PROVIDER;
            } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            } else {
                // 当前没有可用的位置提供器时用Toast提醒用户
                Toast.makeText(context, "请打开GPS或者网络来确定你的位置", Toast.LENGTH_LONG).show();
                //                return null;
            }
        }
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {

        if (!checkPermissions(ArSdkManager.getInstance().getAppContext())) {
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return null;
        }
        location = locationManager.getLastKnownLocation(provider);
        return location;
        //        if (location != null) {
        //            showLocation(location);
        //        }
        //        locationManager.requestLocationUpdates(provider,5*1000,1,locationListener);
    }

    /**
     * 检查是否开启定位权限
     *
     * @return
     */
    public boolean checkPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                         == PackageManager.PERMISSION_GRANTED) && (
                    context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private static int checkSelfPermission(String accessFineLocation) {
        return 0;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void showLocation(Location location) {
        String currentPosition =
                "latitude is " + location.getLatitude() + "\n" + "longitudeude is" + location.getLongitude();
    }

}
