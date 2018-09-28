package map.baidu.ar.camera.find;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import map.baidu.ar.ArPageListener;
import map.baidu.ar.camera.CamGLRender;
import map.baidu.ar.camera.GLCameraTexture;
import map.baidu.ar.camera.GLException;
import map.baidu.ar.camera.GLPOITexture;
import map.baidu.ar.camera.POIItem;
import map.baidu.ar.init.ArSdkManager;
import map.baidu.ar.model.ArPoiScenery;
import map.baidu.ar.model.PoiInfoImpl;
import map.baidu.ar.utils.ArBDLocation;
import map.baidu.ar.utils.CoordinateConverter;
import map.baidu.ar.utils.DistanceByMcUtils;
import map.baidu.ar.utils.Point;
import map.baidu.ar.utils.ResourceUtil;

public class FindArCamGLRender extends CamGLRender implements GLSurfaceView.Renderer {

    private static final String TAG = FindArCamGLRender.class.getName();
    // 显示出来的item
    private ArrayList<POIItem> mPoiItems = new ArrayList<>();

    private boolean noPoiInSreen = false;
    private int mMapLevelDistance = 0;
    private ArPoiScenery mNearestPoi = null;
    private Bitmap mInBitmap = null;
    private ArrayList<PoiInfoImpl> selectPois;
    Context context;

    public void resetDuerState() {
        mNearestPoi = null;
        noPoiInSreen = false;
    }

    public FindArCamGLRender(Context context, SurfaceTexture.OnFrameAvailableListener listener) {
        super(context, listener);
        this.context = context;
    }

    /**
     * 实时计算
     *
     * @param remapValue           硬件返回的屏幕坐标x,y,z
     * @param inflater
     * @param rlCamview            父view
     * @param onSelectNodeListener poi的点击事件
     * @param arPoiList            poi对象集合
     */
    @Override
    public void setFindArSensorState(float[] remapValue, LayoutInflater inflater, RelativeLayout rlCamview,
                                     ArPageListener onSelectNodeListener,
                                     ArrayList<PoiInfoImpl> arPoiList, FragmentActivity activity) {
        if (mCameraWidth <= 0) {
            return;
        }
        // 定位坐标
        try {
            ArBDLocation location = ArSdkManager.listener.onGetBDLocation();
            if (location != null) {

                mX = location.getLongitude();
                mY = location.getLatitude();
            } else {
                Toast.makeText(mContext, "暂时无法获取您的位置", Toast.LENGTH_SHORT).show();
                return;
            }
            if (arPoiList.size() < 2) {
                selectPois = new ArrayList<>();
                selectPois.add(arPoiList.get(0));
            } else {
                //                selectPois = selectPoiList(arPoiList);
                selectPois = arPoiList;
            }
            Collections.sort(selectPois, new Comparator<PoiInfoImpl>() {
                @Override
                public int compare(PoiInfoImpl lhs, PoiInfoImpl rhs) {
                    try {
                        // 升序
                        if (lhs.getDistance() >= rhs.getDistance()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } catch (Exception e) {
                        return 1;
                    }
                }
            });
            for (int i = 0; i < selectPois.size(); i++) {
                moveItem(i, remapValue, rlCamview, onSelectNodeListener, inflater, selectPois, activity);
            }
            // 将不需要显示的poi隐藏
            for (int j = 0; j < mPoiItems.size(); j++) {
                final int finalJ = j;
                final int selectPoisSize = selectPois.size();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mPoiItems != null && selectPoisSize <= finalJ) {
                                mPoiItems.get(finalJ).setVisibility(View.GONE);
                                return;
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                });
            }
        } catch (Exception e) {
            // GL异常，空异常
            e.getMessage();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 初始化时绑定摄像头与画布
        if (mCamera == null) {
            return;
        }
        try {
            for (int i = 0; mPoiList.size() < 15; i++) {
                GLPOITexture poi = newPOI();
                mPoiList.add(poi);
            }
            if (mGLCameraTexture == null) {
                mGLCameraTexture = new GLCameraTexture(mSurfaceWidth, mSurfaceHeight, mCameraWidth, mCameraHeight);
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

    private void moveItem(final int i, float[] remapValue, final RelativeLayout rlCamview,
                          final ArPageListener onSelectNodeListener,
                          final LayoutInflater inflater, final ArrayList<PoiInfoImpl> selectPois,
                          FragmentActivity activity) {

        final FindArGLPOITexture itemGLPoi = (FindArGLPOITexture) mPoiList.get(i);
        Map map = CoordinateConverter.convertLL2MC(selectPois.get(i).getPoiInfo().location.longitude, selectPois
                .get(i).getPoiInfo().location.latitude);

        itemGLPoi.setLoc(((Double) map.get("x")).longValue(),
                ((Double) map.get("y")).longValue(), 100);
        // 给GL模型设值
        itemGLPoi.setAzimuth(mX, mY, selectPois.get(i).getPoiInfo().name);
        itemGLPoi.setmAzimuthX((int) (Math.pow(-1, i) * (i + i % 2)) * 2.5f);
        itemGLPoi.setSensorState(remapValue[1], remapValue[2], remapValue[0]);
        // 绘制poi
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mPoiItems.size() < i + 1) {
                        rlCamview.addView(addPoiItem(selectPois.get(i), i, inflater));
                    }
                    setMargin(itemGLPoi, mPoiItems, i, selectPois.get(i), onSelectNodeListener);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });
        //        PoiInfoImpl nearestPoi = isNoPoiNear(selectPois);
        //        if (nearestPoi != null && (mNearestPoi == null || !mNearestPoi.getUid().equals(nearestPoi.getUid())
        // )) {
        //            mNearestPoi = nearestPoi;
        //            onDuerChangeListenen.nearPoiChanged(nearestPoi);
        //        } else if (nearestPoi == null && mNearestPoi != null) {
        //            mNearestPoi = null;
        //            onDuerChangeListenen.nearPoiChanged(null);
        //        } else if (nearestPoi == null && mPoiItems.size() > 1 && mPoiItems.get(0).getVertex() != null
        //                && mPoiItems.get(0).getVertex().length > 2 && !(mPoiItems.get(0).getVertex()[0] == 0
        //                                                                        && mPoiItems.get(0).getVertex()[1]
        // == 0)) {
        //            boolean noPoiInSreen = isNoPoiInScreen(selectPois);
        //            // 当前没有poi在屏幕内，且之前有poi在屏幕内
        //            if (noPoiInSreen && !this.noPoiInSreen && onDuerChangeListenen != null) {
        //                onDuerChangeListenen.noPoiInScreenChanged(noPoiInSreen);
        //                this.noPoiInSreen = noPoiInSreen;
        //                //            } else if (!noPoiInSreen && this.noPoiInSreen && onDuerChangeListenen != null) {
        //                //                onDuerChangeListenen.noPoiInScreenChanged(noPoiInSreen);
        //            }
        //            //            this.noPoiInSreen = noPoiInSreen;
        //        }
    }

