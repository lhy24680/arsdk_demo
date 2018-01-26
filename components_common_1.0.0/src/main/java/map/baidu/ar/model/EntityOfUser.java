package map.baidu.ar.model;

/**
 * Created by xingdaming on 15/9/2.
 */
public class EntityOfUser {

    public String mUid;
    public String mTelephone;
    public String mBDUSS;
    public String mDisplayName;
    public String mInstruction;
    public String mServiceTel;
    public String mWanDaMemberId;
    public String mCarPlateNumber;
    public int mCarStatus;//1已入场2未入场未预约3未入场已预约

    public EntityOfUser() {
        mUid = "";
        mTelephone = "";
        mBDUSS = "";
        mDisplayName = "";
        mInstruction = "";
        mServiceTel = "";
        mWanDaMemberId = "";
        mCarPlateNumber = "";
        mCarStatus = 0;

    }

    public void clear() {
        mUid = "";
        mTelephone = "";
        mBDUSS = "";
        mDisplayName = "";
        mInstruction = "";
        mServiceTel = "";
        mWanDaMemberId = "";
        mCarPlateNumber = "";
        mCarStatus = 0;
    }
}

