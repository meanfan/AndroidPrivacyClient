package com.mean.androidprivacy.hook;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.mean.androidprivacy.App;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.os.Build.VERSION.SDK_INT;

/**
 * @ClassName: HookEntry
 * @Description: 开发调试时使用的免重启 Xposed Hook入口类
 * @Author: MeanFan
 * @Version: 1.0
 */
public class HookLoader implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    //按照实际使用情况修改下面几项的值

    // 实际hook逻辑处理类
    private final String hookLogicClassName = HookEntry.class.getName();

    // 当前Xposed模块的包名,方便寻找apk文件
    private final static String modulePackageName = App.class.getPackage().getName();

    // 实际hook逻辑处理类的入口方法
    private final String handleLoadPackageMethod = "handleLoadPackage";

    // 实际hook初始化方法
    private final String initZygoteMethod = "initZygote";

    // 保存的实际hook初始化方法参数
    private IXposedHookZygoteInit.StartupParam initZygoteStartupParam;


    /**
    * @Author: MeanFan
    * @Description: 重定向handleLoadPackage函数前会执行initZygote
    * @Param: [loadPackageParam]
    * @return: void
    **/
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        // 排除系统应用
        if (loadPackageParam.appInfo == null ||
                (loadPackageParam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) == 1) {
            return;
        }

        //将loadPackageParam的classloader替换为宿主程序Application的classloader,解决宿主程序存在多个.dex文件时,有时候ClassNotFound的问题
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];
                loadPackageParam.classLoader = context.getClassLoader();
                Class<?> hookLogicClass = getApkClass(context, modulePackageName, HookLoader.this.hookLogicClassName);
                Object instance = hookLogicClass.newInstance();
                try {
                    hookLogicClass.getDeclaredMethod(initZygoteMethod, initZygoteStartupParam.getClass()).invoke(instance, initZygoteStartupParam);
                } catch (NoSuchMethodException e) {
                    // 未实现initZygote方法，跳过
                }
                hookLogicClass.getDeclaredMethod(handleLoadPackageMethod, loadPackageParam.getClass()).invoke(instance, loadPackageParam);
            }
        });
    }


    /**
    * @Author: MeanFan
    * @Description: 实现initZygote，保存启动参数。
    * @Param: [startupParam]
    * @return: void
    **/
    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        this.initZygoteStartupParam = startupParam;
    }

    private Class<?> getApkClass(Context context, String packageName, String className) throws Throwable {
        File apkFile = findApkFile(context, packageName);
        if (apkFile == null) {
            throw new RuntimeException("寻找模块apk失败");
        }

        //加载指定的hook逻辑处理类，并调用它的handleHook方法， 使用XposedBridge.BOOTCLASSLOADER防止找不到de.robv.android.xposed包
        PathClassLoader pathClassLoader = new PathClassLoader(apkFile.getAbsolutePath(), XposedBridge.BOOTCLASSLOADER);

        Class<?> clazz;
        if (SDK_INT >= Build.VERSION_CODES.P) {
            //利用getDeclaredMethod方法调用Class.forName方法绕过安卓P及以上的反射限制
            try {
                Method forName = Class.class.getDeclaredMethod("forName", String.class, boolean.class, ClassLoader.class);
                clazz = (Class<?>) forName.invoke(null, className, true, pathClassLoader);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } else {
            clazz = Class.forName(className, true, pathClassLoader);
        }
        return clazz;
    }


    /**
    * @Author: MeanFan
    * @Description: 根据包名构建目标Context,并调用getPackageCodePath()来定位apk
    * @Param: [context, modulePackageName]
    * @return: java.io.File
    **/
    private File findApkFile(Context context, String modulePackageName) {
        if (context == null) {
            return null;
        }
        try {
            Context moudleContext = context.createPackageContext(modulePackageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            String apkPath = moudleContext.getPackageCodePath();
            return new File(apkPath);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}