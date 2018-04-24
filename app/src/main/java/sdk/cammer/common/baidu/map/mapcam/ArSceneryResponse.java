package sdk.cammer.common.baidu.map.mapcam;

import map.baidu.ar.model.ArInfoScenery;
import map.baidu.ar.utils.INoProGuard;

public class ArSceneryResponse implements INoProGuard {
    // 错误提示语
    private String err_msg;
    // 错误号
    private int err_no;
    // 返回数据
    private ArInfoScenery data;
    // 父点名称
    private String ext;
    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public int getErr_no() {
        return err_no;
    }

    public void setErr_no(int err_no) {
        this.err_no = err_no;
    }

    public ArInfoScenery getData() {
        return data;
    }

    public void setData(ArInfoScenery data) {
        this.data = data;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
