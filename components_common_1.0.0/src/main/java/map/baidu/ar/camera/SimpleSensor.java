package map.baidu.ar.camera;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by xingdaming on 15/12/22.
 */
public class SimpleSensor implements SensorEventListener {

    private boolean[] values;

    public interface OnHoldPositionListener {

        void onOrientationWithRemap(float[] remapValue);

    }

    private SensorManager mSensorManager;
    private Context mContext;
    private OnHoldPositionListener mListener;

    public SimpleSensor(Context context, OnHoldPositionListener listener) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mListener = listener;
    }

    public void startSensor() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_UI);
    }

    public void stopSensor() {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));

    }

    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];
//    private float[] values = new float[3];
//    private float[] r = new float[9];
//    private float[] geomagnetic = new float[3];//用来保存地磁传感器的值

    @Override
    public void onSensorChanged(SensorEvent event) {

                if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                    float inOrientationMatrix[] = new float[16];
                    float outOrientationMatrix[] = new float[16];
                    SensorManager.getRotationMatrixFromVector(inOrientationMatrix, event.values);
                    float orientationRemap[] = new float[3];
                    SensorManager.remapCoordinateSystem(inOrientationMatrix, SensorManager.AXIS_X, SensorManager
         .AXIS_MINUS_Z,
                            outOrientationMatrix);
                    SensorManager.getOrientation(outOrientationMatrix, orientationRemap);
                    if (mListener != null) {
                        mListener.onOrientationWithRemap(orientationRemap);
                    }
                }
//        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            geomagnetic = event.values;
//        }
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            // r从这里返回
//            SensorManager.getRotationMatrix(r, null, event.values, geomagnetic);
//            //values从这里返回
//            SensorManager.getOrientation(r, values);
//            if (mListener != null) {
//                mListener.onOrientationWithRemap(values);
//            }
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void dispose() {
        mListener = null;
    }
}
