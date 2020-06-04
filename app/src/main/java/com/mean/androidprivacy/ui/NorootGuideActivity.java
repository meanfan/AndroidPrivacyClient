package com.mean.androidprivacy.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import com.mean.androidprivacy.R;
import com.mean.androidprivacy.utils.AppInfoUtil;
import com.mean.androidprivacy.utils.VirtualXposedUtil;

public class NorootGuideActivity extends AppCompatActivity {
    private Button btn_step1,btn_step2,btn_step3,btn_step4;

    private static final String VP_DL_URL = "https://github.com/android-hacker/VirtualXposed/releases/download/0.18.2/VirtualXposed_0.18.2.apk";
    private static final String VP_PKG_Name = "io.va.exposed";
    private static final String VP_INNER_XPOSEDMANAGER_PKG_Name = "de.robv.android.xposed.installer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noroot_guide);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_step1 = findViewById(R.id.btn_step1);
        btn_step2 = findViewById(R.id.btn_step2);
        btn_step3 = findViewById(R.id.btn_step3);
        btn_step4 = findViewById(R.id.btn_step4);

        if(AppInfoUtil.checkAPPInstalled(this,VP_PKG_Name)){
            btn_step1.setEnabled(false);
            btn_step1.setText("已安装");
        }

        btn_step1.setOnClickListener(v -> {
            openUrl(VP_DL_URL);
        });
        btn_step2.setOnClickListener(v -> {
            VirtualXposedUtil.updateAPP(this,getPackageName());
        });
        btn_step3.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("操作提示")
                    .setMessage("在即将弹出的界面中\n1. 点击界面左上角“菜单”图标\n2. 点击“模块”\n3. 勾选“AndroidPrivacy”\n4. 返回本界面")
                    .setPositiveButton("前往", (dialog1, which) -> {
                        VirtualXposedUtil.launchAPP(this,VP_INNER_XPOSEDMANAGER_PKG_Name);
                    })
                    .setNegativeButton("我已完成", (dialog12, which) -> {
                        dialog12.dismiss();
                    })
                    .create();
            dialog.show();

        });
        btn_step4.setOnClickListener(v -> {
            finish();
        });

    }

    private void openUrl(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
