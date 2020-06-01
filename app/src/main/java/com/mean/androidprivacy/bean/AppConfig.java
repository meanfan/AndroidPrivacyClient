package com.mean.androidprivacy.bean;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.mean.androidprivacy.converter.DataFlowResultsConverter;
import com.mean.androidprivacy.converter.SourceConfigConverter;
import com.mean.androidprivacy.utils.AppInfoUtil;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;


/**
 * @ClassName: AppConfig
 * @Description: 应用程序的配置类
 * @Author: MeanFan
 * @Version: 1.0
 */
@Entity
public class AppConfig implements Serializable {
    static  final long serialVersionUID = 1;
    @Id
    private String appPackageName;
    private String appName;
    private boolean isEnabled;
    @Convert(columnType = String.class, converter = DataFlowResultsConverter.class)
    private DataFlowResults dataFlowResults;
    @Convert(columnType = String.class, converter = SourceConfigConverter.class)
    private List<SourceConfig> sourceConfigs;

    @Generated(hash = 227514932)
    public AppConfig(String appPackageName, String appName, boolean isEnabled,
            DataFlowResults dataFlowResults, List<SourceConfig> sourceConfigs) {
        this.appPackageName = appPackageName;
        this.appName = appName;
        this.isEnabled = isEnabled;
        this.dataFlowResults = dataFlowResults;
        this.sourceConfigs = sourceConfigs;
    }

    @Generated(hash = 136961441)
    public AppConfig() {
    }

    public void init(String appPackageName,String appName,boolean isEnabled){
        this.appPackageName = appPackageName;
        this.appName = appName;
        this.isEnabled = isEnabled;
    }

    public Drawable getAppIconDrawable(Context context) {
        return AppInfoUtil.getAppIcon(context, appPackageName);
    }

    public String getAppPackageName() {
        return this.appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public DataFlowResults getDataFlowResults() {
        return this.dataFlowResults;
    }

    public void setDataFlowResults(DataFlowResults dataFlowResults) {
        this.dataFlowResults = dataFlowResults;
    }

    public List<SourceConfig> getSourceConfigs() {
        return this.sourceConfigs;
    }

    public void setSourceConfigs(List<SourceConfig> sourceConfigs) {
        this.sourceConfigs = sourceConfigs;
    }
}
