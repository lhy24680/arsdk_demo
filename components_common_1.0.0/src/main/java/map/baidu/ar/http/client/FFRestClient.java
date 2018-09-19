package map.baidu.ar.http.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import map.baidu.ar.http.AsyncHttpClient;
import map.baidu.ar.http.AsyncHttpResponseHandler;
import map.baidu.ar.http.JsonHttpResponseHandler;
import map.baidu.ar.http.PersistentCookieStore;
import map.baidu.ar.http.RequestParams;

/**
 * 网络请求
 */
public class FFRestClient {

    // 设置超时
    private static final int TIMEOUT = 10 * 1000;

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static PersistentCookieStore httpCookieStore = null;

    private static final Handler restHandler = new Handler();

    static {
        client.setTimeout(TIMEOUT);
        client.setMaxRetriesAndTimeout(3, 2000);

    }

    public static void setCookieStore(Context context) {
        if (httpCookieStore == null) {
            httpCookieStore = new PersistentCookieStore(context);
            // 设置cookiestore
            client.setCookieStore(httpCookieStore);
        }
    }

    /**
     * 获取数据
     *
     * @param url             连接服务端地址
     * @param params          请求参数
     * @param responseHandler 结果响应
     */
    public static void get(String url, RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        if (TextUtils.isEmpty(url)) {
            url = ConstantHost.BASE_URL;
        }
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(final int statusCode, final Header[] headers, final Throwable throwable,
                                  final JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                restHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JsonHttpResponseHandler clientRespose = (JsonHttpResponseHandler) responseHandler;
                            clientRespose.onFailure(statusCode, headers, throwable, errorResponse);
                        } catch (Exception e) {
                            //                            MProgressDialog.dismiss();
                            //                            MToast.show(SceneryEntity.applicationContext, "网络连接异常，请稍后再试");
                            e.printStackTrace();
                        }
                    }

                });

            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers, final JSONObject response) {
                try {
                    super.onSuccess(statusCode, headers, response);

                    dispatchOnSuccess(responseHandler, statusCode, headers, response);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private static void get(RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        get(null, params, responseHandler);
    }

    public static void post(String url, String qt, RequestParams params,
                            final AsyncHttpResponseHandler responseHandler) {

        if (TextUtils.isEmpty(url)) {
            url = ConstantHost.BASE_URL;
        }
        //        Uri.Builder builder = new Uri.Builder();
        //        if (!TextUtils.isEmpty(qt)) {
        //            builder.appendQueryParameter("qt", qt);
        //        }
        //        来源统计开始
        //        if (!TextUtils.isEmpty(UniversalSourceFromParameter.getInstance().getResId())) {
        //            builder.appendQueryParameter(UniversalSourceFromParameter.KEY_RES_ID,
        // UniversalSourceFromParameter.getInstance().getResId());
        //        }
        //        if (!TextUtils.isEmpty(UniversalSourceFromParameter.getInstance().getQid())) {
        //            builder.appendQueryParameter(UniversalSourceFromParameter.KEY_QID, UniversalSourceFromParameter
        // .getInstance().getQid());
        //        }
        //        if (!TextUtils.isEmpty(UniversalSourceFromParameter.getInstance().getSrcFrom())) {
        //            builder.appendQueryParameter(UniversalSourceFromParameter.KEY_SRC_FROM,
        // UniversalSourceFromParameter.getInstance().getSrcFrom());
        //        }
        ////        builder.appendQueryParameter("travel_mode", StatisticHelper.getGl);
        //
        //        String myUrl = url + builder.build().toString();

        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(final int statusCode, final Header[] headers, final Throwable throwable,
                                  final JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                restHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JsonHttpResponseHandler clientRespose = (JsonHttpResponseHandler) responseHandler;

                            clientRespose.onFailure(statusCode, headers, throwable, errorResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });

            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers, final JSONObject response) {

                super.onSuccess(statusCode, headers, response);

                dispatchOnSuccess(responseHandler, statusCode, headers, response);

            }
        });

    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return ConstantHost.BASE_URL + relativeUrl;
    }

    public static RequestParams buildBaseParams() {
        RequestParams base = new RequestParams();
        return base;

    }

    public static void buildOptinal(Hashtable optional, RequestParams base) {
        if (optional != null) {
            Set<String> keys = optional.keySet();
            for (String key : keys) {
                base.put(key, optional.get(key));
            }
        }
    }

    private static void dispatchOnSuccess(final AsyncHttpResponseHandler responseHandler, final int statusCode,
                                          final Header[] headers, final JSONObject response) {
        // if (Util.responseSuc(response)) {

        restHandler.post(new Runnable() {
            @Override
            public void run() {
                /* try { */
                if (responseHandler != null) {
                    JsonHttpResponseHandler clientRespose = (JsonHttpResponseHandler) responseHandler;

                    clientRespose.onSuccess(statusCode, headers, response);
                }
                /*
                 * } catch (Exception e) { LogUtils.e("ss",e);
                 *
                 * }
                 */
            }
        });

    }

    public static void getSceneryTopList(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "scope_toplist");
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    //    public static void getVacationListData(AsyncHttpResponseHandler responseHandler) {
    //        RequestParams base = buildBaseParams();
    //        base.put("qt", "vacation_list");
    //        base.put("device_from", "android");
    //        base.put("vacation_type", "around");
    //        base.put("cityId", APIProxy.settings().getLocationCityId());
    //        get(ConstantHost.MENPIAO_BASE_URL, base, responseHandler);
    //    }

    public static void getSceneryChannnelData(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "scope_channel");
        base.put("v", "1.0");
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    //    public static void getUserIsLocalData(JsonHttpResponseHandler responseHandler) {
    //        Hashtable table = new Hashtable();
    //        table.put("qt", "scope_local");
    //        table.put("cuid", APIProxy.sys().getCuid());
    //        table.put("c", APIProxy.settings().getLocationCityId() + "");
    //        RequestParams params = new RequestParams();
    //        buildOptinal(table, params);
    //        get(params, responseHandler);
    //    }

    public static void getSceneryAroundData(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "around_allrecmd");
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    public static void getSceneryWeekActivityData(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "scope_weekendlist");
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    public static void getAroundCondData(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "around_cond");
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    public static void getLineListData(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "scope_reclinelist");
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    public static void getDiscountListData(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "scope_preferential");
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    public static void getOrderInputData(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "order_input");
        //        base.put("isLocal_status", StatisticHelper.isLocal_status);
        //        base.put("travel_mode", StatisticHelper.getGlobalTravelMode());
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    public static void postOrderData(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        buildOptinal(optional, base);
        post(null, "order_create", base, responseHandler);
    }

    public static void postPriceCalculate(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        buildOptinal(optional, base);
        post(null, "order_calculate", base, responseHandler);
    }

    public static void getOrderPay(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "pay_pay");
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    public static void getPromoCode(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "verify_promocode");
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    public static void getLocalAudioList(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "scope_audiolist");
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    public static void getRouteAudioList(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "scope_audio");
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    public static void postPic(Hashtable optional, AsyncHttpResponseHandler responseHandler, File picFile,
                               String contentType) {
        RequestParams base = buildBaseParams();
        buildOptinal(optional, base);
        try {
            base.put("picData", picFile, contentType);
        } catch (FileNotFoundException e) {
            //            LogUtils.e("scenery", e);
        }
        post(null, "scope_addugclive", base, responseHandler);
    }

    public static void getRefundInfo(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        base.put("qt", "order_apply_refundinfo");
        buildOptinal(optional, base);
        get(base, responseHandler);
    }

    public static void postRefundInfo(Hashtable optional, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        buildOptinal(optional, base);
        post(null, "order_apply_refund", base, responseHandler);
    }

    public static void getFEHttp(String url, JSONObject jsonObject, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        Iterator<?> it = jsonObject.keys();
        while (it.hasNext()) {
            String key = it.next().toString();
            if (!TextUtils.isEmpty(key) && jsonObject.has(key) && !TextUtils.isEmpty(jsonObject.optString(key))) {
                base.put(key, jsonObject.optString(key));
            }
        }
        get(url, base, responseHandler);
    }

    public static void postFEHttp(String url, JSONObject jsonObject, AsyncHttpResponseHandler responseHandler) {
        RequestParams base = buildBaseParams();
        Iterator<?> it = jsonObject.keys();
        while (it.hasNext()) {
            String key = it.next().toString();
            if (!TextUtils.isEmpty(key) && jsonObject.has(key) && !TextUtils.isEmpty(jsonObject.optString(key))) {
                base.put(key, jsonObject.optString(key));
            }
        }
        post(url, null, base, responseHandler);
    }
}