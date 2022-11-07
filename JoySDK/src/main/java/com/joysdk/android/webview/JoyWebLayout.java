package com.joysdk.android.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.joysdk.android.JoyCallBackListener;
import com.joysdk.android.JoySDK;
import com.joysdk.android.base.JoyGameEventCode;
import com.joysdk.android.base.ResourceExchange;

public class JoyWebLayout extends LinearLayout {
    private WebView mWebViewBase;
    private FrameLayout mFrameLayoutTop;

    public JoyWebLayout(Activity activity) {
        super(activity);
        init(activity, null, 0);
    }

    public JoyWebLayout(Activity activity, @Nullable AttributeSet attrs) {
        super(activity, attrs);
        init(activity, attrs, 0);
    }

    public JoyWebLayout(Activity activity, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(activity, attrs, defStyleAttr);
        init(activity, attrs, defStyleAttr);
    }

    private void init(Activity activity, @Nullable AttributeSet attrs, int defStyleAttr) {
        final ResourceExchange mRes = ResourceExchange.getInstance(activity);
        inflate(getContext(), mRes.getLayoutId("joy_layout_base_web"), this);
        mWebViewBase = findViewById(mRes.getIdId("joy_id_wv_base"));
        mFrameLayoutTop = findViewById(mRes.getIdId("joy_id_fl_top"));
        mFrameLayoutTop.setOnClickListener(v -> {
            try {
                Message message = new Message();
                message.what = JoyGameEventCode.NEW_TPP_CLOSE;
                if (JoyCallBackListener.mOnJoyGameEventListener != null) {
                    JoyCallBackListener.mOnJoyGameEventListener.sendMessage(message);
                }
                JoySDK.getInstance().hideGameView();
                if (mWebViewBase != null) {
                    mWebViewBase.loadUrl("javascript:window.HttpTool?.NativeToJs('ExitGame')");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        mWebViewBase.setBackgroundColor(0);
        mWebViewBase.requestFocus();
        mWebViewBase.setVerticalScrollBarEnabled(true);
        //设置WebViewSettings
        JoyWebViewSettings.getInstance().setSettings(mWebViewBase);
    }

    public void setWebHeightAndWidth(float viewWidth, float viewHeight) {
        ViewGroup.LayoutParams lp;
        lp = mWebViewBase.getLayoutParams();
        lp.width = (int) viewWidth;
        lp.height = (int) viewHeight;
        mWebViewBase.setLayoutParams(lp);
    }

    public int getWebHeight() {
        return mWebViewBase.getHeight();
    }

    public void loadUrl(String url) {
        mWebViewBase.loadUrl(url);
    }

    @SuppressLint("JavascriptInterface")
    public void addJsInterface(Activity activity, Object object) {
        //设置WebViewClient
        mWebViewBase.setWebViewClient(new JoyWebViewClient(activity));
        mWebViewBase.addJavascriptInterface(object, JoyJSInterface.JS_INTERFACE_NAME);
    }

    public void destroy() {
        mWebViewBase.destroy();
    }
}
