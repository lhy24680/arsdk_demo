package sdk.cammer.common.baidu.map.application;


import android.app.Application;
import map.baidu.ar.init.SDKContext;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKContext.initApplication(this);
    }

}