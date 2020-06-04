package com.mean.androidprivacy.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.mean.androidprivacy.bean.AppConfig;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AppInfoUtil
 * @Description: 从系统获取应用程序信息的工具类
 * @Author: MeanFan
 * @Version: 1.0
 */
public class AppInfoUtil {
    public static final String TAG = "AppInfoUtil";

    /**
    * @Author: MeanFan
    * @Description: 从系统获取所有应用程序信息
    * @Param: [context]
    * @return: java.util.List<android.content.pm.PackageInfo>
    **/
    private static List<PackageInfo> getAllAppInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        return packages;
    }

    /**
    * @Author: MeanFan
    * @Description: 获取应用程序图标
    * @Param: [context, appPackageName]
    * @return: android.graphics.drawable.Drawable
    **/
    public static Drawable getAppIcon(Context context, String appPackageName) {
        PackageManager pm = context.getPackageManager();
        try {
            Drawable drawable = pm.getApplicationIcon(appPackageName);
            return drawable;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
    * @Author: MeanFan
    * @Description: 获取应用程序名称
    * @Param: [context, info]
    * @return: java.lang.String
    **/
    public static String getAppName(Context context, ApplicationInfo info) {
        PackageManager pm = context.getPackageManager();
         return pm.getApplicationLabel(info).toString();
    }

    /**
    * @Author: MeanFan
    * @Description: 获取应用程序名称
    * @Param: [context, pkg]
    * @return: java.lang.String
    **/
    public static String getAppName(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(pkg,0);
            return getAppName(context, applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
    * @Author: MeanFan
    * @Description: 根据系统中的应用程序信息，生成所有用户应用程序对应的AppConfig类
    * @Param: [context]
    * @return: java.util.Map<java.lang.String,com.mean.androidprivacy.bean.AppConfig>
    **/
    public static Map<String,AppConfig> getAllUserAppConfigs(Context context) {
        List<PackageInfo> packageInfos = getAllAppInfo(context);
        Map<String,AppConfig> appConfigMap = new Hashtable<>();
        for(PackageInfo packageInfo:packageInfos){
            // 排除系统应用
            boolean isSysApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
            boolean isSysUpd = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
            boolean isSystemApp = isSysApp || isSysUpd;
            if(isSystemApp){
                continue;
            }
            // 生成appConfig
            AppConfig appConfig = new AppConfig();
            if(initAppConfigByPackageInfo(context,appConfig,packageInfo)) {
                AppConfig appConfigFromDB = AppConfigDBUtil.query(appConfig.getAppPackageName());
                if(appConfigFromDB != null){
                    appConfig.setIsEnabled(appConfigFromDB.getIsEnabled());
                    //TODO 未考虑App由于更新导致的变化
                    appConfig.setDataFlowResults(appConfigFromDB.getDataFlowResults());
                }
                appConfigMap.put(appConfig.getAppPackageName(),appConfig);
            }else {
                Log.e(TAG, "setAppConfigByPackageInfo failure" );
            }
        }
        return appConfigMap;
    }

    /**
    * @Author: MeanFan
    * @Description: 根据PackageInfo初始化AppConfig
    * @Param: [context, appConfig, packageInfo]
    * @return: boolean
    **/
    public static boolean initAppConfigByPackageInfo(Context context,AppConfig appConfig, PackageInfo packageInfo){
        if(appConfig == null || packageInfo == null){
            return false;
        }
        appConfig.setAppName(getAppName(context,packageInfo.applicationInfo));
        appConfig.setAppPackageName(packageInfo.packageName);
        return true;
    }

    /**
    * @Author: MeanFan
    * @Description: 根据包名获取apk文件
    * @Param: [context, packageName]
    * @return: java.io.File
    **/
    public static File getApkFile(Context context, String packageName){
       String apkDir = getApkDir(context,packageName);
        return new File(apkDir);
    }

    /**
    * @Author: MeanFan
    * @Description: 根据包名获取apk文件路径
    * @Param: [context, packageName]
    * @return: java.lang.String
    **/
    public static String getApkDir(Context context, String packageName) {
        String appDir = null;
        try {
            // 通过包名获取程序源文件路径
            appDir = context.getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appDir;
    }

    /**
    * @Author: MeanFan
    * @Description: 检查应用是否已安装
    * @Param: [appPackageName]
    * @return: boolean
    **/
    public static boolean checkAPPInstalled(Context context,String appPackageName){
        try {
            context.getPackageManager().getApplicationInfo(appPackageName, 0);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }
}
