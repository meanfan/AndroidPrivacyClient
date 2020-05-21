package com.mean.androidprivacy.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.mean.androidprivacy.ConfigActivity;
import com.mean.androidprivacy.ConfigDetailActivity;
import com.mean.androidprivacy.R;
import com.mean.androidprivacy.bean.AppConfig;
import com.mean.androidprivacy.bean.DataFlowResults;
import com.mean.androidprivacy.bean.Result;
import com.mean.androidprivacy.bean.SourceConfig;

import java.util.ArrayList;
import java.util.List;

public class ResultRVAdapter extends RecyclerView.Adapter<ResultRVAdapter.VH> {
    public static final String TAG = "PermissionRVAdapter";
    //创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder{
        public final TextView name;
        public final TextView status;
        public VH(View v) {
            super(v);
            name = v.findViewById(R.id.tv_permission_name);
            status = v.findViewById(R.id.tv_permission_config_status);
        }
    }

    private AppConfig appConfig;
    private List<String> methodDesc;
    private List<Boolean> methodStatus;

    private ResultRVAdapter(){}

    public ResultRVAdapter(AppConfig config) {
        this.appConfig = config;
        updateData();
    }

    //在notify前调用
    public void updateData(){
        methodDesc = new ArrayList<>();
        methodStatus = new ArrayList<>();
        try{
            List<SourceConfig> sourceConfigs =  appConfig.getSourceConfigs();
            if(sourceConfigs!=null){
                for(SourceConfig s:sourceConfigs){
                    methodDesc.add(s.toFineString());
                    methodStatus.add(s.isEnable());
                }
            }
        }catch (NullPointerException e){
            return;
        }
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        holder.name.setText(methodDesc.get(position));
        holder.status.setText(methodStatus.get(position) ? "拦截":"不拦截");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appConfig.getIsEnabled()) {
                    Intent intent = new Intent(v.getContext(), ConfigDetailActivity.class);
                    intent.putExtra("appConfig", appConfig);
                    String[] methodDescs = new String[1];
                    boolean[] methodStatuses = new boolean[1];
                    methodDescs[0] = methodDesc.get(position);
                    methodStatuses[0] = methodStatus.get(position);
                    intent.putExtra("methodDescs",methodDescs);
                    intent.putExtra("methodStatuses",methodStatuses);
                    ((ConfigActivity)v.getContext()).startActivityForResult(intent, ConfigActivity.REQUESTCODE_CONFIG);
                }else {
                    Toast.makeText(v.getContext(),"请先启用",Toast.LENGTH_SHORT).show();
                }
            }
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
