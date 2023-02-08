package com.joysdk.android;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joysdk.android.base.JoyGameEventCode;
import com.joysdk.android.base.JoyStateCode;
import com.joysdk.android.floatingview.JoyFloatingLayout;
import com.joysdk.android.model.JoyAppInfo;
import com.joysdk.android.model.JoyGameHoleInfoModel;
import com.joysdk.android.model.JoyGameInfoModel;
import com.joysdk.android.model.JoyGameInitModel;
import com.joysdk.android.util.LogUtil;
import com.joysdk.android.util.ScreenDimenUtil;
import com.joysdk.android.util.SpUtils;
import com.joysdk.android.util.ThreadUtil;
import com.joysdk.android.webview.JoyJSInterface;
import com.joysdk.android.webview.JoyWebLayout;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JoySDK {

    private static final String BASE_UEL = "https://joysdk.com/game";
    private static final String DEBUG_UEL = "https://joysdk.com/game";
    private static final String INIT_SDK = "/game/initSDK";
    public static JoyWebLayout mWebLayout;

    private JoySDK() {

    }

    private static class JoySDKHolder {
        public static JoySDK sInstance = new JoySDK();
    }

    public static JoySDK getInstance() {
        return JoySDKHolder.sInstance;
    }

    private WeakReference<Activity> activity;
    private String token;
    private int gameId = 0;
    private String roomId;
    private String ext;
    private JoyFloatingLayout mFloatingView;
    private ViewGroup.LayoutParams params; //布局初始参数(位置，大小等)

    public int init(final Activity activity, final String appKey, JoyCallBackListener.OnInitCompleteListener onInitCompleteListener) {
        JoyAppInfo.getInstance().setCtx(activity);
        JoyAppInfo.getInstance().setAppKey(appKey);

        if (onInitCompleteListener != null) {
            JoyCallBackListener.mOnInitCompleteListener = onInitCompleteListener;
        }

        init(activity);

        return 1;
    }

    private void init(final Activity activity) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        String baseUrl;
        if (!JoyAppInfo.getInstance().getIsDebug()) {
            baseUrl = BASE_UEL;
        } else {
            baseUrl = DEBUG_UEL;
        }
        String url = baseUrl + INIT_SDK + "?appKey=" + JoyAppInfo.appKey;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                initResponse(s, activity);
            }
        }, new Response.ErrorListener() {
            // 请求失败时执行的函数
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RequestQueue requestQueue = Volley.newRequestQueue(activity);
                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                initResponse(s, activity);
                            }
                        }, new Response.ErrorListener() {
                            // 请求失败时执行的函数
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Message message = new Message();
                                if (volleyError instanceof NoConnectionError) {
                                    message.what = JoyStateCode.NETWORK_ERROR;
                                } else {
                                    message.what = JoyStateCode.SERVER_ERROR;
                                }
                                if (JoyCallBackListener.mOnInitCompleteListener != null) {
                                    JoyCallBackListener.mOnInitCompleteListener.sendMessage(message);
                                }
                            }
                        });
                        requestQueue.add(request);
                    }
                }, 3000);

            }
        });
        requestQueue.add(request);
    }

    private void initResponse(String s, final Activity activity) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            int status = jsonObject.getInt("status");
            Message message = Message.obtain();
            message.what = status;
            if (status == JoyStateCode.NETWORK_SUCCESS) {
                JSONObject data = jsonObject.getJSONObject("data");
                Gson gson = new Gson();
                JoyGameInitModel initModel = gson.fromJson((String) data.toString(), new TypeToken<JoyGameInitModel>() {
                }.getType());
                HashMap<Integer, JoyGameHoleInfoModel> gamesUrlMap = new HashMap<>();
                for (JoyGameHoleInfoModel game : initModel.getGameList()) {
                    gamesUrlMap.put(game.getGameId(), game);
                }
                initModel.setGamesUrlMap(gamesUrlMap);
                SpUtils.putObject(JoyAppInfo.ctx, initModel, new TypeToken<JoyGameInitModel>() {
                }.getType());
                message.obj = new Gson().toJson(initModel.getGameList());
                if (!JoyAppInfo.isNoInitHall) {
                    initHall(activity);
                }
            }

            if (JoyCallBackListener.mOnInitCompleteListener != null) {
                JoyCallBackListener.mOnInitCompleteListener.sendMessage(message);
            }


        } catch (Exception e) {
            Message message = Message.obtain();
            message.what = JoyStateCode.RES_MISS_ERROR;
            if (JoyCallBackListener.mOnInitCompleteListener != null) {
                JoyCallBackListener.mOnInitCompleteListener.sendMessage(message);
            }
            e.printStackTrace();
        }
    }

    private void initHall(final Activity activity) {
        String gameUrl = "";
        int height = 1;
        int width = 1;
        JoyGameInitModel initModel = SpUtils.getObject(activity, new TypeToken<JoyGameInitModel>() {
        }.getType());
        if (initModel != null) {
            gameUrl = initModel.getLobby();
            height = initModel.getHeight();
            width = initModel.getWidth();
            if (gameUrl != null && gameUrl.contains("?")) {
                gameUrl = gameUrl + "&appKey=" + JoyAppInfo.appKey + "&mini=1" + "&pkgName=" + SpUtils.getPkgName(activity);
            } else {
                gameUrl = gameUrl + "?appKey=" + JoyAppInfo.appKey + "&mini=1" + "&pkgName=" + SpUtils.getPkgName(activity);
            }
        }
        LogUtil.d(gameUrl);

        if (mWebLayout == null) {
            mWebLayout = new JoyWebLayout(activity);
        }
        mWebLayout.addJsInterface(activity, new JoyJSInterface(mWebLayout, activity));

        //重新设置宽高
        float screenWidth = ScreenDimenUtil.getInstance(activity).getScreenWidth();
        float viewHeight = screenWidth * height / width;
        mWebLayout.setWebHeightAndWidth(screenWidth, viewHeight);
        //靠底部添加
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        //重新加载链接
        mWebLayout.loadUrl(gameUrl);

        //移除WebView
        ViewGroup parent = (ViewGroup) mWebLayout.getParent();
        if (parent != null) {
            parent.removeView(mWebLayout);
        }
        //添加WebView到activity
        final ViewGroup view = (ViewGroup) activity.getWindow().getDecorView();
        final ViewGroup contentView = view.findViewById(android.R.id.content);
        contentView.addView(mWebLayout, params);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            try {
                if (mWebLayout != null) {
                    mWebLayout.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1);
    }

    public void openGame(final Activity activity, String token, int gameId, String roomId, String ext, JoyCallBackListener.OnJoyGameEventListener onJoyGameEventListener) {
        JoyAppInfo.isShowGameAble = true;
        this.activity = new WeakReference<>(activity);
        this.token = token;
        this.roomId = roomId;
        this.ext = ext;
        if (onJoyGameEventListener != null) {
            JoyCallBackListener.mOnJoyGameEventListener = onJoyGameEventListener;
        }
        LogUtil.d("openGame --step1-- start");
        if (TextUtils.isEmpty(JoyAppInfo.appKey)) {
            Message message = new Message();
            message.what = JoyGameEventCode.INIT_FAILED;
            if (JoyCallBackListener.mOnJoyGameEventListener != null) {
                JoyCallBackListener.mOnJoyGameEventListener.sendMessage(message);
            }
            LogUtil.d("openGame -- Code:" + JoyGameEventCode.INIT_FAILED);
            return;
        }
        initFloatingButton(activity, gameId);
        if (mWebLayout == null) {
            LogUtil.d("openGame -- mWebLayout is null");
            JoyAppInfo.isNoInitHall = true;
            init(activity, JoyAppInfo.appKey, new JoyCallBackListener.OnInitCompleteListener() {
                @Override
                public void onComplete(int code, List<JoyGameInfoModel> joyGameInfoModels) {
                    addGameToAct(activity, token, gameId, roomId, ext);
                }

                @Override
                public void onError(int code) {

                }
            });
        } else {
            addGameToAct(activity, token, gameId, roomId, ext);
        }
    }

    private void addGameToAct(final Activity activity, String token, int gameId, String roomId, String ext) {
        JoyAppInfo.isOpenHall = false;
        JoyGameInitModel initModel = SpUtils.getObject(activity, new TypeToken<JoyGameInitModel>() {
        }.getType());
        if (initModel != null) {
            String lobbyUrl = initModel.getLobby();
            Map<Integer, JoyGameHoleInfoModel> gamesUrlMap = initModel.getGamesUrlMap();
            JoyGameHoleInfoModel joyGameHoleInfoModel = gamesUrlMap.get(gameId);
            if (joyGameHoleInfoModel != null) {
                int height = joyGameHoleInfoModel.getHeight();
                int width = joyGameHoleInfoModel.getWidth();
                if (mWebLayout == null) {
                    mWebLayout = new JoyWebLayout(activity);
                }
                //添加Js接口
                mWebLayout.addJsInterface(activity, new JoyJSInterface(mWebLayout, activity));
                //重新设置宽高
                float viewWidth = ScreenDimenUtil.getInstance(activity).getScreenWidth();
                float viewHeight = viewWidth * height / width;
                mWebLayout.setWebHeightAndWidth(viewWidth, viewHeight);

                //靠底部添加
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
                //移除WebView
                ViewGroup parent = (ViewGroup) mWebLayout.getParent();
                if (parent != null) {
                    parent.removeView(mWebLayout);
                }
                //添加WebView到activity
                final ViewGroup view = (ViewGroup) activity.getWindow().getDecorView();
                final ViewGroup contentView = view.findViewById(android.R.id.content);
                contentView.addView(mWebLayout, params);
                mWebLayout.moveToViewLocation();
                LogUtil.d("openGame --step2-- startAnimation");
                //判断是否自己的游戏和判断是否缓存过和判断是否在大厅
                Set<Integer> integers = SpUtils.getObject(activity, new TypeToken<Set<Integer>>() {
                }.getType());
                if (JoyAppInfo.isHallHasPreloaded && integers != null && integers.contains(gameId) && TextUtils.isEmpty(joyGameHoleInfoModel.getGameUrl())) {
                    JoyAppInfo.isHasLoadingView = false;
                    openGameByIdToJs(token, gameId, roomId, ext);
                } else {
                    //重新加载链接
                    JoyAppInfo.isHallHasPreloaded = false;
                    JoyAppInfo.isHasLoadingView = true;
                    if (joyGameHoleInfoModel.getIsCPGame() != 0) {
                        //第三方提供的游戏
                        lobbyUrl = joyGameHoleInfoModel.getGameUrl();
                    } else {
                        if (lobbyUrl != null && lobbyUrl.contains("?")) {
                            lobbyUrl = lobbyUrl + "&appKey=" + JoyAppInfo.appKey + "&token=" + token + "&gameId=" + gameId + "&mini=1" + "&pkgName=" + SpUtils.getPkgName(activity);
                        } else {
                            lobbyUrl = lobbyUrl + "?appKey=" + JoyAppInfo.appKey + "&token=" + token + "&gameId=" + gameId + "&mini=1" + "&pkgName=" + SpUtils.getPkgName(activity);
                        }
                        if (!TextUtils.isEmpty(roomId)) {
                            lobbyUrl = lobbyUrl + "&roomId=" + roomId;
                        }
                        if (!TextUtils.isEmpty(ext)) {
                            lobbyUrl = lobbyUrl + "&ext=" + ext;
                        }
                    }
                    LogUtil.d("openGame --step3-- Link: " + lobbyUrl);
                    mWebLayout.loadUrl(lobbyUrl);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        try {
                            if (mWebLayout != null) {
                                mWebLayout.setVisibility(View.VISIBLE);
                            }
                            LogUtil.d("openGame --step4-- showGameByLink");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 300);
                }
            } else {
                LogUtil.d("openGame -- gameModel is null");
            }
        } else {
            LogUtil.d("openGame -- initModel is null");
        }
    }

    public void openHall(final Activity activity, String token, String roomId, String ext, JoyCallBackListener.OnJoyGameEventListener onJoyGameEventListener) {
        JoyAppInfo.isShowGameAble = true;
        this.activity = new WeakReference<>(activity);
        this.token = token;
        this.roomId = roomId;
        this.ext = ext;
        if (onJoyGameEventListener != null) {
            JoyCallBackListener.mOnJoyGameEventListener = onJoyGameEventListener;
        }
        if (TextUtils.isEmpty(JoyAppInfo.appKey)) {
            Message message = new Message();
            message.what = JoyGameEventCode.INIT_FAILED;
            if (JoyCallBackListener.mOnJoyGameEventListener != null) {
                JoyCallBackListener.mOnJoyGameEventListener.sendMessage(message);
            }
            return;
        }
        hideFloatingButton();
        if (mWebLayout == null) {
            JoyAppInfo.isNoInitHall = true;
            init(activity, JoyAppInfo.appKey, new JoyCallBackListener.OnInitCompleteListener() {
                @Override
                public void onComplete(int code, List<JoyGameInfoModel> joyGameInfoModels) {
                    addHallToAct(activity, token, roomId, ext);
                }

                @Override
                public void onError(int code) {

                }
            });
        } else {
            addHallToAct(activity, token, roomId, ext);
        }
    }

    private void addHallToAct(final Activity activity, String token, String roomId, String ext) {
        JoyAppInfo.isOpenHall = true;
        JoyGameInitModel initModel = SpUtils.getObject(activity, new TypeToken<JoyGameInitModel>() {
        }.getType());
        if (initModel != null) {
            String gameUrl = initModel.getLobby();
            int height = initModel.getHeight();
            int width = initModel.getWidth();
            if (mWebLayout == null) {
                mWebLayout = new JoyWebLayout(activity);
            }
            //添加Js接口
            mWebLayout.addJsInterface(activity, new JoyJSInterface(mWebLayout, activity));
            //重新设置宽高
            float viewWidth = ScreenDimenUtil.getInstance(activity).getScreenWidth();
            float viewHeight = viewWidth * height / width;
            mWebLayout.setWebHeightAndWidth(viewWidth, viewHeight);
            //靠底部添加
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            //移除WebView
            ViewGroup parent = (ViewGroup) mWebLayout.getParent();
            if (parent != null) {
                parent.removeView(mWebLayout);
            }
            //添加WebView到activity
            final ViewGroup view = (ViewGroup) activity.getWindow().getDecorView();
            final ViewGroup contentView = view.findViewById(android.R.id.content);
            contentView.addView(mWebLayout, params);
            mWebLayout.moveToViewLocation();

            int gameId = 0;
            Set<Integer> integers = SpUtils.getObject(activity, new TypeToken<Set<Integer>>() {
            }.getType());
            if (JoyAppInfo.isHallHasPreloaded && integers != null && integers.contains(gameId)) {
                JoyAppInfo.isHasLoadingView = false;
                openGameByIdToJs(token, gameId, roomId, ext);
            } else {
                //重新加载链接
                JoyAppInfo.isHasLoadingView = true;
                if (gameUrl != null && gameUrl.contains("?")) {
                    gameUrl = gameUrl + "&appKey=" + JoyAppInfo.appKey + "&token=" + token + "&mini=1" + "&pkgName=" + SpUtils.getPkgName(activity);
                } else {
                    gameUrl = gameUrl + "?appKey=" + JoyAppInfo.appKey + "&token=" + token + "&mini=1" + "&pkgName=" + SpUtils.getPkgName(activity);
                }
                if (!TextUtils.isEmpty(roomId)) {
                    gameUrl = gameUrl + "&roomId=" + roomId;
                }
                if (!TextUtils.isEmpty(ext)) {
                    gameUrl = gameUrl + "&ext=" + ext;
                }
                LogUtil.d(gameUrl);
                mWebLayout.loadUrl(gameUrl);
            }
            mWebLayout.setVisibility(View.VISIBLE);
        }
    }


    public void refreshGameBalance() {
        try {
            if (mWebLayout != null) {
                mWebLayout.loadUrl("javascript:window.HttpTool.NativeToJs('recharge')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void destroy() {
        if (mWebLayout != null) {
            ViewGroup parent = (ViewGroup) mWebLayout.getParent();
            if (parent != null) {
                parent.removeView(mWebLayout);
            }
            mWebLayout.destroy();
            mWebLayout = null;
        }
        JoyAppInfo.isHallHasPreloaded = false;
    }

    private void openGameByIdToJs(String token, int gameId, String roomId, String ext) {
        try {
            LogUtil.d("openGame --step3-- Js: gameId: " + gameId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", token);
            jsonObject.put("gameId", String.valueOf(gameId));
            jsonObject.put("roomId", roomId);
            jsonObject.put("ext", ext);
            if (mWebLayout != null) {
                String url = "javascript:window.HttpTool.NativeToJs('openGame','" + jsonObject.toString() + "')";
                mWebLayout.loadUrl(url);
                LogUtil.d("openGame --step3-- Js: " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHeight(final Activity activity, int gameId) {
        int height = 1;
        int width = 1;
        JoyGameInitModel initModel = SpUtils.getObject(activity, new TypeToken<JoyGameInitModel>() {
        }.getType());
        if (initModel != null) {
            Map<Integer, JoyGameHoleInfoModel> gamesUrlMap = initModel.getGamesUrlMap();
            JoyGameHoleInfoModel joyGameHoleInfoModel = gamesUrlMap.get(gameId);
            if (joyGameHoleInfoModel != null) {
                height = joyGameHoleInfoModel.getHeight();
                width = joyGameHoleInfoModel.getWidth();
            }
        }

        //重新设置宽高
        float viewWidth = ScreenDimenUtil.getInstance(activity).getScreenWidth();//屏幕宽度
        float viewHeight = viewWidth * height / width;//游戏高度
        int layoutHeight = mWebLayout.getHeight();//当前整个布局的高度
        int webViewHeight = mWebLayout.getWebHeight();//当前webview的高度
        mWebLayout.requestLayout();
        mWebLayout.setWebHeightAndWidth(viewWidth, viewHeight);
        if (!JoyAppInfo.isHasLoadingView) {
            mWebLayout.moveToViewLocation();
        } else {
            //设置高度做动画
            mWebLayout.moveToViewLocation(layoutHeight, webViewHeight, viewHeight);
        }
    }

    public void hideGameView() {
        if (JoyAppInfo.isShowGameAble) {
            JoyAppInfo.isShowGameAble = false;
            if (mWebLayout != null) {
                mWebLayout.loadUrl("javascript:window.HttpTool.NativeToJs('ExitGame')");
            }
        }
        if (mWebLayout != null) {
            mWebLayout.setVisibility(View.GONE);
        }
        showFloatingButton();
    }

    public void setFloatingButtonFrame(ViewGroup.LayoutParams params) {
        this.params = params;
        if (!JoyAppInfo.getInstance().getShowFloatingButton()) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mFloatingView != null) {
                mFloatingView.setLayoutParams(null == this.params ? defaultParams() : this.params);
                mFloatingView.requestLayout();
            }
        });
    }

    public JoySDK setDebug(boolean debug) {
        JoyAppInfo.getInstance().setIsDebug(debug);
        return JoySDKHolder.sInstance;
    }

    public void initFloatingButton(Activity activity, int gameId) {
        this.gameId = gameId;
        if (!JoyAppInfo.getInstance().getShowFloatingButton()) {
            return;
        }
        if (mFloatingView == null) {
            mFloatingView = new JoyFloatingLayout(activity);
            mFloatingView.setLayoutParams(null == params ? defaultParams() : params);
        }

        JoyGameInitModel initModel = SpUtils.getObject(activity, new TypeToken<JoyGameInitModel>() {
        }.getType());
        if (initModel != null) {
            Map<Integer, JoyGameHoleInfoModel> gamesUrlMap = initModel.getGamesUrlMap();
            JoyGameHoleInfoModel joyGameHoleInfoModel = gamesUrlMap.get(gameId);
            if (joyGameHoleInfoModel != null) {
                mFloatingView.updateIconUrl(joyGameHoleInfoModel.getIconUrl());
            }
        }

        mFloatingView.setMagnetViewListener(event -> {
            //悬浮窗打开游戏
            openGame(this.activity.get(), token, this.gameId, roomId, ext, JoyCallBackListener.mOnJoyGameEventListener);
        });
        attach(this.activity.get());
    }

    private void hideFloatingButton() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mFloatingView != null) {
                mFloatingView.setVisibility(View.GONE);
            }
        });
    }

    private void showFloatingButton() {
        if (!JoyAppInfo.getInstance().getShowFloatingButton()) {
            return;
        }
        ThreadUtil.runOnUiThread(() -> {
            if (mFloatingView != null) {
                mFloatingView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 将view绑定到activity的布局中
     */
    private void attach(Activity activity) {
        attach(getActivityRoot(activity));
    }

    /**
     * 将view绑定到布局中
     */
    private void attach(FrameLayout container) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mFloatingView != null) {
                ViewGroup parent = (ViewGroup) mFloatingView.getParent();
                if (parent != null) {
                    parent.removeView(mFloatingView);
                }
                if (container != null) {
                    container.addView(mFloatingView);
                }
                mFloatingView.setVisibility(View.GONE);
            }
        });
    }

    private FrameLayout.LayoutParams defaultParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(165, 165);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 800);
        return params;
    }

    private FrameLayout getActivityRoot(Activity activity) {
        if (activity == null) {
            return null;
        }
        try {
            return (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JoySDK setShowFloatingButton(boolean showFloatingButton) {
        JoyAppInfo.getInstance().setShowFloatingButton(showFloatingButton);
        return JoySDKHolder.sInstance;
    }

    public JoySDK setIsLog(boolean isLog) {
        LogUtil.setDeBug(isLog);
        return JoySDKHolder.sInstance;
    }
}
