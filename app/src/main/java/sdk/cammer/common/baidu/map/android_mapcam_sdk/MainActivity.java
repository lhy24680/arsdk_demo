package sdk.cammer.common.baidu.map.android_mapcam_sdk;

import org.apache.http.Header;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import map.baidu.ar.http.JsonHttpResponseHandler;
import map.baidu.ar.http.RequestParams;
import map.baidu.ar.http.client.ConstantHost;
import map.baidu.ar.http.client.FFRestClient;
import map.baidu.ar.model.ArInfoScenery;
import map.baidu.ar.utils.LocSdkClient;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button mArOperation;
    public static ArInfoScenery arInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_arsdk);
        mArOperation = (Button) findViewById(R.id.app_operate);
        mArOperation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_operate:
                BDLocation location = LocSdkClient.getInstance(this).getLocationStart().getLastKnownLocation();
                Toast.makeText(MainActivity.this, String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
                RequestParams params = new RequestParams();
                params.put("qt", "scope_v2_arguide");
                params.put("uid", "62d852adf09e449a2fb17ef5");
                params.put("ver", 2);
                FFRestClient.get(ConstantHost.SCOPE_URL, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                        //                        MProgressDialog.dismiss();
                        if (response != null) {
                            ArResponse arResponse = fromJson(String.valueOf(response), ArResponse.class);
                            if (arResponse != null && arResponse.getData() != null
                                    && arResponse.getData().getSon() != null
                                    && arResponse.getData().getSon().size() > 0
                                    && arResponse.getData().getAois() != null
                                    && arResponse.getData().getAois().size() > 0 && arResponse.getData().getAois()
                                    .get(0) != null && arResponse.getData().getAois().get(0).length > 0) {
                                arInfo = arResponse.getData();
                                arInfo.init();
                                //                                int x;
                                //                                int y;
                                // 定位坐标*
                                //                                Location locNaData = LocNativeUtil.getLocation
                                // (context);
                                //                                Map<String, Double> hashMap;
                                //                                hashMap = CoordinateConverter.convertLL2MC(locNaData
                                //                                        .getLongitude(), locNaData.getLatitude());
                                //                                if (locNaData != null) {
                                //                                    x = (int) hashMap.get("x").intValue();
                                //                                    y = (int) hashMap.get("y").intValue();

                                //                                    x = (int) locNaData.getLongitude();
                                //                                    y = (int) locNaData.getLatitude();
                                //                                } else {
                                //权限处理
                                //                                    SceneryEntity.navigateTo(comId,
                                // ARAuthorityPage.class.getName(), null, bundle);
                                //                                    return;
                                //                                }
                                //                                boolean isFirstInSceneryARPage = PreferencesUtil
                                // .getInstance()
                                //                                        .getValue(applicationContext,
                                // IS_FIRST_IN_SCENERY_AR_PAGE, true);
                                //                                if (isFirstInSceneryARPage) {
                                //                                    SceneryEntity.navigateTo(comId, ARGuidePage
                                // .class.getName(), null, bundle);
                                //                                    return;
                                //                                }
                                //                                bundle.putBoolean(ARMainPage
                                // .IS_FIRST_IN_SCENERY_AR_PAGE, isFirstInSceneryARPage);
                                //                                gotoForwardPage(comId, bundle);

                                Intent intent = new Intent(MainActivity.this, SceneryArActivity.class);
                                MainActivity.this.startActivity(intent);
                            } else {
                                Toast.makeText(getBaseContext(), "数据出错，请稍后再试", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "数据出错，请稍后再试", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(getBaseContext(), "FFRestClient 请求网络错误 ", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                break;
        }
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
