package map.baidu.ar.camera;

import android.opengl.GLES20;
import android.opengl.Matrix;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;

/**
 * Created by xingdaming on 15/12/28.
 */
public class GLCameraTexture {

    // GL_TEXTURE0
    private int mTexture;
    private ProgramMgr mProgramMgr;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mCameraWidth;
    private int mCameraHeight;

    private float[] mMVPMatrix = new float[16];

    private float[] mMMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] mPMatrix = new float[16];

    private final float[] mQuadPositonCoord = {

            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f, 1.0f
    };

    public GLCameraTexture(int surfaceWidth, int surfaceHeight, int camWidth, int camHeight) throws GLException {

        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;
        mCameraWidth = camWidth;
        mCameraHeight = camHeight;

        mTexture = -1;

        float ViewRatio = (float) mSurfaceWidth / mSurfaceHeight;
        float CamRatio = (float) mCameraWidth / mCameraHeight;

        float left = -1.0f;
        float right = 1.0f;
        float bottom = -1.0f;
        float top = 1.0f;

        float ratio = CamRatio / ViewRatio;

        if (ratio >= 1.0) {
            left = -1.0f / ratio;
            right = 1.0f / ratio;
        } else {
            bottom = -ratio;
            top = ratio;
        }

        Matrix.setIdentityM(mMMatrix, 0);
        // Matrix.rotateM(mMMatrix, 0, 90, 0, 0, 1);
        Matrix.rotateM(mMMatrix, 0, 270, 0, 0, 1);
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0, 0, 0, 0, 1, 0);
        Matrix.frustumM(mPMatrix, 0, left, right, bottom, top, 3, 7);

        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mPMatrix, 0, mMVPMatrix, 0);

        mProgramMgr = new ProgramMgr(mQuadPositonCoord);
        mProgramMgr.initProgramOES();
        // Matrix.setRotateM(mMVPMatrix, 0, 270, 0, 0, 1);
    }

    public int setUpTexOES() {
        mTexture = GLUtil.genTextureOES();
        return mTexture;
    }

    public void drawTexOES(float[] stMatrix) throws GLException {

        mProgramMgr.useProgram(mMVPMatrix, stMatrix);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTexture);
        GLES20.glUniform1i(mProgramMgr.getTextureHandle(), 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

    }

}
