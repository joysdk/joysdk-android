package com.joysdk.android.util;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public class AnimationUtil {

    /**
     * 从控件的底部移动到控件所在位置
     *
     * @return
     */
    public static AnimationSet moveToViewLocation() {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(600);
        animationSet.addAnimation(mHiddenAction);
        return animationSet;
    }

    public static AnimationSet moveToViewLocation(float layoutHeight, float webViewHeight, float viewHeight) {
        AnimationSet animationSet = new AnimationSet(true);
        //起始坐标减去终点坐标占后来的游戏高度的多少
        //layoutHeight 整个布局高度; webViewHeight 原webView高度; viewHeight 变化后的高度.
        float radio = ((layoutHeight - webViewHeight) - (layoutHeight - viewHeight)) / viewHeight;
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                radio * 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(600);
        animationSet.addAnimation(mHiddenAction);
        return animationSet;
    }
}
