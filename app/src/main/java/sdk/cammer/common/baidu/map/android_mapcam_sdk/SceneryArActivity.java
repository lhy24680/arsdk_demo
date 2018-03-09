package sdk.cammer.common.baidu.map.android_mapcam_sdk;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import map.baidu.ar.ArPageListener;
import map.baidu.ar.camera.SimpleSensor;
import map.baidu.ar.camera.sceneryimpl.SceneryCamGLView;
import map.baidu.ar.model.ArGeo;
import map.baidu.ar.model.ArInfoScenery;
import map.baidu.ar.model.ArPoi;
import map.baidu.ar.model.ArPoiScenery;
import map.baidu.ar.utils.TypeUtils;

public class SceneryArActivity extends FragmentActivity implements View.OnClickListener, ArPageListener {

    RelativeLayout camRl;
    SceneryCamGLView mCamGLView;
    SimpleSensor mSensor;
    private ArInfoScenery mInfo;
    private Bundle mSavedInstanceState;
    private RelativeLayout mArPoiItemRl;
    private static final int ISINAOISTATE_NULL = -1;
    private static final int ISINAOISTATE_TRUE = 1;
    private static final int ISINAOISTATE_FALSE = 0;
    // 是否在景区里的状态位（-1为没判断过，0为景区，1为非景区）
    private int isInAoiState = ISINAOISTATE_NULL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scenery_ar);
        mInfo = MainActivity.arInfo;
        mSavedInstanceState = savedInstanceState;
        mArPoiItemRl = (RelativeLayout) findViewById(R.id.ar_poi_item_rl);
        mArPoiItemRl.setVisibility(View.VISIBLE);
        initView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamGLView != null) {
            if (mCamGLView.mDialog != null) {
                mCamGLView.mDialog.dismiss();
            }
        }
    }

    private void initView() {
        camRl = (RelativeLayout) findViewById(R.id.cam_rl);
        mCamGLView = (SceneryCamGLView) LayoutInflater.from(this).inflate(R.layout.layout_cam_gl_view, null);
        mCamGLView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom == 0 || oldBottom != 0 || mCamGLView == null) {
                    return;
                }
                RelativeLayout.LayoutParams params = TypeUtils.safeCast(
                        mCamGLView.getLayoutParams(), RelativeLayout.LayoutParams.class);
                if (params == null) {
                    return;
                }
                params.height = bottom - top;
                mCamGLView.requestLayout();
            }
        });
        //        mArPoiItemRl = (RelativeLayout) view.findViewById(R.id.ar_poi_item_rl);
        //        mArPoiItemRl.setVisibility(View.VISIBLE);
        //        arModelShowView.setVisibility(View.VISIBLE);
        camRl.addView(mCamGLView);
        //        addCameraMask();
        //        addCameraBackground();
        //        mCamGLView.getRender().setArChangeListener(new ArChangeListener() {
        //            @Override
        //            public void levelChanged(final int level, final double maxDistance) {
        //                getActivity().runOnUiThread(new Runnable() {
        //                    @Override
        //                    public void run() {
        //                        mRadarPresenter.getArChangeListener().levelChanged(level, maxDistance);
        //                    }
        //                });
        //            }
        //
        //            @Override
        //            public void locChanged(final double x, final double y) {
        //                getActivity().runOnUiThread(new Runnable() {
        //                    @Override
        //                    public void run() {
        //                        mRadarPresenter.getArChangeListener().locChanged(x, y);
        //                    }
        //                });
        //            }
        //
        //            @Override
        //            public void azimuthChanged(final float azimuth) {
        //                getActivity().runOnUiThread(new Runnable() {
        //                    @Override
        //                    public void run() {
        //                        mRadarPresenter.getArChangeListener().azimuthChanged(azimuth);
        //                    }
        //                });
        //            }
        //        });
        //        mCamGLView.getRender().setOnDuerChangeListenen(this);
        //        mCamGLView.getRender().setCameraReadyListener(new Runnable() {
        //            @Override
        //            public void run() {
        //                mHandler.post(new Runnable() {
        //                    @Override
        //                    public void run() {
        //                        removeCameraMask();
        //                    }
        //                });
        //            }
        //        });
        initSensor();
        // 保持屏幕不锁屏
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public LayoutInflater getLayoutInflater() {

        return LayoutInflater.from(SceneryArActivity.this).cloneInContext(SceneryArActivity.this);
    }

    private void initSensor() {
        if (mSensor == null) {
            mSensor = new SimpleSensor(this, new HoldPositionListenerImp());
        }
        mSensor.startSensor();
    }

    private class HoldPositionListenerImp implements SimpleSensor.OnHoldPositionListener {
        @Override
        public void onOrientationWithRemap(float[] remapValue) {
            if (mCamGLView != null && mInfo != null) {
                if (mInfo.getIsInAoi() && mInfo.getSon() != null && mInfo.getSon()
                        .size() > 0) {
                    // 在景区则传入子点集合
                    mCamGLView.setScenerySensorState(remapValue, getLayoutInflater(),
                            mArPoiItemRl, SceneryArActivity.this, mInfo.getSon(), SceneryArActivity.this);
                    // 在景区的时候停止不在景区的动画
                    //                                if (isInAoiState != ISINAOISTATE_FALSE) {
                    //                                    isInAoiState = ISINAOISTATE_FALSE;
                    //                                    mDuerAnim.duerLeaveNotInAoi();
                    //                                }
                    //                                if (notInAoiTv != null) {
                    //                                    notInAoiTv.setVisibility(View.GONE);
                    //                                }
                    mInfo.getFather().setShowInAr(false);
                } else {
                    // 不在景区则传入父点
                    ArrayList<ArPoiScenery> father = new ArrayList<>();
                    mInfo.getFather().setShowInAr(true);
                    father.add(mInfo.getFather());
                    for (int i = 0; i < mInfo.getSon().size(); i++) {
                        mInfo.getSon().get(i).setShowInAr(false);
                    }
                    //                                if (notInAoiTv != null) {
                    //                                    notInAoiTv.setVisibility(isArModel ? View.VISIBLE : View
                    // .GONE);
                    //                                }
                    mCamGLView.setScenerySensorState(remapValue, getLayoutInflater(),
                            mArPoiItemRl, SceneryArActivity.this, father, SceneryArActivity.this);
                    //                                if (isInAoiState != ISINAOISTATE_TRUE && isArModel && mDuerText
                    // != null && mCanSwitch) {
                    //                                    // 不在景区的动画提示
                    //                                    isInAoiState = ISINAOISTATE_TRUE;
                    //                                    mDuerAnim.duerAnimNotInAoi(mDuerText, arrowMapIv);
                    //                                }
                }
            }
        }

    }

    @Override
    public void selectItem(ArGeo arPoi) {

    }

    @Override
    public void noPoiInScreen(boolean isNoPoiInScreen) {

    }

    @Override
    public void selectItem(ArPoi var1) {

    }

    @Override
    public void selectItem(ArPoiScenery arPoi) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finishCamInternal();
    }

    private void finishCamInternal() {
        if (mCamGLView != null) {
            mCamGLView.stopCam();
            camRl.removeAllViews();
            mCamGLView = null;

        }
        if (mArPoiItemRl != null) {
            mArPoiItemRl.removeAllViews();
        }
        if (mSensor != null) {
            mSensor.stopSensor();
        }
        // 恢复屏幕自动锁屏
        SceneryArActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_operate:
                Intent intent = new Intent(SceneryArActivity.this, SceneryArActivity.class);
                this.startActivity(intent);
                break;
            default:
                break;
        }
    }
}
