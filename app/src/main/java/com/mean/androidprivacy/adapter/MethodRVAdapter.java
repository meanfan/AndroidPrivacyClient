package com.mean.androidprivacy.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.mean.androidprivacy.ui.ConfigActivity;
import com.mean.androidprivacy.ui.MethodConfigDialogActivity;
import com.mean.androidprivacy.ui.MethodDetailActivity;
import com.mean.androidprivacy.R;
import com.mean.androidprivacy.bean.AppConfig;
import com.mean.androidprivacy.bean.SourceConfig;
import com.mean.androidprivacy.utils.AppConfigDBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MethodRVAdapter
 * @Description: 配置界面配置项列表的RecyclerView适配器
 * @Author: MeanFan
 * @Version: 1.0
 */
public class MethodRVAdapter extends RecyclerView.Adapter<MethodRVAdapter.VH> {
    public static final String TAG = "MethodRVAdapter";

    public static final int REQUEST_CODE_METHOD_CONFIG = 0;
    public static final int REQUEST_CODE_METHOD_DETAIL = 1;

    //创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder{
        public final TextView name;
        public final Switch status;
        public VH(View v) {
            super(v);
            name = v.findViewById(R.id.tv_method_desc);
            status = v.findViewById(R.id.sw_enable);
        }
    }

    private AppConfig appConfig;
    private List<String> methodDesc;
    private List<Boolean> methodStatus;

    private MethodRVAdapter(){}

    public MethodRVAdapter(AppConfig config) {
        updateData(config);
    }

    /**
    * @Author: MeanFan
    * @Description: 在notify前调用，用于更新列表数据
    * @Date: 10:54 2020/6/1 0001
    * @Param: [config]
    * @return: void
    **/
    public void updateData(AppConfig config){
        this.appConfig = config;
        methodDesc = new ArrayList<>();
        methodStatus = new ArrayList<>();
        try{
            List<SourceConfig> sourceConfigs =  appConfig.getSourceConfigs();
            if(sourceConfigs!=null){
                for(SourceConfig s:sourceConfigs){
                    methodDesc.add(s.toFineString());
                    methodStatus.add(s.isEnable());
                }
                notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            return;
        }
    }
    public void updateData() {
        updateData(appConfig);
    }


    /**
     * @Author: MeanFan
     * @Description: 初始化指定位置的列表项目
     * @Param: [holder, position]
     * @return: void
     **/
    @Override
    public void onBindViewHolder(VH holder, final int position) {
        Activity activity = (Activity)holder.itemView.getContext();
        String methodDescString = methodDesc.get(position);
        boolean methodStatusBoolean = methodStatus.get(position);
        holder.name.setText(methodDescString);
        holder.status.setChecked(methodStatusBoolean);
        holder.status.setText(methodStatusBoolean ? "拦截":"不拦截");
        holder.status.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!appConfig.getIsEnabled()) {
                Toast.makeText(activity,"请启用",Toast.LENGTH_SHORT).show();
                //buttonView.setChecked(!isChecked);
                return;
            }

            buttonView.setText(isChecked?"拦截":"不拦截");

            if(isChecked){
                // 弹出配置窗口
                Intent intent = new Intent(activity, MethodConfigDialogActivity.class);
                intent.putExtra("methodDesc",methodDescString);
                intent.putExtra("methodPosInSourceConfigs",position);
                activity.startActivityForResult(intent,REQUEST_CODE_METHOD_CONFIG);

            }else {
                // 关闭
                SourceConfig sourceConfig = appConfig.getSourceConfigs().get(position);
                sourceConfig.setEnable(false);
                AppConfigDBUtil.insert(appConfig);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MethodDetailActivity.class);
            intent.putExtra("appPackageName", appConfig.getAppPackageName());
            intent.putExtra("methodDesc",methodDesc.get(position));
            ((ConfigActivity)v.getContext()).startActivityForResult(intent, REQUEST_CODE_METHOD_DETAIL);
        });
    }

    @Override
    public int getItemCount() {
        try{
            return appConfig.getSourceConfigs().size();
        }catch (NullPointerException e){
            return 0;
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.method_list_rv_item, parent, false);
        return new VH(v);
    }
}
