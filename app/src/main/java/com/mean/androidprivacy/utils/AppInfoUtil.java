package com.mean.androidprivacy.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.mean.androidprivacy.bean.AppConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class AppInfoUtil {
    public static final String TAG = "AppUtil";

    private static List<PackageInfo> getAllAppInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        return packages;
    }

    public static Drawable getAppIcon(Context context, String appPackageName) {
        PackageManager pm = context.getPackageManager();
        try {
            Drawable drawable = pm.getApplicationIcon(appPackageName);
            return drawable;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String getAppName(Context context, ApplicationInfo info) {
        PackageManager pm = context.getPackageManager();
         return pm.getApplicationLabel(info).toString();
    }

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



    public static Map<String,AppConfig> getAllUserAppConfigs(Context context) {
        List<PackageInfo> packageInfos = getAllAppInfo(context);
        Map<String,AppConfig> appConfigMap = new Hashtable<>();
        for(PackageInfo packageInfo:packageInfos){
            // 跳过系统应用
            boolean isSysApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
            boolean isSysUpd = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
            boolean isSystemApp = isSysApp || isSysUpd;
            if(isSystemApp){
                continue;
            }
            if(DebugUtil.isDebugMode(context)){
                if(!packageInfo.packageName.equals("com.mean.locationleak")){
                    continue;
                }
            }
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
            if(DebugUtil.isDebugMode(context)){
                if(packageInfo.packageName.equals("com.mean.locationleak")){
                    break;
                }
            }
        }
        return appConfigMap;
    }

    public static boolean initAppConfigByPackageInfo(Context context,AppConfig appConfig, PackageInfo packageInfo){
        if(appConfig == null || packageInfo == null){
            return false;
        }
        appConfig.setAppName(getAppName(context,packageInfo.applicationInfo));
        appConfig.setAppPackageName(packageInfo.packageName);
        return  true;
    }

    public static void copyAssetFile(Context context, String assetsFileName,
                            String savePath, String saveName) {
        String filename = savePath + "/" + saveName;
        File dir = new File(savePath);
        // 如果目录不中存在，创建这个目录
        if (!dir.exists())
            dir.mkdir();
        try {
            if (!(new File(filename)).exists()) {
                InputStream is = context.getResources().getAssets()
                        .open(assetsFileName);
                FileOutputStream fos = new FileOutputStream(filename);
                byte[] buffer = new byte[7168];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static File getApkFile(Context context, String packageName){
       String apkDir = getApkDir(context,packageName);
        return new File(apkDir);
    }

    public static String getApkDir(Context context, String packageName) {
        String appDir = null;
        try {
            //通过包名获取程序源文件路径
            appDir = context.getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appDir;
    }
}
