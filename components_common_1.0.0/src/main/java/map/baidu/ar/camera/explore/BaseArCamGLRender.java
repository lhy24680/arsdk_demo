package map.baidu.ar.camera.explore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.baidu.location.BDLocation;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import map.baidu.ar.ArPageListener;
import map.baidu.ar.camera.CamGLRender;
import map.baidu.ar.camera.GLException;
import map.baidu.ar.camera.GLPOITexture;
import map.baidu.ar.camera.POIItem;
import map.baidu.ar.init.SDKContext;
import map.baidu.ar.model.Angle;
import map.baidu.ar.model.ArInfo;
import map.baidu.ar.utils.DistanceByMcUtils;
import map.baidu.ar.utils.LocSdkClient;
import map.baidu.ar.utils.Point;
import map.baidu.ar.utils.ResourceUtil;
import map.baidu.ar.utils.Task;

/**
 * Created by xingdaming on 15/12/22.
 */
public class BaseArCamGLRender extends CamGLRender implements GLSurfaceView.Renderer {

    private static final String TAG = BaseArCamGLRender.class.getName();
    // 显示出来的item
    private ArrayList<POIItem> mPoiItems = new ArrayList<>();
    // 根据推荐算法算出来以后的poi点集合
    private ArrayList<ArInfo> selectPois = new ArrayList<>();
    private int padding;
    private int textWidth;
    private float[] mRemapValue;
    private LayoutInflater mInflater;
    private TextView mMessageTv;
    private RelativeLayout mRlCamview;
    private ArPageListener mOnSelectNodeListener;
    private ArrayList<ArInfo> mArPoiList;
    private FragmentActivity mActivity;
    private String inAoiName;

    public BaseArCamGLRender(Context context, SurfaceTexture.OnFrameAvailableListener listener) {
        super(context, listener);
    }

    /**
     * 实时计算
     *
     * @param remapValue           硬件返回的屏幕坐标x,y,z
     * @param inflater
     * @param rl_camview           父view
     * @param onSelectNodeListener poi的点击事件
     * @param arPoiList            poi对象集合
     */
    @Override
    public void setBaseArSensorState(float[] remapValue, LayoutInflater inflater, TextView messageTv,
                                     RelativeLayout rl_camview, ArPageListener onSelectNodeListener,
                                     ArrayList<ArInfo> arPoiList, FragmentActivity activity) {
        mRemapValue = remapValue;
        mMessageTv = messageTv;
        mInflater = inflater;
        mOnSelectNodeListener = onSelectNodeListener;
        mRlCamview = rl_camview;
        mArPoiList = arPoiList;
        mActivity = activity;
        Task.back(new Runnable() {
            @Override
            public void run() {
                drawPoi();
            }
        }).run();

    }

