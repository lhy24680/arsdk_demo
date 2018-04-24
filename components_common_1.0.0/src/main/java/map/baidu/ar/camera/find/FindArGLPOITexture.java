package map.baidu.ar.camera.find;

import android.opengl.Matrix;
import android.util.Log;
import map.baidu.ar.camera.GLException;
import map.baidu.ar.camera.GLPOITexture;

/**
 * Created by xingdaming on 16/1/26.
 */
public class FindArGLPOITexture extends GLPOITexture {

    public static int WINDOW_VALUE_ERROR = -9999;

    public FindArGLPOITexture(int surfaceWidth, int surfaceHeight) throws GLException {
        super(surfaceWidth, surfaceHeight);
    }

    @Override
    public void drawTex() {
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
        Matrix.rotateM(mVMatrix, 0, mRotationZ + mAzimuth, 0, 1, 0);
        Matrix.rotateM(mVMatrix, 0, -mRotationX + mAzimuthX, 1, 0, 0);
        Matrix.rotateM(mVMatrix, 0, -mRotationY, 0, 0, 1);
        // 设置投影矩阵的参数，near能看多近，far能看多远
        Matrix.frustumM(mPMatrix, 0, left, right, bottom, top, 1f, 1000);
        // 投影矩阵与模型矩阵相乘，获得modelview矩阵
        Matrix.multiplyMM(modelview, 0, mMMatrix, 0, mVMatrix, 0);
        // 屏幕大小投射的参数
        float[] unitMatrix = new float[]{0, 0, mSurfaceWidth, mSurfaceHeight};
        // 算出屏幕坐标
        pointXY = BGLProjectf(0, 0, -2, modelview, mPMatrix, unitMatrix);
        Log.e("pointXY", "  x = " + pointXY[0] + " y = " + pointXY[1]);
    }

    /**
     * 设置屏幕旋转角
     *
     * @param x 屏幕宽方向
     * @param y 屏幕高方向
     * @param z 屏幕垂直方向
     */
    public void setSensorState(float x, float y, float z) {
        float rotationX = (float) Math.toDegrees(x);
        float rotationY = (float) Math.toDegrees(y);
        float rotationZ = (float) Math.toDegrees(z);
        float diffX = rotationX - mRotationX;
        float diffY = rotationY - mRotationY;
        float diffZ = rotationZ - mRotationZ;
        // 防抖
        if (Math.pow(diffX * 5, 2) * Math.pow(diffY * 5, 2) * Math.pow(diffZ * 5, 2) < 1) {
            return;
        }
        mRotationX = (float) Math.toDegrees(x);
        mRotationY = (float) Math.toDegrees(y);
        mRotationZ = (float) Math.toDegrees(z);
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
     * @return
     */
    public float[] BGLProjectf(float objx, float objy, float objz, float[] modelview, float[] projection,
                               float[] viewport) {
        float[] window_value = new float[3];
        window_value[0] = -9999;
        window_value[1] = -9999;
        // Transformation vectors
        float[] fTempo = new float[8];
        // Modelview transform
        fTempo[0] = modelview[0] * objx + modelview[4] * objy + modelview[8] * objz + modelview[12];    // w is always 1
        fTempo[1] = modelview[1] * objx + modelview[5] * objy + modelview[9] * objz + modelview[13];
        fTempo[2] = modelview[2] * objx + modelview[6] * objy + modelview[10] * objz + modelview[14];
        fTempo[3] = modelview[3] * objx + modelview[7] * objy + modelview[11] * objz + modelview[15];
        // Projection transform, the final row of projection matrix is always [0 0 -1 0]
        // so we optimize for that.
        fTempo[4] = projection[0] * fTempo[0] + projection[4] * fTempo[1] + projection[8] * fTempo[2] + projection[12] * fTempo[3];
        fTempo[5] = projection[1] * fTempo[0] + projection[5] * fTempo[1] + projection[9] * fTempo[2] + projection[13] * fTempo[3];
        fTempo[6] = projection[2] * fTempo[0] + projection[6] * fTempo[1] + projection[10] * fTempo[2] + projection[14] * fTempo[3];
        fTempo[7] = -fTempo[2];

        // The result normalizes between -1 and 1
        if (fTempo[7] == 0.0)    // The w value
            return window_value;

        fTempo[7] = 1.0f / fTempo[7];
        // Perspective division
        fTempo[4] *= fTempo[7];
        fTempo[5] *= fTempo[7];
        fTempo[6] *= fTempo[7];
        // Window coordinates
        // Map x, y to range 0-1
        // x
        window_value[0] = (fTempo[4] * 0.5f + 0.5f) * viewport[2] + viewport[0];
        // y
        window_value[1] = viewport[3] - ((fTempo[5] * 0.5f + 0.5f) * viewport[3] + viewport[1]);
        // This is only correct when glDepthRange(0.0, 1.0)
        // Between 0 and 1
        window_value[2] = (1.0f + fTempo[6]) * 0.5f;

        if (window_value[2] < 0 || window_value[2] > 1) {
            //在z轴另一方向，返回错误值
            window_value[0] = -9999;
            window_value[1] = -9999;
            return window_value;
        }
        return window_value;
    }
}
