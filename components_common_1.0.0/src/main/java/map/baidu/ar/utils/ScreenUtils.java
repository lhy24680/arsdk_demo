package map.baidu.ar.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class ScreenUtils {
    private static Display defaultDisplay;
    private static float mDensity = 0.0F;
    private static float mScaledDensity = 0.0F;
    private static int mScreenHeight = 0;

    public ScreenUtils() {
    }

    public static float getDensity(Context context) {
        if(mDensity == 0.0F) {
            mDensity = context.getResources().getDisplayMetrics().density;
        }

        return mDensity;
    }

    public static float getScaledDensity(Context context) {
        if(mScaledDensity == 0.0F) {
            mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        }

        return mScaledDensity;
    }

    public static int dip2px(float dip, Context context) {
        return (int)(0.5F + getDensity(context) * dip);
    }


    public static int px2dip(int px, Context context) {
        return (int)(0.5F + (float)px / getDensity(context));
    }

    public static int px2sp(float pxValue, Context context) {
        return (int)(pxValue / getScaledDensity(context) + 0.5F);
    }

    public static int sp2px(float spValue, Context context) {
        return (int)(spValue * getScaledDensity(context) + 0.5F);
    }

    public static int getScreenWidth(Context context) {
        return context != null?context.getResources().getDisplayMetrics().widthPixels:0;
    }

    public static int getScreenHeight(Context context) {
        return context != null?context.getResources().getDisplayMetrics().heightPixels:0;
    }

    public static void setViewScreenHeight(int height) {
        if(mScreenHeight >= 0 && mScreenHeight - height < mScreenHeight / 4) {
            mScreenHeight = height;
        }

    }

    public static int getViewScreenHeight(Context context) {
        return context == null?0:(mScreenHeight > 0?mScreenHeight:getScreenHeight(context) - getStatusBarHeight(context));
    }

    @SuppressLint("WrongConstant")
    public static Display getDefaultDisplay(Context context) {
        if(defaultDisplay == null) {
            defaultDisplay = ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
        }

        return defaultDisplay;
    }

    public static int getHeight(Context context) {
        return getDefaultDisplay(context).getHeight();
    }

    public static int getWidth(Context context) {
        return getDefaultDisplay(context).getWidth();
    }

    public static int percentHeight(float percent, Context context) {
        return (int)(percent * (float)getHeight(context));
    }

    public static int percentWidth(float percent, Context context) {
        return (int)(percent * (float)getWidth(context));
    }

    public static int getStatusBarHeight(Context context) {
        if(context instanceof Activity) {
            Rect rectangle = new Rect();
            Window window = ((Activity)context).getWindow();
            if(window != null && window.getDecorView() != null) {
                window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
            }

            return rectangle.top;
        } else {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int STATUS_BAR_HEIGHT = (int)Math.ceil((double)(25.0F * metrics.density));
            return STATUS_BAR_HEIGHT;
        }
    }
}