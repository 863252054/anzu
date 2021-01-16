package com.example.anzu.query;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.anzu.Constants;
import com.example.anzu.bean.Result;
import com.example.anzu.bean.Shop;
import com.example.anzu.bean.ShopUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpShopQuery implements Runnable {
    private Handler handler;
    private Map params;
    private static String url = "https://www.yuan619.xyz:8886/shop/saveShop";

    public UpShopQuery(Handler handler, Map params) {
        this.handler = handler;
        this.params = params;
    }

    @Override
    public void run() {
        try {
            saveShop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveShop() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), JSON.toJSONString(params));
        final Request request = new Request.Builder()
                .url(url)
                .post(body)//默认就是GET请求，可以不写
                .build();
        Response response = okHttpClient.newCall(request).execute();
        Log.i("success", response.toString());
        if(response.isSuccessful()){
            String result = response.body().string();
            Log.i("testQuery", result);
            //解析Json数据
//            JSON.parseObject(result, User.class);
            Result<Shop> userResult = JSON.parseObject(
                    result,
                    new TypeReference<Result<Shop>>
                            (Shop.class){});
            Shop shop = userResult.getData();
            Message msg = new Message();
            if (shop != null) {
                msg.what = Constants.OK;
                msg.obj = shop;
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
