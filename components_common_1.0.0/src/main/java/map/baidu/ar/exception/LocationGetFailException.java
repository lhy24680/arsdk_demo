package map.baidu.ar.exception;

/**
 * 调用定位信息，获取不到定位信息的时候抛出的异常
 */

public class LocationGetFailException extends Exception {

    public LocationGetFailException() {
        super("获取用户位置信息失败");
    }
}
