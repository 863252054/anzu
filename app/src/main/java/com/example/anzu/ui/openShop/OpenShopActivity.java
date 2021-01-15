package com.example.anzu.ui.openShop;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.anzu.MyApplication;
import com.example.anzu.R;
import com.example.anzu.bean.ShopUser;
import com.example.anzu.utils.FileProvider7;
import com.example.anzu.utils.FileUtil;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.util.Auth;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class OpenShopActivity extends AppCompatActivity {
    //AK P6Hy0f7PHEo0A13_ow3-0_OGvYdFibL8r4eEicIg
    //SK b4_OZ4LFs_wHwGoYVHPbVDZhsKGP_7HmzBqmdbZp
    //bucket yuan619
    public static final int RC_TAKE_PHOTO = 1;
    public static final int RC_CHOOSE_PHOTO = 2;

    private String mTempPhotoPath;
    private Uri imageUri;

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_shop);

        logo = (ImageView) findViewById(R.id.open_iv_logo);

        //加载圆形图片
        // "https://www.baidu.com/img/flexible/logo/pc/result.png"
        // http://yuan619.xyz/test.jpg
        Glide.with(this).load("http://yuan619.xyz/test.jpg")
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(logo);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (ContextCompat.checkSelfPermission(OpenShopActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    //未授权，申请授权(从相册选择图片需要读取存储卡的权限)
//                    ActivityCompat.requestPermissions(OpenShopActivity.this, new String[]{Manifest.permission.CAMERA}, RC_TAKE_PHOTO);
//                } else {
//                    //已授权，获取照片
//                    takePhoto();
//                }
                if (ContextCompat.checkSelfPermission(OpenShopActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //未授权，申请授权(从相册选择图片需要读取存储卡的权限)
                    ActivityCompat.requestPermissions(OpenShopActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_CHOOSE_PHOTO);
                } else {
                    //已授权，获取照片
                    choosePhoto();
                }
            }
        });
    }
    /**
     权限申请结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_TAKE_PHOTO:   //拍照权限申请返回
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
                break;
            case RC_CHOOSE_PHOTO:   //相册选择照片权限申请返回
                choosePhoto();
                break;
        }
    }

    //跳转相册获取图片
    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, RC_CHOOSE_PHOTO);
    }


    //调用拍照
    private void takePhoto() {
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fileDir = new File(Environment.getExternalStorageDirectory() + File.separator + "photoTest" + File.separator);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File photoFile = new File(fileDir, "photo.jpeg");
        mTempPhotoPath = photoFile.getAbsolutePath();
        imageUri = FileProvider7.getUriForFile(this, photoFile);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentToTakePhoto, RC_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_CHOOSE_PHOTO:
                if (data == null) {
                    return;
                } else {
                    Uri uri = data.getData();
                    String filePath = FileUtil.getFilePathByUri(this, uri);
                    MyApplication myApplication = (MyApplication) getApplication();
                    myApplication.setLogoPath(filePath);
                    if (!TextUtils.isEmpty(filePath)) {
                        RequestOptions requestOptions1 = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
                        //将照片显示在 ivImage上
                        System.out.println("path:" + filePath);
                        Glide.with(this).load(filePath)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(logo);
                        uploadPic(filePath);
                    }
                }
                break;
            case RC_TAKE_PHOTO:
                RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
                //将图片显示在ivImage上
                Glide.with(this).load(mTempPhotoPath)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(logo);
                break;
        }
    }

    protected void uploadPic(String data) {
        //指定zone的具体区域
        //FixedZone.zone0   华东机房
        //FixedZone.zone1   华北机房
        //FixedZone.zone2   华南机房
        //FixedZone.zoneNa0 北美机房


    /*
    Configuration config = new Configuration.Builder()
            .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
            .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
            .connectTimeout(10)           // 链接超时。默认10秒
            .useHttps(true)               // 是否使用https 默认是false
            .responseTimeout(60)          // 服务器响应超时。默认60秒
            .recorder(recorder)           // recorder分片上传时，已上传片记录器。默认null
            .recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
            .zone(FixedZone.zone0)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
    */

        Configuration config = new Configuration.Builder()
                .useHttps(true)               // 是否使用https上传域名
                .zone(FixedZone.zone0)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();

        UploadManager uploadManager = new UploadManager(config); // UploadManager对象只需要创建一次重复使用

//        data = "/storage/emulated/0/DCIM/Camera/IMG_20190623_172115.jpg"; //要上传的文件
        MyApplication myApplication = (MyApplication) getApplication();
//        String key = "anzu/" + myApplication.getUid() + "-logo.jpg"; //在服务器的文件名
        String key = "test.jpg";
        /**
         * 生成token
         * create()方法的两个参数分别是 AK SK
         * uoloadToken()方法的参数是 要上传到的空间(bucket)
         */
        String token = Auth.create("P6Hy0f7PHEo0A13_ow3-0_OGvYdFibL8r4eEicIg", "b4_OZ4LFs_wHwGoYVHPbVDZhsKGP_7HmzBqmdbZp").uploadToken("yuan619");

        /**
         * 调用put方法上传
         * 第一个参数 data：可以是字符串，是要上传图片的路径
         *                可以是File对象，是要上传的文件
         *                可以是byte[]数组，要是上传的数据
         * 第二个参数 key：字符串，是图片在服务器上的名称，要具有唯一性，可以用UUID
         * 第三个参数 token：根据开发者的 AK和SK 生成的token，这个token 应该在后端提供一个接口，然后android代码中发一个get请求获得这个tocken，但这里为了演示，直接写在本地了.
         * 第四个参数：UpCompletionHandler的实例，有个回调方法
         * 第五个参数：可先参数
         */
        uploadManager.put(
                        data, key, token,
                        new UpCompletionHandler() {
                            /**
                             * 回调方法
                             * @param key 开发者设置的 key 也就是文件名
                             * @param info 日志，包含上传的ip等
                             * @param res 包括一个hash值和key
                             */
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject res) {
                                if(info.isOK()) {
                                    Log.i("上传结果：", "Upload Success");
                                }
                                else {
                                    Log.i("上传结果：", "Upload Fail");
                                    //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                                }
                                Log.i("key：", key + "\ninfo：" + info + "\nres：" + res);
                            }
                        },
                        null
                );
    }

}
