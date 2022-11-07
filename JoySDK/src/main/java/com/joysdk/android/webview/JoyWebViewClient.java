package com.joysdk.android.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Arrays;
import java.util.List;


public class JoyWebViewClient extends WebViewClient {
    private final Activity context;
    private final List<String> HTTP_SCHEMES = Arrays.asList("http", "https");

    public JoyWebViewClient(Activity activity) {
        this.context = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (shouldOverrideInnerUrl(view, url)) {
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request != null && request.getUrl() != null ? request.getUrl().toString() : "";
        if (shouldOverrideInnerUrl(view, url)) {
            return true;
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    private boolean shouldOverrideInnerUrl(WebView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                if ("intent".equals(uri.getScheme())) {
                    try {
                        Intent intent = Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            PackageManager pm = context.getPackageManager();
                            ResolveInfo ri = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            if (ri != null) {
                                context.startActivity(Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME));
                                return true;
                            } else {
                                String backUrl = intent.getStringExtra("browser_fallback_url");
                                if (!TextUtils.isEmpty(backUrl)) {
                                    if (backUrl.startsWith("market://"))
                                        jumpAppMarketWithUrl(context, backUrl);
                                    else view.loadUrl(backUrl);
                                    return true;
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                if (!HTTP_SCHEMES.contains(uri.getScheme())) {
                    jumpUrl(context, url);
                    return true;
                }
            }
        }
        return false;
    }

    public static void jumpUrl(Context context, String url) {
        if (context != null && !TextUtils.isEmpty(url)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
            }
        }
    }

    public static boolean hasActivity(Context context, Intent intent, String packageName) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfos) {
            if (ri.activityInfo.packageName.equals(packageName)) return true;
        }
        return false;
    }

    public static void jumpAppMarketWithUrl(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (hasActivity(context, intent, "com.android.vending")) {
                intent.setPackage("com.android.vending");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                jumpUrl(context, url);
            } catch (Exception e1) {
            }
        }
    }
}

