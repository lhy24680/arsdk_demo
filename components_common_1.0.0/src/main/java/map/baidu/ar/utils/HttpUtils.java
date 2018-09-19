package map.baidu.ar.utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Http 操作工具类
 */
public class HttpUtils {

    //    public final static String NET = String.valueOf(ComAPIManager.getComAPIManager().getSystemAPI()
    // .getNetworkType());
    //    public final static String RESID = ComAPIManager.getComAPIManager().getSystemAPI().getResId();
    //    public final static String CHANNEL = ComAPIManager.getComAPIManager().getSystemAPI().getChannel();
    //    public final static String DPI = String.valueOf(ComAPIManager.getComAPIManager().getSystemAPI()
    // .getScreenDensity());
    //    public final static String CUID = ComAPIManager.getComAPIManager().getSystemAPI().getCuid();
    //    public final static String OS =
    //            ComAPIManager.getComAPIManager().getSystemAPI().getOsName() + ComAPIManager
    //                    .getComAPIManager().getSystemAPI().getOsVersion();
    //    public final static String SV = ComAPIManager.getComAPIManager().getSystemAPI().getAppVersion();
    //    public final static String HTTP_PHPUI_OS =
    //            "&os=" + ComAPIManager.getComAPIManager().getSystemAPI().getOsName() + ComAPIManager
    // .getComAPIManager()
    //                    .getSystemAPI().getOsVersion();
    //    public final static String HTTP_PHPUI_SV = "&sv=" + ComAPIManager.getComAPIManager().getSystemAPI()
    // .getAppVersion();

    // HTTP constant
    private static final int HTTP_TIMEOUT = 10000;
    private static final String HTTP_GET = "GET";

    private HttpUtils() {
    }

    public interface IAsyHttpCallBack {

        public void onHttpRecieved(String json);

        public void onHttpError();

    }

