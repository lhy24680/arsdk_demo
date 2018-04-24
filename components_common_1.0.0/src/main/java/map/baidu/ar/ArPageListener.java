package map.baidu.ar;

/**
 * Created by zhujingsi on 2017/6/14.
 */

public interface ArPageListener<T> {
    void noPoiInScreen(boolean isNoPoiInScreen);

    void selectItem(T iMapPoiItem);
}
