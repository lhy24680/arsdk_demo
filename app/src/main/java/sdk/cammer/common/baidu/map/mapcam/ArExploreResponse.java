package sdk.cammer.common.baidu.map.mapcam;

import java.util.ArrayList;

import map.baidu.ar.model.ArInfo;
import map.baidu.ar.utils.INoProGuard;

/**
 * Ar探索返回数据解析
 */

public class ArExploreResponse implements INoProGuard {
    // 错误提示语
    private String errstr;
    // 错误号
    private int errno;
    // 半径
    private int radius;
    // 返回数据
    private ArrayList<ArInfo> buildings;

    public String getErrstr() {
        return errstr;
    }

    public void setErrstr(String errstr) {
        this.errstr = errstr;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public ArrayList<ArInfo> getBuildings() {
        return buildings;
    }

    public void setBuildings(ArrayList<ArInfo> buildings) {
        this.buildings = buildings;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
