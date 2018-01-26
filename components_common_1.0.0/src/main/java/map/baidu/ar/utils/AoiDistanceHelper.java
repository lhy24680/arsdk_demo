package map.baidu.ar.utils;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comjni.tools.AppTools;

import java.util.ArrayList;

import map.baidu.ar.utils.callback.Tuple;

/**
 * 计算点与Aoi距离最近的点
 * <p>
 * Created by lixiang34 on 2017/6/16.
 */
public class AoiDistanceHelper {
    /**
     * 将ArrayList<float[]>转换为ArrayList<ArrayList<Point>>类型，方便计算
     */
    public static ArrayList<ArrayList<GeoPoint>> getAoiList(ArrayList<float[]> aoiListOriginal) {
        ArrayList<ArrayList<GeoPoint>> aoiListTarget = new ArrayList<>();
        for (float[] aoi : aoiListOriginal) {
            if (aoi != null) {
                ArrayList<GeoPoint> aoiTarget = new ArrayList<>();
                for (int i = 0; i + 1 < aoi.length; i = i + 2) {
                    GeoPoint point = new GeoPoint(aoi[i + 1], aoi[i]);
                    aoiTarget.add(point);
                }
                aoiListTarget.add(aoiTarget);
            }
        }
        return aoiListTarget;
    }

    /**
     * 获取一个点集合构成的面与指定点距离最近的点
     *
     * @param newLocation 指定点
     * @param aoiList     点集合构成的面
     *
     * @return 点与面距离最近的点, 以及这个点和指定点的距离
     */
    public static Tuple<GeoPoint, Double> getNearestPoint(GeoPoint newLocation,
                                                          ArrayList<ArrayList<GeoPoint>> aoiList) {
        GeoPoint nearestPoint = null;
        double nearestDistance = -1;
        for (ArrayList<GeoPoint> aoi : aoiList) {
            if (aoi != null) {
                for (int i = 0; i < aoi.size(); i++) {
                    int nestIndex = aoi.size() > i + 1 ? i + 1 : 0;
                    GeoPoint aPoint = aoi.get(i);
                    GeoPoint bPoint = aoi.get(nestIndex);
                    if (aPoint != null && bPoint != null) {
                        Tuple<GeoPoint, Double> currentNearestPoint = getNearestPoint(newLocation, aPoint, bPoint);
                        if (nearestDistance == -1 || currentNearestPoint.getItem2() < nearestDistance) {
                            nearestPoint = currentNearestPoint.getItem1();
                            nearestDistance = currentNearestPoint.getItem2();
                        }
                    }
                }
            }
        }
        return new Tuple<>(nearestPoint, nearestDistance);
    }

    private static Tuple<GeoPoint, Double> getNearestPoint(GeoPoint point, GeoPoint lineA, GeoPoint lineB) {
        double distancePA = getPointsDistance(point, lineA);
        double distanceAB = getPointsDistance(lineA, lineB);
        double distancePB = getPointsDistance(lineB, point);
        if (distancePA < 1) {
            return new Tuple<>(lineA, distancePA);
        } else if (distancePB < 1) {
            return new Tuple<>(lineB, distancePB);
        } else if (distanceAB < 1) {
            return new Tuple<>(lineA, distancePA);
        }

        if (distancePA * distancePA >= distancePB * distancePB + distanceAB * distanceAB) {
            return new Tuple<>(lineB, distancePB);
        } else if (distancePB * distancePB >= distancePA * distancePA + distanceAB * distanceAB) {
            return new Tuple<>(lineA, distancePA);
        }

        double x0 = point.getLongitude();
        double y0 = point.getLatitude();
        double x1 = lineA.getLongitude();
        double y1 = lineA.getLatitude();
        double x2 = lineB.getLongitude();
        double y2 = lineB.getLatitude();

        double k = ((x0 - x1) * (x2 - x1) + (y0 - y1) * (y2 - y1)) / ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        double x = x1 + k * (x2 - x1);
        double y = y1 + k * (y2 - y1);
        double distance = Math.sqrt((x - x0) * (x - x0) + (y - y0) * (y - y0));
        return new Tuple<>(new GeoPoint(y, x), distance);
    }

    private static double getPointsDistance(GeoPoint point1, GeoPoint point2) {
        return AppTools.getDistanceByMc(point1, point2);
    }
}
