package com.mean.androidprivacy.server;

import java.io.File;
import java.io.IOException;

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

    public String getResult(String apkMd5){
        OkHttpClient client = new OkHttpClient();
        String format = serverURL.concat(GET_RESULT_FORMAT);
        String urlStr = String.format(format,apkMd5);

        Request request = new Request.Builder().url(urlStr).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if(responseBody!=null) {
                return responseBody.string();
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getResult(File apkFile) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String urlStr = serverURL.concat(POST_APK_FORMAT);
        MediaType.get("application/json; charset=utf-8");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", apkFile.getName(),
                                 RequestBody.create(MediaType.parse("multipart/form-data"), apkFile))
                .build();
        Request request = new Request.Builder()
                .url(urlStr)
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
