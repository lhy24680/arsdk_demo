
package map.baidu.ar.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ArPoiInfo implements Parcelable {
    public String name;
    public String uid;
    public String address;
    public String city;
    public String phoneNum;
    public String postCode;
    public ArPoiInfo.POITYPE type;
    public ArLatLng location;
    public boolean hasCaterDetails;
    public boolean isPano;

    public ArPoiInfo() {
    }

    protected ArPoiInfo(Parcel var1) {
        this.name = var1.readString();
        this.uid = var1.readString();
        this.address = var1.readString();
        this.city = var1.readString();
        this.phoneNum = var1.readString();
        this.postCode = var1.readString();
        this.type = (ArPoiInfo.POITYPE) var1
                .readValue(ArPoiInfo.POITYPE.class.getClassLoader());
        this.location = (ArLatLng) var1.readParcelable(ArLatLng.class.getClassLoader());
        this.hasCaterDetails = (Boolean) var1.readValue(Boolean.class.getClassLoader());
        this.isPano = (Boolean) var1.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<ArPoiInfo> CREATOR = new Creator<ArPoiInfo>() {
        @Override
        public ArPoiInfo createFromParcel(Parcel in) {
            return new ArPoiInfo(in);
        }

        @Override
        public ArPoiInfo[] newArray(int size) {
            return new ArPoiInfo[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel var1, int var2) {
        var1.writeString(this.name);
        var1.writeString(this.uid);
        var1.writeString(this.address);
        var1.writeString(this.city);
        var1.writeString(this.phoneNum);
        var1.writeString(this.postCode);
        var1.writeValue(this.type);
        var1.writeParcelable(this.location, 1);
        var1.writeValue(this.hasCaterDetails);
        var1.writeValue(this.isPano);
    }

    public static enum POITYPE {
        POINT(0),
        BUS_STATION(1),
        BUS_LINE(2),
        SUBWAY_STATION(3),
        SUBWAY_LINE(4);

        private int a;

        private POITYPE(int var3) {
        }

        public int getInt() {
            return this.a;
        }

        public static POITYPE fromInt(int var0) {
            switch (var0) {
                case 0:
                    return POINT;
                case 1:
                    return BUS_STATION;
                case 2:
                    return BUS_LINE;
                case 3:
                    return SUBWAY_STATION;
                case 4:
                    return SUBWAY_LINE;
                default:
                    return null;
            }
        }
    }
}

