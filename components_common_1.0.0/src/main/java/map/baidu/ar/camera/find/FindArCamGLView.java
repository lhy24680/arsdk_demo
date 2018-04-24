package map.baidu.ar.camera.find;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.Toast;
import map.baidu.ar.ArPageListener;
import map.baidu.ar.camera.CamGLView;
import map.baidu.ar.model.PoiInfoImpl;

public class FindArCamGLView extends CamGLView implements SurfaceTexture.OnFrameAvailableListener {

    private static String TAG = FindArCamGLView.class.getName();

    public FindArCamGLView(Context context) {
        super(context);
        mContext = context;
        mRender = new FindArCamGLRender(context, this);
        super.startCam(mRender);
    }

    public FindArCamGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRender = new FindArCamGLRender(context, this);
        super.startCam(mRender);
    }

    public void setFindArSensorState(float[] remapValue, LayoutInflater inflater, RelativeLayout
            rlView,
                                     ArPageListener onSelectNodeListener, ArrayList<PoiInfoImpl> arPoiList,
                                     FragmentActivity activity) {
        if (mRender != null) {
            mRender.setFindArSensorState(remapValue, inflater, rlView, onSelectNodeListener, arPoiList, activity);
        }
    }

    @Override
    public void showAlertDialog() {
        Toast.makeText(mContext, "请检查相机权限", Toast.LENGTH_SHORT).show();
    }
}
