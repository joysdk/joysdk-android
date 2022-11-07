package com.joysdk.android.webview;

import android.content.Context;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.joysdk.android.base.Constants;

public class JoyWebViewSettings {
    private WebSettings mWebSettings;

    public static JoyWebViewSettings getInstance() {
        return new JoyWebViewSettings();
    }

    private JoyWebViewSettings() {
    }

    public void setSettings(WebView webView) {
        mWebSettings = webView.getSettings();

        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        mWebSettings.setTextZoom(100);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setSupportMultipleWindows(true);
        mWebSettings.setBlockNetworkImage(false);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebSettings.setSaveFormData(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        String cacheDir = getDiskCacheDir(webView.getContext());
        mWebSettings.setDatabasePath(cacheDir);
        mWebSettings.setAppCachePath(cacheDir);
        mWebSettings.setAppCacheMaxSize(1024 * 1024 * 20);

        String uaStr = mWebSettings.getUserAgentString();
        mWebSettings.setUserAgentString(String.format("%s yoli/%s", uaStr, Constants.VERSION_NAME));
        mWebSettings.setPluginState(WebSettings.PluginState.ON);
        mWebSettings.setDisplayZoomControls(false);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        try {
            webView.removeJavascriptInterface("searchBoxJavaBridge_");
            webView.removeJavascriptInterface("accessibility");
            webView.removeJavascriptInterface("accessibilityTraversal");
        } catch (Exception e) {
        }
        webView.enableSlowWholeDocumentDraw();
    }

    private static String getDiskCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

}