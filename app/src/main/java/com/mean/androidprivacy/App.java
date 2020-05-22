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

import java.io.File;
import java.util.Map;

public class App extends Application {
    public static Map<String,AppConfig> appConfigs;
    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        initFile();
        initGreenDAO();
    }


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

    public static void initAppConfig(Context context){
        App.appConfigs = AppInfoUtil.getAllUserAppConfigs(context);
    }

    public static AppConfig getAppConfig(Context context,String appPackageName){
        if(appConfigs == null){
            return null;
        }else {
            return appConfigs.get(appPackageName);
        }
    }

    private void initFile(){
        AppInfoUtil.copyAssetFile(this, "contacts2_fake.db",
                                  getExternalFilesDir(null)+ File.separator+"fakedb", "contacts2_fake.db");
    }

}
