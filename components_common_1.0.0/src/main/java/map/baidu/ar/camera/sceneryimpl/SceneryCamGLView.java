package map.baidu.ar.camera.sceneryimpl;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

//import com.baidu.baidumaps.common.util.ScreenUtils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.Toast;
import map.baidu.ar.ArPageListener;
import map.baidu.ar.camera.CamGLView;
import map.baidu.ar.model.ArPoiScenery;
import map.baidu.ar.utils.Task;

public class SceneryCamGLView extends CamGLView {

    private static String TAG = SceneryCamGLView.class.getName();

    public SceneryCamGLView(Context context) {
        super(context);
        mContext = context;
        mRender = new SceneryCamGLRender(context,this);
        super.startCam(mRender);
    }

    public SceneryCamGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRender = new SceneryCamGLRender(context,this);
        super.startCam(mRender);
    }


    public void setScenerySensorState(final float[] remapValue, final LayoutInflater inflater, final RelativeLayout rlView,
                               final ArPageListener onSelectNodeListener, @NotNull final ArrayList<ArPoiScenery> arPoiList, final FragmentActivity activity) {
        Task.back(new Runnable() {
            @Override
            public void run() {
                if (mRender != null) {
                    mRender.setScenerySensorState(remapValue, inflater, rlView, onSelectNodeListener, arPoiList, activity);
                }
            }
        }).run();
    }


    @Override
    public void showAlertDialog() {
        Toast.makeText(mContext,"请检查相机权限",Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int parentWidth = ScreenUtils.getScreenWidth();
//        int parentHeight =  ScreenUtils.getViewScreenHeight(getContext());
//        if (parentWidth > 0 && parentHeight > 0) {
//            mSurfaceWidth = parentWidth;
//            mSurfaceHeight = parentHeight;
//        }
        setupCamera(mSurfaceWidth, mSurfaceHeight);
        //setupEGL(mContext);
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
        this.setMeasuredDimension(mSurfaceWidth, mSurfaceWidth);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    /**
     * 设置相机view大小
     *
     * @param parameters 相机参数
     * @param w          相机宽
     * @param h          相机高
     */
    @Override
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
            Camera.Size size = getBestPreSize(mSurfaceHeight,
                    mSurfaceWidth,
                    mSupportedPreviewSizes);
            parameters.setPreviewSize(size.width, size.height);
        } else {
            parameters.setPreviewSize(mPreviewHeight, mPreviewWidth);
        }
        mCamera.setParameters(parameters);
    }

    /**
     * 获取最佳预览尺寸
     *`
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

        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) surfaceWidth) / (float) surfaceHeight;
        float curRatio;
        float deltaRatio;
        float deltaRatioMin = 0.1f;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / (float) size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        // 如果没找到合适的size，重新根据height找一个最接近的size
        double minDiff = Double.MAX_VALUE;
        if (retSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : preSizeList) {
                if (Math.abs(size.height - surfaceHeight) < minDiff) {
                    retSize = size;
                    minDiff = Math.abs(size.height - surfaceHeight);
                }
            }
        }

        return retSize;
    }
}
