package com.mean.androidprivacy.hook;

import android.app.Activity;

import com.mean.androidprivacy.ui.MainActivity;
import com.mean.androidprivacy.utils.DebugUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static String MODULE_PATH = null;
    public final static String modulePackageName = "com.mean.androidprivacy";
    protected static Activity currentActivity = null;

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

        if(!lpparam.packageName.equals("com.mean.locationleak")){
            return;
        }

        XposedBridge.log("load app:"+lpparam.packageName);
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        MODULE_PATH = startupParam.modulePath;
    }

}
