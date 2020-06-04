package com.mean.androidprivacy.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.mean.androidprivacy.App;
import com.mean.androidprivacy.R;
import com.mean.androidprivacy.adapter.AppRVAdapter;
import com.mean.androidprivacy.utils.PreferenceUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

/**
 * @ClassName: MainActivity
 * @Description: 主页面Activity
 * @Author: MeanFan
 * @Version: 1.0
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final int REQUEST_CODE_CONFIG = 0;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    public static boolean isEnable = false;
    public static boolean isNorootMode = false;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs = getSharedPreferences(PreferenceUtils.MODULE_CONFIG_NAME, Context.MODE_PRIVATE);
        initRefreshLayout();
        swipeRefreshLayout.setRefreshing(true);
        forceRefresh();  // 异步刷新列表
        checkXposed();  // 检查Xposed状态
    }

    /**
    * @Author: MeanFan
    * @Description: 初始化列表
    * @Param: []
    * @return: void
    **/
    private void initRefreshLayout(){
        swipeRefreshLayout = findViewById(R.id.app_list_sfl);
        swipeRefreshLayout.setProgressViewOffset(true,50,200);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        forceRefresh();
                    }
                }
        );
    }

    /**
    * @Author: MeanFan
    * @Description: 异步刷新列表
    * @Param: []
    * @return: void
    **/
    private void forceRefresh(){
        new Thread(){
            @Override
            public void run() {
                App.initAppConfig(MainActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView = findViewById(R.id.app_list_rv);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerView.setAdapter(new AppRVAdapter(App.appConfigs));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }.start();

    }

    /**
    * @Author: MeanFan
    * @Description: 菜单栏初始化（启用按钮）
    * @Param: [menu]
    * @return: boolean
    **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 将menu中的项目添加到Action Bar
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // 获取启用按钮项目
        final MenuItem toggleService = menu.findItem(R.id.action_toggle);
        final Switch enableSwitch = (Switch) toggleService.getActionView();

        final MenuItem norootEnableItem = menu.findItem(R.id.action_noroot_enable);
        norootEnableItem.setChecked(prefs.getBoolean(PreferenceUtils.NOROOT_MODE,false));

        final MenuItem menuItem = menu.findItem(R.id.action_noroot_tip);
        menuItem.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(MainActivity.this,NorootGuideActivity.class));
            return false;
        });

        // 根据启用状态设置按钮状态
        if(!isModuleActive()&&!norootEnableItem.isChecked()){
            enableSwitch.setChecked(false);
        }else {
            enableSwitch.setChecked(prefs.getBoolean(PreferenceUtils.ENABLE,true));
        }
        isEnable = enableSwitch.isChecked();
        enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor mEditor  = prefs.edit();
            isEnable = isChecked;
            mEditor.putBoolean(PreferenceUtils.ENABLE, isChecked).apply();
        });
        return true;
    }

    /**
    * @Author: MeanFan
    * @Description: 检查Xposed是否正常
    * @Param: []
    * @return: void
    **/
    private void checkXposed(){
        if(isModuleActive()){
            Toast.makeText(this,"模块已启用",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Module Enabled");
        }else {
            Toast.makeText(this,"模块未启用",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Module Not Enabled");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_noroot_enable){
            isNorootMode = !item.isChecked();
            item.setChecked(isNorootMode);
            SharedPreferences.Editor mEditor  = prefs.edit();
            mEditor.putBoolean(PreferenceUtils.NOROOT_MODE, isNorootMode).apply();
            forceRefresh();
            return false;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
    * @Author: MeanFan
    * @Description: 默认返回false，若Xposed正常，则由HookEntry改为true
    * @Param: []
    * @return: boolean
    **/
    private boolean isModuleActive(){
        return false;
    }

    /**
    * @Author: MeanFan
    * @Description: 从配置页面返回后的列表状态更新
    * @Param: [requestCode, resultCode, data]
    * @return: void
    **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_CONFIG){
            if(resultCode == RESULT_OK){
                boolean isEnabled = data.getBooleanExtra("isEnabled",false);
                int pos = data.getIntExtra("appListPos",-1);
                if(pos>=0){
                    //App.appConfigs.get(pos).setEnabled(isEnabled);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }
    }
}
