package sdk.cammer.common.baidu.map.mapcam;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import map.baidu.ar.http.JsonHttpResponseHandler;
import map.baidu.ar.http.RequestParams;
import map.baidu.ar.http.client.ConstantHost;
import map.baidu.ar.http.client.FFRestClient;
import map.baidu.ar.init.SDKContext;
import map.baidu.ar.model.ArInfoScenery;
import map.baidu.ar.model.PoiInfoImpl;
import map.baidu.ar.utils.LocSdkClient;

public class MainActivity extends Activity implements View.OnClickListener, OnGetPoiSearchResultListener {

    private Button mArOperation;
    private Button mArExplore;
    private Button mArFind;
    private EditText mEditText;
    public static ArInfoScenery arInfoScenery; // 景区
    public static ArExploreResponse arExploreResponse; // 识楼
    public static List<PoiInfoImpl> poiInfos; // 探索
    private PoiSearch mPoiSearch = null;
    private LatLng center = new LatLng(40.047854, 116.313459);
    int radius = 500;//500米半径
    private int loadIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_arsdk);
        mArOperation = (Button) findViewById(R.id.app_operate);
        mArExplore = (Button) findViewById(R.id.app_explore);
        mArFind = (Button) findViewById(R.id.app_find);
        mEditText = (EditText) findViewById(R.id.category);
        mArOperation.setOnClickListener(this);
        mArExplore.setOnClickListener(this);
        mArFind.setOnClickListener(this);
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        // 判断权限
        PermissionsChecker permissionsChecker = new PermissionsChecker(this);
        if (permissionsChecker.lacksPermissions()) {
            Toast.makeText(this, "缺少权限，请开启权限！", Toast.LENGTH_LONG).show();
            openSetting();
        }
    }

    public void openSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 景区功能
            case R.id.app_operate:
                BDLocation location = LocSdkClient.getInstance(this).getLocationStart().getLastKnownLocation();
                Toast.makeText(MainActivity.this,
                        "lng:" + String.valueOf(location.getLongitude()) + "," + "lat:" + String
                                .valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
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
                            ArSceneryResponse arSceneryResponse =
                                    fromJson(String.valueOf(response), ArSceneryResponse.class);
                            if (arSceneryResponse != null && arSceneryResponse.getData() != null
                                    && arSceneryResponse.getData().getSon() != null
                                    && arSceneryResponse.getData().getSon().size() > 0
                                    && arSceneryResponse.getData().getAois() != null
                                    && arSceneryResponse.getData().getAois().size() > 0
                                    && arSceneryResponse.getData().getAois()
                                    .get(0) != null && arSceneryResponse.getData().getAois().get(0).length > 0) {
                                arInfoScenery = arSceneryResponse.getData();
                                arInfoScenery.init();
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
            // 识楼功能
            case R.id.app_explore:
                BDLocation loc =
                        LocSdkClient.getInstance(SDKContext.getInstance().getAppContext()).getLocationStart()
                                .getLastKnownLocation();
                int x;
                int y;
                if (loc != null) {
                    x = (int) loc.getLongitude();
                    y = (int) loc.getLatitude();
                } else {
                    Toast.makeText(getBaseContext(), "暂时无法获取您的位置", Toast.LENGTH_LONG).show();
                    return;
                }
                RequestParams ps = new RequestParams();
                String locString = x + "," + y;
                ps.put("loc", Base64.encodeBase64String(locString.getBytes()));
                FFRestClient.get(ConstantHost.AR_BUILDING_URL, ps, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                        if (response != null) {
                            ArExploreResponse arResponse = fromJson(String.valueOf(response), ArExploreResponse.class);
                            if (arResponse != null) {
                                arExploreResponse = arResponse;
                                Intent intent = new Intent(MainActivity.this, ExploreArActivity.class);
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
            // 探索功能
            case R.id.app_find:
                searchNearbyProcess();
                break;
            default:
                break;
        }
    }

    /**
     * 响应周边搜索按钮点击事件
     *
     * @param
     */
    public void searchNearbyProcess() {
        PoiNearbySearchOption nearbySearchOption =
                new PoiNearbySearchOption().keyword(mEditText.getText().toString()).sortType(PoiSortType
                        .distance_from_near_to_far)
                        .location(center)
                        .radius(radius).pageNum(loadIndex);
        mPoiSearch.searchNearby(nearbySearchOption);
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

    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            poiInfos = new ArrayList<PoiInfoImpl>();
            for (PoiInfo poi : result.getAllPoi()) {
                PoiInfoImpl poiImpl = new PoiInfoImpl();
                poiImpl.setPoiInfo(poi);
                poiInfos.add(poiImpl);
            }
            Toast.makeText(this, "查询到: " + poiInfos.size() + " ,个POI点", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, FindArActivity.class);
            MainActivity.this.startActivity(intent);
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}
