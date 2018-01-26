package map.baidu.ar;


import map.baidu.ar.model.ArGeo;
import map.baidu.ar.model.ArPoi;
import map.baidu.ar.model.ArPoiScenery;

/**
 * Created by zhujingsi on 2017/6/14.
 */

public interface ArPageListener {
    void selectItem(ArGeo arPoi);
    void noPoiInScreen(boolean isNoPoiInScreen);

    void selectItem(ArPoi var1);
    void selectItem(ArPoiScenery arPoi);
}
