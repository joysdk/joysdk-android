package com.joysdk.android;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.joysdk.android.base.Constants;

public abstract class JoyCallBackBaseListener {
    private Handler mHandler;
    private static final String TAG = Constants.JOY_TAG + "JoyCallBackBaseListener";

    public JoyCallBackBaseListener() {
        if (Looper.myLooper() != null) {
            mHandler = new Handler() {
                public void handleMessage(Message msg) {
                    JoyCallBackBaseListener.this.handleMessage(msg);
                }
            };
        }
    }

    public abstract void handleMessage(Message msg);

    public final void sendMessage(Message msg) {
        if (mHandler != null) {
            mHandler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }

    }

    public final void sendEmptyMessage(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        if (mHandler != null) {
            mHandler.sendEmptyMessage(what);
        } else {
            handleMessage(msg);
        }
    }
}

