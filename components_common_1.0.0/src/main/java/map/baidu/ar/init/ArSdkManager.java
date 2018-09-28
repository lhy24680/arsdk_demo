package map.baidu.ar.init;

import org.apache.http.Header;
import org.json.JSONObject;

import com.baidu.lbsapi.auth.LBSAuthManager;
import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;
import map.baidu.ar.http.JsonHttpResponseHandler;
import map.baidu.ar.http.RequestParams;
import map.baidu.ar.http.client.ConstantHost;
import map.baidu.ar.http.client.FFRestClient;

/**
 * ArSdkManager 调度管理类
 */

public class ArSdkManager {
    private Context appContext;
    private String appKey;
    private static boolean isIllegalARSDKUser = true;
    public static MKGeneralListener listener;
    public OnGetDataResultListener mDatalistener;

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
     * 初始化接口
     *
     * @param mContext  Application mContext
     * @param lis 注册回调事件
     *
     * @return true 执行成功
     */
    public boolean initApplication(final Application mContext, final MKGeneralListener lis) {
        listener = lis;
        LBSAuthManager auth = LBSAuthManager.getInstance(mContext);
        // TODO 确定鉴权 传递"lbs_arsdk" 值
        auth.authenticate(true, "lbs_arsdk", null, new LBSAuthManagerListener() {
            @Override
            public void onAuthResult(int status, String message) {
                if (status == 0) {
                    ArSdkManager.getInstance().setAppContext(mContext.getApplicationContext());
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

    /**
     * 设置获取数据接口监听
     *
     * @param listener OnGetDataResultListener监听
     */
    public void setOnGetDataResultListener(OnGetDataResultListener listener) {
        mDatalistener = listener;
    }

    /**
     * 调用Ar景区接口
     *
     * @param uid 景区uid信息
     */
    public void searchSceneryInfo(String uid) {
        if (TextUtils.isEmpty(uid)) {
            Toast.makeText(appContext, "uid不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mDatalistener == null) {
            Toast.makeText(appContext, "未设置获取数据接口监听OnGetDataResultListener", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("qt", "scope_v2_arguide");
        params.put("uid", uid);
        params.put("ver", 2);
        FFRestClient.get(ConstantHost.SCOPE_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (response != null) {
                    ArSceneryResponse arSceneryResponse =
                            fromJson(String.valueOf(response), ArSceneryResponse.class);
                    mDatalistener.onGetSceneryResult(arSceneryResponse);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(appContext, "请求网络错误 ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 调用Ar识楼接口
     *
     */
    public void searchBuildingInfo() {
        if (mDatalistener == null) {
            Toast.makeText(appContext, "未设置获取数据接口监听OnGetDataResultListener", Toast.LENGTH_SHORT).show();
            return;
        }
        int x;
        int y;
        if (listener != null) {
            x = (int) listener.onGetBDLocation().getLongitude();
            y = (int) listener.onGetBDLocation().getLatitude();
        } else {
            Toast.makeText(appContext, "暂时无法获取您的位置", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams ps = new RequestParams();
        String locString = x + "," + y;
        ps.put("loc", Base64.encodeToString(locString.getBytes(), Base64.NO_WRAP));
        FFRestClient.get(ConstantHost.AR_BUILDING_URL, ps, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (response != null) {
                    ArBuildingResponse  arResponse = fromJson(String.valueOf(response), ArBuildingResponse
                            .class);
                    mDatalistener.onGetBuildingResult(arResponse);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(appContext, "请求网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            return gson.fromJson(json, clazz);
        } catch (Exception ex) {
            return null;
        }
    }
}
