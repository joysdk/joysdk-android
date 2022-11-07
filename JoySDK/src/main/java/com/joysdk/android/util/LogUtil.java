package com.joysdk.android.util;

import android.util.Log;

import com.google.gson.Gson;

public class LogUtil {

    public static boolean sDeBug = false;

    public static void d(Object object) {
        if (sDeBug) {
            if (object instanceof String) {
                Log.d("JoyLog", (String) object);
            } else {
                Log.d("JoyLog", new Gson().toJson(object));
            }
        }
    }

    public static void setDeBug(boolean deBug) {
        sDeBug = deBug;
    }
}
