package com.joysdk.android.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenDimenUtil {

    private int mScreenWidth;

    private static ScreenDimenUtil sInstance;

    private ScreenDimenUtil(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        display.getRealMetrics(dm);
    }

    public static ScreenDimenUtil getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ScreenDimenUtil.class) {
                if (sInstance == null) {
                    sInstance = new ScreenDimenUtil(context);
                }
            }
        }
        return sInstance;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }
}

