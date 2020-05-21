package com.mean.androidprivacy.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mean.androidprivacy.ui.MainActivity;
import com.mean.androidprivacy.ui.ConfigActivity;
import com.mean.androidprivacy.R;
import com.mean.androidprivacy.bean.AppConfig;
import com.mean.androidprivacy.utils.AppInfoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppRVAdapter extends RecyclerView.Adapter<AppRVAdapter.VH> {
    //创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder{
        public final TextView name;
        public final ImageView icon;
        public final TextView status;
        public VH(View v) {
            super(v);
            name = v.findViewById(R.id.tv_name);
            icon = v.findViewById(R.id.iv_icon);
            status = v.findViewById(R.id.tv_status);
        }
    }

    private Map<String,AppConfig> appConfigMap;
    private List<String> appPackageNameList;
    private List<String> appNameList;
    public AppRVAdapter(Map<String,AppConfig> appConfigMap) {
        this.appConfigMap = appConfigMap;
        appPackageNameList = new ArrayList<>();
        appNameList = new ArrayList<>();
        for (String s : appConfigMap.keySet()) {
            appPackageNameList.add(s);
            appNameList.add(appConfigMap.get(s).getAppName());
        }
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        //设置name
        final String appName = appNameList.get(position);
        holder.name.setText(appName);
        //设置icon
        final String appPackageName = appPackageNameList.get(position);
        Drawable icon = AppInfoUtil.getAppIcon(holder.itemView.getContext(), appPackageName);
        if(icon != null) {
            holder.icon.setImageDrawable(icon);
        }
        if(appConfigMap.get(appPackageName).getIsEnabled()){
            holder.status.setText("启用");
        }else {
            holder.status.setText("未启用");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ConfigActivity.class);
                intent.putExtra("appPackageName",appPackageName);
                ((MainActivity)v.getContext()).startActivityForResult(intent,MainActivity.REQUEST_CODE_CONFIG);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appNameList.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_rv_item, parent, false);
        return new VH(v);
    }
}
