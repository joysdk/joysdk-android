package com.joysdk.android.floatingview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.joysdk.android.util.ScreenDimenUtil;

public class JoyFloatingLayout extends FloatingMagnetView {

    ImageView iv_icon;
    TextView btn_close;

    public JoyFloatingLayout(Context context) {
        super(context);
        int margin2 = ScreenDimenUtil.getInstance(context).dp2px(5);
        int size2 = ScreenDimenUtil.getInstance(context).dp2px(15);
        iv_icon = new ImageView(context);
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params1.setMargins(params1.leftMargin, margin2, margin2, params1.bottomMargin);
        iv_icon.setLayoutParams(params1);
        addView(iv_icon);

        btn_close = new TextView(context);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(size2, size2);
        params2.gravity = Gravity.TOP | Gravity.END;
        btn_close.setLayoutParams(params2);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#80000000"));
        gd.setCornerRadius(size2);
        btn_close.setBackground(gd);
        btn_close.setText("X");
        btn_close.setTextSize(11);
        btn_close.setGravity(Gravity.CENTER);
        btn_close.setTextColor(Color.parseColor("#FFFFFF"));
        btn_close.setOnClickListener(v -> {
            setVisibility(View.GONE);
        });
        addView(btn_close);
    }


    public void updateIconUrl(String iconUrl) {
        try {
            if (!TextUtils.isEmpty(iconUrl) && getContext() != null) {
                Volley.newRequestQueue(getContext()).add(new ImageRequest(iconUrl, response -> {
                    try {
                        if (iv_icon != null) {
                            iv_icon.setImageBitmap(response);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {
                }));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
