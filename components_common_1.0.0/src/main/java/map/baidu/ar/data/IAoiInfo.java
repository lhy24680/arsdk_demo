package map.baidu.ar.data;

import com.baidu.platform.comapi.basestruct.GeoPoint;

import java.util.ArrayList;

import android.content.Context;
import map.baidu.ar.model.ArPoiScenery;

/**
 * Created by lixiang34 on 2017/6/15.
 */

public interface IAoiInfo {
    boolean getIsInAoi(float x, float y);

    boolean getIsInAoi(Context context);

    GeoPoint getNearestPoint(GeoPoint newLocation);

    ArrayList<ArPoiScenery> getSon();

    ArrayList<float[]> getAois();
}
