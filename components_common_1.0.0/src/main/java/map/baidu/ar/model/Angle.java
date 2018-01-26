package map.baidu.ar.model;

import map.baidu.ar.utils.INoProGuard;

/**
 * Created by daju on 2018/1/9.
 */

public class Angle implements INoProGuard {
    public static double ANGLE_ERROR = 9999;
    private double mAngleA = 9999;
    private double mAngleB = 9999;
    // 屏幕与坐标们最小的夹角
    private double mMinAngleA = 9999;
    // 屏幕与坐标们最小夹角的线
    private double mMinazimuthA = 9999;

    // 屏幕与坐标们最小的夹角
    private double mMinAngleB = 9999;
    // 屏幕与坐标们最小夹角的线
    private double mMinazimuthB = 9999;

    public Angle(double angleA, double angleB) {
        mAngleA = corrAngle(angleA);
        mAngleB = corrAngle(angleB);
    }

    public static double corrAngle(double angle) {
        if (angle >= 360) {
            return angle - 360;
        } else if (angle < 0) {
            return angle + 360;
        }
        return angle;
    }

    public Angle() {

    }

    public double getAngleA() {
        return mAngleA;
    }

    public void setmAngleA(double mAngleA) {
        this.mAngleA = mAngleA;
    }

    public double getAngleB() {
        return mAngleB;
    }

    public void setmAngleB(double mAngleB) {
        this.mAngleB = mAngleB;
    }

    public void addOneOfAngle(double oneOfAngle) {
        if (mAngleA == ANGLE_ERROR) {
            mAngleA = oneOfAngle;
        } else {
            mAngleB = oneOfAngle;
        }
    }

    public void updateAngle() {
        if (mAngleA == ANGLE_ERROR) {
            if (mMinazimuthA == ANGLE_ERROR) {
                mAngleA = mAngleB;
            } else {
                mAngleA = mMinazimuthA;
            }
        }
        if (mAngleB == ANGLE_ERROR) {
            if (mMinazimuthB == ANGLE_ERROR) {
                mAngleB = mAngleA;
            }
            mAngleB = mMinazimuthB;
        }
    }

    public void setMinAngleA(double azimuths, double screenAngle) {
        double angle = Math.abs(azimuths - screenAngle);
        angle = angle > 180 ? 360 - angle : angle;
        if (angle < mMinAngleA) {
            mMinAngleA = angle;
            mMinazimuthA = azimuths;
        }
    }

    public void setMinAngleB(double azimuths, double screenAngle) {
        double angle = Math.abs(azimuths - screenAngle);
        angle = angle > 180 ? 360 - angle : angle;
        if (angle < mMinAngleB) {
            mMinAngleB = angle;
            mMinazimuthB = azimuths;
        }
    }

    /**
     * 两个坐标的夹角是否小于diff值
     *
     * @param diff 是否小于该值
     *
     * @return
     */
    public boolean angleDiff(double oneAzimuth, double twoAzimuth, double diff) {
        double angle = Math.abs(oneAzimuth - twoAzimuth);
        angle = angle > 180 ? 360 - angle : angle;
        return angle < diff;
    }

    /**
     * 这个角的夹角的中线
     *
     * @return
     */
    public double angleMidd() {
        double angle = Math.abs(mAngleA - mAngleB);
        angle = angle > 180 ? 360 - angle : angle;
        return mAngleB - angle / 2;
    }

}
