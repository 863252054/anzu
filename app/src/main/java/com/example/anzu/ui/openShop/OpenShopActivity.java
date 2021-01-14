package com.example.anzu.ui.openShop;

import android.Manifest;
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

import java.io.File;
import java.io.FileNotFoundException;

public class OpenShopActivity extends AppCompatActivity {
    public static final int RC_TAKE_PHOTO = 1;
    public static final int RC_CHOOSE_PHOTO = 2;

    private String mTempPhotoPath;
    private Uri imageUri;

    private ImageView logo;
    private ImageView test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_shop);

        logo = (ImageView) findViewById(R.id.open_iv_logo);
        test = (ImageView) findViewById(R.id.test_image);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication myApplication = (MyApplication) getApplication();
                Glide.with(OpenShopActivity.this).load(myApplication.getLogoPath())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(test);
            }
        });

        //加载圆形图片
        // "https://www.baidu.com/img/flexible/logo/pc/result.png"
        Glide.with(this).load(R.drawable.ic_lease)
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


    //拍照
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

}
