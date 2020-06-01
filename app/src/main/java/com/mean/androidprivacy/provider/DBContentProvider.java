package com.mean.androidprivacy.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mean.androidprivacy.App;
import com.mean.androidprivacy.bean.AppConfigDao;

/**
 * @ClassName: DBContentProvider
 * @Description: 配置文件内容提供者，仅支持读
 * @Author: MeanFan
 * @Version: 1.0
 */
public class DBContentProvider extends ContentProvider {
    //AUTHORITY为AndroidManifest.xml中配置的authorities
    private static final String AUTHORITY = "com.mean.androidprivacy.dbProvider";
    //URI匹配码
    private static final int MATCH_CODE = 123;
    private static UriMatcher uriMatcher;
    static {
        //匹配的默认情况（不成功）
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //此处需要匹配的uri
        uriMatcher.addURI(AUTHORITY,"appConfig", MATCH_CODE);
    }
    @Override
    public boolean onCreate() {
        return false;
    }

    /**
    * @Author: MeanFan
    * @Description: 重写查询方法
    * @Param: [uri, projection, selection, selectionArgs, sortOrder]
    * @return: android.database.Cursor
    **/
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);  //uri匹配
        if (match == MATCH_CODE){
            return App.getDaoSession().getAppConfigDao().queryBuilder()
                    .where(AppConfigDao.Properties.AppPackageName.eq(selection))
                    .buildCursor().query();  //返回Cursor指针
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
