package map.baidu.ar.init;

import com.baidu.lbsapi.auth.LBSAuthManager;
import com.baidu.lbsapi.auth.LBSAuthManagerListener;

import android.app.Application;
import android.content.Context;
import map.baidu.ar.utils.LocSdkClient;

/**
 * ArSdkManager 调度管理类
 */

public class ArSdkManager {
    private Context appContext;
    private String appKey;
    private static boolean isIllegalARSDKUser = true;

    private ArSdkManager() {
    }

    public String getAppKey() {
        return appKey;
    }

    public void init(String appKey) {
        this.appKey = appKey;
    }

    private static class SDKContextHolder {
        static ArSdkManager instance = new ArSdkManager();
    }

    public static ArSdkManager getInstance() {
        return SDKContextHolder.instance;
    }

    public Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }

    /**
     * 初始化
     *
     * @param listener 注册回调事件
     *
     * @return true 执行成功
     */
    public static boolean initApplication(final Application mContext, final MKGeneralListener listener) {
        LBSAuthManager auth = LBSAuthManager.getInstance(mContext);
        // TODO 确定鉴权 传递"lbs_arsdk" 值
        auth.authenticate(true, "lbs_arsdk", null, new LBSAuthManagerListener() {
            @Override
            public void onAuthResult(int status, String message) {
                if (status == 0) {
                    ArSdkManager.getInstance().setAppContext(mContext.getApplicationContext());
                    LocSdkClient.getInstance(mContext);
                    isIllegalARSDKUser = true;
                    listener.onGetPermissionState(status);
                } else {
                    isIllegalARSDKUser = false;
                    listener.onGetPermissionState(300);
                }
            }
        });
        return isIllegalARSDKUser;
    }
}
