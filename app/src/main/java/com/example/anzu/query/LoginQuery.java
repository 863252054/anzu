package com.example.anzu.query;

import android.os.Message;
import android.util.Log;

import java.io.IOException;
import android.os.Handler;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.anzu.Constants;
import com.example.anzu.bean.Result;
import com.example.anzu.bean.ShopUser;
import com.example.anzu.ui.login.LoginActivity;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginQuery implements Runnable {
    private Handler handler;
    private String cellphone;
    private String password;
    private String uid = "";
    private Message msg = new Message();
    private static String url = "https://www.yuan619.xyz:8886/shopUser/login";
    private static String url2 = "https://www.yuan619.xyz:8886/shop/getByUid";

    public LoginQuery(String cellphone, String password, Handler handler) {
        this.cellphone = cellphone;
        this.password = password;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            doGet();
            if (msg.what == Constants.OK) {
                getShop();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doGet() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("cellphone", cellphone)
                .addFormDataPart("password", password)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()){
            String result = response.body().string();
            Log.i("登录请求返回值", result);
            Result<ShopUser> userResult = JSON.parseObject(
                    result,
                    new TypeReference<Result<ShopUser>>
                            (ShopUser.class){});
            ShopUser shopUser = userResult.getData();
            if (shopUser != null) {
                msg.what = Constants.OK;
                msg.obj = shopUser;
                this.uid = shopUser.getUid();
            } else {
                msg.what = Constants.FAIL;
                msg.obj = null;
            }
            this.handler.sendMessage(msg);
        }
    }

    public void getShop() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uid", uid)
                .build();
        final Request request = new Request.Builder()
                .url(url2)
                .post(multipartBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()) {
            Message msg = new Message();
            String result = response.body().string();
            Log.i("查询是否入驻", result);
            Result<ShopUser> userResult = JSON.parseObject(
                    result,
                    new TypeReference<Result<ShopUser>>
                            (ShopUser.class){});
            if (userResult.getCode() == 400) {
                msg.what = Constants.NO;
            } else {
                ShopUser shopUser = userResult.getData();
                if (shopUser != null) {
                    msg.what = Constants.OPENED;
                    msg.obj = shopUser;
                }
            }
            this.handler.sendMessage(msg);
        }
    }

}
