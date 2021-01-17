package com.example.anzu.query;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.anzu.Constants;
import com.example.anzu.bean.Goods;
import com.example.anzu.bean.Result;
import com.example.anzu.bean.ShopUser;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetGoodsQuery implements Runnable {
    private Handler handler;
    private String uid;
    private static String url = "https://www.yuan619.xyz:8886/goods/getGoods";

    public GetGoodsQuery(Handler handler, String uid) {
        this.handler = handler;
        this.uid = uid;
    }

    @Override
    public void run() {
        try {
            getGoods();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getGoods() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uid", uid)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        Log.i("success", response.toString());
        if(response.isSuccessful()){
            String result = response.body().string();
            Log.i("testQuery", result);
            Result<Goods> userResult = JSON.parseObject(
                    result,
                    new TypeReference<Result<Goods>>
                            (Goods.class){});
            Goods goods = userResult.getData();
            Message msg = new Message();
            if (goods != null) {
                msg.what = Constants.OK;
                msg.obj = goods;
            } else {
                msg.what = Constants.FAIL;
                msg.obj = null;
            }
            this.handler.sendMessage(msg);
        } else {
            Message msg = new Message();
            msg.what = Constants.NO;
            msg.obj = null;
            this.handler.sendMessage(msg);
        }
    }
}
