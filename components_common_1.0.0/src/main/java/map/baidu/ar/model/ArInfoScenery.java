package map.baidu.ar.model;

import com.baidu.mapframework.location.LocationManager;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import map.baidu.ar.data.IAoiInfo;
import map.baidu.ar.utils.AoiDistanceHelper;
import map.baidu.ar.utils.INoProGuard;
import map.baidu.ar.utils.ListUtils;
import map.baidu.ar.utils.LocUtil;
import map.baidu.ar.utils.callback.SafeTuple;
import map.baidu.ar.utils.callback.Tuple;

/**
 * Created by zhujingsi on 2017/6/6.
 */
public class ArInfoScenery implements IAoiInfo, INoProGuard {

    private static final int FAR_DISTANCE_INTERVAL = 5000;
    // 子景点集合
    private ArrayList<ArPoiScenery> son;
    // 父景点
    private ArPoiScenery father;
    // aoi面
    private ArrayList<float[]> aois;
    private static final double AOI_NEAR = 100;


    public ArPoiScenery getFather() {
        return father;
    }

    public void setFather(ArPoiScenery father) {
        this.father = father;
    }

    @Override
    public ArrayList<float[]> getAois() {
        return aois;
    }

    public void setAois(ArrayList<float[]> aois) {
        this.aois = aois;
    }

    public void init() {
        for (int i = 0; i < ListUtils.getCount(son); i++) {
            ArPoiScenery poi = ListUtils.getItem(son, i);
            if (poi != null) {
                poi.setArInfo(this);
            }
        }
    }


    @Override
    public ArrayList<ArPoiScenery> getSon() {
        return son;
    }

    public void setSon(ArrayList<ArPoiScenery> son) {
        this.son = son;
    }


    /**
     * 判断用户在不在景区内
     *
     * @return 用户在不在景区内
     */
    @Override
    public boolean getIsInAoi() {
        if (aois == null || aois.size() == 0) {
            return false;
        }
        int x, y;
        LocationManager.LocData locData = LocUtil.getCurLocation();
        if (locData != null) {
            x = (int) (locData.longitude);
            y = (int) (locData.latitude);
        } else {
//            MToast.show(getContext(), "暂时无法获取您的位置");
            return false;
        }
        return getIsInAoi(x, y);
    }

    @Override
    public boolean getIsInAoi(float x, float y) {
        for (int i = 0; i < aois.size(); i++) {
            if (isInView(aois.get(i), x, y) || isNearAoi(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否在aoi面附近的 AOI_NEAR 米以内
     * 用于解决在售票处被判定为 非景区 的问题
     *
     * @return true为在附近 AOI_NEAR 米以内，false为在aoi面的 AOI_NEAR 米以外
     */
    public boolean isNearAoi(float x, float y) {
        Tuple<GeoPoint, Double> tuple = AoiDistanceHelper.getNearestPoint(new GeoPoint(y, x),
                AoiDistanceHelper.getAoiList(aois));
        return tuple.getItem2() < AOI_NEAR;
    }

    @Override
    public GeoPoint getNearestPoint(GeoPoint newLocation) {
        Tuple<GeoPoint, Double> result = AoiDistanceHelper.getNearestPoint(newLocation,
                AoiDistanceHelper.getAoiList(aois));
        if (result == null) {
            return null;
        }
        return result.getItem1();
    }

    /**
     * 判断点是否在范围内
     *
     * @param points       坐标点集合
     * @param touchStartX0 点击的x轴坐标值
     * @param touchStartY0 点击
     * @return
     */
    private boolean isInView(float[] points, float touchStartX0, float touchStartY0) {
        try {
            int x = 2;
            int y = 2;
            for (int i = 0; i < points.length - 2; i += 2) {
                if ((points[i] > touchStartX0 && touchStartX0 > points[i + 2] && touchStartY0 > points[i + 1]
                        && touchStartY0 > points[i + 3]) || (points[i] < touchStartX0
                        && touchStartX0 < points[i + 2] && touchStartY0 > points[i + 1]
                        && touchStartY0 > points[i + 3])) {

                    x++;
                }
                if ((points[i + 1] > touchStartY0 && touchStartY0 > points[i + 3] && touchStartX0 > points[i]
                        && touchStartX0 > points[i + 2]) || (points[i + 1] < touchStartY0
                        && touchStartY0 < points[i + 3] && touchStartX0 > points[i]
                        && touchStartX0 > points[i + 2])) {
                    y++;
                }
            }
            if ((points[0] > touchStartX0 && touchStartX0 > points[points.length - 2] && touchStartY0 > points[1]
                    && touchStartY0 > points[points.length - 1]) || (points[0] < touchStartX0
                    && touchStartX0 < points[points.length - 2] && touchStartY0 > points[1]
                    && touchStartY0 > points[points.length - 1])) {
                x++;
            }
            if ((points[1] > touchStartY0 && touchStartY0 > points[points.length - 1] && touchStartX0 > points[0]
                    && touchStartX0 > points[points.length - 2]) || (points[1] < touchStartY0
                    && touchStartY0 < points[points.length - 1] && touchStartX0 > points[0]
                    && touchStartX0 > points[points.length - 2])) {
                y++;
            }
            if (1 == x % 2 && 1 == y % 2) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private SafeTuple<GeoPoint, Boolean> mNotFarCache;

    boolean isNotFar() {
        LocationManager.LocData locData = LocUtil.getCurLocation();
        if (locData == null) {
            return true;
        }
        GeoPoint currentLocation = new GeoPoint(locData.latitude, locData.longitude);
        if (mNotFarCache != null && currentLocation.equals(mNotFarCache.getItem1())) {
            return mNotFarCache.getItem2();
        }
        boolean result = isNotFarInternal(currentLocation);
        mNotFarCache = new SafeTuple<>(currentLocation, result);
        return result;
    }

    private boolean isNotFarInternal(@NotNull GeoPoint currentLocation) {
        if (getIsInAoi((float) currentLocation.getLongitude(), (float) currentLocation.getLatitude())) {
            return true;
        }
        Tuple<GeoPoint, Double> result = AoiDistanceHelper.getNearestPoint(
                currentLocation, AoiDistanceHelper.getAoiList(getAois()));
        if (result == null || result.getItem2() == null) {
            return true;
        }
        double distance = result.getItem2();
        return distance < FAR_DISTANCE_INTERVAL;
    }
}