package com.mean.androidprivacy.server;

import com.mean.androidprivacy.ui.AnalyzeResultCallBack;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * @ClassName: RemoteServer
 * @Description: 服务器API调用工具类，单例模式，支持异步调用
 * @Author: MeanFan
 * @Version: 1.0
 */
public class RemoteServer {
    public static final String TAG = "RemoteServer";
    public static final String serverURL = "http://192.168.0.10:8081";  //服务器地址/套接字
    public static final String GET_RESULT_FORMAT = "/result?apkMd5=%s"; //获取已有分析结果接口的url格式
    public static final String POST_APK_FORMAT = "/upload";  //上传apk获取分析结果接口的url格式

    private static volatile RemoteServer instance;   //单例模式：静态原子化实例

    /**
    * @Author: MeanFan
    * @Description: 单例模式：私有化构造器
    * @Param: []
    * @return:
    **/
    private RemoteServer(){}

    /**
    * @Author: MeanFan
    * @Description: 单例模式：获取实例
    * @Param: []
    * @return: com.mean.androidprivacy.server.RemoteServer
    **/
    public static RemoteServer getInstance(){
        if(instance==null){
            synchronized (RemoteServer.class){
                if(instance==null){
                    instance = new RemoteServer();
                }
            }
        }
        return instance;
    }

    /**
    * @Author: MeanFan
    * @Description: 根据MD5获取已有分析结果，并通过接口回调通知实现回调的类（ConfigActivity）
    * @Param: [callBack, apkMd5]
    * @return: void
    **/
    public void getResult(AnalyzeResultCallBack callBack, String apkMd5){
        // 生成完整的请求url
        String format = serverURL.concat(GET_RESULT_FORMAT);
        String urlStr = String.format(format,apkMd5);

        // 生成HTTP请求（GET）
        Request request = new Request.Builder().url(urlStr).build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);

        // 设置HTTP响应回调
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.handleMD5Result(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    callBack.handleMD5Result(response.body().string());
                }else {
                    callBack.handleMD5Result(null);
                }
            }
        });
    }

    /**
     * @Author: MeanFan
     * @Description: 通过上传apk文件获取已有分析结果，并通过接口回调通知实现回调的类（ConfigActivity）
     * @Param: [callBack, apkFile]
     * @return: void
     **/
    public void getResult(AnalyzeResultCallBack callBack,File apkFile) {
        // 设置 OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .build();

        // 生成HTTP请求
        String urlStr = serverURL.concat(POST_APK_FORMAT);

        // 生成HTTP请求包文的报文体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uploadApkFile", apkFile.getName(),
                                 RequestBody.create(MediaType.parse("multipart/form-data"), apkFile))
                .build();

        // 生成HTTP请求（POST）
        Request request = new Request.Builder()
                .url(urlStr)
                .post(requestBody)
                .build();

        // 设置HTTP响应回调
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.handleAnalyzeResult(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    callBack.handleAnalyzeResult(response.body().string());
                }else {
                    callBack.handleAnalyzeResult(null);
                }
            }
        });
    }

}
