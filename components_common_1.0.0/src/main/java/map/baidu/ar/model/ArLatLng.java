package map.baidu.ar.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class ArLatLng implements Parcelable {
    public  double latitude;
    public  double longitude;
    public final double latitudeE6;
    public final double longitudeE6;

    public ArLatLng(double var1, double var3) {
        if (!Double.isNaN(var1) && !Double.isNaN(var3) && !Double.isInfinite(var1) && !Double.isInfinite(var3)) {
            double var5 = var1 * 1000000.0D;
            double var7 = var3 * 1000000.0D;
            this.latitudeE6 = var5;
            this.longitudeE6 = var7;
            this.latitude = var5 / 1000000.0D;
            this.longitude = var7 / 1000000.0D;
        } else {
            this.latitudeE6 = 0.0D;
            this.longitudeE6 = 0.0D;
            this.latitude = 0.0D;
            this.longitude = 0.0D;
        }
    }

    protected ArLatLng(Parcel var1) {
        this.latitude = var1.readDouble();
        this.longitude = var1.readDouble();
        this.latitudeE6 = var1.readDouble();
        this.longitudeE6 = var1.readDouble();
    }

    public static final Creator<ArLatLng> CREATOR = new Creator<ArLatLng>() {
        @Override
        public ArLatLng createFromParcel(Parcel in) {
            return new ArLatLng(in);
        }

        @Override
        public ArLatLng[] newArray(int size) {
            return new ArLatLng[size];
        }
    };

    public String toString() {
        String var1 = new String("latitude: ");
        var1 = var1 + this.latitude;
        var1 = var1 + ", longitude: ";
        var1 = var1 + this.longitude;
        return var1;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel var1, int var2) {
        var1.writeDouble(this.latitude);
        var1.writeDouble(this.longitude);
        var1.writeDouble(this.latitudeE6);
        var1.writeDouble(this.longitudeE6);
    }
}