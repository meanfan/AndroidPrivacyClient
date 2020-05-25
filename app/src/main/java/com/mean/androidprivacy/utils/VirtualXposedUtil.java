package com.mean.androidprivacy.utils;

import android.content.Context;
import android.content.Intent;

public class VirtualXposedUtil {

    private static Intent getCMDIntent(String cmd){
        Intent intent = new Intent();
        intent.setAction("io.va.exposed.CMD");
        intent.putExtra("cmd",cmd);
        return intent;
    }

    private static Intent getCMDPKGIntent(String cmd,String pkg){
        Intent intent = getCMDIntent(cmd);
        intent.putExtra("pkg",pkg);
        return intent;
    }

    // 启动 VirtualXposed容器
    public static void boot(Context context){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("io.va.exposed");
        context.startActivity(intent);
    }

    // 重启 VirtualXposed容器
    public static void reboot(Context context){
        context.sendBroadcast(getCMDIntent("reboot"));
    }

    // 安装/更新应用至VirtualXposed容器
    public static void updateAPP(Context context,String packageName){
        context.sendBroadcast(getCMDPKGIntent("update",packageName));
    }

    // 启动VirtualXposed容器中的应用
    public static void launchAPP(Context context,String packageName){
        context.sendBroadcast(getCMDPKGIntent("launch",packageName));
    }
}
