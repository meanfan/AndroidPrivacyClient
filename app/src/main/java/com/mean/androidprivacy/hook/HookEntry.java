package com.mean.androidprivacy.hook;

import android.app.Activity;
import android.content.Intent;

import com.mean.androidprivacy.MainActivity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static String MODULE_PATH = null;
    public final static String modulePackageName = "com.mean.mypermissions";
    public static final int INTENT_REQUEST_CODE_PERMISSION_REQUEST = 2342;
    public static final int INTENT_REQUEST_CODE_PERMISSION_CHECK = 2343;
    public static final int INTENT_REQUEST_CODE_FAKE_CONTACT = 2344;

    public static final Set<String> permissionWhiteSet = new HashSet<>();
    protected static Activity currentActivity = null;
    private Hashtable<String,Integer> runtimePermissionStatus = new Hashtable<>();
    public String fakeContactName;
    public String fakeContactPhone;

    private static final String[] permissionWhiteList= new String[]{
            "android.permission.INTERACT_ACROSS_USERS",
            "android.permission.INTERACT_ACROSS_USERS_FULL",
    };

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        /*
         * desc:   hook自身，用于检查是否已启用
         * class:  MainActivity
         * method: isModuleActive();
         * type:   MethodReplacement
         * ref:
         */

        if(lpparam.packageName.equals(modulePackageName)){
            XposedHelpers.findAndHookMethod(MainActivity.class.getName(),
                                            lpparam.classLoader,
                                            "isModuleActive",
                                            XC_MethodReplacement.returnConstant(true));
            XposedBridge.log("self hooked~~~~~");
            return;
        }

        if(!lpparam.packageName.equals("com.mean.permissionexample")){
            return;
        }

        XposedBridge.log("load app:"+lpparam.packageName);

        permissionWhiteSet.addAll(Arrays.asList(permissionWhiteList));
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        MODULE_PATH = startupParam.modulePath;
    }

    public void updateRuntimePermissionStatus(String permissionName, int mode){
        if(permissionName!=null){
            runtimePermissionStatus.put(permissionName,mode);
        }
    }
    public int getRuntimePermissionStatus(String permissionName){
        if(permissionName!=null){
            Integer mode = runtimePermissionStatus.get(permissionName);
            if(mode!=null){
                return mode;
            }
        }
        return -1;
    }

    public boolean isPermissionOnWhiteList(String permission){
        XposedBridge.log("isPermissionOnWhiteList: arg permission: " + permission);
        if(permission!=null){

            return permissionWhiteSet.contains(permission);
        }else {
            XposedBridge.log("ERROR: isPermissionOnWhiteList: arg permission is null");
            return false;
        }

    }
}
