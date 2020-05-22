package com.mean.androidprivacy.hook;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.mean.androidprivacy.bean.AppConfig;
import com.mean.androidprivacy.bean.SourceConfig;
import com.mean.androidprivacy.converter.SourceConfigConverter;
import com.mean.androidprivacy.ui.MainActivity;
import com.mean.androidprivacy.utils.ReflectUtil;

import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static String MODULE_PATH = null;
    public final static String modulePackageName = "com.mean.androidprivacy";
    private String packageName;
    private AppConfig appConfig;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        packageName = lpparam.packageName;
        /*
         * desc:   hook自身，用于检查是否已启用
         * class:  MainActivity
         * method: isModuleActive();
         * type:   MethodReplacement
         * ref:
         */

        if(packageName.equals(modulePackageName)){
            XposedHelpers.findAndHookMethod(MainActivity.class.getName(),
                                            lpparam.classLoader,
                                            "isModuleActive",
                                            XC_MethodReplacement.returnConstant(true));
            XposedBridge.log("self hooked~~~~~");
            return;
        }

        if(!packageName.equals("com.mean.locationleak")){
            return;
        }

        XposedBridge.log("load app:"+packageName);

        final Application[] currentApplication = new Application[1];
        final Activity[] currentActivity = new Activity[1];

        String AUTHORITY = "com.mean.androidprivacy.dbProvider";
        Uri appConfigUri = Uri.parse("content://" + AUTHORITY + "/appConfig");


        XposedHelpers.findAndHookMethod(Application.class,"onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String TAG = "Application:onCreate";
                Application mApplication =  (Application) param.thisObject;
                currentApplication[0] = mApplication;
                Cursor cursor = mApplication.getContentResolver().query(appConfigUri, null, packageName, null, null);
                if(cursor!=null && cursor.getCount()>0){
                    cursor.moveToNext();
                    appConfig = new AppConfig();
                    int isEnabled = cursor.getInt(cursor.getColumnIndex("IS_ENABLED"));
                    if(isEnabled == 0){
                        appConfig.setIsEnabled(false);
                    }else {
                        appConfig.setIsEnabled(true);
                    }
                    appConfig.setAppName(cursor.getString(cursor.getColumnIndex("APP_NAME")));
                    String sourceConfigsJson = cursor.getString(cursor.getColumnIndex("SOURCE_CONFIGS"));
                    appConfig.setSourceConfigs(new SourceConfigConverter().convertToEntityProperty(sourceConfigsJson));
                    XposedBridge.log("sourceConfigs:\n"+sourceConfigsJson);
                    Log.d(TAG, "sourceConfigs: \n"+sourceConfigsJson);


                    for(SourceConfig s:appConfig.getSourceConfigs()){
                        String className = s.getClassName();
                        Object returnObj = null;
                        int mode = s.getMode();
                        if(className.equals("byte")){
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0;
                                    break;
                                case 2:
                                    returnObj = Byte.parseByte(s.getModifyData());
                            }
                        }else if(className.equals("short")){
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0;
                                    break;
                                case 2:
                                    returnObj = Short.parseShort(s.getModifyData());
                            }
                        }else if(className.equals("int")){
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0;
                                    break;
                                case 2:
                                    returnObj = Integer.parseInt(s.getModifyData());
                            }
                        }else if(className.equals("long")){
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0;
                                    break;
                                case 2:
                                    returnObj = Long.parseLong(s.getModifyData());
                            }
                        }else if(className.equals("float")){
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0;
                                    break;
                                case 2:
                                    returnObj = Float.parseFloat(s.getModifyData());
                            }
                        }else if(className.equals("double")){
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0;
                                    break;
                                case 2:
                                    returnObj = Double.parseDouble(s.getModifyData());
                            }
                        }else if(className.equals("char")){
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0;
                                    break;
                                case 2:
                                    returnObj = s.getModifyData().charAt(0);
                            }
                        }else if(className.equals("boolean")){
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = false;
                                    break;
                                case 2:
                                    returnObj = (Integer.parseInt(s.getModifyData()) == 1);
                            }
                        }else if(className.equals("String")){
                            switch (mode) {
                                case 0:
                                    returnObj = null;
                                    break;
                                case 1:
                                    returnObj = "";
                                    break;
                                case 2:
                                    returnObj = s.getModifyData();
                            }
                        }else {
                            Class clazz = Class.forName(className);
                            switch (mode) {
                                case 0:
                                    returnObj = null;
                                    break;
                                case 1:
                                    returnObj = clazz.newInstance();
                                    break;
                                case 2:
                                    returnObj = JSON.parse(s.getModifyData());
                            }
                        }
                        XposedHelpers.findAndHookMethod(Class.forName(s.getClassName()),s.getFunctionName(),XC_MethodReplacement.returnConstant(returnObj));
                    }
                }

            }
        });
        XposedHelpers.findAndHookMethod(Activity.class, "onResume",new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity =  (Activity) param.thisObject;
                currentActivity[0] = activity;
            }
        });


    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        MODULE_PATH = startupParam.modulePath;
    }

}
