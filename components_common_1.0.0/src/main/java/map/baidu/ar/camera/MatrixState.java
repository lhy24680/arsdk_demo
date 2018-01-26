package map.baidu.ar.camera;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Stack;

/**
 * Created by xingdaming on 16/1/26.
 */
public class MatrixState {
    // 4x4矩阵 投影用
    private static float[] mProjMatrix = new float[16];
    // 摄像机位置朝向9参数矩阵
    static float[] mVMatrix = new float[16];
    // 最后起作用的总变换矩阵
    static float[] mMVPMatrix;
    // 红色定位光光源位置
    public static float[] lightLocationRed = new float[] {0, 0, 0};
    // 天蓝色定位光光源位置
    public static float[] lightLocationGreenBlue = new float[] {0, 0, 0};
    public static FloatBuffer cameraFB;
    public static FloatBuffer lightPositionFBRed;
    public static FloatBuffer lightPositionFBGreenBlue;
    // 定位光光源位置
    public static float[] lightLocation = new float[] {0, 0, 0};
    public static FloatBuffer lightPositionFB;

    // 设置摄像机
    public static void setCamera
    (
            // 摄像机位置x
            float cx,
            // 摄像机位置y
            float cy,
            // 摄像机位置z
            float cz,
            // 摄像机目标点x
            float tx,
            // 摄像机目标点y
            float ty,
            // 摄像机目标点z
            float tz,
            // 摄像机UP向量X分量
            float upx,
            // 摄像机UP向量Y分量
            float upy,
            // 摄像机UP向量Z分量
            float upz
    ) {
        Matrix.setLookAtM
                (
                        mVMatrix,
                        0,
                        cx,
                        cy,
                        cz,
                        tx,
                        ty,
                        tz,
                        upx,
                        upy,
                        upz
                );
        // 摄像机位置
        float[] cameraLocation = new float[3];
        cameraLocation[0] = cx;
        cameraLocation[1] = cy;
        cameraLocation[2] = cz;
        // 摄像机位置矩阵
        ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        llbb.order(ByteOrder.nativeOrder());
        // 设置字节顺序
        cameraFB = llbb.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);
    }

    // 设置透视投影参数
    public static void setProjectFrustum
    (
            // near面的left
            float left,
            // near面的right
            float right,
            // near面的bottom
            float bottom,
            // near面的top
            float top,
            // near面距离
            float near,
            // far面距离
            float far
    ) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    // 设置正交投影参数
    public static void setProjectOrtho
    (
            // near面的left
            float left,
            // near面的right
            float right,
            // near面的bottom
            float bottom,
            // near面的top
            float top,
            // near面距离
            float near,
            // far面距离
            float far
    ) {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    // 获取具体物体的总变换矩阵
    public static float[] getFinalMatrix() {
        float[] mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }

    // 设置灯光位置的方法
    public static void setLightLocation(float x, float y, float z) {
        lightLocation[0] = x;
        lightLocation[1] = y;
        lightLocation[2] = z;
        ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        // 设置字节顺序
        llbb.order(ByteOrder.nativeOrder());
        lightPositionFB = llbb.asFloatBuffer();
        lightPositionFB.put(lightLocation);
        lightPositionFB.position(0);
    }

    // 设置红色灯光位置的方法
    public static void setLightLocationRed(float x, float y, float z) {
        lightLocationRed[0] = x;
        lightLocationRed[1] = y;
        lightLocationRed[2] = z;
        ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        // 设置字节顺序
        llbb.order(ByteOrder.nativeOrder());
        lightPositionFBRed = llbb.asFloatBuffer();
        lightPositionFBRed.put(lightLocationRed);
        lightPositionFBRed.position(0);
    }

    // 设置天蓝色灯光位置的方法
    public static void setLightLocationGreenBlue(float x, float y, float z) {
        lightLocationGreenBlue[0] = x;
        lightLocationGreenBlue[1] = y;
        lightLocationGreenBlue[2] = z;
        ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        // 设置字节顺序
        llbb.order(ByteOrder.nativeOrder());
        lightPositionFBGreenBlue = llbb.asFloatBuffer();
        lightPositionFBGreenBlue.put(lightLocationGreenBlue);
        lightPositionFBGreenBlue.position(0);
    }

    public static Stack<float[]> mStack = new Stack<float[]>();

    static float[] currMatrix;

    public static void setIdentity() {
        currMatrix = new float[16];
        Matrix.setIdentityM(currMatrix, 0);
    }

    // 保护变换矩阵
    public static void pushMatrix() {
        mStack.push(currMatrix.clone());
    }

    // 恢复变换矩阵
    public static void popMatrix() {
        currMatrix = mStack.pop();
    }

    // 设置沿xyz轴移动
    public static void translate(float x, float y, float z) {
        Matrix.translateM(currMatrix, 0, x, y, z);
    }

    // 设置绕xyz轴移动
    public static void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(currMatrix, 0, angle, x, y, z);
    }

    public static void scale(float x, float y, float z) {
        Matrix.scaleM(currMatrix, 0, x, y, z);
    }

    public static void matrix(float[] self) {
        float[] result = new float[16];

        Matrix.multiplyMM(result, 0, currMatrix, 0, self, 0);
        currMatrix = result;
    }

    public static float[] getMMatrix() {
        return currMatrix;
    }

}
