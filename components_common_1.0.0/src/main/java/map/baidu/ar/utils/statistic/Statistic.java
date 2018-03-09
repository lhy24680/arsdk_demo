package map.baidu.ar.utils.statistic;

/**
 * 简化统计接口
 * Created by 享 on 2016/8/23.
 */
public class Statistic {

    private static final StatisticEntity ENTITY = new StatisticEntity();

    /**
     * 注意要在UI线程使用，否则会串参数。
     * 后续优化保证线程同步。
     *
     * @param name  统计项参数名
     * @param value 统计项参数值
     *
     * @return FluentInterface entity
     */
    public static StatisticEntity param(String name, String value) {
//        ControlLogStatistics.getInstance().addArg(name, value);
        return ENTITY;
    }

    /**
     * 注意要在UI线程使用，否则会串参数。
     * 后续优化保证线程同步。
     *
     * @param name  统计项参数名
     * @param value 统计项参数值
     *
     * @return FluentInterface entity
     */
    public static StatisticEntity param(String name, int value) {
//        ControlLogStatistics.getInstance().addArg(name, value);
        return ENTITY;
    }

    /**
     * 注意要在UI线程使用，否则会串参数。
     * 后续优化保证线程同步。
     *
     * @param key 统计项参数Key
     */
    public static void key(String key) {
//        ControlLogStatistics.getInstance().addLog(key);
    }
}
