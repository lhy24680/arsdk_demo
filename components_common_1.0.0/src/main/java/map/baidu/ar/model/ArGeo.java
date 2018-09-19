package map.baidu.ar.model;


import map.baidu.ar.utils.INoProGuard;

public class ArGeo implements INoProGuard {
    // 楼块儿坐标集合
    private String pts;
    // 权重
    private int rank;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getPts() {
        return pts;
    }

    public void setPts(String pts) {
        this.pts = pts;
    }
}
