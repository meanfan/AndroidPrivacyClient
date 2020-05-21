package com.mean.androidprivacy.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mean.androidprivacy.R;

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

        Intent recvIntent = getIntent();
        String methodDescString = recvIntent.getStringExtra("methodDesc");
        int methodPosInSourceConfigs = recvIntent.getIntExtra("methodPosInSourceConfigs",-1);

        if(methodDescString == null ||methodPosInSourceConfigs == -1){
            Toast.makeText(this,"参数错误",Toast.LENGTH_SHORT).show();
            finish();
        }

        tv_method_desc.setText(methodDescString);
        rg_mode.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.rb_modify){
                et_diy.setVisibility(View.VISIBLE);
            }else {
                et_diy.setVisibility(View.GONE);
            }
        });

        et_diy.setVisibility(View.GONE);

        btn_cancel.setOnClickListener(v -> {
            Intent sndIntent = new Intent();
            sndIntent.putExtra("methodPosInSourceConfigs",methodPosInSourceConfigs);
            setResult(RESULT_CANCELED,sndIntent);
            finish();
        });

        btn_confirm.setOnClickListener(v->{
            int mode = 0;
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
            if(mode == -1){
                Toast.makeText(MethodConfigDialogActivity.this,"请选择",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent sndIntent = new Intent();
            sndIntent.putExtra("mode",mode);
            sndIntent.putExtra("modifyString",modifyString);
            sndIntent.putExtra("methodPosInSourceConfigs",methodPosInSourceConfigs);
            setResult(RESULT_OK,sndIntent);
            finish();
        });




    }

}
