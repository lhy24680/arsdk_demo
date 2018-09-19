/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package map.baidu.ar.init;

/**
 * <p>
 * 一般事件通知接口<br>
 * <p>
 * 该接口返回网络状态，授权验证等结果，用户需要实现该接口以处理相应事件
 */
public interface MKGeneralListener {

    /**
     * 返回授权验证错误
     * 
     * @param iError 授权验证码，iError为 300时表示验证失败。
     */
    public void onGetPermissionState(int iError);
}