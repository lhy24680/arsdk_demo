package map.baidu.ar.utils;

import static android.content.Context.LOCATION_SERVICE;

import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by kanglichen on 2018/3/6.
 */

public class LocNativeUtil {
    //通过LocationManager来获取位置
    private static LocationManager locationManager;
    //显示位置提供器的类型
    private static String provider;

    public static Location getLocation(Context context) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            //当前位置提供器为GPS
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            //当前没有可用的位置提供器时用Toast提醒用户
            //            Toast.makeText(this, "请打开GPS或者网络来确定你的位置", Toast.LENGTH_LONG).show();
            return null;
        }
        //判断是否有访问位置的权限
        Location location = null;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
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

    private static int checkSelfPermission(String accessFineLocation) {
        return 0;
    }

    LocationListener locationListener=new LocationListener() {
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



    private void showLocation(Location location){
        String currentPosition="latitude is "+location.getLatitude()+"\n" +
                "longitudeude is"+location.getLongitude();
    }

}
