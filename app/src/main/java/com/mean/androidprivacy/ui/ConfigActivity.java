package com.mean.androidprivacy.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mean.androidprivacy.App;
import com.mean.androidprivacy.R;
import com.mean.androidprivacy.adapter.MethodRVAdapter;
import com.mean.androidprivacy.bean.AppConfig;
import com.mean.androidprivacy.bean.DataFlowResults;
import com.mean.androidprivacy.bean.Result;
import com.mean.androidprivacy.bean.Source;
import com.mean.androidprivacy.bean.SourceConfig;
import com.mean.androidprivacy.converter.DataFlowResultsConverter;
import com.mean.androidprivacy.server.RemoteServer;
import com.mean.androidprivacy.utils.AppConfigDBUtil;
import com.mean.androidprivacy.utils.AppInfoUtil;
import com.mean.androidprivacy.utils.Md5CalcUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigActivity extends AppCompatActivity implements AnalyzeResultCallBack{
    public static final String TAG = "ConfigActivity";

    private ImageView iv_icon;
    private TextView tv_name;
    private Switch sw_enable;
    private RecyclerView rv_permission_list;
    private TextView tv_none_hint,tv_wait_hint;
    private FloatingActionButton fab_restore;
    AlertDialog dialog;

    private String appPackageName;

    private AppConfig appConfig;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == MethodRVAdapter.REQUEST_CODE_METHOD_CONFIG){
            if(resultCode == RESULT_CANCELED && data!=null){
                int methodPosInSourceConfigs = data.getIntExtra("methodPosInSourceConfigs",-1);

                if(methodPosInSourceConfigs>=0 &&  methodPosInSourceConfigs<appConfig.getSourceConfigs().size()){
                    appConfig.getSourceConfigs().get(methodPosInSourceConfigs).setEnable(false);
                    ((MethodRVAdapter)rv_permission_list.getAdapter()).updateData(appConfig);
                }
            }else if(resultCode == RESULT_OK && data!=null){
                int mode = data.getIntExtra("mode",-1);
                String modifyString = data.getStringExtra("modifyString");
                int methodPosInSourceConfigs = data.getIntExtra("methodPosInSourceConfigs",-1);

                if(methodPosInSourceConfigs>=0 &&  methodPosInSourceConfigs<appConfig.getSourceConfigs().size()){
                    SourceConfig sourceConfig = appConfig.getSourceConfigs().get(methodPosInSourceConfigs);
                    sourceConfig.setEnable(true);
                    sourceConfig.setMode(mode);
                    sourceConfig.setModifyData(modifyString);
                    AppConfigDBUtil.insert(appConfig);
                }
            }
        }else if(requestCode == MethodRVAdapter.REQUEST_CODE_METHOD_CONFIG && data!=null){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        iv_icon = findViewById(R.id.iv_icon);
        tv_name = findViewById(R.id.tv_name);
        sw_enable = findViewById(R.id.sw_enable);
        rv_permission_list = findViewById(R.id.rv_permission_list);
        tv_none_hint = findViewById(R.id.tv_none_hint);
        tv_wait_hint = findViewById(R.id.tv_wait_hint);
        fab_restore = findViewById(R.id.fab_restore);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        appPackageName = (String) getIntent().getSerializableExtra("appPackageName");
        AppConfig appConfigFromDB = AppConfigDBUtil.query(appPackageName);
        if(appConfigFromDB!=null){
            appConfig = appConfigFromDB;
            Log.d(TAG, "onCreate: load appConfig from db success");
        }else {
            Log.d(TAG, "onCreate: not found appConfig from db");
            appConfig = new AppConfig();
            appConfig.init(appPackageName, AppInfoUtil.getAppName(this, appPackageName), false);
        }

        // 设置UI
        Drawable icon = AppInfoUtil.getAppIcon(this, appConfig.getAppPackageName());
        if(icon != null) {
            iv_icon.setImageDrawable(icon);
        }
        tv_name.setText(appConfig.getAppName());
        sw_enable.setChecked(appConfig.getIsEnabled());
        sw_enable.setText(appConfig.getIsEnabled()?"已启用":"未启用");
        sw_enable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonView.setText(isChecked?"已启用":"未启用");

            appConfig.setIsEnabled(isChecked);
            Intent intent = new Intent();
            int appListPos = getIntent().getIntExtra("appListPos",-1);
            intent.putExtra("appListPos",appListPos);
            intent.putExtra("isEnabled",isChecked);
            setResult(RESULT_OK,intent);
            AppConfigDBUtil.insert(appConfig);
        });

        try{
            if(appConfig.getDataFlowResults().getImplicitResults().size() == 0){
                rv_permission_list.setVisibility(View.GONE);
                tv_none_hint.setVisibility(View.VISIBLE);
            }
        }catch (NullPointerException e){
            rv_permission_list.setVisibility(View.GONE);
            tv_none_hint.setVisibility(View.VISIBLE);
        }
        rv_permission_list.setLayoutManager(new LinearLayoutManager(ConfigActivity.this));
        rv_permission_list.setAdapter(new MethodRVAdapter(appConfig));
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);
        fab_restore.setOnClickListener(v -> {
            builder.setTitle("提示")
                    .setMessage("确定从服务器更新该应用所有配置？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        rv_permission_list.setVisibility(View.GONE);
                        tv_none_hint.setVisibility(View.GONE);
                        tv_wait_hint.setVisibility(View.VISIBLE);
                        AppConfigDBUtil.delete(appConfig); //直接从数据库删除
                        // 从服务器更新该应用配置
                        String ApkMD5 = Md5CalcUtil.calcMD5(ConfigActivity.this,AppInfoUtil.getApkDir(ConfigActivity.this,appConfig.getAppPackageName()));
                        RemoteServer.getInstance().getResult(ConfigActivity.this,ApkMD5);

                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public void handleMD5Result(String xml) {
        if(xml==null || xml.length()==0){
            RemoteServer.getInstance().getResult(this,AppInfoUtil.getApkFile(ConfigActivity.this,appConfig.getAppPackageName()));
        }else {
            handleAnalyzeResult(xml);
        }
    }

    @Override
    public void handleAnalyzeResult(String xml) {
        if(xml==null || xml.length()==0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ConfigActivity.this,"获取配置失败",Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        DataFlowResultsConverter converter = new DataFlowResultsConverter();
        DataFlowResults dataFlowResults = converter.convertToEntityProperty(xml);
        appConfig.setDataFlowResults(dataFlowResults);
        List<SourceConfig> sourceConfigs = new ArrayList<>();
        Set<String> sourceConfigSet = new HashSet<>();
        for(Result result:dataFlowResults.getImplicitResults()){
            for(Source source:result.getImplicitSources()){
                String statement = source.getStatement();
                String classAndFunction = statement.substring(statement.indexOf('<')+1,statement.indexOf('>'));
                String[] classAndFunctionArray = classAndFunction.split(":");
                String className = classAndFunctionArray[0].trim();
                String functionName = classAndFunctionArray[1].trim().split(" ")[1].trim();
                String returnType = source.getAccessPath().getType();
                String catSource = String.format("%s;%s;%s",className,functionName,returnType);
                sourceConfigSet.add(catSource);
            }
        }
        for(String sourceConfigStr:sourceConfigSet){
            String[] sourceConfigStrs = sourceConfigStr.split(";");
            String className = sourceConfigStrs[0];
            String functionName = sourceConfigStrs[1];
            String returnType = sourceConfigStrs[2];
            SourceConfig sourceConfig = new SourceConfig();
            sourceConfig.setClassName(className);
            sourceConfig.setFunctionName(functionName);
            sourceConfig.setReturnType(returnType);
            sourceConfigs.add(sourceConfig);
        }
        appConfig.setSourceConfigs(sourceConfigs);
        AppConfigDBUtil.insert(appConfig);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(appConfig.getSourceConfigs().size()>0){
                    rv_permission_list.setVisibility(View.VISIBLE);
                    tv_none_hint.setVisibility(View.GONE);
                    tv_wait_hint.setVisibility(View.GONE);
                }else {
                    rv_permission_list.setVisibility(View.GONE);
                    tv_none_hint.setVisibility(View.VISIBLE);
                    tv_wait_hint.setVisibility(View.GONE);
                }

                ((MethodRVAdapter)rv_permission_list.getAdapter()).updateData(appConfig);
                sw_enable.setChecked(false);
                if(dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
                Toast.makeText(ConfigActivity.this,"获取配置成功",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
