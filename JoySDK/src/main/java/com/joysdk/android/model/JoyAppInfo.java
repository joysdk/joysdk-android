package com.joysdk.android.model;

import android.content.Context;

public class JoyAppInfo {
    public static Context ctx;
    public static String appKey;
    public static String token;
    public static boolean isHallHasPreloaded = false;   //Is it in the lobby? 是否在大厅
    public static boolean isOpenHall = false;           //Whether to open the hall 是否打开大厅
    public static boolean isNoInitHall = false;         //Whether it is uninitialized 是否没初始化
    private static boolean isDebug = false;             //Test or not suit 是否测试服
    public static boolean isShowGameAble = false;       //Whether you can show the game 是否可以展示游戏
    public static boolean isHasLoadingView = false;     //Whether there is a load animation 是否有加载动画
    public static boolean showFloatingButton = false;   //Whether to enable the suspension window function是否开启悬浮窗功能

    private volatile static JoyAppInfo instance = null;

    private JoyAppInfo() {
    }

    public static JoyAppInfo getInstance() {
        if (instance == null) {
            synchronized (JoyAppInfo.class) {
                if (instance == null) {
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

    public boolean getShowFloatingButton() {
        return showFloatingButton;
    }

    public void setShowFloatingButton(boolean showFloatingButton) {
        JoyAppInfo.showFloatingButton = showFloatingButton;
    }
}
