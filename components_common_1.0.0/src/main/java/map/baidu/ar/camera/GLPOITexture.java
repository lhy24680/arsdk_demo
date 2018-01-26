package map.baidu.ar.camera;

import android.opengl.Matrix;

import map.baidu.ar.model.ArInfo;
import map.baidu.ar.model.ArPoi;

/**
 * Created by xingdaming on 16/1/26.
 */
public abstract class GLPOITexture {
    protected final float[] mQuadPositonCoord = {
            // X, Y, Z, U, V
            -0.25f, -0.25f, -2.0f, 0.0f, 0.0f,
            0.25f, -0.25f, -2.0f, 1.0f, 0.0f,
            -0.25f, 0.25f, -2.0f, 0.0f, 1.0f,
            0.25f, 0.25f, -2.0f, 1.0f, 1.0f,
    };

    protected float[] modelview = new float[16];

    protected float[] mMMatrix = new float[16];
    protected float[] mVMatrix = new float[16];
    protected float[] mPMatrix = new float[16];

    protected float[] mIdentityMatrix = new float[16];

    protected ProgramMgr mProgramMgr;

    protected int mSurfaceHeight;
    protected int mSurfaceWidth;
    // 旋转角度x,y,z
    protected float mRotationX;
    protected float mRotationY;
    protected float mRotationZ;
    // Poi与定位坐标连线与南北方向线的角度
    protected float mAzimuth;
    // 屏幕上下偏移的坐标
    protected float mAzimuthX;
    protected float[] pointXY = new float[2];
    protected long x, y, z;
    protected ArInfo arInfo;

    protected ArPoi arPoi;

    private int width;
    private int height;

    // 是否在当前屏显示
    public boolean isShowInScreen() {
        if (-width / 2 < pointXY[0] && pointXY[0] < mSurfaceWidth + width / 2 && -height / 2 < pointXY[1]
                && pointXY[1] < mSurfaceHeight + height / 2) {
            return true;
        } else {
            return false;
        }
    }

    public void setShowInScreen(int width, int height) {
        this.width = width;
        this.height = height;
    }



    public void setLoc(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    /**
     * 计算偏转角度
     *
     * @param myX
     * @param myY
     * @param poiName
     */
    public void setAzimuth(double myX, double myY, String poiName) {
        // 定位坐标（myX,myY）与poi坐标(x,y)的连线，与东西正方形夹角
        double azimuth = Math.toDegrees(Math.atan2(y - myY, x - myX)) + 90;
        mAzimuth = (float) azimuth;
    }

    public void setAzimuth(float mAzimuth) {
        this.mAzimuth = mAzimuth;
    }

    public float getmAzimuth() {
        return mAzimuth;
    }

    public GLPOITexture(int surfaceWidth, int surfaceHeight) throws GLException {

        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;

        mProgramMgr = new ProgramMgr(mQuadPositonCoord);
        mProgramMgr.initProgram();

        Matrix.setIdentityM(mIdentityMatrix, 0);

    }

    public void drawMultiTex() {
        MatrixState.setIdentity();
        MatrixState.pushMatrix();
        mMMatrix = MatrixState.getMMatrix();
        drawTex();
        MatrixState.popMatrix();
    }

    protected void drawTex() {
        float ratio = (float) mSurfaceWidth / mSurfaceHeight;
        // 设置投影矩阵的坐标，与near的比例决定了偏移的速度。
        float left = -0.25f;
        float right = 0.25f;
        float bottom = -0.25f;
        float top = 0.25f;
        if (ratio >= 1.0) {
            left = -ratio;
            right = ratio;
        } else {
            // 投影矩阵的near面设置成与屏幕等比例
            bottom = -0.25f / ratio;
            top = 0.25f / ratio;
        }
        // 设置视口矩阵的参数，眼睛坐标0,0,0，物体坐标0,0,-2(眼睛默认看向z轴负方向)
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 0, 0, 0, -2, 0, 1, 0);
        // 设置旋转角，屏幕垂直的是z轴，mRotation为陀螺仪监听转换出的角度。
        Matrix.rotateM(mVMatrix, 0, -mRotationX + mAzimuthX, 1, 0, 0);
        Matrix.rotateM(mVMatrix, 0, mRotationY + mAzimuth, 0, 1, 0);
        Matrix.rotateM(mVMatrix, 0, -mRotationZ, 0, 0, 1);
        // 设置投影矩阵的参数，near能看多近，far能看多远
        Matrix.frustumM(mPMatrix, 0, left, right, bottom, top, 1f, 1000);
        // 投影矩阵与模型矩阵相乘，获得modelview矩阵
        Matrix.multiplyMM(modelview, 0, mMMatrix, 0, mVMatrix, 0);
        // 屏幕大小投射的参数
        float[] unitMatrix = new float[] {0, 0, mSurfaceWidth, mSurfaceHeight};
        // 算出屏幕坐标
        pointXY = BGLProjectf(0, 0, -2, modelview, mPMatrix, unitMatrix);
        //        Log.e("pointXY", "  x = " + pointXY[0] + " y = " + pointXY[1]);
    }

    /**
     * 设置屏幕旋转角
     *
     * @param x 屏幕宽方向
     * @param y 屏幕高方向
     * @param z 屏幕垂直方向
     */
    public void setSensorState(float x, float y, float z) {
        //        float rotationX = (float) Math.toDegrees(x);
        //        float rotationY = (float) Math.toDegrees(y);
        //        float rotationZ = (float) Math.toDegrees(z);
        //        float diffX = rotationX - mRotationX;
        //        float diffY = rotationY - mRotationY;
        //        float diffZ = rotationZ - mRotationZ;
        //        // 防抖
        //        if (Math.pow(diffX * 5, 2) * Math.pow(diffZ * 5, 2) * Math.pow(diffY * 5, 2) < 1) {
        //            return;
        //        }

        float omegaMagnitude = (float) Math.sqrt(x * x + y * y + z * z);

        if (omegaMagnitude <= 0.1) { // 非自然陀螺仪数据抖动
            return;
        }
        mRotationX = (float) Math.toDegrees(x);
        mRotationY = (float) Math.toDegrees(y);
        mRotationZ = (float) Math.toDegrees(z);
    }

    public float[] getPointXY() {
        return pointXY;
    }

    /**
     * 将世界坐标转换为屏幕坐标
     *
     * @param objx       视口矩阵中物体的坐标x
     * @param objy       视口矩阵中物体的坐标y
     * @param objz       视口矩阵中物体的坐标z
     * @param modelview  视口矩阵与模型矩阵相乘
     * @param projection 投影矩阵
     * @param viewport   屏幕坐标转换
     *
     * @return
     */
    public abstract float[] BGLProjectf(float objx, float objy, float objz, float[] modelview, float[] projection,
                               float[] viewport) ;

    public void setmAzimuthX(float mAzimuthX) {
        this.mAzimuthX = mAzimuthX;
    }

    public ArInfo getArInfo() {
        return arInfo;
    }
    public void setArInfo(ArInfo arInfo) {
        this.arInfo = arInfo;
    }

    public ArPoi getArPoi(){
        return arPoi;
    }

    public void setArPoi(ArPoi arPoi){
        this.arPoi = arPoi;
    }

}
