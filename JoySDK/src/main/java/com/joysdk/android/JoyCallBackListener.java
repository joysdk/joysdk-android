package com.joysdk.android;

import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joysdk.android.base.JoyStateCode;
import com.joysdk.android.model.JoyGameInfoModel;

import java.util.List;

public class JoyCallBackListener {

    public static OnInitCompleteListener mOnInitCompleteListener;     //初始化

    public static OnJoyGameEventListener mOnJoyGameEventListener;

    public static abstract class OnInitCompleteListener extends JoyCallBackBaseListener {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == JoyStateCode.NETWORK_SUCCESS) {
                Gson gson = new Gson();
                List<JoyGameInfoModel> joyGameInfoModels = gson.fromJson((String) msg.obj, new TypeToken<List<JoyGameInfoModel>>() {
                }.getType());
                onComplete(msg.what, joyGameInfoModels);
            } else {
                onError(msg.what);
            }
        }

        public abstract void onComplete(int code, List<JoyGameInfoModel> joyGameInfoModels);

        public abstract void onError(int code);
    }

    public static abstract class OnJoyGameEventListener extends JoyCallBackBaseListener {
        @Override
        public void handleMessage(Message msg) {
            callback(msg.what, (String) msg.obj);
        }

        public abstract void callback(int code, String json);
    }
}
