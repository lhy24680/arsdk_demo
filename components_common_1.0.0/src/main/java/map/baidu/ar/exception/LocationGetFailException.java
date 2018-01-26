package map.baidu.ar.exception;

/**
 * 调用基线定位信息，获取不到定位信息的时候抛出的异常
 * Created by lixiang34 on 2017/7/10.
 */

public class LocationGetFailException extends Exception {

    public LocationGetFailException() {
        super("获取用户位置信息失败");
    }
}