    public static void asyHttpGet(final String url, final IAsyHttpCallBack callback) {

        Thread httpThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    String json = doHttpGet(url);

                    //String json = getJsonContent(url, "utf-8");
                    if (callback != null) {
                        callback.onHttpRecieved(json);
                    }
                } catch (Exception e) {
                    Log.e("HttpUtil", e.toString());
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onHttpError();
                    }
                }

            }
        });

        httpThread.start();

    }

    public static String doHttpGet(String url) {
        BasicNameValuePair[] pairs = null;
        return doHttpGet(url, pairs);
    }

    public static byte[] doHttpGetBytes(String url, NameValuePair... nameValuePairs) {

        HttpGet httpget = null;
        if (nameValuePairs == null || nameValuePairs.length == 0) {
            httpget = createHttpGet(url);
        } else {
            httpget = createHttpGet(url, nameValuePairs);
        }

        byte[] bytes = null;

        try {
            HttpResponse response = executeHttpRequest(httpget);
            HttpEntity httpEntity = response.getEntity();
            bytes = EntityUtils.toByteArray(httpEntity);
            //strTmp = EntityUtils.toString(httpEntity, "utf-8");
            int statusCode = response.getStatusLine().getStatusCode();

            switch (statusCode) {
                case 200:
                    Log.e(HttpUtils.class.getName(), "GET:\t" + bytes.toString());
                    break;

                default:
                    Log.e(HttpUtils.class.getName(), "POST ErrorCode:\t" + "statusCode");
                    response.getEntity().consumeContent();
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static String doHttpGet(String url, NameValuePair... nameValuePairs) {

        HttpGet httpGet = createHttpGet(url, nameValuePairs);

        String strTmp = "";

        try {
            HttpResponse response = executeHttpRequest(httpGet);
            HttpEntity httpEntity = response.getEntity();
            //EntityUtils.toByteArray()
            strTmp = EntityUtils.toString(httpEntity, "utf-8");
            int statusCode = response.getStatusLine().getStatusCode();

            switch (statusCode) {
                case 200:
                    Log.e(HttpUtils.class.getName(), "GET:\t" + strTmp);
                    break;

                default:
                    Log.e(HttpUtils.class.getName(), "POST ErrorCode:\t" + "statusCode");
                    response.getEntity().consumeContent();
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return strTmp;
    }

    public static String doHttpPost(String url, NameValuePair... nameValuePairs) {

        HttpPost httpPost = createHttpPost(url, nameValuePairs);

        String strTmp = "";
        try {
            HttpResponse response = executeHttpRequest(httpPost);
            HttpEntity httpEntity = response.getEntity();
            strTmp = EntityUtils.toString(httpEntity, "UTF-8");
            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 200:
                    Log.e(HttpUtils.class.getName(), "POST:\t" + strTmp);
                    break;

                default:
                    Log.e(HttpUtils.class.getName(), "POST ErrorCode:\t" + "statusCode");
                    response.getEntity().consumeContent();
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strTmp;
    }

    private static List<NameValuePair> stripNulls(NameValuePair... nameValuePairs) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        if (nameValuePairs != null) {
            for (int i = 0; i < nameValuePairs.length; i++) {
                NameValuePair param = nameValuePairs[i];
                if (param.getValue() != null) {
                    params.add(param);
                }
            }
        }

        //        NameValuePair sv = new BasicNameValuePair("sv", SV);
        //        NameValuePair os = new BasicNameValuePair("os", OS);
        //        NameValuePair cuid = new BasicNameValuePair("cuid", CUID);
        //        NameValuePair net = new BasicNameValuePair("net", NET);
        //        NameValuePair resid = new BasicNameValuePair("resid", RESID);
        //        NameValuePair channel = new BasicNameValuePair("channel", CHANNEL);
        //        NameValuePair dpi = new BasicNameValuePair("dpi", DPI);
        //
        //        params.add(sv);
        //        params.add(os);
        //        params.add(cuid);
        //        params.add(net);
        //        params.add(resid);
        //        params.add(channel);
        //        params.add(dpi);

        return params;
    }

    private static HttpPost createHttpPost(String url, NameValuePair... nameValuePairs) {

        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(stripNulls(nameValuePairs), HTTP.UTF_8));
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException("Unable to encode http parameters.");
        }

        return httpPost;
    }

    private static HttpGet createHttpGet(String url, NameValuePair... nameValuePairs) {
        HttpGet httpget = null;
        //if (nameValuePairs != null && nameValuePairs.length > 0) {
        String query = URLEncodedUtils.format(stripNulls(nameValuePairs), HTTP.UTF_8);
        httpget = new HttpGet(url + query);
        //} else {
        //    httpGet = new HttpGet(url);
        //}
        //httpGet.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
        //httpGet.addHeader("Accept-Encoding", "gzip");

        return httpget;
    }

    private static HttpGet createHttpGet(String url) {
        HttpGet httpget = new HttpGet(url);
        return httpget;
    }

    //    private static void localCookieStore(AbstractHttpClient client) {
    //
    //        HttpClientParams.setCookiePolicy(client.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
    //
    //        Boolean hasBduss = AccountManager.getInstance().isLogin();
    //        if (hasBduss) {
    //            final CookieStore cookieStore = new BasicCookieStore();
    //            BasicClientCookie cookie =
    //                    new BasicClientCookie("BDUSS", AccountManager.getInstance().getUserInfo().mBDUSS);
    //            cookie.setVersion(0);
    //            cookie.setDomain(".baidu.com");
    //            cookie.setPath("/");
    //            cookieStore.addCookie(cookie);
    //            client.setCookieStore(cookieStore);
    //        }
    //    }

    private static HttpResponse executeHttpRequest(HttpRequestBase httpRequest) throws IOException {
        try {
            HttpClient client = new DefaultHttpClient();
            //            localCookieStore((AbstractHttpClient) client);
            HttpResponse httpResponse = client.execute(httpRequest);
            return httpResponse;
        } catch (IOException e) {
            httpRequest.abort();
            throw e;
        }
    }

    //预留方法
    /*
    public static Bitmap getBitmap(String url) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl
                    .openConnection();
            conn.setConnectTimeout(HTTP_TIMEOUT);
            conn.setRequestMethod(HTTP_GET);
            conn.setDoInput(true);
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            Log.e("", e.getMessage(), e);
        }
        return bitmap;
    }
    */

    public static void getBytesToFile(String url_path, File file) {

        try {
            /*URL url = new URL(url_path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(HTTP_TIMEOUT);
            conn.setRequestMethod(HTTP_GET);
            conn.setDoInput(true);
            InputStream is = conn.getInputStream();
            byte[] data = IOUtils.getBytesInputSteam(is);*/
            byte[] data = doHttpGetBytes(url_path);
            FileOutputStream os = new FileOutputStream(file);
            os.write(data);
            os.close();
        } catch (Exception e) {
            //Log.e(HttpUtils.class.getName(), e.getMessage());
            e.printStackTrace();
        }
        return;
    }

    /*
    private static String changeInputStream(InputStream inputStream,
                                            String encode) {

        String jsonString = null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {

            while ((len = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, len);
            }

            jsonString = new String(outputStream.toByteArray(), encode);
            inputStream.close();

        } catch (IOException e) {

            Log.e("", e.getMessage(), e);
        }

        return jsonString;
    }
    */

}
