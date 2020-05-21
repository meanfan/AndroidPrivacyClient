package com.mean.androidprivacy.hook;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.mean.androidprivacy.receiver.RequestPermissionReceiver;

public class HookPermissionMethods extends HookMethods{
    public static final String PERMISSION_SPECIAL_SKIP_CONTROL = "com.mean.permission.special.SKIP_CONTROL";

    @Deprecated
    private static void sendBroadCast(Context context, String packageName, String permissionName) {
        Intent intent = new Intent();
        intent.setAction("com.mean.mypermissions.action.REQUEST");
        intent.setComponent(new ComponentName("com.mean.mypermissions",
                                              "com.mean.mypermissions.receiver.RequestPermissionReceiver"));
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra(RequestPermissionReceiver.INTENT_EXTRA_APP_NAME, packageName);
        intent.putExtra(RequestPermissionReceiver.INTENT_EXTRA_PERMISSION_NAME,permissionName);
        context.sendBroadcast(intent);
    }

    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) context;
            return findActivity(wrapper.getBaseContext());
        } else {
            return null;
        }
    }
}
