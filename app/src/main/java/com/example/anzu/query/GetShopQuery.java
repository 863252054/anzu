package com.example.anzu.query;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.anzu.Constants;
import com.example.anzu.MyApplication;
import com.example.anzu.bean.Result;
import com.example.anzu.bean.Shop;
import com.example.anzu.bean.ShopUser;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetShopQuery implements Runnable {
    private Handler handler;
    private String uid;
    private static String url = "https://www.yuan619.xyz:8886/shop/getByUid";

    public GetShopQuery(Handler handler, String uid) {
        this.handler = handler;
        this.uid = uid;
    }

    @Override
    public void run() {
        try {
            getShop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getShop() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        System.out.println("uid" + uid);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uid", uid)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            Message msg = new Message();
            String result = response.body().string();
            Log.i("getShopQuery", result);
            Result<Shop> userResult = JSON.parseObject(
                    result,
                    new TypeReference<Result<Shop>>
                            (Shop.class){});
            if (userResult.getCode() == 400) {
                msg.what = Constants.FAIL;
            } else {
                Shop shop = userResult.getData();
                if (shop != null) {
                    msg.what = Constants.OK;
                    msg.obj = shop;
                }
            }
            this.handler.sendMessage(msg);
        }
    }
}
