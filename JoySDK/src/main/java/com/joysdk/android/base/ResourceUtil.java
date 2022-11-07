package com.joysdk.android.base;

import android.content.Context;

public class ResourceUtil {

    public static int getLayoutId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "layout",
                paramContext.getPackageName());
    }

    public static int getId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "id",
                paramContext.getPackageName());
    }

}
