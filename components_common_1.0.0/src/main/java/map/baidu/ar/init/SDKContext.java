package map.baidu.ar.init;

import android.app.Application;
import android.content.Context;
import map.baidu.ar.utils.LocSdkClient;

/**
 * Created by kanglichen on 2018/3/7.
 */

public class SDKContext {
    private Context appContext;
    private String appKey;

    private SDKContext() {
    }

    public String getAppKey() {
        return appKey;
    }

    public void init(String appKey) {
        this.appKey = appKey;
    }

    private static class SDKContextHolder {
        static SDKContext instance = new SDKContext();
    }

    public static SDKContext getInstance() {
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
     * @param application
     */
    public static void initApplication(Application application) {
        SDKContext.getInstance().setAppContext(application.getApplicationContext());
        LocSdkClient.getInstance(application);
    }
}
