package map.baidu.ar.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtils {

    private static final int REQUIRED_SIZE = 128;

    public static String getStringInputSteam(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line = "";

        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();

    }

    public static byte[] getBytesInputSteam(InputStream is) {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        int len = 0;

        try {
            while ((len = is.read(buffer)) != -1) {

                outStream.write(buffer, 0, len);
            }

            is.close();
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outStream.toByteArray();
    }

    public static Bitmap decodeFile(File f) {
        try {
            //BitmapFactory.Options o = new BitmapFactory.Options();
            //o.inJustDecodeBounds = true;
            //BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            //int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            /*while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}*/

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            return bitmap;
        } catch (FileNotFoundException e) {
            Log.e("", e.getMessage(), e);
        }
        return null;
    }
}