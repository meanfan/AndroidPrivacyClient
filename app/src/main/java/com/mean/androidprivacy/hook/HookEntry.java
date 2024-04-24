package com.mean.androidprivacy.hook;

import android.app.Activity;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.mean.androidprivacy.bean.AppConfig;
import com.mean.androidprivacy.bean.SourceConfig;
import com.mean.androidprivacy.converter.SourceConfigConverter;
import com.mean.androidprivacy.ui.MainActivity;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @ClassName: HookEntry
 * @Description: Xposed Hook 入口类
 * @Author: MeanFan
 * @Version: 1.0
 */
public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static String MODULE_PATH = null;
    public final static String modulePackageName = "com.mean.androidprivacy";
    private String packageName;
    private AppConfig appConfig;

    /**
    * @Author: MeanFan
    * @Description: 加载每一个包（应用程序）时进行处理
    * @Param: [lpparam]
    * @return: void
    **/
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        packageName = lpparam.packageName;

        /*
         * desc:   hook自身，用于检查是否已启用
         * targetClass:  MainActivity
         * targetMethod: isModuleActive();
         * hookType:   MethodReplacement
         */
        if(packageName.equals(modulePackageName)){
            XposedHelpers.findAndHookMethod(MainActivity.class.getName(),
                                            lpparam.classLoader,
                                            "isModuleActive",
                                            XC_MethodReplacement.returnConstant(true));
            XposedBridge.log("self hooked~~~~~");
            return;
        }

        XposedBridge.log("load app:"+packageName);

        final Application[] currentApplication = new Application[1];
        final Activity[] currentActivity = new Activity[1];


        /*
         * desc:   在每个应用程序唯一的Application类实例onCreate时，从Url读入配置到该应用程序内存
         * targetClass:  Application
         * targetMethod: onCreate()
         * hookType:   XC_MethodHook
         */
        // 获取配置的URI
        String AUTHORITY = "com.mean.androidprivacy.dbProvider";
        Uri appConfigUri = Uri.parse("content://" + AUTHORITY + "/appConfig");
        XposedHelpers.findAndHookMethod(Application.class,"onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String TAG = "Application:onCreate";

                //获取当前应用程序的Application类
                Application mApplication =  (Application) param.thisObject;
                currentApplication[0] = mApplication;

                //获取当前应用程序的配置的Cursor用于读取配置
                Cursor cursor = mApplication.getContentResolver().query(appConfigUri, null, packageName, null, null);
                if(cursor!=null && cursor.getCount()>0){
                    cursor.moveToNext();
                    appConfig = new AppConfig();

                    // 获取启用状态
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

                    // 读取Source配置
                    for(SourceConfig s:appConfig.getSourceConfigs()){
                        if(!s.isEnable()){
                            continue;
                        }
                        XposedBridge.log("returnClassTypeName"+s.getReturnType());
                        // 获取返回类型
                        String returnClassTypeName = s.getReturnType();
                        // 根据返回类型和配置模式（空、默认值、自定义）生成返回实例
                        Object returnObj = null;
                        int mode = s.getMode();
                        if(returnClassTypeName.equals("byte")){  //byte类型返回值
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0;
                                    break;
                                case 2:
                                    returnObj = Byte.parseByte(s.getModifyData());
                            }
                        }else if(returnClassTypeName.equals("short")){  //short类型返回值
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0;
                                    break;
                                case 2:
                                    returnObj = Short.parseShort(s.getModifyData());
                            }
                        }else if(returnClassTypeName.equals("int")){   //int类型返回值
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0;
                                    break;
                                case 2:
                                    returnObj = Integer.parseInt(s.getModifyData());
                            }
                        }else if(returnClassTypeName.equals("long")){  //long类型返回值
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0;
                                    break;
                                case 2:
                                    returnObj = Long.parseLong(s.getModifyData());
                            }
                        }else if(returnClassTypeName.equals("float")){  //float类型返回值
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0.0f;
                                    break;
                                case 2:
                                    returnObj = Float.parseFloat(s.getModifyData());
                            }
                        }else if(returnClassTypeName.equals("double")){  //double类型返回值
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0.0d;
                                    break;
                                case 2:
                                    returnObj = Double.parseDouble(s.getModifyData());
                            }
                        }else if(returnClassTypeName.equals("char")){  //char类型返回值
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = 0;
                                    break;
                                case 2:
                                    //取自定义数据的第一个字符
                                    returnObj = s.getModifyData().charAt(0);
                            }
                        }else if(returnClassTypeName.equals("boolean")){  //boolean类型返回值
                            switch (mode) {
                                case 0:
                                case 1:
                                    returnObj = false;
                                    break;
                                case 2:
                                    // 自定义数据为1表示true，0表示false
                                    returnObj = (Integer.parseInt(s.getModifyData()) == 1);
                            }
                        }else if(returnClassTypeName.equals("String")
                                ||returnClassTypeName.equals("java.lang.String")){  //String类型返回值
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
                        }else {                                               //其它类类型返回值
                            Class clazz = Class.forName(returnClassTypeName);
                            switch (mode) {
                                case 0:
                                    returnObj = null;
                                    break;
                                case 1:
                                    try {
                                        returnObj = clazz.newInstance();   //调用该类无参构造方法生成实例
                                    }catch (Exception e){   // 若失败(InstantiationException和IllegalAccessException)则回退为空模式
                                        returnObj = null;
                                    }
                                    break;
                                case 2:
                                    returnObj = JSON.parse(s.getModifyData());  //由JSON生成类实例
                            }
                        }

                        /*
                         * desc:   在每个应用程序唯一的Application类实例onCreate时，从Url读入配置到该应用程序内存
                         * targetClass:  Source所在类的Class，即SourceConfig.className
                         * targetMethod: Source所在方法名,即SourceConfig.functionName
                         * hookType:   XC_MethodReplacement
                         */
                        // 获得Source所在类的Class
                        Class clazz = Class.forName(s.getClassName());
                        // 获得Source所在方法名
                        String functionName = s.getFunctionName().substring(0,s.getFunctionName().length()-2);
                        // 进行方法返回值替换
                        XposedHelpers.findAndHookMethod(clazz,functionName,XC_MethodReplacement.returnConstant(returnObj));
                        XposedBridge.log(clazz.getName()+" "+functionName +"(): return "+returnObj.toString());
                    }
                }
                cursor.close();

            }
        });

        /*
         * desc:   获取当前Activity实例
         * targetClass:  Activity
         * targetMethod: onResume()
         * hookType:   XC_MethodHook
         */
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
