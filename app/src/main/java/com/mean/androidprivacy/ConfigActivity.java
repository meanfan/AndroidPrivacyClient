package com.mean.androidprivacy;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mean.androidprivacy.adapter.ResultRVAdapter;
import com.mean.androidprivacy.bean.AppConfig;
import com.mean.androidprivacy.bean.DataFlowResults;
import com.mean.androidprivacy.bean.Result;
import com.mean.androidprivacy.bean.Source;
import com.mean.androidprivacy.bean.SourceConfig;
import com.mean.androidprivacy.converter.DataFlowResultsConverter;
import com.mean.androidprivacy.server.RemoteServer;
import com.mean.androidprivacy.utils.AppConfigDBUtil;
import com.mean.androidprivacy.utils.AppInfoUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigActivity extends AppCompatActivity {
    public static final String TAG = "PermissionActivity";
    public static final int REQUESTCODE_CONFIG = 0;
    private ImageView iv_icon;
    private TextView tv_name;
    private Switch sw_enable;
    private RecyclerView rv_permission_list;
    private TextView tv_none_hint;
    private FloatingActionButton fab_restore;

    private String appPackageName;

    private AppConfig appConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        iv_icon = findViewById(R.id.iv_icon);
        tv_name = findViewById(R.id.tv_name);
        sw_enable = findViewById(R.id.sw_enable);
        rv_permission_list = findViewById(R.id.rv_permission_list);
        tv_none_hint = findViewById(R.id.tv_none_hint);
        fab_restore = findViewById(R.id.fab_restore);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
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
        sw_enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appConfig.setIsEnabled(isChecked);
                Intent intent = new Intent();
                int appListPos = getIntent().getIntExtra("appListPos",-1);
                intent.putExtra("appListPos",appListPos);
                intent.putExtra("isEnabled",isChecked);
                setResult(RESULT_OK,intent);
                AppConfigDBUtil.insert(appConfig);
            }
        });

        try{
            if(appConfig.getDataFlowResults().getResults().getResults().size() == 0){
                rv_permission_list.setVisibility(View.GONE);
                tv_none_hint.setVisibility(View.VISIBLE);
            }
        }catch (NullPointerException e){
            rv_permission_list.setVisibility(View.VISIBLE);
            tv_none_hint.setVisibility(View.GONE);
        }
        rv_permission_list.setLayoutManager(new LinearLayoutManager(ConfigActivity.this));
        rv_permission_list.setAdapter(new ResultRVAdapter(appConfig));

        fab_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);
                builder.setTitle("提示")
                        .setMessage("确定从服务器更新该应用所有配置？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppConfigDBUtil.delete(appConfig); //直接从数据库删除
                                appConfig.setDataFlowResults(null);
                                sw_enable.setChecked(false);
                                // 从服务器更新该应用配置
                                String xml = RemoteServer.getInstance().getResult("5E7D6134D494CAC48A8A1E34BB356577");
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

                                ((ResultRVAdapter)rv_permission_list.getAdapter()).updateData();
                                rv_permission_list.getAdapter().notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }
}