    private void drawPoi() {
        if (mCameraWidth <= 0) {
            return;
        }

        BDLocation locData =
                LocSdkClient.getInstance(SDKContext.getInstance().getAppContext()).getLocationStart()
                        .getLastKnownLocation();
        // 定位坐标
        //        try {
        if (locData != null) {
            mX = locData.getLongitude();
            mY = locData.getLatitude();
        } else {
//            MToast.show(mContext, "暂时无法获取您的位置");
            return;
        }
        if (selectPois == null) {
            selectPois = new ArrayList<>();
        }

        selectPois = selectPoiList(mArPoiList);

        for (int i = 0; i < mPoiList.size(); i++) {
            moveItem(mRemapValue, (BaseArGLPOITexture) mPoiList.get(i), i);
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int j = 0;

                if (inAoiName != null && !inAoiName.isEmpty()) {
                    mMessageTv.setVisibility(View.VISIBLE);
                    mMessageTv.setText(String.format("您当前位于%s内", inAoiName));

                } else {
                    mMessageTv.setVisibility(View.GONE);
                }

                for (int i = 0; i < mPoiList.size(); i++) {
                    BaseArGLPOITexture mpoiTextture = (BaseArGLPOITexture) mPoiList.get(i);
                    if (mpoiTextture == null) {
                        return;
                    }
                    int x = (int) (mpoiTextture.getPointXY()[0]);
                    int y = (int) (mpoiTextture.getPointXY()[1]);
                    if (width == 0 || height == 0) {
                        width =
                                (int) ResourceUtil.getDimension(mContext,ResourceUtil.getDimenId(mContext,"ar_poi_width"));
                        height = (int) ResourceUtil.getDimension(mContext,ResourceUtil.getDimenId(mContext,
                                "ar_poi_height"));
                    }
                    if (!mpoiTextture.getArInfo().isShowInAr() || !(-width < x && x < mSurfaceWidth + width
                                                                           && -height < y
                                                                           && y < mSurfaceHeight + height)) {
                        continue;
                    }
                    if (mPoiItems.size() <= j) {
                        mRlCamview.addView(addPoiItem(j, mInflater));
                    }
                    setMargin(mpoiTextture, mPoiItems.get(j), mOnSelectNodeListener, j);
                    j++;
                }
//                mRlCamview.setVisibility(View.VISIBLE);  //new klc 可删除
                for (int k = j; k < mPoiItems.size(); k++) {
                    mPoiItems.get(k).setVisibility(View.GONE);
                }
                boolean noPoiInScreen = isNoPoiInScreen(mPoiList);
                if (mOnSelectNodeListener != null) {
                    mOnSelectNodeListener.noPoiInScreen(noPoiInScreen);
                }
            }
        });
    }

    private void moveItem(float[] remapValue, final BaseArGLPOITexture itemGLPoi, int k) {
        ArInfo arPoi = itemGLPoi.getArInfo();
        //        itemGLPoi.setLoc((long) arPoi.getPt_x(),
        //                (long) arPoi.getPt_y(), 100);
        // 给GL模型设值
        double screenPoint = Angle.corrAngle(-Math.toDegrees(remapValue[0]));
        itemGLPoi.setAzimuth(arPoi.getmAzimuth(new Angle(screenPoint - 20, screenPoint + 20)));
//        Log.e("moveItem", " screenPoint = " + screenPoint);
        itemGLPoi.setmAzimuthX(arPoi.getmAzimuthX());
        itemGLPoi.setSensorState(remapValue[1], remapValue[0], remapValue[2]);
        mPoiList.get(k).setArInfo(arPoi);
    }

    private BaseArGLPOITexture newPOI(ArInfo arInfo) throws GLException {
        BaseArGLPOITexture mBaseArGLPOITexture = new BaseArGLPOITexture(mSurfaceWidth, mSurfaceHeight);
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.postScale(-1, 1);
        mBaseArGLPOITexture.setArInfo(arInfo);
        return mBaseArGLPOITexture;
    }

    public void setCameraReadyListener(Runnable runnable) {
        mCameraReadyListener = runnable;
    }

    /**
     * 近大远小，近实远虚
     *
     * @param distance poi距离用户定位
     * @param poiItem  poi的View
     */
    private void setPOITextSize(double distance, POIItem poiItem) throws Exception {
        if (padding == 0) {
            padding = (int) ResourceUtil.getDimension(mContext,ResourceUtil.getDimenId(mContext,
                    "ar_poi_item_padding"));
        }
        if(textWidth == 0){
            textWidth = (int) ResourceUtil.getDimension(mContext,ResourceUtil.getDimenId(mContext,
                    "ar_poi_item_text_width"));
        }
        TextView poiItemName = (TextView) poiItem.findViewById(ResourceUtil.getId(mContext, "poi_item_name"));
        TextView ar_bubble_tag = (TextView) poiItem.findViewById(ResourceUtil.getId(mContext, "poi_distance"));
        View poiItemRl = poiItem.findViewById(ResourceUtil.getId(mContext, "poi_item_rl"));
        int poiSize;
        int poiDistanceSize;
        int repadding;
        int poiTextWidth;
        // 近大远小，近实远虚
        if (distance < 50) {
            poiSize = 14;
            poiDistanceSize = 12;
            repadding = padding;
            poiTextWidth = textWidth;
            poiItemRl.getBackground().mutate().setAlpha(153);
        } else if (distance < 100) {
            poiSize = 13;
            poiDistanceSize = 11;
            repadding = (int) (padding * 0.85);
            poiTextWidth = (int) (textWidth * 0.91);
            poiItemRl.getBackground().mutate().setAlpha(153);
        } else if (distance < 150) {
            poiSize = 13;
            poiDistanceSize = 10;
            repadding = (int) (padding * 0.7);
            poiTextWidth = (int) (textWidth * 0.91);
            poiItemRl.getBackground().mutate().setAlpha(102);
        } else {
            poiDistanceSize = 10;
            poiSize = 11;
            repadding = (int) (padding * 0.55);
            poiTextWidth = (int) (textWidth * 0.8);
            poiItemRl.getBackground().mutate().setAlpha(102);
        }
        poiItemName.setTextSize(poiSize);
        ar_bubble_tag.setTextSize(poiDistanceSize);
        poiItemName.getLayoutParams().width = poiTextWidth;
        poiItemName.requestLayout();
        poiItemRl.setPadding(repadding, repadding, repadding, repadding);
    }




    /**
     * 通过GLPOITexture计算出的屏幕坐标 ，对item进行边距设置
     *
     * @param mpoiTextture         通过物理世界坐标系计算出对应屏幕坐标的方法
     * @param poiItem              需要被设置的UIview
     * @param onSelectNodeListener poi的点击事件监听
     */
    private void setMargin(final GLPOITexture mpoiTextture, final POIItem poiItem,
                           final ArPageListener onSelectNodeListener, int j) {
        if (mpoiTextture == null || mpoiTextture.getArInfo() == null) {
            if (poiItem != null) {
                poiItem.setVisibility(View.GONE);
            }
            return;
        }
        final ArInfo arInfo = mpoiTextture.getArInfo();
        int x = (int) (mpoiTextture.getPointXY()[0]);
        int y = (int) (mpoiTextture.getPointXY()[1]);
        int width = Math.max(poiItem.getWidth(), poiItem.getVertex()[2]);
        int height = poiItem.getHeight();
        if (!arInfo.isShowInAr() || !(-width < x && x < mSurfaceWidth + width
                && -height < y && y < mSurfaceHeight + height)) {
            poiItem.setVisibility(View.GONE);
            return;
        }
        poiItem.setVisibility(View.VISIBLE);
        poiItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectNodeListener.selectItem(arInfo);
            }
        });
        TextView poi_name = (TextView) poiItem.findViewById(ResourceUtil.getId(mContext, "poi_item_name"));
        TextView poi_distance = (TextView) poiItem.findViewById(ResourceUtil.getId(mContext, "poi_distance"));
        String poiName = arInfo.getName();
        String distance = arInfo.getDistanceText(mX, mY);
        //        int poiNameIndex = poiName.indexOf("(");
        //        if (poiNameIndex != -1) {
        //            poiName = poiName.substring(0, poiNameIndex);
        //        }
        //        int poiNameIndex1 = poiName.indexOf("（");
        //        if (poiNameIndex1 != -1) {
        //            poiName = poiName.substring(0, poiNameIndex1);
        //        }
        poi_name.setText(poiName);
        poi_distance.setText(distance);
        try {
            setPOITextSize(arInfo.getDistance(mX, mY), poiItem);
        } catch (Exception e) {
            e.getMessage();
            if (poiItem != null) {
                poiItem.setVisibility(View.GONE);
            }
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) poiItem.getLayoutParams();

        // 比较的长方形
        int[] vertexs = new int[4];
        vertexs[0] = x;
        vertexs[1] = y;
        vertexs[2] = width;
        vertexs[3] = height;
        //        for(int j = 1;)
        //        POIItem poiItem_ = poiItems.get(i-j)
        //        if(isCollisionWithRect(poiItem.getVertex()[0],poiItem.getVertex()[1],poiItem.getVertex()[2],
        // poiItem
        // .getVertex
        //                ()[3],)

        //        if (width != 0 && height != 0 && -width < x && x < mSurfaceWidth && -height < y && y < mSurfaceHeight
        //                && i > 0) {
        //            // 取前面一个方形的4个角
        //
        //        }

        //        if (width != 0 && height != 0 && -width < x && x < mSurfaceWidth + width && -height < y && y <
        // mSurfaceHeight +
        //                height) {
        lp.setMargins(x - width / 2, y - height / 2,
                Math.max(-width * 2, Math.min(0, -x + mSurfaceWidth - width * 2)),
                Math.max(-height * 2, Math.min(0, -y + mSurfaceHeight - height * 2)));
        //        } else if (width != 0 && height != 0) {
        //            poiItem.setVisibility(View.GONE);
        //        }

        //        if (width > 0) {
        //            lp.width = Math.max(width, ScreenUtils.dip2px(100, poiItem.getContext()));
        //        }
        mpoiTextture.setShowInScreen(width, height);
        poiItem.setVertex(vertexs);
        poiItem.setLayoutParams(lp);

    }

    private boolean isNoPoiInScreen(ArrayList<GLPOITexture> arPois) {
        for (int i = 0; i < arPois.size(); i++) {
            // 只要有一个poi展示在当前屏，则返回否
            if (arPois.get(i).isShowInScreen()) {
                return false;
            }
        }
        return true;
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
     * @param i        位于集合中第几个
     * @param inflater
     *
     * @return
     */
    private POIItem addPoiItem(final int i, LayoutInflater inflater) {
        POIItem poiItemLayout = (POIItem) inflater.inflate(ResourceUtil.getLayoutId(mContext,
                "ar_layout_explore_item"),
                null);
        //        poiItemLayout.setVisibility(View.GONE);
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
    private ArrayList<ArInfo> selectPoiList(ArrayList<ArInfo> arPoiList) {
        long now = new Date().getTime();
        // 间隔大于10秒强刷
        if (now - sortTime > 10000) {
            sortTime = now;
            sortX = mX;
            sortY = mY;
        } else {
            // 重排poi间隔大于2秒
            if (sortTime != 0 && now - sortTime < 2000) {
                return selectPois;
            } else {
                sortTime = now;
            }
            // 重排poi用户位置移动大于3米
            if (!(sortX == 0 && sortY == 0) && DistanceByMcUtils.getDistanceByLLToText(new Point(sortX, sortY)
                    , new Point(mX, mY)) < 3) {
                return selectPois;
            } else {
                sortX = mX;
                sortY = mY;
            }
        }
        // 筛选后的poi列表
        ArrayList<ArInfo> selectPoiArray = new ArrayList<>();
        // 模型
        ArrayList<GLPOITexture> selectPoiList = new ArrayList<>();

        // 距离排序
        Collections.sort(arPoiList, new Comparator<ArInfo>() {
            @Override
            public int compare(ArInfo lhs, ArInfo rhs) {
                try {
                    // 升序
                    if (lhs.getDistance(mX, mY) > rhs.getDistance(mX, mY)) {
                        return 1;
                    } else if (lhs.getDistance(mX, mY) < rhs.getDistance(mX, mY)) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    return 1;
                }
            }
        });

        int strat = isInAoiView(arPoiList);
        // 第一个要开始计算角不用参与避让（权重最高，所以直接设置0层显示）
        if (arPoiList.size() >= 1 + strat) {
            ArInfo arPoi = arPoiList.get(strat);
            // 获取楼块儿夹角
            arPoi.setmAzimuth(mX, mY);
            arPoi.setDodgeLevel(0);
            // 防止模型错乱，超过范围不显示泡泡
            if (Math.abs(arPoi.getmAzimuthX()) < 24) {
                arPoi.setShowInAr(true);
                selectPoiArray.add(arPoi);
                try {
                    selectPoiList.add(newPOI(arPoi));
                } catch (Exception e) {
                    return selectPoiArray;
                }
            } else {
                arPoi.setShowInAr(false);
            }
        }
        for (int i = 1 + strat; i < arPoiList.size(); i++) {
            ArInfo arPoi = arPoiList.get(i);
            arPoi.setmAzimuth(mX, mY);
            for (int j = strat; j < i; j++) {
                // 减角
                if (isResetAngle(arPoi, arPoiList.get(j), 20)) {
                    // 发生了碰撞
                    arPoi.setDodgeLevel(arPoiList.get(j).getDodgeLevel() + 1);
                }
                // 减完为空不显示poi
                if (arPoi.getmAngle() != null) {
                    arPoi.setShowInAr(true);
                } else {
                    arPoi.setShowInAr(false);
                    arPoi.setDodgeLevel(0);
                    break;
                }
                if (Math.abs(arPoi.getmAzimuthX()) < 24) {
                    arPoi.setShowInAr(true);
                } else {
                    arPoi.setShowInAr(false);
                }
                // 如果减完以后的角度小于10度，不显示poi
                if (arPoi.interAngle(arPoi.getmAngle().getAngleA(), arPoi.getmAngle().getAngleB()) < 15) {
                    arPoi.setShowInAr(false);
                    arPoi.setDodgeLevel(0);
                    break;
                }
            }
            if (arPoi.isShowInAr()) {
                selectPoiArray.add(arPoi);

                try {
                    selectPoiList.add(newPOI(arPoi));
                } catch (Exception e) {
                    return selectPoiArray;
                }
            }
            //            arPoi.resetDodgeLevel();
        }
        // 排序后的poi列表
        ArrayList<ArInfo> arPoiLists = new ArrayList<>();
        arPoiLists.clear();
        arPoiLists.addAll(selectPoiArray);
        mPoiList = selectPoiList;
        return selectPoiArray;
    }

    private int isInAoiView(ArrayList<ArInfo> arPoiList) {
        int sum = 0;
        inAoiName = "";
        for (int i = 0; i < arPoiList.size(); i++) {
            if (arPoiList.get(i).isInAoi()) {
                if (sum > 0) {
                    inAoiName += "/";
                }
                inAoiName += arPoiList.get(i).getName();
                sum++;
            }
            arPoiList.get(i).setDodgeLevel(0);
        }
        return sum;
    }

    public static String listToString(ArrayList<?> list) {
        StringBuffer sb = new StringBuffer();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null || list.get(i) == "") {
                    continue;
                }
                sb.append(list.get(i));

            }
        }
        return sb.toString();
    }

    public void setmBitmapReadyCallbacks(BitmapReadyCallbacks mBitmapReadyCallbacks) {
        this.mBitmapReadyCallbacks = mBitmapReadyCallbacks;
    }

    // 在3D上是否碰撞
    private boolean isCollisionWith3D(ArInfo one, ArInfo two, int diff) {
        // 如果任意一个角不显示，那么肯定不存在碰撞
        if (!one.isShowInAr() || !two.isShowInAr()) {
            return false;
        }
        double oneAzimuth = one.getmMidAngle();
        double twoAzimuth = two.getmMidAngle();
        double angle = Math.abs(oneAzimuth - twoAzimuth);
        angle = angle > 180 ? 360 - angle : angle;
        return angle < diff && one.getmAzimuthX() == two.getmAzimuthX();
    }

    /**
     * 是重设角度
     *
     * @param one 用于比较的poi,如果碰撞，则避让
     * @param two 被比较的poi（权重大，优先展示的）
     *
     * @return
     */
    private boolean isResetAngle(ArInfo one, ArInfo two, int diff) {
        if (two.isInAoi()) {
            return false;
        }
        Angle angleOne = one.getmAngle();
        Angle angleTwo = two.getmAngle();
        if (angleOne == null || angleTwo == null) {
            return false;
        }
        if (ArInfo.isInterAngle(angleOne.getAngleA(), angleTwo)) {
            if (ArInfo.isInterAngle(angleOne.getAngleB(), angleTwo)) {
                // one在two内，one完全被two避让掉
                one.setmAngle(null);
                return true;
            } else {
                // one的a边被夹,b边需要被重制，b边没事儿
                angleOne.setmAngleA(Angle.ANGLE_ERROR);
                angleOne.setMinAngleA(angleTwo.getAngleA(), angleOne.getAngleB());
                angleOne.setMinAngleA(angleTwo.getAngleB(), angleOne.getAngleB());
                angleOne.updateAngle();
                one.setmAngle(angleOne);
                return true;
            }
        } else {
            if (ArInfo.isInterAngle(angleOne.getAngleB(), angleTwo)) {
                // one的b边被夹，a边没事儿
                angleOne.setmAngleB(Angle.ANGLE_ERROR);
                angleOne.setMinAngleB(angleTwo.getAngleA(), angleOne.getAngleA());
                angleOne.setMinAngleB(angleTwo.getAngleB(), angleOne.getAngleA());
                angleOne.updateAngle();
                one.setmAngle(angleOne);
                return true;
            } else {
                if (ArInfo.isInterAngle(angleTwo.getAngleA(), angleOne)) {
                    // two在one内，two把one拆成了2个，强哥说抛弃
                    one.setmAngle(null);
                    return true;
                } else {
                    // 没有碰撞
                    if (ArInfo.interAngle(one.getmAngle().getAngleA(), two.getmAngle()) > diff
                            && ArInfo.interAngle(one.getmAngle().getAngleB(), two.getmAngle()) > diff) {
                        // 加上泡泡宽度也没碰撞
                        return false;
                    }
                    return true;
                }
            }
        }
    }
}