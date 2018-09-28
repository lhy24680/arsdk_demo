/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package map.baidu.ar.init;

import com.google.gson.annotations.SerializedName;

import map.baidu.ar.model.ArInfoScenery;
import map.baidu.ar.utils.INoProGuard;

/**
 * Ar景区返回数据解析
 */
public class ArSceneryResponse implements INoProGuard {
    // 错误提示语
    @SerializedName("err_msg")
    private String errMsg;
    // 错误号
    @SerializedName("err_no")
    private int errNo;
    // 返回数据
    private ArInfoScenery data;
    // 父点名称
    private String ext;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
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
