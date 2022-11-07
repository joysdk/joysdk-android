package com.joysdk.android.webview;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.webkit.JavascriptInterface;

import com.google.gson.reflect.TypeToken;
import com.joysdk.android.JoyCallBackListener;
import com.joysdk.android.JoySDK;
import com.joysdk.android.base.JoyGameEventCode;
import com.joysdk.android.model.JoyAppInfo;
import com.joysdk.android.util.LogUtil;
import com.joysdk.android.util.SpUtils;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class JoyJSInterface {
    public static final String JS_INTERFACE_NAME = "JSBridgeService";           //JS调用类名
    private Activity activity;
    private JoyWebLayout webLayout;

    public JoyJSInterface(JoyWebLayout webLayout, Activity activity) {
        this.webLayout = webLayout;
        this.activity = activity;
    }

    @JavascriptInterface
    public void newTppClose() {
        LogUtil.d("newTppClose");
        if (JoyAppInfo.isShowGameAble) {
            Message message = new Message();
            message.what = JoyGameEventCode.NEW_TPP_CLOSE;
            if (JoyCallBackListener.mOnJoyGameEventListener != null) {
                JoyCallBackListener.mOnJoyGameEventListener.sendMessage(message);
            }
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    JoySDK.getInstance().hideGameView();
                });
            }
        }
    }

    @JavascriptInterface
    public void recharge() {
        Message message = new Message();
        message.what = JoyGameEventCode.RECHARGE;
        if (JoyCallBackListener.mOnJoyGameEventListener != null) {
            JoyCallBackListener.mOnJoyGameEventListener.sendMessage(message);
        }
    }

    @JavascriptInterface
    public void OpenGameSucc(String json) {
        try {
            LogUtil.d("OpenGameSucc:" + json);
            //持久化打开过的game
            JSONObject jsonObject = new JSONObject(json);
            int gameId = jsonObject.getInt("gameId");
            Set<Integer> integers = SpUtils.getObject(activity, new TypeToken<Set<Integer>>() {
            }.getType());
            if (integers == null) {
                integers = new HashSet<>();
            }
            integers.add(gameId);
            SpUtils.putObject(activity, integers, new TypeToken<Set<Integer>>() {
            }.getType());
            if (gameId != 0 && activity != null) {
                JoyAppInfo.isHallHasPreloaded = false;
                if (JoyAppInfo.isOpenHall) {
                    //大厅模式打开游戏成功
                    activity.runOnUiThread(() -> {
                        try {
                            JoySDK.getInstance().setHeight(activity, gameId);
                            if (JoyAppInfo.isShowGameAble) {
                                JoySDK.getInstance().showGameView();
                            } else {
                                webLayout.loadUrl("javascript:window.HttpTool?.NativeToJs('ExitGame')");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    //列表模式打开游戏成功
                    activity.runOnUiThread(() -> {
                        try {
                            JoySDK.getInstance().showGameView();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } else {
                JoyAppInfo.isHallHasPreloaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void OpenGameBegin(String json) {
        try {
            LogUtil.d("OpenGameBegin:" + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void Log(String json) {
        try {
            LogUtil.d(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
