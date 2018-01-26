package map.baidu.ar.model;

import map.baidu.ar.utils.INoProGuard;

/**
 * Created by zhujingsi on 2017/6/12.
 */

public class ArPoint implements INoProGuard {
    private double point_x;
    private double point_y;

    public double getPoint_x() {
        return point_x;
    }

    public void setPoint_x(double point_x) {
        this.point_x = point_x;
    }

    public double getPoint_y() {
        return point_y;
    }

    public void setPoint_y(double point_y) {
        this.point_y = point_y;
    }
}
