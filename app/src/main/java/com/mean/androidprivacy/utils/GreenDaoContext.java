package com.mean.androidprivacy.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;


/**
 * @ClassName: GreenDaoContext
 * @Description: GreenDAO使用的Context，用于设置数据库文件存储位置
 * @Author: MeanFan
 * @Version: 1.0
 */
public class GreenDaoContext extends ContextWrapper {
    private String dbFolderName = "database";
    private Context mContext;

    public GreenDaoContext(Context context) {
        super(context);
        this.mContext = context;
    }

    /**
    * @Author: MeanFan
    * @Description: 重写获取数据库路径的方法
    * @Param: [dbName]
    * @return: java.io.File
    **/
    @Override
    public File getDatabasePath(String dbName) {
        File file = mContext.getFilesDir();
        StringBuffer buffer = new StringBuffer();
        buffer.append(file.getPath());
        buffer.append(File.separator);
        buffer.append(dbFolderName);
        File dbFolder = new File(buffer.toString());
        if (!dbFolder.exists()){
            dbFolder.mkdirs();
        }
        buffer.append(File.separator);
        buffer.append(dbName);
        String dbDir = buffer.toString(); // 数据库路径
        File dbFile = new File(dbDir);

        // 判断文件是否存在，不存在则创建该文件
        if (!dbFile.exists()) {
            try{
                dbFile.createNewFile(); // 创建文件
                return dbFile;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }else {
            return dbFile;
        }
    }

    /**
    * @Author: MeanFan
    * @Description: 重写打开数据库的方法
    * @Param: [name, mode, factory]
    * @return: android.database.sqlite.SQLiteDatabase
    **/
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
        return result;
    }

    /**
    * @Author: MeanFan
    * @Description: 重写打开数据库的方法
    * @Date: 12:31 2020/6/1 0001
    * @Param: [name, mode, factory, errorHandler]
    * @return: android.database.sqlite.SQLiteDatabase
    **/
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
        return result;
    }
}
