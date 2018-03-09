package map.baidu.ar.utils;


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
    public static ArrayList<ArrayList<Point>> getAoiList(ArrayList<float[]> aoiListOriginal) {
        ArrayList<ArrayList<Point>> aoiListTarget = new ArrayList<>();
        for (float[] aoi : aoiListOriginal) {
            if (aoi != null) {
                ArrayList<Point> aoiTarget = new ArrayList<>();
                for (int i = 0; i + 1 < aoi.length; i = i + 2) {
                    Point point = new Point(aoi[i + 1], aoi[i]);
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
    public static Tuple<Point, Double> getNearestPoint(Point newLocation,
                                                          ArrayList<ArrayList<Point>> aoiList) {
        Point nearestPoint = null;
        double nearestDistance = -1;
        for (ArrayList<Point> aoi : aoiList) {
            if (aoi != null) {
                for (int i = 0; i < aoi.size(); i++) {
                    int nestIndex = aoi.size() > i + 1 ? i + 1 : 0;
                    Point aPoint = aoi.get(i);
                    Point bPoint = aoi.get(nestIndex);
                    if (aPoint != null && bPoint != null) {
                        Tuple<Point, Double> currentNearestPoint = getNearestPoint(newLocation, aPoint, bPoint);
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

    private static Tuple<Point, Double> getNearestPoint(Point point, Point lineA, Point lineB) {
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

        double x0 = point.getX();
        double y0 = point.getY();
        double x1 = lineA.getX();
        double y1 = lineA.getY();
        double x2 = lineB.getX();
        double y2 = lineB.getY();

        double k = ((x0 - x1) * (x2 - x1) + (y0 - y1) * (y2 - y1)) / ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        double x = x1 + k * (x2 - x1);
        double y = y1 + k * (y2 - y1);
        double distance = Math.sqrt((x - x0) * (x - x0) + (y - y0) * (y - y0));
        return new Tuple<>(new Point(y, x), distance);
    }

    private static double getPointsDistance(Point point1, Point point2) {
        return DistanceByMcUtils.getDistanceByLL(point1, point2);
    }
}
