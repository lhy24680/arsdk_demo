package map.baidu.ar.utils;

/**
 * 比例尺和Level对应关系
 * Created by 享 on 2017/2/6.
 */

public class MapScaleUtils {
    public static final int SCALE_5M = 21; // 放大到最大，显示5M，Level是21
    public static final int SCALE_10M = 20; // 稍缩小一点，就变成10M比例尺。当比例尺显示10M时，Level在20-21之间变动。
    public static final int SCALE_20M = 19; // 以此类推。
    public static final int SCALE_50M = 18;
    public static final int SCALE_100M = 17;
    public static final int SCALE_200M = 16; // 屏幕显示比例尺介于200和500之间
    public static final int SCALE_500M = 15;
    public static final int SCALE_1KM = 14;
    public static final int SCALE_2KM = 13;
    public static final int SCALE_5KM = 12;
    public static final int SCALE_10KM = 11;
    public static final int SCALE_20KM = 10;
    public static final int SCALE_25KM = 9;
    public static final int SCALE_50KM = 8;
    public static final int SCALE_100KM = 7;
    public static final int SCALE_200KM = 6;
    public static final int SCALE_500KM = 5;
    public static final int SCALE_1000KM = 4; // 缩小到最小，显示1000KM，Level是4。稍放大一点，比例尺仍是1000KM，Level是4-5之间变动
}
