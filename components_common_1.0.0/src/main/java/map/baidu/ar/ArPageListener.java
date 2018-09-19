package map.baidu.ar;

/**
 * Ar 页面监听接口
 */
public interface ArPageListener<T> {
    /**
     * 是否有poi在屏幕显示
     */
    void noPoiInScreen(boolean isNoPoiInScreen);
    /**
     * 选择poi
     */
    void selectItem(T iMapPoiItem);
}
