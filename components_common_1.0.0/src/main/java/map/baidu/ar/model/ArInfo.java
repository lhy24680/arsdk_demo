package map.baidu.ar.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import map.baidu.ar.utils.AoiDistanceHelper;
import map.baidu.ar.utils.INoProGuard;
import map.baidu.ar.utils.Point;
import map.baidu.ar.utils.callback.Tuple;
/**
 * Created by zhujingsi on 2017/6/6.
         */
public class ArInfo implements INoProGuard {

    // 楼块儿面集合
    private ArrayList<ArGeo> geos;
    // 楼块uid
    private String uid;
    // 楼块名称
    private String name;

    // poi坐标集合与定位坐标连线与南方向的夹角 集合
    private double[] mAzimuth;

    // poi坐标集合与定位坐标连线与南方向的夹角 中点
    private double mMidAzimuth;

    // 通过楼层算出的上下偏移量
    private int mAzimuthX = 0;

    // 楼层避让系数
    private int dodgeLevel = 0;

    private double[] mPoints;

    // 根据排点逻辑算出是否要在AR模式展示poi
    private boolean isShowInAr = false;
    // 是否在aoi内,如果在aoi内，则实际poi的角度集合大于180度
    private boolean isInAoi = false;
    // 该poi所有坐标连线集合组成的合角
    private Angle mAngle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ArGeo> getGeos() {
        return geos;
    }

