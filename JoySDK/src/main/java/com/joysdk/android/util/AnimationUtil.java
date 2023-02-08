package com.joysdk.android.util;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public class AnimationUtil {

    /**
     * Move from the bottom of the control to the location of the control
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
        //Start coordinates minus end coordinates divided by subsequent game height
        //layoutHeight, the height of the entire layout; webViewHeight, original webView height; viewHeight, the height after the change.
        float radio = ((layoutHeight - webViewHeight) - (layoutHeight - viewHeight)) / viewHeight;
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                radio * 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(600);
        animationSet.addAnimation(mHiddenAction);
        return animationSet;
    }
}
