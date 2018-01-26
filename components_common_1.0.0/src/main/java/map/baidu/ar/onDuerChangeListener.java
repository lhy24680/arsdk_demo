package map.baidu.ar;

import map.baidu.ar.model.ArPoiScenery;

/**
 * Created by daju on 2017/6/22.
 */

public interface onDuerChangeListener {
    void noPoiInScreenChanged(boolean noPoiInSreen);
    void nearPoiChanged(ArPoiScenery arPoi);
}
