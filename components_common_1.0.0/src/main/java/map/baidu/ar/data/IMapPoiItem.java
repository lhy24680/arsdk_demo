package map.baidu.ar.data;

import map.baidu.ar.exception.LocationGetFailException;
import map.baidu.ar.utils.Point;

/**
 * 底图上的Poi需要的数据
 */
public interface IMapPoiItem {

    Point getGeoPoint();

    String getName();

    float getWeight();

    String getUid();

    String getMapDistanceText();

    double getDistance() throws LocationGetFailException;

    boolean isNear();

    boolean isNotFar();
}