    public void setGeos(ArrayList<ArGeo> geos) {
        this.geos = geos;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private double myX;
    private double myY;

    // 算距离
    public int getDistance(double myX, double myY) {
        if (mPoints == null || mPoints.length < 1) {
            getPoints();
        }
        isInAoi = isInView(getPoints(), myX, myY);
        if (isInAoi) {
            return 0;
        }
        ArrayList<ArrayList<Point>> todo = new ArrayList<>();
        todo.add(0, (ArrayList<Point>) getGeosList());
        Tuple<Point, Double> tuple = AoiDistanceHelper.getNearestPoint(new Point(myY, myX), todo);
        double distance = tuple.getItem2();
        int dis = (int) distance;
        if (dis <= 0) {
            isInAoi = true;
            isShowInAr = false;
        }
        return dis;
    }

    public String getDistanceText(double myX, double myY) {
        int distance = getDistance(myX, myY);
        return distance <= 0 ? "1m" : String.format("%dm", distance);
    }

    public boolean isInAoi() {
        return isInAoi;
    }

    /**
     * 判断点是否在范围内
     *
     * @param points       坐标点集合
     * @param touchStartX0 点击的x轴坐标值
     * @param touchStartY0 点击
     *
     * @return
     */
    private boolean isInView(double[] points, double touchStartX0, double touchStartY0) {
        try {
            int x = 2;
            int y = 2;
            for (int i = 0; i < points.length - 2; i += 2) {
                if ((points[i] > touchStartX0 && touchStartX0 > points[i + 2] && touchStartY0 > points[i + 1]
                        && touchStartY0 > points[i + 3]) || (points[i] < touchStartX0
                        && touchStartX0 < points[i + 2]
                        && touchStartY0 > points[i + 1]
                        && touchStartY0 > points[i + 3])) {

                    x++;
                }
                if ((points[i + 1] > touchStartY0 && touchStartY0 > points[i + 3] && touchStartX0 > points[i]
                        && touchStartX0 > points[i + 2]) || (points[i + 1] < touchStartY0
                        && touchStartY0 < points[i + 3]
                        && touchStartX0 > points[i]
                        && touchStartX0 > points[i + 2])) {
                    y++;
                }
            }
            if ((points[0] > touchStartX0 && touchStartX0 > points[points.length - 2] && touchStartY0 > points[1]
                    && touchStartY0 > points[points.length - 1]) || (points[0] < touchStartX0
                    && touchStartX0 < points[points.length
                    - 2] && touchStartY0 > points[1]
                    && touchStartY0 > points[points.length
                    - 1])) {
                x++;
            }
            if ((points[1] > touchStartY0 && touchStartY0 > points[points.length - 1] && touchStartX0 > points[0]
                    && touchStartX0 > points[points.length - 2]) || (points[1] < touchStartY0
                    && touchStartY0 < points[points.length
                    - 1] && touchStartX0 > points[0]
                    && touchStartX0 > points[points.length
                    - 2])) {
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

    public boolean isShowInAr() {
        return isShowInAr;
    }

    public void setShowInAr(boolean showInAr) {
        isShowInAr = showInAr;
    }

    private void setmAzimuthX(int dodgeLevel) {
        this.mAzimuthX = (dodgeLevel + 1) * 8;
    }

    public void setDodgeLevel(int dodgeLevel) {
        this.dodgeLevel = dodgeLevel;
        setmAzimuthX(dodgeLevel);
    }

    /**
     * 设置楼块儿坐标集合与定位点的正南夹角s（用户移动才会变动的值，可以2秒刷一次）
     *
     * @param myX 定位坐标x
     * @param myY 定位坐标y
     */
    public void setmAzimuth(double myX, double myY) {
        this.myX = myX;
        this.myY = myY;
        mAzimuth = new double[getGeosList().size()];
        for (int i = 0; i < getGeosList().size(); i++) {
            double azimuth = Math.toDegrees(Math.atan2(getGeosList().get(i).getY() - myY, getGeosList().get
                    (i).getX() - myX)) + 90;
            mAzimuth[i] = Angle.corrAngle(azimuth);
        }
        mAngle = getPoiAngle(mAzimuth);
    }

    /**
     * 获取该poi所有坐标连线集合组成的合角的中线
     *
     * @return
     */
    public double getmMidAngle() {
        return mathAngleCenter(mAngle);
    }

    public Angle getmAngle() {
        return mAngle;
    }

    public void setmAngle(Angle mAngle) {
        this.mAngle = mAngle;
    }

    public float getmAzimuth(Angle screenAngle) {
        if (isInAoi) {
            return (float) screenAngle.corrAngle(screenAngle.angleMidd());
        }
        return (float) getBuildingCenter(screenAngle);
        //        return (float) mMidAzimuth;
    }

    public int getmAzimuthX() {
        return mAzimuthX;
    }

    public int getDodgeLevel() {
        return dodgeLevel;
    }

    private List<Point> mGeoPoints = new ArrayList<>();

    public List<Point> getGeosList() {
        if (mGeoPoints == null || mGeoPoints.size() < 1) {
            mGeoPoints = new ArrayList<>();
            // 服务端说一定只会返回一串坐标
            String[] geosString = geos.get(0).getPts().split(";");
            for (int j = 0; j < geosString.length; j++) {
                String[] point = geosString[j].split(",");
                double y = Double.valueOf(point[0]);
                double x = Double.valueOf(point[1]);
                mGeoPoints.add(new Point(x, y));
            }
        }
        return mGeoPoints;
    }

    public double[] getPoints() {
        if (mPoints == null || mPoints.length < 1) {
            // 服务端说一定只会返回一串坐标
            String[] geosString = geos.get(0).getPts().split(";");
            mPoints = new double[geosString.length * 2];
            for (int i = 0; i < geosString.length; i++) {
                String[] point = geosString[i].split(",");
                mPoints[i * 2] = Double.valueOf(point[0]);
                mPoints[i * 2 + 1] = Double.valueOf(point[1]);
            }
        }
        return mPoints;
    }

    /**
     * 角度是否在夹角间(角度均为与某一方向如正南的角度)
     *
     * @param azimuth 被比较的角度(屏幕边)
     * @param angle   两角度形成的夹角
     *
     * @return true = 角度在夹角间
     */
    public static boolean isInterAngle(double azimuth, Angle angle) {
        if (Math.abs(angle.getAngleA() - angle.getAngleB()) < 180) {
            if (Math.min(angle.getAngleA(), angle.getAngleB()) <= azimuth
                    && azimuth <= Math.max(angle.getAngleA(), angle.getAngleB())) {
                return true;
            } else {
                return false;
            }
        } else {
            if ((0 <= azimuth && azimuth <= Math.min(angle.getAngleA(), angle.getAngleB()))
                    || (Math.max(angle.getAngleA(), angle.getAngleB()) <= azimuth && azimuth <= 360)) {
                Log.e("isInterAngle", "azimuth = " + azimuth + "   |    angle.a = " + angle.getAngleA() + "   |    "
                        + "angle.b = " + angle.getAngleB());
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 两(多)角形成的夹角
     *
     * @param azimuths    多角度形成的全楼块儿角
     * @param screenAngle 屏幕所在角度
     *
     * @return azimuths与screenAngle形成的夹角
     */
    private Angle mathAngle(double[] azimuths, Angle screenAngle) {
        Angle angle = new Angle();
        for (int i = 0; i < azimuths.length; i++) {
            for (int j = 0; j < azimuths.length; j++) {
                Angle azimuthAngle = new Angle(azimuths[i], azimuths[j]);
                if (isInterAngle(screenAngle.getAngleA(), azimuthAngle)) {
                    angle.setmAngleA(screenAngle.getAngleA());
                    break;
                } else {
                    angle.setMinAngleA(azimuths[i], screenAngle.getAngleA());
                    angle.setMinAngleA(azimuths[j], screenAngle.getAngleA());
                }
            }
        }
        for (int i = 0; i < azimuths.length; i++) {
            for (int j = 0; j < azimuths.length; j++) {
                Angle azimuthAngle = new Angle(azimuths[i], azimuths[j]);
                if (isInterAngle(screenAngle.getAngleB(), azimuthAngle)) {
                    angle.setmAngleB(screenAngle.getAngleB());
                    break;
                } else {
                    angle.setMinAngleB(azimuths[i], screenAngle.getAngleB());
                    angle.setMinAngleB(azimuths[j], screenAngle.getAngleB());
                }
            }
        }
        angle.updateAngle();
        return angle;
    }

    /**
     * 楼块儿与屏幕的夹角中线（实时获取的屏幕朝向，需实时刷新，一般为1秒60帧）
     */
    public double getBuildingCenter(Angle screenAngle) {
        if (mAzimuth == null || mAzimuth.length == 0) {
            //            isShowInAr = false;
            return 0;
        }
        double[] azimuth = {mAngle.getAngleA(), mAngle.getAngleB()};
        Angle angle = mathAngle(azimuth, screenAngle);
        mMidAzimuth = mathAngleCenter(angle);
        return mMidAzimuth;
    }

    /**
     * 通过两向量角求中线
     *
     * @param angle
     */
    public double mathAngleCenter(Angle angle) {
        double midAngleCenter;
        if (Math.abs(angle.getAngleA() - angle.getAngleB()) <= 180) {
            midAngleCenter = (angle.getAngleA() + angle.getAngleB()) / 2;
            return midAngleCenter;
        } else {
            midAngleCenter = (angle.getAngleA() + angle.getAngleB() + 360) / 2;
            if (midAngleCenter > 360) {
                midAngleCenter = midAngleCenter - 360;
                return midAngleCenter;
            }
            return midAngleCenter;
        }
    }

    /**
     * 两个向量的夹角
     *
     * @return
     */
    public static double interAngle(double oneAzimuth, double twoAzimuth) {
        double angle = Math.abs(oneAzimuth - twoAzimuth);
        angle = angle > 180 ? 360 - angle : angle;
        return angle;
    }

    /**
     * 向量与角的夹角
     *
     * @return
     */
    public static double interAngle(double azimuth, Angle angle) {
        double aAzimuth = interAngle(azimuth, angle.getAngleA());
        double bAzimuth = interAngle(azimuth, angle.getAngleB());
        return Math.min(aAzimuth, bAzimuth);
    }

    /**
     * 计算一群角度的合角
     *
     * @param azimuth 角度集合
     *
     * @return 合角
     */
    private Angle getPoiAngle(double[] azimuth) {
        Angle angle = new Angle();
        insertSort(azimuth);
        double inter = interAngle(azimuth[0], azimuth[azimuth.length - 1]);
        double maxAngle = inter;
        angle.setmAngleB(azimuth[0]);
        angle.setmAngleA(azimuth[azimuth.length - 1]);
        double sum = inter;
        for (int i = 1; i < azimuth.length; i++) {
            inter = interAngle(azimuth[i], azimuth[i - 1]);
            if (maxAngle < inter) {
                maxAngle = inter;
                angle.setmAngleB(azimuth[i]);
                angle.setmAngleA(azimuth[i - 1]);
            }
            sum += inter;
        }
        //        if (359 <= sum && sum <= 361) {
        //            isInAoi = true;
        //        }
        return angle;
    }

    public static void insertSort(double[] array) {
        if (array == null || array.length < 2) {
            return;
        }

        for (int i = 1; i < array.length; i++) {
            double currentValue = array[i];
            int position = i;
            for (int j = i - 1; j >= 0; j--) {
                if (array[j] > currentValue) {
                    array[j + 1] = array[j];
                    position -= 1;
                } else {
                    break;
                }
            }

            array[position] = currentValue;
        }
    }

}