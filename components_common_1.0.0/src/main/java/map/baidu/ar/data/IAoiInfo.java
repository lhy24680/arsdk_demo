package map.baidu.ar.data;


import java.util.ArrayList;
import map.baidu.ar.model.ArPoiScenery;
import map.baidu.ar.utils.Point;

/**
 * Aoi面信息
 */

public interface IAoiInfo {
    boolean getIsInAoi(float x, float y);

    boolean getIsInAoi();

    Point getNearestPoint(Point newLocation);

    ArrayList<ArPoiScenery> getSon();

    ArrayList<float[]> getAois();
}
