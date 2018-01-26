package map.baidu.ar.model;


import map.baidu.ar.utils.INoProGuard;

/**
 * Created by zhujingsi on 2017/6/6.
 */

public class BubbleList implements INoProGuard {
    // 普通气泡
    private BubbleA A;
    // 特型气泡
    private BubbleB B;

    public BubbleA getA() {
        return A;
    }

    public void setA(BubbleA a) {
        A = a;
    }

    public BubbleB getB() {
        return B;
    }

    public void setB(BubbleB b) {
        B = b;
    }
}