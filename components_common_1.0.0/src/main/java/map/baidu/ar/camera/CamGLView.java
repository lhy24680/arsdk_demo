package map.baidu.ar.camera;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import java.util.List;

import map.baidu.ar.utils.ScreenUtils;

/**
 * 相机视图基类
 */
public abstract class CamGLView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener {

    private static String TAG = CamGLView.class.getName();

    protected Context mContext;
    protected CamGLRender mRender;
    protected Camera mCamera;
    protected int mPreviewWidth;
    protected int mPreviewHeight;
    protected int mPictureWidth;
    protected int mPictureHeight;
    protected int mSurfaceWidth;
    protected int mSurfaceHeight;
    protected Thread matchThread;
    protected Runnable matchRunnable;
    public AlertDialog mDialog;
    protected boolean mWaitForTakePhoto;

    protected static FlashMode flashMode = FlashMode.OFF;

    protected void setupEGL(Context context) {
        mContext = context;
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setZOrderMediaOverlay(true);
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    public CamGLView(Context context) {
        super(context);
        mContext = context;
        //        startCam();
    }

    public CamGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        //        startCam();
    }

    /**
     * 关闭相机
     */
    public void stopCam() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
            } catch (Exception e) {
                mCamera = null;
            }
            //            mRender = null;
        }
    }

    public void pauseCam() {
        super.onPause();

        Log.e(CamGLView.class.getName(), "onPause");
    }

    //    @Override
    //    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    //
    //
    //        this.setMeasuredDimension(parentWidth, parentHeight);
    //        super.onMeasure(MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY), MeasureSpec
    //                .makeMeasureSpec(parentHeight, MeasureSpec.EXACTLY));
    //    }

    /**
     * 开启相机
     */
    protected void startCam(CamGLRender glRender) {
        mCamera = openCamera();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        //        parameters.setPreviewSize(1920, 1080);//一般都支持  暂且简单写这个  需要获取支持列表选取
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        //        parameters.setPictureSize(1080, 1920);
        mCamera.setParameters(parameters);
        mRender = glRender;
        setupEGL(mContext);
        int parentWidth = ScreenUtils.getScreenWidth(mContext);
        int parentHeight = ScreenUtils.getScreenHeight(mContext);
        if (parentWidth > 0 && parentHeight > 0) {
            mSurfaceWidth = parentWidth;
            mSurfaceHeight = parentHeight;
        }
        setupCamera(mSurfaceWidth, mSurfaceHeight);
        // setupEGL(mContext);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mRender != null) {
                    mRender.setSurfaceSize(mSurfaceWidth, mSurfaceHeight);
                    // 因为摄像头宽高比是横向的，所以
                    mRender.setCameraPreviewSize(mPreviewWidth, mSurfaceHeight);
                    mRender.setCamera(mCamera);
                }
            }
        });
    }

    /**
     * 设置相机宽高
     */
    protected void setupCamera(int w, int h) {
        if (mCamera == null) {
            return;
        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            setupCaptureParams(parameters, w, h);
            setupPictureSizes(parameters, 1000);
        } catch (Exception e) {
            // todo 不用时release()相机
            showAlertDialog();
            e.getMessage();
        }
    }

    public abstract void showAlertDialog();

    protected Camera openCamera() {
        try {
            if (mCamera == null) {
                mCamera = Camera.open();
            }
        } catch (Exception e) {
            e.getMessage();
            if (mCamera != null) {
                mCamera = null;
            }
            // 无相机权限，提醒用户去地图模式
            showAlertDialog();
        }
        try {
            if (mCamera != null) {
                mCamera.autoFocus(null);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return mCamera;
    }

    public enum FlashMode {
        ON,
        OFF,
    }

    public void switchFlashMode() {
        //        flashMode = flashMode == FlashMode.OFF ? FlashMode.ON : FlashMode.OFF;
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        switch (flashMode) {
            case OFF:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                break;
            case ON:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                break;
        }
        mCamera.setParameters(parameters);
    }

    /**
     * 设置相机view大小
     *
     * @param parameters 相机参数
     * @param w          相机宽
     * @param h          相机高
     */
    public void setupCaptureParams(Camera.Parameters parameters, int w, int h) {
        mPreviewWidth = w;
        mPreviewHeight = 0;
        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        for (int i = 0; i < mSupportedPreviewSizes.size(); i++) {
            Camera.Size size = mSupportedPreviewSizes.get(i);
            if (size.height == w && Math.abs(h - size.width) < Math.abs(h - mPreviewHeight)) {
                mPreviewHeight = size.width;
            }
        }
        if (mPreviewHeight == 0) {
            Camera.Size size = getBestPreSize(ScreenUtils.getScreenHeight(mContext),
                    ScreenUtils.getScreenWidth(mContext),
                    mSupportedPreviewSizes);
            parameters.setPreviewSize(size.width, size.height);
        } else {
            parameters.setPreviewSize(mPreviewHeight, mPreviewWidth);
        }
        mCamera.setParameters(parameters);
    }

    /**
     * 设置照片大小（传给定位sdk，他们说需要宽度最接近1000的照片尺寸）
     *
     * @param parameters 相机参数
     */
    public void setupPictureSizes(Camera.Parameters parameters, int near) {
        mPictureWidth = 1000;
        mPictureHeight = 0;
        int diff = near * 10;
        List<Camera.Size> mSupportedPictureSizes = parameters.getSupportedPictureSizes();
        for (int i = 0; i < mSupportedPictureSizes.size(); i++) {
            Camera.Size size = mSupportedPictureSizes.get(i);
            int sDiff = Math.abs(size.height - near);
            if (sDiff < diff) {
                mPictureHeight = size.width;
                mPictureWidth = size.height;
                diff = sDiff;
            }
        }
        parameters.setPictureSize(mPictureHeight, mPictureWidth);
        mCamera.setParameters(parameters);
    }

    public CamGLRender getRender() {
        return mRender;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }

    public Dialog getmDialog() {
        return mDialog;
    }

    public FlashMode getFlashMode() {
        return flashMode;
    }

    /**
     * 获取最佳预览尺寸
     *
     * @param surfaceWidth
     * @param surfaceHeight
     * @param preSizeList
     *
     * @return
     */
    protected Camera.Size getBestPreSize(int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {

        // 先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (Camera.Size size : preSizeList) {
            if ((size.width == surfaceWidth) && (size.height == surfaceHeight)) {
                return size;
            }
        }

        double minDiff = Double.MAX_VALUE;
        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) surfaceWidth) / (float) surfaceHeight;
        float curRatio;
        float deltaRatio;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / (float) size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < minDiff) {
                minDiff = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }

    public void setFlashMode(FlashMode mode) {
        this.flashMode = mode;
    }
}
