package com.mean.androidprivacy.ui;

/**
 * @Description: RemoteServer的API响应回调接口
 * @Author: MeanFan
 * @Version: 1.0
 */
public interface AnalyzeResultCallBack {
    void handleMD5Result(String xml);
    void  handleAnalyzeResult(String xml);
}
