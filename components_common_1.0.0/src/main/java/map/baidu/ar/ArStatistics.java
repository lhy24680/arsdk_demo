package map.baidu.ar;

/**
 * Created by lixiang34 on 2017/7/11.
 */

public class ArStatistics {

    // 商场Ar统计

    /**
     * 统计项动作：AR页面展现
     */
    public static final String KEY_INDOOR_AR_PAGE_SHOW = "indoorguide.ar.pageshow";

    /**
     * 统计项动作：活动气泡点击
     */
    public static final String KEY_INDOOR_AR_SPECIAL_BUBBLE = "indoorguide.ar.specialbubble";

    /**
     * 统计项动作：普通气泡点击
     */
    public static final String KEY_INDOOR_AR_NORMAL_BUBBLE = "indoorguide.ar.normalbubble";


    /**
     * 统计项动作：景区BAR智能导游按钮点击PV
     */
    public static final String KEY_AR_GUIDE_ENTRANCE = "scenery.arGuide.entrance";

    /**
     * 统计项动作：智能导游页面PV/UV
     */
    public static final String KEY_AR_GUIDE_PAGE_SHOW = "scenery.arGuide.pageShow";

    /**
     * 统计项动作：智能导游页面PV/UV
     */
    public static final String KEY_AR_GUIDE_POI_CLICK = "scenery.arGuide.poiClick";

    /**
     * 统计项动作：切换模式按钮点击
     */
    public static final String KEY_AR_GUIDE_MODE_SWITCH_CLICK = "scenery.arGuide.modeSwitchClick";

    /**
     * 统计项动作：获取权限页 PV/UV
     */
    public static final String KEY_AR_PERMISSION_PAGE_SHOW = "scenery.arPermission.pageShow";

    /**
     * 统计项动作：点击相机权限按钮PV
     */
    public static final String KEY_AR_PERMISSION_CAMERA_CLICK = "scenery.arPermission.cameraClick";

    /**
     * 统计项动作：点击定位权限按钮PV
     */
    public static final String KEY_AR_PERMISSION_LOCATION_CLICK = "scenery.arPermission.locationClick";

    /**
     * 统计项动作：点击地图模式按钮PV
     */
    public static final String KEY_AR_PERMISSION_MAP_CLICK = "scenery.arPermission.mapClick";

    /**
     * 统计项动作：景点讲解页PV/UV
     */
    public static final String KEY_AR_DETAIL_PAGE_SHOW = "cenery.arDetail.pageShow";

    /**
     * 统计项动作：播放按钮点击PV
     */
    public static final String KEY_AR_DETAIL_PLAY_CLICK = "scenery.arDetail.playClick";

    /**
     * 统计项动作：播放条-点击播放按钮PV
     */
    public static final String KEY_AR_DETAIL_BAR_PLAY_CLICK = "scenery.arDetail.barPlayClick";

    /**
     * 统计项动作：分享页面PV/UV
     */
    public static final String KEY_AR_SHARE_PAGE_SHOW = "scenery.arShare.pageShow";

    /**
     * 统计项动作：分享页面点击分享Item
     */
    public static final String KEY_AR_SHARE_ITEM_CLICK = "scenery.arShare.itemClick";

    /**
     * 统计项动作：发起步行导航点击PV
     */
    public static final String KEY_AR_DETAIL_NAVIGATE_CLICK = "scenery.arDetail.navigateClick";

    public static final String PARAM_KEY_AR_GUIDE_ENTRANCE_FROM = "src_from";

    public static final String PARAM_VALUE_AR_GUIDE_ENTRANCE_BAR = "scenery_function_bar";
    public static final String PARAM_VALUE_AR_GUIDE_ENTRANCE_BUBBLE = "scenery_function_bubble";
    public static final String PARAM_VALUE_AR_GUIDE_ENTRANCE_PANEL = "scenery_function_panel";

    /**
     * 是否在景区内，值为0（不在景区内）或1（在景区内）
     */
    public static final String PARAM_KEY_IS_IN_SCOPE = "isInScope";

    /**
     * 是否地图模式，值为0（AR模式）或1（地图模式）
     */
    public static final String PARAM_KEY_IS_MAP_MODE = "isMapMode";

    /**
     * 是否切换到地图模式，值为0（切换到AR模式）或1（切换到地图模式）
     */
    public static final String PARAM_KEY_TO_MAP_MODE = "toMapMode";

    /**
     * 分享点击Item
     */
    public static final String PARAM_KEY_SHARE_TYPE = "shareType";

    public static final int PARAM_VALUE_SHARE_TYPE_WEIXIN = 1;
    public static final int PARAM_VALUE_SHARE_TYPE_PENGYOUQUAN = 2;
    public static final int PARAM_VALUE_SHARE_TYPE_WEIBO = 3;



}
