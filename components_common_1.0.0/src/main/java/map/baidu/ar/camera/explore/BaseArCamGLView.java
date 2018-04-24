package map.baidu.ar.camera.explore;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import map.baidu.ar.ArPageListener;
import map.baidu.ar.camera.CamGLView;
import map.baidu.ar.model.ArInfo;

public class BaseArCamGLView extends CamGLView implements SurfaceTexture.OnFrameAvailableListener {

    private static String TAG = BaseArCamGLView.class.getName();

    public BaseArCamGLView(Context context) {
        super(context);
        mContext = context;
        mRender = new BaseArCamGLRender(context, this);
        super.startCam(mRender);
    }

    public BaseArCamGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRender = new BaseArCamGLRender(context, this);
        super.startCam(mRender);
    }

    public void setBaseArSensorState(float[] remapValue, LayoutInflater inflater, TextView messageTv, RelativeLayout
            rlView,
                                     ArPageListener onSelectNodeListener, ArrayList<ArInfo> arPoiList,
                                     FragmentActivity activity) {
        //        if(matchThread == null || matchRunnable == null){
        if (mRender != null) {
            mRender.setBaseArSensorState(remapValue, inflater, messageTv, rlView, onSelectNodeListener, arPoiList,
                    activity);
        }
    }

    @Override
    public void showAlertDialog() {
        Toast.makeText(mContext, "请检查相机权限", Toast.LENGTH_SHORT).show();
    }
}
