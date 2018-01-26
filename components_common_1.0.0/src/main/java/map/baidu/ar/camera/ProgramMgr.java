package map.baidu.ar.camera;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * Created by xingdaming on 15/12/28.
 */
public class ProgramMgr {

    private static final String TAG = ProgramMgr.class.getName();
    private static final String POSITION = "aPosition";
    private static final String TEXCOORD = "aTextureCoord";
    private static final String TEXTURE = "sTexture";
    private static final String MVPMATRIX = "uMVPMatrix";
    private static final String STMATRIX = "uSTMatrix";
    private static final String RESOLUTION = "uResolution";

    private final String mVertexShader =
            "uniform mat4 uMVPMatrix;\n"
                    + "uniform mat4 uSTMatrix;\n"
                    + "uniform vec2 uResolution;\n"
                    + "attribute vec4 aPosition;\n"
                    + "attribute vec4 aTextureCoord;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "void main() {\n"
                    + "  gl_Position = uMVPMatrix * aPosition;\n"
                    + "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n"
                    + "}\n";

    private final String mFragmentShader =
            "precision mediump float;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "uniform sampler2D sTexture;\n"
                    + "void main() {\n"
                    + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
                    + "  gl_FragColor = color;\n"
                    + "}\n";

    private final String mFragmentShaderOES =
            "#extension GL_OES_EGL_image_external : require\n\n"
                    + "precision mediump float;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "uniform samplerExternalOES sTexture;\n"
                    + "void main() {\n"
                    + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
                    + "  gl_FragColor = color;\n"
                    + "}\n";

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int QUAD_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int QUAD_VERTICES_DATA_POS_OFFSET = 0;
    private static final int QUAD_VERTICES_DATA_UV_OFFSET = 3;

    private int maPositionHandle;
    private int maTexCoordHandle;
    private int maTextureHandle;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private int muResolutionHandle;

    public int mProgram;

    private FloatBuffer mQuadVertices;

    public ProgramMgr(float[] QuadPositonCoord) {
        // allocate in bytes
        // always remember 4bytes in float
        mQuadVertices = ByteBuffer.allocateDirect(
                QuadPositonCoord.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mQuadVertices.put(QuadPositonCoord).position(0);
    }

    public void initProgramOES() throws GLException {

        assembleProgramOES();
        getAttribLocation();
    }

    public void initProgram() throws GLException {

        assembleProgram();
        getAttribLocation();
    }

    public int getTextureHandle() {
        return maTextureHandle;
    }

    public void useProgram(float[] mvpMatrix, float[] stMatrix) throws GLException {

        GLES20.glUseProgram(mProgram);

        mQuadVertices.position(QUAD_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, QUAD_VERTICES_DATA_STRIDE_BYTES,
                mQuadVertices);
        checkGlError("glVertexAttribPointer maPosition");

        GLES20.glEnableVertexAttribArray(maPositionHandle);
        checkGlError("glEnableVertexAttribArray maPositionHandle");

        mQuadVertices.position(QUAD_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(maTexCoordHandle, 2, GLES20.GL_FLOAT, false,
                QUAD_VERTICES_DATA_STRIDE_BYTES, mQuadVertices);
        checkGlError("glVertexAttribPointer maTexCoordHandle");

        GLES20.glEnableVertexAttribArray(maTexCoordHandle);
        checkGlError("glEnableVertexAttribArray maTexCoordHandle");

        // Matrix.setRotateM(MVPMatrix, 0, 270, 0, 0, 1);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mvpMatrix, 0);
        checkGlError("glUniformMatrix4fv muMVPMatrixHandle");

        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, stMatrix, 0);
        checkGlError("glUniformMatrix4fv muSTMatrixHandle");

    }

    private void assembleProgramOES() throws GLException {

        mProgram = createProgram(mVertexShader, mFragmentShaderOES);
    }

    private void assembleProgram() throws GLException {

        mProgram = createProgram(mVertexShader, mFragmentShader);
    }

    private int loadShader(int shaderType, String source) {

        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private int createProgram(String vertexSource, String fragmentSource) throws GLException {

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    private void checkGlError(String op) throws GLException {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new GLException(op + ": glError " + error);
        }
    }

    private boolean getAttribLocation() {

        boolean ret = true;

        maPositionHandle = GLES20.glGetAttribLocation(mProgram, POSITION);
        maTexCoordHandle = GLES20.glGetAttribLocation(mProgram, TEXCOORD);
        maTextureHandle = GLES20.glGetUniformLocation(mProgram, TEXTURE);
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, MVPMATRIX);
        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, STMATRIX);
        muResolutionHandle = GLES20.glGetUniformLocation(mProgram, RESOLUTION);

        if (maPositionHandle == -1 || maTextureHandle == -1 || maTexCoordHandle == -1 || muMVPMatrixHandle == -1
                || muSTMatrixHandle == -1 || muResolutionHandle == -1) {
            ret = false;
            // Log.e(TAG, "program init error");
        }

        return ret;
    }
}
