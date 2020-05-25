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
import okhttp3.ResponseBody;

public class RemoteServer {
    public static final String TAG = "RemoteServer";
    public static final String serverURL = "http://192.168.0.10:8081";
    public static final String GET_RESULT_FORMAT = "/result?apkMd5=%s";
    public static final String POST_APK_FORMAT = "/upload";
    private static volatile RemoteServer instance;

    private RemoteServer(){}

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

    public void getResult(AnalyzeResultCallBack callBack, String apkMd5){
        OkHttpClient client = new OkHttpClient();
        String format = serverURL.concat(GET_RESULT_FORMAT);
        String urlStr = String.format(format,apkMd5);

        Request request = new Request.Builder().url(urlStr).build();
        Call call = client.newCall(request);
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

    public void getResult(AnalyzeResultCallBack callBack,File apkFile) {
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .build();;
        String urlStr = serverURL.concat(POST_APK_FORMAT);
        MediaType.get("application/json; charset=utf-8");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uploadApkFile", apkFile.getName(),
                                 RequestBody.create(MediaType.parse("multipart/form-data"), apkFile))
                .build();
        Request request = new Request.Builder()
                .url(urlStr)
                .post(requestBody)
                .build();
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
