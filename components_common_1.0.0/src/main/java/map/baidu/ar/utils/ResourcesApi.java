package map.baidu.ar.utils;

import android.app.Activity;
import android.view.LayoutInflater;

import com.baidu.components.api.extra.APIProxy;

/**
 * 统一资源获取类
 * Created by 享 on 2016/11/7.
 */
public class ResourcesApi {

    /**
     * 获取组件资源
     *
     * @return 组件资源
     */
    public static android.content.res.Resources getComRes() {
        return APIProxy.res().getResources();
    }

    /**
     * 获取组件的LayoutInflater
     *
     * @return 组件的LayoutInflater
     */
    public static LayoutInflater getComInflater() {
        return APIProxy.res().getInflater();
    }

    /**
     * 获取组件容器的Activity
     *
     * @return 组件容器的Activity
     */
    public static Activity getBaseActivity() {
        return APIProxy.res().getBaseActivity();
    }
}
