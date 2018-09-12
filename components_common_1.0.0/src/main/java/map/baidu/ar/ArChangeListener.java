package map.baidu.ar;

/**
 * Ar 数据变化接口
 */

public interface ArChangeListener {
    /**
     * 地图比例尺等级变化
     */
    void levelChanged(int level, double distance);

    /**
     * 用户定位坐标变化
     */
    void locChanged(double x, double y);

    /**
     * poi与用户的连线跟南北方向的夹角
     */
    void azimuthChanged(float azimuth);
}
