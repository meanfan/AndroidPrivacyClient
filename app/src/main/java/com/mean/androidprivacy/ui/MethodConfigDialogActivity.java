package com.mean.androidprivacy.ui;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mean.androidprivacy.R;

/**
 * @ClassName: MethodConfigDialogActivity
 * @Description: 配置项进行配置的对话框
 * @Author: MeanFan
 * @Version: 1.0
 */
public class MethodConfigDialogActivity extends AppCompatActivity {
    private TextView tv_method_desc;
    private RadioGroup rg_mode;
    private EditText et_diy;
    private Button btn_cancel,btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method_config_dialog);
        tv_method_desc = findViewById(R.id.tv_method_desc);
        rg_mode = findViewById(R.id.rg_mode);
        et_diy = findViewById(R.id.et_diy);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_confirm = findViewById(R.id.btn_confirm);

        // 获取配置项信息
        Intent recvIntent = getIntent();
        String methodDescString = recvIntent.getStringExtra("methodDesc");
        int methodPosInSourceConfigs = recvIntent.getIntExtra("methodPosInSourceConfigs",-1);

        // 检查信息正确性
        if(methodDescString == null ||methodPosInSourceConfigs == -1){
            Toast.makeText(this,"参数错误",Toast.LENGTH_SHORT).show();
            finish();
        }

        // 根据信息设置界面中的控件状态
        tv_method_desc.setText(methodDescString);
        rg_mode.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.rb_modify){
                et_diy.setVisibility(View.VISIBLE);
            }else {
                et_diy.setVisibility(View.GONE);
            }
        });
        et_diy.setVisibility(View.GONE);

        // 设置取消按钮点击事件
        btn_cancel.setOnClickListener(v -> {
            Intent sndIntent = new Intent();
            sndIntent.putExtra("methodPosInSourceConfigs",methodPosInSourceConfigs);
            setResult(RESULT_CANCELED,sndIntent);
            finish();
        });

        // 设置确认按钮点击事件
        btn_confirm.setOnClickListener(v->{
            // 根据选中状态的到配置模式
            int mode = -1;
            String modifyString = "";
            switch (rg_mode.getCheckedRadioButtonId()){
                case R.id.rb_null:
                    mode = 0;
                    break;
                case R.id.rb_default:
                    mode = 1;
                    break;
                case R.id.rb_modify:
                    mode = 2;
                    modifyString = et_diy.getText().toString();
                    break;
                default:
                    mode = -1;
            }
            // 未选择，提示用户
            if(mode == -1){
                Toast.makeText(MethodConfigDialogActivity.this,"请选择",Toast.LENGTH_SHORT).show();
                return;
            }
            // 模式为自定义但未输入数据，提示用户
            if(mode == 2 && modifyString.length()==0){
                Toast.makeText(MethodConfigDialogActivity.this,"请输入自定义数据",Toast.LENGTH_SHORT).show();
                return;
            }

            // 返回配置结果给ConfigActivity
            Intent sndIntent = new Intent();
            sndIntent.putExtra("mode",mode);
            sndIntent.putExtra("modifyString",modifyString);
            sndIntent.putExtra("methodPosInSourceConfigs",methodPosInSourceConfigs);
            setResult(RESULT_OK,sndIntent);
            finish();
        });




    }

}
