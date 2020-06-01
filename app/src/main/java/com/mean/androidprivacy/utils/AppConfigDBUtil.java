package com.mean.androidprivacy.utils;

import com.mean.androidprivacy.App;
import com.mean.androidprivacy.bean.AppConfig;
import com.mean.androidprivacy.bean.DaoSession;

import java.util.List;

/**
 * @ClassName: AppConfigDBUtil
 * @Description: 应用程序配置数据库操作的工具类
 * @Author: MeanFan
 * @Version: 1.0
 */
public class AppConfigDBUtil {
    /**
    * @Author: MeanFan
    * @Description: 插入操作
    * @Param: [appConfig]
    * @return: void
    **/
    public static void insert(AppConfig appConfig){
        DaoSession daoSession = App.getDaoSession();
        daoSession.insertOrReplace(appConfig);
    }

    /**
    * @Author: MeanFan
    * @Description: 删除操作
    * @Param: [appConfig]
    * @return: void
    **/
    public static void delete(AppConfig appConfig){
        DaoSession daoSession = App.getDaoSession();
        daoSession.delete(appConfig);
    }

    /**
    * @Author: MeanFan
    * @Description: 更新操作
    * @Param: [appConfig]
    * @return: void
    **/
    public static void update(AppConfig appConfig){
        DaoSession daoSession = App.getDaoSession();
        daoSession.update(appConfig);
    }

    /**
    * @Author: MeanFan
    * @Description: 查询操作
    * @Param: [appPackageName]
    * @return: com.mean.androidprivacy.bean.AppConfig
    **/
    public static AppConfig query(String appPackageName) {
        DaoSession daoSession = App.getDaoSession();
        List<AppConfig> appConfigs = daoSession.queryRaw(AppConfig.class, " where APP_PACKAGE_NAME = ?", appPackageName);
        if(appConfigs!=null && appConfigs.size()>0) {
            return appConfigs.get(0);
        }else{
            return null;
        }
    }
}
