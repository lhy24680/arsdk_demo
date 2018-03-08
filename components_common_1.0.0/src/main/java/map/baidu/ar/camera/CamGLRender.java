package map.baidu.ar.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import map.baidu.ar.ArChangeListener;
import map.baidu.ar.ArPageListener;
import map.baidu.ar.model.ArInfo;
import map.baidu.ar.model.ArPoi;
import map.baidu.ar.model.ArPoiScenery;
import map.baidu.ar.onDuerChangeListener;
import map.baidu.ar.utils.AsyncImageLoader;


public class CamGLRender implements GLSurfaceView.Renderer {

    private static final String TAG = CamGLRender.class.getName();
    // 显示出来的item
    protected Context mContext;
    protected double mX;
    protected double mY;
    protected double mOldX;
    protected double mOldY;

    protected int mCameraTexId;
    protected GLCameraTexture mGLCameraTexture;
    // 丢到模型里算出世界坐标转换屏幕坐标的textture
    protected ArrayList<GLPOITexture> mPoiList = new ArrayList<>();

    protected SurfaceTexture mSurfaceTexture;

    protected Camera mCamera;
    protected int mSurfaceWidth;
    protected int mSurfaceHeight;
    protected int mCameraWidth;
    protected int mCameraHeight;
    protected float[] mSTMatrix = new float[16];
    protected Runnable mCameraReadyListener;
    protected SurfaceTexture.OnFrameAvailableListener mListener;
    protected BitmapReadyCallbacks mBitmapReadyCallbacks;
    protected AsyncImageLoader asyncImageLoader;
    protected long sortTime = 0;
    protected double sortX = 0;
    protected double sortY = 0;
    // 当前楼层在数组的第几个
    protected int floorLenth;
    // 当前用户所在楼层
    protected String floorName;
    protected int width;
    protected int height;
    protected boolean shouldTakePic;
    protected ArChangeListener mArChangeListener;
    protected onDuerChangeListener onDuerChangeListenen;

    public String getFloorName() {
        return floorName;
    }

    public void setShouldTakePic(boolean shouldTakePic) {
        this.shouldTakePic = shouldTakePic;
    }

    public CamGLRender(Context context, SurfaceTexture.OnFrameAvailableListener listener) {
        mContext = context;
        mListener = listener;
        asyncImageLoader = AsyncImageLoader.getInstance(mContext);
    }

    public void setSurfaceSize(int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    public void setCameraPreviewSize(int width, int height) {
        mCameraWidth = width;
        mCameraHeight = height;
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    public Camera getmCamera() {
        return mCamera;
    }

    /**
     * BaseAr 实时计算
     */
    public void setBaseArSensorState(float[] remapValue, LayoutInflater inflater, TextView messageTv, RelativeLayout
            rlView,
                                     ArPageListener onSelectNodeListener, ArrayList<ArInfo> arPoiList,
                                     FragmentActivity activity) {

    }

    /**
     * Indoor 调用 实时计算
     */
    public void setSensorState(float[] remapValue, final LayoutInflater inflater,
                               final RelativeLayout rlCamview, final ArPageListener onSelectNodeListener,
                               ArrayList<ArrayList<ArPoi>> arPoiList, FragmentActivity activity) {

    }



    /**
     * Scenery 调用 实时计算
     *
     * @param remapValue           硬件返回的屏幕坐标x,y,z
     * @param inflater
     * @param rlCamview           父view
     * @param onSelectNodeListener poi的点击事件
     * @param arPoiList            poi对象集合
     */
    public void setScenerySensorState(float[] remapValue, LayoutInflater inflater, RelativeLayout rlCamview,
                                      ArPageListener onSelectNodeListener,
                                      ArrayList<ArPoiScenery> arPoiList, FragmentActivity activity) {
    }

    public void setOnDuerChangeListenen(onDuerChangeListener onDuerChangeListenen) {
        this.onDuerChangeListenen = onDuerChangeListenen;
    }

    public void setArChangeListener(ArChangeListener arChangeListener) {
        this.mArChangeListener = arChangeListener;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 初始化时绑定摄像头与画布
        if (mCamera == null) {
            return;
        }
        try {
            if (mGLCameraTexture == null) {
                mGLCameraTexture = new GLCameraTexture(mSurfaceWidth, mSurfaceHeight,
                        mCameraWidth, mCameraHeight);
            }
            mCameraTexId = mGLCameraTexture.setUpTexOES();

            mSurfaceTexture = new SurfaceTexture(mCameraTexId);

            mSurfaceTexture.setOnFrameAvailableListener(mListener);
            // 摄像头，设置画布
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mCamera != null && mCameraReadyListener != null) {
            mCameraReadyListener.run();
        }
    }

    public void setCameraReadyListener(Runnable runnable) {
        mCameraReadyListener = runnable;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mSurfaceTexture == null) {
            return;
        }
        // 画布更新
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mSTMatrix);
        Matrix.setIdentityM(mSTMatrix, 0);
        try {
            mGLCameraTexture.drawTexOES(mSTMatrix);

            for (int i = 0; i < mPoiList.size(); i++) {
                mPoiList.get(i).drawMultiTex();

            }

        } catch (Exception e) {
            e.getMessage();
        }
        if (shouldTakePic) {
            Bitmap bitmap = takeCameraShot(0, 0, mSurfaceWidth, mSurfaceHeight, gl);
            if (mBitmapReadyCallbacks != null) {
                mBitmapReadyCallbacks.getCameraBitmap(bitmap);
            }
            shouldTakePic = false;
            mBitmapReadyCallbacks = null;
        }
    }

    public interface BitmapReadyCallbacks {
        void getCameraBitmap(Bitmap data);
    }

    protected Bitmap takeCameraShot(int x, int y, int w, int h, GL10 gl) {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
        int offset1, offset2;
        for (int i = 0; i < h; i++) {
            offset1 = i * w;
            offset2 = (h - i - 1) * w;
            for (int j = 0; j < w; j++) {
                int texturePixel = bitmapBuffer[offset1 + j];
                int blue = (texturePixel >> 16) & 0xff;
                int red = (texturePixel << 16) & 0x00ff0000;
                int pixel = (texturePixel & 0xff00ff00) | red | blue;
                bitmapSource[offset2 + j] = pixel;
            }
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }
}