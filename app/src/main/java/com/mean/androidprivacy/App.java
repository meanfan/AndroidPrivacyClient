package com.mean.androidprivacy;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.multidex.MultiDex;

import com.mean.androidprivacy.bean.AppConfig;
import com.mean.androidprivacy.bean.DaoMaster;
import com.mean.androidprivacy.bean.DaoSession;
import com.mean.androidprivacy.utils.AppInfoUtil;
import com.mean.androidprivacy.utils.GreenDaoContext;

import java.util.Map;

/**
 * @ClassName: App
 * @Description: 重写唯一的Application类
 * @Author: MeanFan
 * @Version: 1.0
 */
public class App extends Application {
    public static Map<String,AppConfig> appConfigs;
    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        initGreenDAO();
    }


    /**
    * @Author: MeanFan
    * @Description: 初始化GreenDAO数据库
    * @Param: []
    * @return: void
    **/
    private void initGreenDAO(){
        DaoMaster.DevOpenHelper helper= new DaoMaster.DevOpenHelper(new GreenDaoContext(this), "appConfigs.db", null );
        //DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "appConfigs-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    /**
    * @Author: MeanFan
    * @Description: 从系统初始化AppConfig信息
    * @Param: [context]
    * @return: void
    **/
    public static void initAppConfig(Context context){
        App.appConfigs = AppInfoUtil.getAllUserAppConfigs(context);
    }
}
