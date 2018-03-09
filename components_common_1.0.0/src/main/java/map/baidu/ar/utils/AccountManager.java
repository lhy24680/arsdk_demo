package map.baidu.ar.utils;

import map.baidu.ar.model.EntityOfUser;
/**
 * Created by xingdaming on 15/9/2.
 */
public class AccountManager {

    private final String KEY_HTTP_CODE = "code";
    private final String KEY_HTTP_PHONE = "phone";
    private final String KEY_HTTP_RESULT = "result";
    private final String KEY_HTTP_ERROR_MSG = "error_msg";
    private final String KEY_HTTP_INSTRUCTION = "instrunction";
    private final String KEY_HTTP_SERVICE_TEL = "service_tel";

    // 停车缴费
    //private final String KEY_HTTP_STATUS ="status";
    private final String KEY_HTTP_CARSTATUS = "carStatus";
    private final String KEY_HTTP_PARKINGINFO = "parkingInfo";
    private final String KEY_HTTP_INTIME = "inTime";
    private final String KEY_HTTP_PARKINGFEE = "parkingFee";
    private final String KEY_HTTP_CARLICENSE = "carLicense";

    //private final String KEY_HTTP_MSG ="msg";

    private static AccountManager mInstance;
    private EntityOfUser mUserInfo;
    //private EntityOfMemberCarInfo m
//    private ComAccountApi mAccountApi;
    private String mBid;
    private static Boolean mbHasMobile = false;

    private AccountManager() {
        mUserInfo = new EntityOfUser();
//        mAccountApi = ComAPIManager.getComAPIManager().getAccountApi();

    }

    public static AccountManager getInstance() {
        if (mInstance == null) {
            mInstance = new AccountManager();
        }
        return mInstance;
    }

    public EntityOfUser getUserInfo() {
        if (!isLogin()) {
            mUserInfo.clear();
        } else {
//            mUserInfo.mBDUSS = mAccountApi.getBDUSS();
//            mUserInfo.mDisplayName = mAccountApi.getDisplayName();
//            mUserInfo.mUid = mAccountApi.getUid();
        }
        return mUserInfo;
    }

    public boolean isLogin() {
        boolean bIsLogin = false;

//        bIsLogin = mAccountApi.isLogin();

        return bIsLogin;

    }
}
