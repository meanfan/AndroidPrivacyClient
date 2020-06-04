package com.mean.androidprivacy.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mean.androidprivacy.R;
import com.mean.androidprivacy.utils.AppInfoUtil;
import com.mean.androidprivacy.utils.VirtualXposedUtil;

public class NorootConfigActivity extends AppCompatActivity {
    private String appPackageName;
    private ImageView iv_icon;
    private TextView tv_name;
    private Button btn_install,btn_config,btn_launch,btn_remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noroot_config);
        setTitle("应用配置");
        appPackageName = getIntent().getStringExtra("appPackageName");
        if(appPackageName == null || appPackageName.length()==0 || !AppInfoUtil.checkAPPInstalled(this,appPackageName)){
            Toast.makeText(this,"参数错误",Toast.LENGTH_SHORT).show();
            finish();
        }
        iv_icon = findViewById(R.id.iv_icon);
        tv_name = findViewById(R.id.tv_name);
        btn_install = findViewById(R.id.btn_install);
        btn_config = findViewById(R.id.btn_config);
        btn_launch = findViewById(R.id.btn_launch);
        btn_remove = findViewById(R.id.btn_remove);

        iv_icon.setImageDrawable(AppInfoUtil.getAppIcon(this,appPackageName));
        tv_name.setText(AppInfoUtil.getAppName(this,appPackageName));

        btn_install.setOnClickListener(v -> {
            VirtualXposedUtil.updateAPP(NorootConfigActivity.this,appPackageName);
            Toast.makeText(this,"请等待",Toast.LENGTH_SHORT).show();
        });

        btn_config.setOnClickListener(v -> {
            VirtualXposedUtil.launchAPP(this,getPackageName());
        });

        btn_launch.setOnClickListener(v -> {
            VirtualXposedUtil.launchAPP(this,appPackageName);
        });

        btn_remove.setOnClickListener(v -> {
            //TODO VirtualXposedUtil.removeAPP()
        });



    }
}
