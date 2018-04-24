package map.baidu.ar.detail;

import map.baidu.ar.exception.LocationGetFailException;
import map.baidu.ar.model.ArPoint;
import map.baidu.ar.utils.Point;

/**
 * ArTTSMediaController 展示内容所需的数据
 * Created by lixiang34 on 2018/1/2.
 */

public interface IMediaControllerData {
    /**
     * 景点介绍，一般很长
     *
     * @return
     */
    String getDescription() throws LocationGetFailException;

    /**
     * 景点名字，一般几个字
     *
     * @return
     */
    String getName();

    /**
     * 数据来源
     *
     * @return
     */
    String getSource();

    /**
     * 实时计算的距离
     *
     * @return
     * @throws LocationGetFailException
     */
    double getDistance() throws LocationGetFailException;

    /**
     * 图片地址列表序列化
     *
     * @return
     */
    String getImageUrlText();

    /**
     * 第一张图片的地址
     *
     * @return
     */
    String getFirstImageUrl();

    /**
     * 子景点 UID
     *
     * @return
     */
    String getUid();

    /**
     * 子景点坐标，两个字段数据一样，只是格式不同。
     *
     * @return
     */
    ArPoint getPoint();

    Point getGeoPoint();
}
