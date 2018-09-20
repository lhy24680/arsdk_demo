package map.baidu.ar.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import map.baidu.ar.data.IMapPoiItem;
import map.baidu.ar.detail.IMediaControllerData;
import map.baidu.ar.exception.LocationGetFailException;
import map.baidu.ar.init.ArSdkManager;
import map.baidu.ar.topimage.HeadImage;
import map.baidu.ar.utils.ArBDLocation;
import map.baidu.ar.utils.DistanceByMcUtils;
import map.baidu.ar.utils.INoProGuard;
import map.baidu.ar.utils.Point;

/**
 * Created by zhujingsi on 2017/6/6.
 */

public class ArPoiScenery implements IMapPoiItem, INoProGuard, IMediaControllerData {
    // 必传
    private String uid;
    // 坐标
    private ArPoint point;
    // pv
    private int priority;
    // 景点名称
    private String name;
    // 文本
    private String description;
    // banner图片集合
    @SerializedName("image_url")
    private ArrayList<HeadImage> imageUrl;

    // 来源
    private String source;

    private ArInfoScenery mArInfo;

    // 非服务端传入数据

    // 根据排点逻辑算出是否要在AR模式展示poi
    private boolean isShowInAr;
    // 是否在当前屏显示
    private boolean isShowInScreen;
    // 度秘是否在附近
    private boolean isDuerNear = false;
    // 度秘在NEAR_VALUE 米内泡泡显示为在附近，并出现听音乐度秘
    public static int NEAR_VALUE = 20;

    // 获取距离
    public double getDistance() throws LocationGetFailException {
        ArBDLocation location = ArSdkManager.listener.onGetBDLocation();
        if (location != null) {
            double myX = location.getLongitude();
            double myY = location.getLatitude();
            return DistanceByMcUtils.getDistanceByLL(new Point(myX, myY), new Point(point
                    .getPoint_x(), point.getPoint_y()));
        } else {
            throw new LocationGetFailException();
        }
    }

    @Override
    public boolean isNear() {
        try {
            return getDistance() <= NEAR_VALUE;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isNotFar() {
        if (mArInfo == null) {
            return true;
        }
        return mArInfo.isNotFar();
    }

    // 获取距离文本
    public String getDistanceText() {
        ArBDLocation location = ArSdkManager.listener.onGetBDLocation();
        if (location == null) {
            return "";
        }
        double myX = location.getLongitude();
        double myY = location.getLatitude();
        double mDistance = DistanceByMcUtils.getDistanceByLLToText(new Point(myX, myY),
                new Point(point.getPoint_x(), point.getPoint_y()));
        if (mDistance > 1000) {
            return ((int) mDistance / 100) / 10.0f + "km";
        }
        return (int) mDistance + "m";
    }

    public String getUid() {
        return uid;
    }

    @Override
    public String getMapDistanceText() {
        if (!isNotFar()) {
            return null;
        }
        return getDistanceText();
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public Point getGeoPoint() {
        if (point != null) {
            return new Point(point.getPoint_y(), point.getPoint_x());
        } else {
            return new Point(0, 0);
        }
    }

    public String getName() {
        return name == null ? "" : name;
    }

    @Override
    public float getWeight() {
        return 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isShowInAr() {
        return isShowInAr;
    }

    public void setShowInAr(boolean showInAr) {
        isShowInAr = showInAr;
    }

    public boolean isShowInScreen() {
        return isShowInScreen;
    }

    public void setShowInScreen(boolean showInScreen) {
        isShowInScreen = showInScreen;
    }
    //        public ArrayList<Double> spath;

    /**
     * 判断坐标是否在n米之内
     *
     * @param myX 定位点坐标x
     * @param myY 定位点坐标y
     * @param n   判断多少米
     *
     * @return
     */
    public boolean withinMeters(int myX, int myY, int n) {
        if (point == null) {
            return false;
        }
        double azimuth =
                Math.toDegrees(Math.atan2(Math.abs(point.getPoint_y() - myY), Math.abs(point.getPoint_x() - myX)));
        if (azimuth < n) {
            return true;
        } else {
            return false;
        }
    }

    public ArPoint getPoint() {
        return point;
    }

    public void setPoint(ArPoint point) {
        this.point = point;
    }

    public String getImageUrlText() {
        Gson gson = new Gson();
        return gson.toJson(imageUrl);
    }

    public String getFirstImageUrl() {
        if (imageUrl != null && imageUrl.size() > 0 && imageUrl.get(0) != null) {
            return imageUrl.get(0).getImgUrl();
        } else {
            return "";
        }
    }

    public void setImageUrl(ArrayList<HeadImage> imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ArPoiScenery) {
            if (uid != null && ((ArPoiScenery) o).getUid() != null) {
                return uid.equals(((ArPoiScenery) o).getUid());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (uid != null) {
            return uid.hashCode();
        }

        return super.hashCode();
    }

    public String getSource() {
        return source == null ? "" : source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isDuerNear() {
        return isDuerNear;
    }

    public void setDuerNear(boolean duerNear) {
        isDuerNear = duerNear;
    }

    public void setArInfo(ArInfoScenery arInfo) {
        mArInfo = arInfo;
    }
}