    private boolean isNoPoiInScreen(ArrayList<ArPoiScenery> arPois) {
        for (int i = 0; i < arPois.size(); i++) {
            // 只要有一个poi展示在当前屏，则返回否
            if (arPois.get(i).isShowInScreen()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否有在附近的poi点
     *
     * @param arPois 排序与筛选后的poi集合
     *
     * @return 最近的poi, 返回null时附近没有poi
     */
    private PoiInfoImpl isNoPoiNear(ArrayList<PoiInfoImpl> arPois) {
        for (int i = 0; i < arPois.size(); i++) {
            // 只要有一个poi展示在当前屏，则返回否
            if (arPois.get(i).isNear()) {
                return arPois.get(i);
            }
        }
        return null;
    }

    private FindArGLPOITexture newPOI() throws GLException {
        FindArGLPOITexture mGLPOITexture = new FindArGLPOITexture(mSurfaceWidth, mSurfaceHeight);
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.postScale(-1, 1);
        return mGLPOITexture;
    }

    /**
     * 近大远小，近实远虚
     *
     * @param arPoi   poi对象的属性
     * @param poiItem poi的View
     */
    private void setPOITextSize(PoiInfoImpl arPoi, POIItem poiItem) throws Exception {
        TextView poiItemName = (TextView) poiItem.findViewById(ResourceUtil.getId(mContext, "poi_item_name"));
        TextView poiDistance = (TextView) poiItem.findViewById(ResourceUtil.getId(mContext, "poi_distance"));
        View poiItemRl = poiItem.findViewById(ResourceUtil.getId(mContext, "poi_item_rl"));
        View poiNearTv = poiItem.findViewById(ResourceUtil.getId(mContext, "poi_near_tv"));
        int poiSize;
        int poiDistanceSize;
        // 小于十米的时候显示在附近
        if (arPoi.isNear()) {
            poiNearTv.setVisibility(View.VISIBLE);
            poiDistance.setVisibility(View.GONE);
        } else {
            poiNearTv.setVisibility(View.GONE);
            poiDistance.setVisibility(View.VISIBLE);
        }
        // 近大远小，近实远虚
        if (arPoi.getDistance() < 200) {
            poiSize = 14;
            poiDistanceSize = 11;
            poiItemRl.getBackground().mutate().setAlpha(179);
        } else if (arPoi.getDistance() < 500) {
            poiSize = 13;
            poiDistanceSize = 10;
            poiItemRl.getBackground().mutate().setAlpha(153);
        } else if (arPoi.getDistance() < 1000) {
            poiSize = 13;
            poiDistanceSize = 10;
            poiItemRl.getBackground().mutate().setAlpha(127);
        } else {
            poiDistanceSize = 10;
            poiSize = 12;
            poiItemRl.getBackground().mutate().setAlpha(102);
        }
        poiItemName.setTextSize(poiSize);
        poiDistance.setTextSize(poiDistanceSize);
    }

    /**
     * 通过GLPOITexture计算出的屏幕坐标 ，对item进行边距设置
     *
     * @param mpoiTextture         通过物理世界坐标系计算出对应屏幕坐标的方法
     * @param poiItems             坐标集合，用于碰撞偏移
     * @param i                    坐标的优先级，用于碰撞偏移
     * @param arPoi                poi对象的属性
     * @param onSelectNodeListener poi的点击事件监听
     */
    private void setMargin(GLPOITexture mpoiTextture, ArrayList<POIItem> poiItems, int i, final PoiInfoImpl arPoi,
                           final ArPageListener onSelectNodeListener) throws Exception {
        POIItem poiItem = poiItems.get(i);
        TextView poiName = (TextView) poiItem.findViewById(ResourceUtil.getId(mContext, "poi_item_name"));
        poiName.setText(arPoi.getPoiInfo().name);
        poiItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectNodeListener.selectItem(arPoi);
            }
        });
        TextView poiDistance = (TextView) poiItem.findViewById(ResourceUtil.getId(mContext, "poi_distance"));
//        poiDistance.setText((int) arPoi.getDistance() + "m");
        poiDistance.setText(arPoi.getDistanceText());
        setPOITextSize(arPoi, poiItem);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) poiItem.getLayoutParams();
        int x = (int) (mpoiTextture.getPointXY()[0]);
        int y = (int) (mpoiTextture.getPointXY()[1]);
        int width = Math.max(poiItem.getWidth(), poiItem.getVertex()[2]);
        int height = poiItem.getHeight();
        // 比较的长方形
        int[] vertexs = new int[4];
        vertexs[0] = x;
        vertexs[1] = y;
        vertexs[2] = width;
        vertexs[3] = height;
        lp.setMargins(x - width / 2, y - height / 2,
                Math.max(-width * 2, Math.min(0, -x + mSurfaceWidth - width * 2)),
                Math.max(-height * 2, Math.min(0, -y + mSurfaceHeight - height * 2)));
        poiItem.setVertex(vertexs);
        poiItem.setLayoutParams(lp);
        //        if (onDuerChangeListenen != null) {
        //            try {
        //                if (arPoi.getDistance() < ArPoiScenery.NEAR_VALUE && !arPoi.isDuerNear()) {
        //                    arPoi.setDuerNear(true);
        //                } else if (arPoi.getDistance() > ArPoiScenery.NEAR_VALUE && arPoi.isDuerNear()) {
        //                    arPoi.setDuerNear(false);
        //                } else if (arPoi.getDistance() > ArPoiScenery.NEAR_VALUE) {
        //                    arPoi.setDuerNear(false);
        //                }
        //            } catch (Exception e) {
        //                e.getMessage();
        //            }
        //            if (-width / 2 < x && x < mSurfaceWidth + width / 2 && -height / 2 < y && y < mSurfaceHeight +
        // height / 2) {
        //                arPoi.setShowInScreen(true);
        //            } else {
        //                arPoi.setShowInScreen(false);
        //            }
        //        }
        if (x != 0 && y != 0) {
            poiItem.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 检测两个矩形是否碰撞
     *
     * @return
     */
    public boolean isCollisionWithRect(int x1, int y1, int w1, int h1,
                                       int x2, int y2, int w2, int h2) {
        if (x1 >= x2 && x1 >= x2 + w2) {
            return false;
        } else if (x1 <= x2 && x1 + w1 <= x2) {
            return false;
        } else if (y1 >= y2 && y1 >= y2 + h2) {
            return false;
        } else if (y1 <= y2 && y1 + h1 <= y2) {
            return false;
        }
        return true;
    }

    /**
     * 创建poi的在主线程下的item视图
     *
     * @param arPoi    需要创建的对象属性
     * @param i        位于集合中第几个
     * @param inflater
     *
     * @return
     */
    private POIItem addPoiItem(final PoiInfoImpl arPoi, final int i, LayoutInflater inflater
    ) {
        POIItem poiItemLayout = (POIItem) inflater.inflate(ResourceUtil.getLayoutId(mContext,
                "ar_layout_find_poi_item"), null);
        poiItemLayout.setVisibility(View.GONE);
        mPoiItems.add(i, poiItemLayout);
        return poiItemLayout;
    }

    /**
     * 根据策略排序poi
     *
     * @param arPoiList 需要排序的全Poi列表
     *
     * @return 排序后poi列表
     */
    private ArrayList<PoiInfoImpl> selectPoiList(ArrayList<PoiInfoImpl> arPoiList) throws Exception {
        long now = new Date().getTime();
        // 间隔大于10秒强刷
        if (now - sortTime < 10000) {
            // 重排poi间隔大于2秒
            if (sortTime != 0 && now - sortTime < 2000) {
                Log.e("sort", sortTime + "");
                return selectPois;
            } else {
                sortTime = now;
            }
            // 重排poi用户位置移动大于3米
            if (!(sortX == 0 && sortY == 0)
                    && DistanceByMcUtils.getDistanceByLL(new Point(sortX, sortY), new Point(mX, mY)) < 3) {
                Log.e("sort", "sortX = " + sortX + "   |   sortY = " + sortY);
                return selectPois;
            } else {
                sortX = mX;
                sortY = mY;
            }
        } else {
            sortTime = now;
            sortX = mX;
            sortY = mY;
        }

        ArrayList<PoiInfoImpl> selectPoiArray = new ArrayList<>();
        int sum = 0;
        // 排序
        Collections.sort(arPoiList, new Comparator<PoiInfoImpl>() {
            @Override
            public int compare(PoiInfoImpl lhs, PoiInfoImpl rhs) {
                try {
                    // 升序
                    if (lhs.getDistance() >= rhs.getDistance()) {
                        return 1;
                    } else {
                        return -1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return 1;
                }
            }
        });

        for (int i = 0; i < arPoiList.size(); i++) {
            // 距离500⽶以内的所有⼦景点，最多显示7个⽓泡，优先排序权衡距离和优先级
            //            if (arPoiList.get(i).getDistance() <= 500 && sum < 7) {
            //                sum++;
            arPoiList.get(i).setShowInAr(true);
            selectPoiArray.add(arPoiList.get(i));
            //            } else {
            //                arPoiList.get(i).setShowInAr(false);
            //            }
        }
        //        if (sum >= 3) {
        //            if (mMapLevelDistance != MapScaleUtils.SCALE_500M && mArChangeListener != null) {
        //                mArChangeListener.levelChanged(MapScaleUtils.SCALE_500M, 500);
        //            }
        return selectPoiArray;
        //        }
        //        sum = 0;
        //        // 如果500⽶以内的⼦景点不⾜3个，则补充2公⾥以内的⼦景点，按距离排序，补充到3个⼦景点为⽌
        //        for (int j = 0; j < 3; j++) {
        //            if (arPoiList.size() > j && arPoiList.get(j).getDistance() <= 2000) {
        //                sum++;
        //                arPoiList.get(j).setShowInAr(true);
        //                selectPoiArray.add(arPoiList.get(j));
        //            }
        //        }
        //        if (sum > 1) {
        //            if (mMapLevelDistance != MapScaleUtils.SCALE_2KM && mArChangeListener != null) {
        //                mArChangeListener.levelChanged(MapScaleUtils.SCALE_2KM, 2000);
        //            }
        //            return selectPoiArray;
        //        } else {
        //            // 如果2公⾥以内⼀个⼦景点都没有，则取景区范围内最近的1个⼦景点
        //            if (mArChangeListener != null) {
        //                mArChangeListener.levelChanged(-1, arPoiList.get(0).getDistance());
        //            }
        //            arPoiList.get(0).setShowInAr(true);
        //            selectPoiArray.add(arPoiList.get(0));
        //            return selectPoiArray;
        //        }
    }

    public void setmBitmapReadyCallbacks(BitmapReadyCallbacks mBitmapReadyCallbacks) {
        this.mBitmapReadyCallbacks = mBitmapReadyCallbacks;
    }
}