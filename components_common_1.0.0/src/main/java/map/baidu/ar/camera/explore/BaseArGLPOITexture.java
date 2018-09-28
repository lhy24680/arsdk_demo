package map.baidu.ar.camera.explore;

import map.baidu.ar.camera.GLException;
import map.baidu.ar.camera.GLPOITexture;

public class BaseArGLPOITexture extends GLPOITexture {

    public static int WINDOW_VALUE_ERROR = -9999;

    public BaseArGLPOITexture(int surfaceWidth, int surfaceHeight) throws GLException {
        super(surfaceWidth, surfaceHeight);
    }

    /**
     * 将世界坐标转换为屏幕坐标
     *
     * @param obJx       视口矩阵中物体的坐标x
     * @param obJy       视口矩阵中物体的坐标y
     * @param objz       视口矩阵中物体的坐标z
     * @param modelView  视口矩阵与模型矩阵相乘
     * @param projection 投影矩阵
     * @param viewport   屏幕坐标转换
     *
     * @return
     */
    @Override
    public float[] bGLProjectf(float obJx, float obJy, float obJz, float[] modelView, float[] projection,
                               float[] viewport) {
        float[] windowValue = new float[3];
        windowValue[0] = WINDOW_VALUE_ERROR;
        windowValue[1] = WINDOW_VALUE_ERROR;
        // Transformation vectors
        float[] fTempo = new float[8];
        // Modelview transform
        fTempo[0] = modelView[0] * obJx + modelView[4] * obJy + modelView[8] * obJz + modelView[12];
        // w is always 1
        fTempo[1] = modelView[1] * obJx + modelView[5] * obJy + modelView[9] * obJz + modelView[13];
        fTempo[2] = modelView[2] * obJx + modelView[6] * obJy + modelView[10] * obJz + modelView[14];
        fTempo[3] = modelView[3] * obJx + modelView[7] * obJy + modelView[11] * obJz + modelView[15];
        // Projection transform, the final row of projection matrix is always [0 0 -1 0]
        // so we optimize for that.
        fTempo[4] = projection[0] * fTempo[0] + projection[4] * fTempo[1] + projection[8] * fTempo[2]
                + projection[12] * fTempo[3];
        fTempo[5] = projection[1] * fTempo[0] + projection[5] * fTempo[1] + projection[9] * fTempo[2]
                + projection[13] * fTempo[3];
        fTempo[6] = projection[2] * fTempo[0] + projection[6] * fTempo[1] + projection[10] * fTempo[2]
                + projection[14] * fTempo[3];
        fTempo[7] = -fTempo[2];
        // The result normalizes between -1 and 1
        if (fTempo[7] == 0.0) {
            // The w value
            return windowValue;
        }
        fTempo[7] = 1.0f / fTempo[7];
        // Perspective division
        fTempo[4] *= fTempo[7];
        fTempo[5] *= fTempo[7];
        fTempo[6] *= fTempo[7];
        // Window coordinates
        // Map x, y to range 0-1
        // x
        windowValue[0] = (fTempo[4] * 0.5f + 0.5f) * viewport[2] + viewport[0];
        // y
        windowValue[1] = viewport[3] - ((fTempo[5] * 0.5f + 0.5f) * viewport[3] + viewport[1]);
        // This is only correct when glDepthRange(0.0, 1.0)
        // Between 0 and 1
        windowValue[2] = (1.0f + fTempo[6]) * 0.5f;
        if (windowValue[2] < 0 || windowValue[2] > 1) {
            // 在z轴另一方向，返回错误值
            windowValue[0] = WINDOW_VALUE_ERROR;
            windowValue[1] = WINDOW_VALUE_ERROR;
            return windowValue;
        }
        return windowValue;
    }
}
