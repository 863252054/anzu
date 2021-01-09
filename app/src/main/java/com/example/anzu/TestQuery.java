package com.example.anzu;


import android.util.Log;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestQuery implements Runnable {
    private static String url = "https://www.yuan619.xyz:8887/user/byopenid";

    @Override
    public void run() {
        try {
            doGet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doGet() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
//        Map<String, String> params = new HashMap<>();
//        params.put("openid", "onVTd4qGDLtqrVoFx0pptKXuAOys");
//        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), JSON.toJSONString(params));
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("openid", "onVTd4qGDLtqrVoFx0pptKXuAOys")
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)//默认就是GET请求，可以不写
                .build();
        Response response = okHttpClient.newCall(request).execute();
        Log.i("success", response.toString());
        if(response.isSuccessful()){
            String result = response.body().string();
            Log.i("testQuery", result);
            //解析Json数据
//            JSON.parseObject(result, User.class);

//            Result<User> iotResult = JSON.parseObject(
//                    result,
//                    new TypeReference<Result<User>>
//                            (User.class){});
//            Log.i("iotResult", iotResult.getData().toString());
        }
    }


}
