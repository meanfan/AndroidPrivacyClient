package com.mean.androidprivacy.utils;

import android.content.Context;
import android.content.Intent;

/**
 * @ClassName: VirtualXposedUtil
 * @Description: VirtualXposed工具，用于控制内部程序
 * @Author: MeanFan
 * @Version: 1.0
 */
public class VirtualXposedUtil {

    /**
    * @Author: MeanFan
    * @Description: 获取cmd Intent
    * @Param: [cmd]
    * @return: android.content.Intent
    **/
    private static Intent getCMDIntent(String cmd){
        Intent intent = new Intent();
        intent.setAction("io.va.exposed.CMD");
        intent.putExtra("cmd",cmd);
        return intent;
    }


    /**
    * @Author: MeanFan
    * @Description: 获取包含包名参数的cmd Intent
    * @Param: [cmd, pkg]
    * @return: android.content.Intent
    **/
    private static Intent getCMDPKGIntent(String cmd,String pkg){
        Intent intent = getCMDIntent(cmd);
        intent.putExtra("pkg",pkg);
        return intent;
    }

    /**
    * @Author: MeanFan
    * @Description: 启动 VirtualXposed容器
    * @Param: [context]
    * @return: void
    **/
    public static void boot(Context context){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("io.va.exposed");
        context.startActivity(intent);
    }

    /**
    * @Author: MeanFan
    * @Description: 重启 VirtualXposed容器
    * @Param: [context]
    * @return: void
    **/
    public static void reboot(Context context){
        context.sendBroadcast(getCMDIntent("reboot"));
    }

    /**
    * @Author: MeanFan
    * @Description: 安装/更新应用至VirtualXposed容器
    * @Param: [context, packageName]
    * @return: void
    **/
    public static void updateAPP(Context context,String packageName){
        context.sendBroadcast(getCMDPKGIntent("update",packageName));
    }

    /**
    * @Author: MeanFan
    * @Description: 启动VirtualXposed容器中的应用
    * @Param: [context, packageName]
    * @return: void
    **/
    public static void launchAPP(Context context,String packageName){
        context.sendBroadcast(getCMDPKGIntent("launch",packageName));
    }
}
