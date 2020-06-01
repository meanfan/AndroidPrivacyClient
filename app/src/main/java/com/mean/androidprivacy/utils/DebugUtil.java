package com.mean.androidprivacy.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * @ClassName: DebugUtil
 * @Description: 调试工具类
 * @Author: MeanFan
 * @Version: 1.0
 */
public class DebugUtil {
    /**
    * @Author: MeanFan
    * @Description: 判断是否处于调试模式
    * @Param: [context]
    * @return: boolean
    **/
    public static boolean isDebugMode(Context context) {
            return context.getApplicationInfo() != null
                    && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
