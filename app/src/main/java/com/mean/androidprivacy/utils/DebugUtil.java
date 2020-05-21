package com.mean.androidprivacy.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class DebugUtil {
    public static boolean isDebugMode(Context context) {
            return context.getApplicationInfo() != null
                    && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
