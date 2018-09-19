package map.baidu.ar.model;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.core.PoiInfo;

import map.baidu.ar.data.IMapPoiItem;
import map.baidu.ar.detail.IMediaControllerData;
import map.baidu.ar.exception.LocationGetFailException;
import map.baidu.ar.init.ArSdkManager;
import map.baidu.ar.utils.DistanceByMcUtils;
import map.baidu.ar.utils.INoProGuard;
import map.baidu.ar.utils.LocSdkClient;
import map.baidu.ar.utils.Point;

/**
 * Created by kanglichen on 2018/4/13.
 */

public class PoiInfoImpl implements IMapPoiItem, INoProGuard, IMediaControllerData {

    private PoiInfo poiInfo;

    // 根据排点逻辑算出是否要在AR模式展示poi
    private boolean isShowInAr;
    // 是否在当前屏显示
    private boolean isShowInScreen;

    @Override
    public Point getGeoPoint() {
        return null;
    }

    @Override
    public String getDescription() throws LocationGetFailException {
        return null;
    }

    public PoiInfo getPoiInfo() {
        return poiInfo;
    }

    public void setPoiInfo(PoiInfo poiInfo) {
        this.poiInfo = poiInfo;
    }

    public boolean isShowInAr() {
        return isShowInAr;
    }

    public void setShowInAr(boolean showInAr) {
        isShowInAr = showInAr;
    }

    public boolean isShowInScreen() {
        return isShowInScreen;
    }

    public void setShowInScreen(boolean showInScreen) {
        isShowInScreen = showInScreen;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getSource() {
        return null;
    }

    @Override
    public float getWeight() {
        return 0;
    }

    @Override
    public String getUid() {
        return null;
    }

    @Override
    public ArPoint getPoint() {
        return null;
    }

    @Override
    public String getMapDistanceText() {
        return null;
    }

    @Override
    public double getDistance() throws LocationGetFailException {
        BDLocation location = LocSdkClient.getInstance(ArSdkManager.getInstance().getAppContext()).getLocationStart()
                .getLastKnownLocation();
        if (location != null) {
            double myX = location.getLongitude();
            double myY = location.getLatitude();
            return DistanceByMcUtils
                    .getDistanceByLOrl(new Point(myX, myY), new Point(poiInfo.location.longitude,
                            poiInfo.location.latitude));
        } else {
            throw new LocationGetFailException();
        }
    }

    @Override
    public String getImageUrlText() {
        return null;
    }

    @Override
    public String getFirstImageUrl() {
        return null;
    }

    @Override
    public boolean isNear() {
        return false;
    }

    @Override
    public boolean isNotFar() {
        return false;
    }
}
