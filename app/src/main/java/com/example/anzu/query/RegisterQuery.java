package com.example.anzu.query;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.anzu.Constants;
import com.example.anzu.bean.Result;
import com.example.anzu.bean.ShopUser;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterQuery implements Runnable {
    private Handler handler;
    private String cellphone;
    private String code;
    private String password;
    private static String url = "https://www.yuan619.xyz:8886/shopUser/saveShopUser";

    public RegisterQuery(Handler handler, String cellphone, String code, String password) {
        this.handler = handler;
        this.cellphone = cellphone;
        this.code = code;
        this.password = password;
    }

    @Override
    public void run() {
        try {
            saveShopUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveShopUser() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
//        Map<String, String> params = new HashMap<>();
//        params.put("openid", "onVTd4qGDLtqrVoFx0pptKXuAOys");
//        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), JSON.toJSONString(params));
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("cellphone", cellphone)
                .addFormDataPart("password", password)
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
            Result<ShopUser> userResult = JSON.parseObject(
                    result,
                    new TypeReference<Result<ShopUser>>
                            (ShopUser.class){});
            ShopUser shopUser = userResult.getData();
            Message msg = new Message();
            if (shopUser != null) {
                msg.what = Constants.OK;
                msg.obj = shopUser;
            } else {
                msg.what = Constants.FAIL;
                msg.obj = null;
            }
            this.handler.sendMessage(msg);
        } else {
            Message msg = new Message();
            msg.what = Constants.NET;
            msg.obj = null;
            this.handler.sendMessage(msg);
        }
    }
}
