package com.mean.androidprivacy;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mean.androidprivacy.bean.AppConfig;
import com.mean.androidprivacy.bean.AppConfigDao;
import com.mean.androidprivacy.utils.AppConfigDBUtil;

public class DBContentProvider extends ContentProvider {
    //这里的AUTHORITY就是我们在AndroidManifest.xml中配置的authorities
    private static final String AUTHORITY = "com.mean.androidprivacy.dbProvider";

    //匹配成功后的匹配码
    private static final int MATCH_CODE = 100;

    private static UriMatcher uriMatcher;


    static {
        //匹配不成功返回NO_MATCH(-1)
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //添加我们需要匹配的uri
        uriMatcher.addURI(AUTHORITY,"appConfig", MATCH_CODE);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);
        if (match == MATCH_CODE){
            return App.getDaoSession().getAppConfigDao().queryBuilder()
                    .where(AppConfigDao.Properties.AppPackageName.eq(selection))
                    .buildCursor().query();
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
