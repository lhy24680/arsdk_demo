package map.baidu.ar.utils.statistic;

/**
 * StatisticEntity
 * Created by 享 on 2016/8/23.
 */
public class StatisticEntity {
    public StatisticEntity param(String param, String value) {
//        ControlLogStatistics.getInstance().addArg(param, value);
        return this;
    }

    public StatisticEntity param(String param, int value) {
//        ControlLogStatistics.getInstance().addArg(param, value);
        return this;
    }

    public void key(String key) {
//        ControlLogStatistics.getInstance().addLog(key);
    }

}
