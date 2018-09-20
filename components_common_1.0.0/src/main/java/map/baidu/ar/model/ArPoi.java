package map.baidu.ar.model;

import map.baidu.ar.exception.LocationGetFailException;
import map.baidu.ar.init.ArSdkManager;
import map.baidu.ar.utils.ArBDLocation;
import map.baidu.ar.utils.DistanceByMcUtils;
import map.baidu.ar.utils.INoProGuard;
import map.baidu.ar.utils.Point;

public class ArPoi implements INoProGuard {
    // 必传
    private String uid;
    // 坐标x
    private String ptX;
    // 坐标y
    private String ptY;
    // 权重
    private int rank;

    // 泡泡集合
    private BubbleList bubble;
    // 来源
    private String source;

    // 非服务端传入数据

    // 是否在当前屏显示
    private boolean isShowInScreen;
    // 度秘是否在附近
    private boolean isDuerNear = false;
    // 度秘在NEAR_VALUE 米内泡泡显示为在附近，并出现听音乐度秘
    public static int NEAR_VALUE = 20;

    // poi坐标与定位坐标连线与南北方向的夹角
    private float mAzimuth = 0;
    // 通过楼层算出的上下偏移量
    private int mAzimuthX = 0;
    // 根据排点逻辑算出是否要在AR模式展示poi
    private boolean isShowInAr = false;
    // 楼层避让系数
    private int dodgeLevel = 0;

    public ArPoi() {
    }

    // 获取距离
    public double getDistance() throws LocationGetFailException {
        ArBDLocation location = ArSdkManager.listener.onGetBDLocation();
        if (location != null) {
            double myX = location.getLongitude();
            double myY = location.getLatitude();
            return DistanceByMcUtils.getDistanceByLL(new Point(myX, myY), new Point(Double.valueOf(ptX), Double
                    .valueOf(ptY)));
        } else {
            throw new LocationGetFailException();
        }
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
                new Point(Double.valueOf(ptX), Double.valueOf(ptY)));
        if (mDistance > 1000) {
            return ((int) mDistance / 100) / 10.0f + "km";
        }
        return (int) mDistance + "m";
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Point getGeoPoint() {
        return new Point(Double.valueOf(ptY), Double.valueOf(ptX));
    }

    public float getWeight() {
        return 0;
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
        double azimuth =
                Math.toDegrees(Math.atan2(Math.abs(Double.valueOf(ptY) - myY), Math.abs(Double.valueOf(ptX) - myX)));
        if (azimuth < n) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ArPoi) {
            if (uid != null && ((ArPoi) o).getUid() != null) {
                return uid.equals(((ArPoi) o).getUid());
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

    public double getPtX() {
        return Double.valueOf(ptX);
    }

    public void setPtX(String ptX) {
        this.ptX = ptX;
    }

    public double getPtY() {
        return Double.valueOf(ptY);
    }

    public void setPtY(String ptY) {
        this.ptY = ptY;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public BubbleList getBubble() {
        return bubble;
    }

    public void setBubble(BubbleList bubble) {
        this.bubble = bubble;
    }

    public int getmAzimuthX() {
        return mAzimuthX;
    }

    public void setmAzimuthX(int reFloor) {
        this.mAzimuthX = ((reFloor * 2) + dodgeLevel) * 8;
    }

    /**
     * 设置屏幕竖方向高度
     *
     * @param floor      poi所在楼层
     * @param floorLenth 当前用户所在楼层
     *
     * @return true要显示在推荐策略中，flase不显示在推荐策略中
     */
    public boolean setmAzimuthX(int floor, int floorLenth) {
        if (!setDodgeLevel(floor - floorLenth)) {
            mAzimuthX = -9999;
            isShowInAr = false;
            return isShowInAr;
        }
        isShowInAr = true;
        //        this.mAzimuthX = (((floor - floorLenth) * 2) + dodgeLevel) * 5;
        setmAzimuthX(floor - floorLenth);
        return isShowInAr;
    }

    public float getmAzimuth() {
        return mAzimuth;
    }

    public void setmAzimuth(double myX, double myY) {
        double azimuth = Math.toDegrees(Math.atan2(getPtY() - myY, getPtX() - myX)) + 90;
        mAzimuth = (float) azimuth;
    }

    /**
     * 设置避让系数，0，1，-1，为当前层
     *
     * @param reFloor 当前相对楼层数
     *
     * @return true:要显示，false：避无可避，隐藏
     */
    public boolean setDodgeLevel(int reFloor) {
        if (reFloor == 0) {
            if (dodgeLevel == 0) {
                dodgeLevel = 1;
                return true;
            } else if (dodgeLevel == 1) {
                dodgeLevel = -1;
                return true;
            } else {
                dodgeLevel = -999;
                return false;
            }
        }

        if (dodgeLevel == 0) {
            dodgeLevel = reFloor / Math.abs(reFloor);
            return true;
        } else {
            dodgeLevel = -999;
            return false;
        }
    }

    public int getDodgeLevel() {
        return dodgeLevel;
    }

    public void resetDodgeLevel() {
        dodgeLevel = 0;
    }

}
