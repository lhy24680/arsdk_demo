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
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import map.baidu.ar.http.JsonHttpResponseHandler;
import map.baidu.ar.http.RequestParams;
import map.baidu.ar.http.client.ConstantHost;
import map.baidu.ar.http.client.FFRestClient;
import map.baidu.ar.model.ArInfoScenery;
import map.baidu.ar.model.ArLatLng;
import map.baidu.ar.model.ArPoiInfo;
import map.baidu.ar.model.PoiInfoImpl;
import sdk.cammer.common.baidu.map.utils.LocSdkClient;

/**
 * ArSdk主页面 Activity
 */
public class MainActivity extends Activity implements View.OnClickListener, OnGetPoiSearchResultListener {

    private Button mArOperation;
    private Button mArExplore;
    private Button mArFind;
    private Button mArCustom;
    private Button mArMoreCustom;
    private EditText mEtCategory;
    private EditText mEtCustom;
    public static ArInfoScenery arInfoScenery; // 景区
    public static ArExploreResponse arExploreResponse; // 识楼
    public static List<PoiInfoImpl> poiInfos; // 探索
    private PoiSearch mPoiSearch = null;
    private LatLng center = new LatLng(40.047854, 116.313459);
    int radius = 500; // 500米半径
    private int loadIndex = 0;
    private ArLatLng[] latLngs = {new ArLatLng(40.082545, 116.376188), new ArLatLng(40.04326, 116.376781),
            new ArLatLng(40.043204, 116.300784), new ArLatLng(39.892352, 116.433015),
            new ArLatLng(39.970696, 116.267439), new ArLatLng(40.040553, 116.315732),
            new ArLatLng(40.032156, 116.316307), new ArLatLng(40.012707, 116.265714),
            new ArLatLng(40.010497, 116.335279), new ArLatLng(40.124643, 116.701359),
            new ArLatLng(40.042321, 116.15648), new ArLatLng(41.092678, 116.343903),
            new ArLatLng(40.083846, 116.234669), new ArLatLng(40.094444, 116.29216)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_arsdk);
        mArOperation = (Button) findViewById(R.id.app_operate);
        mArExplore = (Button) findViewById(R.id.app_explore);
        mArFind = (Button) findViewById(R.id.app_find);
        mArCustom = (Button) findViewById(R.id.app_custom);
        mArMoreCustom = (Button) findViewById(R.id.app_more_custompoint);
        mEtCategory = (EditText) findViewById(R.id.category);
        mEtCustom = (EditText) findViewById(R.id.custom_category);
        mArOperation.setOnClickListener(this);
        mArExplore.setOnClickListener(this);
        mArFind.setOnClickListener(this);
        mArCustom.setOnClickListener(this);
        mArMoreCustom.setOnClickListener(this);
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        // 判断权限
        PermissionsChecker permissionsChecker = new PermissionsChecker(this);
        if (permissionsChecker.lacksPermissions()) {
            Toast.makeText(this, "缺少权限，请开启权限！", Toast.LENGTH_LONG).show();
            openSetting();
        }
        // 初始化定位SDK
        LocSdkClient.getInstance(this).getLocationStart();
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
                BDLocation location =
                        LocSdkClient.getInstance(this).getLocationStart().getLastKnownLocation();
                if (location == null) {
                    Toast.makeText(getBaseContext(), "暂时无法获取您的位置", Toast.LENGTH_LONG).show();
                    return;
                }
                RequestParams params = new RequestParams();
                params.put("qt", "scope_v2_arguide");
                params.put("uid", "2a7a25ecf9cf13636d3e1bad"); // 更换不同景区的uid，进入相应景区ar视图
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
                        LocSdkClient.getInstance(this).getLocationStart()
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
                ps.put("loc", Base64.encodeToString(locString.getBytes(), Base64.NO_WRAP));
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
            // 单点数据展示
            case R.id.app_custom:
                poiInfos = new ArrayList<PoiInfoImpl>();
                ArPoiInfo poiInfo = new ArPoiInfo();
                ArLatLng arLatLng = new ArLatLng(40.082545, 116.376188);
                poiInfo.name = mEtCustom.getText().toString();
                poiInfo.location = arLatLng;
                PoiInfoImpl poiImpl = new PoiInfoImpl();
                poiImpl.setPoiInfo(poiInfo);
                poiInfos.add(poiImpl);
                Intent intent = new Intent(MainActivity.this, FindArActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            // 多点数据展示
            case R.id.app_more_custompoint:
                poiInfos = new ArrayList<PoiInfoImpl>();

                int i = 0;
                for (ArLatLng all : latLngs) {
                    ArPoiInfo pTest = new ArPoiInfo();
                    pTest.name = "testPoint" + i++;
                    pTest.location = all;
                    PoiInfoImpl poiImplT = new PoiInfoImpl();
                    poiImplT.setPoiInfo(pTest);
                    poiInfos.add(poiImplT);
                }
                Intent inten = new Intent(MainActivity.this, FindArActivity.class);
                MainActivity.this.startActivity(inten);
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
                new PoiNearbySearchOption().keyword(mEtCategory.getText().toString()).sortType(PoiSortType
                        .distance_from_near_to_far).location(center).radius(radius).pageNum(loadIndex);
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
                ArPoiInfo poiInfo = new ArPoiInfo();
                ArLatLng arLatLng = new ArLatLng(poi.location.latitude, poi.location.longitude);
                poiInfo.name = poi.name;
                poiInfo.location = arLatLng;
                PoiInfoImpl poiImpl = new PoiInfoImpl();
                poiImpl.setPoiInfo(poiInfo);
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
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}
