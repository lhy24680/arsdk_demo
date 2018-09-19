package sdk.cammer.common.baidu.map.application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import android.app.Application;
import android.widget.Toast;
import map.baidu.ar.init.ArSdkManager;
import map.baidu.ar.init.MKGeneralListener;

/**
 * Ar sdk application
 */
public class DemoApplication extends Application {
    private static DemoApplication mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // AR模块 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        super.onCreate();
        mInstance = this;
        ArSdkManager.initApplication(this, new MyGeneralListener());
        // 检索模块 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        // 检索模块 自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        // 包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    public static DemoApplication getInstance() {
        return mInstance;
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        @Override
        public void onGetPermissionState(int iError) {
            // 非零值表示key验证未通过
            if (iError != 0) {
                // 授权Key错误：
                Toast.makeText(DemoApplication.getInstance().getApplicationContext(),
                        "arsdk 验证异常，请在AndoridManifest.xml中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError, Toast
                                .LENGTH_LONG).show();
            } else {
                Toast.makeText(DemoApplication.getInstance().getApplicationContext(), "key认证成功", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

}