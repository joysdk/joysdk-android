package com.joysdk.android.webview;

import android.app.Activity;
import android.os.Message;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.google.gson.reflect.TypeToken;
import com.joysdk.android.JoyCallBackListener;
import com.joysdk.android.JoySDK;
import com.joysdk.android.base.JoyGameEventCode;
import com.joysdk.android.model.JoyAppInfo;
import com.joysdk.android.util.LogUtil;
import com.joysdk.android.util.SpUtils;
import com.joysdk.android.util.ThreadUtil;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class JoyJSInterface {
    public static final String JS_INTERFACE_NAME = "JSBridgeService";
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
            JoyAppInfo.isShowGameAble = false;
            ThreadUtil.runOnUiThread(() -> {
                JoySDK.getInstance().hideGameView();
            });
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
            //Persist opened games 持久化打开过的game
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
                ThreadUtil.runOnUiThread(() -> {
                    try {
                        JoySDK.getInstance().setHeight(activity, gameId);
                        if (JoyAppInfo.isShowGameAble) {
                            if (webLayout != null) {
                                webLayout.setVisibility(View.VISIBLE);
                            }
                            LogUtil.d("openGame --step4-- showGameByJs");
                        } else {
                            if (webLayout != null) {
                                webLayout.loadUrl("javascript:window.HttpTool.NativeToJs('ExitGame')");
                            }
                            LogUtil.d("Have been hidden");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
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
            if (JoyAppInfo.isOpenHall) {
                JoyAppInfo.isHallHasPreloaded = false;
                JoyAppInfo.isHasLoadingView = true;
                //添加悬浮窗
                JSONObject jsonObject = new JSONObject(json);
                int gameId = jsonObject.getInt("gameId");
                JoySDK.getInstance().initFloatingButton(activity, gameId);
            }
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
