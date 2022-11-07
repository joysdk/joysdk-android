package com.joysdk.android.model;

import android.content.Context;

public class JoyAppInfo {
    public static Context ctx;
    public static String appKey;
    public static String token;
    public static boolean isHallHasPreloaded = false;
    public static boolean isOpenHall = false;
    public static boolean isNoInitHall = false;
    private static boolean isDebug = false;
    public static boolean isShowGameAble = false;

    private volatile static JoyAppInfo instance = null;

    private JoyAppInfo() {
    }

    public static JoyAppInfo getInstance() {
        if (instance == null) {   //先检查实例是否存在，如果不存在才进入下面的同步块
            synchronized (JoyAppInfo.class) {    //同步块，线程安全的创建实例
                if (instance == null) {   //再次检查实例是否存在，如果不存在才真正的创建实例
                    instance = new JoyAppInfo();
                }
            }
        }
        return instance;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        JoyAppInfo.ctx = ctx;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        JoyAppInfo.appKey = appKey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        JoyAppInfo.token = token;
    }

    public boolean getIsHallHasPreloaded() {
        return isHallHasPreloaded;
    }

    public void setIsHallHasPreloaded(boolean isHallHasPreloaded) {
        JoyAppInfo.isHallHasPreloaded = isHallHasPreloaded;
    }

    public boolean getIsOpenHall() {
        return isOpenHall;
    }

    public void setIsOpenHall(boolean isOpenHall) {
        JoyAppInfo.isOpenHall = isOpenHall;
    }

    public boolean getIsDebug() {
        return isDebug;
    }

    public void setIsDebug(boolean isDebug) {
        JoyAppInfo.isDebug = isDebug;
    }
}
