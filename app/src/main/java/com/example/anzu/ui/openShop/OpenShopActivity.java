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
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.anzu.Constants;
import com.example.anzu.MainActivity;
import com.example.anzu.MyApplication;
import com.example.anzu.R;
import com.example.anzu.bean.Shop;
import com.example.anzu.bean.ShopUser;
import com.example.anzu.query.UpShopQuery;
import com.example.anzu.ui.components.TakePhotoPopWin;
import com.example.anzu.ui.login.LoginActivity;
import com.example.anzu.ui.register.RegisterActivity;
import com.example.anzu.utils.FileProvider7;
import com.example.anzu.utils.FileUtil;
import com.example.anzu.utils.StringDesignUtil;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.util.Auth;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class OpenShopActivity extends AppCompatActivity implements View.OnClickListener {
    //AK P6Hy0f7PHEo0A13_ow3-0_OGvYdFibL8r4eEicIg
    //SK b4_OZ4LFs_wHwGoYVHPbVDZhsKGP_7HmzBqmdbZp
    //bucket yuan619

    public static final int RC_TAKE_PHOTO = 1; //拍照
    public static final int RC_CHOOSE_PHOTO = 2; //相册
    private String tempPhotoPath;
    private Uri imageUri;
    private String localUrl = "";
    private String upUrl = "";
    private String licenseUrl = "";
    private boolean flag = false;
    private boolean selected = false;
    private boolean checked = false;
    //图像处理组件
    private TakePhotoPopWin takePhotoPopWin;
    private ImageView currentPic; //当前选中的图片组件
    private ImageView logo; //logo
    private ImageView license; //license
    //其他组件
    private ImageView back;
    private EditText holderName;
    private EditText holderPhone;
    private EditText shopName;
    private Spinner shopType;
    private EditText address;
    private CheckBox checkBox;
    private Button commit;
    //警告icon
    private ImageView warn1;
    private ImageView warn2;
    private ImageView warn3;
    private ImageView warn4;
    private ImageView warn5;
    private ImageView warn6;
    private ImageView warn7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_shop);
        //初始化
        init();

        //handler
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Constants.OK:
                        Intent intent = new Intent(OpenShopActivity.this, MainActivity.class);
                        startActivity(intent);
//                        TimerTask task = new TimerTask() {
//                            @Override
//                            public void run() {
//                                startActivity(intent);
//                            }
//                        };
//                        Timer timer = new Timer();
//                        timer.schedule(task, 2000);
                        OpenShopActivity.this.finish();
                        break;
                    case Constants.FAIL:
                        Toast.makeText(OpenShopActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.NET:
                        Toast.makeText(OpenShopActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        //监听点击事件
        back.setOnClickListener(this);
        logo.setOnClickListener(this);
        license.setOnClickListener(this);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;
                if (holderName.getText().toString().length() == 0) {
                    warn1.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (holderPhone.getText().toString().length() == 0) {
                    warn2.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (shopName.getText().toString().length() == 0) {
                    warn3.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (!selected) {
                    warn4.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (address.getText().toString().length() == 0) {
                    warn5.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (licenseUrl == "") {
                    warn6.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (!checked) {
                    warn7.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (flag == 1) {
                    Toast.makeText(OpenShopActivity.this, "提交失败，请完善或修改红标信息", Toast.LENGTH_SHORT).show();
                } else {
                    uploadPic(localUrl);
                    MyApplication myApplication = (MyApplication) getApplication();
                    Map<String, String> params = new HashMap<>();
                    params.put("uid", myApplication.getUid());
                    params.put("holderName", holderName.getText().toString());
                    params.put("holderPhone", holderPhone.getText().toString());
                    params.put("shopName", shopName.getText().toString());
                    params.put("shopType", shopType.getSelectedItem().toString());
                    params.put("shopAddress", address.getText().toString());
                    params.put("shopLogo", upUrl);
                    UpShopQuery upShopQuery = new UpShopQuery(handler, params);
                    Thread upShopThread = new Thread(upShopQuery);
                    upShopThread.start();
                    Toast.makeText(OpenShopActivity.this, "提交成功，信息上传中，请稍候...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //监听文字输入事件
        holderName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (holderName.getText().toString().length() != 0) {
                    warn1.setVisibility(View.GONE);
                } else {
                    warn1.setVisibility(View.VISIBLE);
                }
            }
        });
        holderPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (holderPhone.getText().toString().length() != 0) {
                    warn2.setVisibility(View.GONE);
                } else {
                    warn2.setVisibility(View.VISIBLE);
                }
            }
        });
        shopName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (shopName.getText().toString().length() != 0) {
                    warn3.setVisibility(View.GONE);
                } else {
                    warn3.setVisibility(View.VISIBLE);
                }
            }
        });
        shopType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    warn4.setVisibility(View.GONE);
                    flag = true;
                    selected = true;
                } else {
                    selected = false;
                    if (!flag) {
                    } else {
                        warn4.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (address.getText().toString().length() != 0) {
                    warn5.setVisibility(View.GONE);
                } else {
                    warn5.setVisibility(View.VISIBLE);
                }
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checked = true;
                    warn7.setVisibility(View.GONE);
                } else {
                    checked = false;
                    warn7.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.open_iv_logo: //logo
                currentPic = logo;
                showPopFormBottom(view);
                break;
            case R.id.open_iv_license: //license
                currentPic = license;
                showPopFormBottom(view);
                break;
            case R.id.open_iv_back:
                Intent intent = new Intent(OpenShopActivity.this, LoginActivity.class);
                startActivity(intent);
                OpenShopActivity.this.finish();
        }
    }

    //底部弹出
    public void showPopFormBottom(View view) {
        takePhotoPopWin = new TakePhotoPopWin(OpenShopActivity.this, onClickListener);
        //showAtLocation(View parent, int gravity, int x, int y)
        takePhotoPopWin.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
    //选择拍照或相册
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    takePhotoPopWin.dismiss();
                    System.out.println("takePhoto");
                    if (ContextCompat.checkSelfPermission(OpenShopActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //未授权，申请授权
                        ActivityCompat.requestPermissions(OpenShopActivity.this, new String[]{Manifest.permission.CAMERA}, RC_TAKE_PHOTO);
                    } else {
                        //已授权，拍照
                        takePhoto();
                    }
                    break;
                case R.id.btn_pick_photo:
                    takePhotoPopWin.dismiss();
                    System.out.println("choosePhoto");
                    if (ContextCompat.checkSelfPermission(OpenShopActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        //未授权，申请授权
                        ActivityCompat.requestPermissions(OpenShopActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_CHOOSE_PHOTO);
                    } else {
                        //已授权，获取照片
                        choosePhoto();
                    }
                    break;
            }
        }
    };

    //权限申请
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
//            case RC_TAKE_PHOTO:   //拍照权限申请返回
//                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    takePhoto();
//                }
//                break;
            case RC_CHOOSE_PHOTO:  //相册选择照片权限申请返回
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    choosePhoto();
                }
                break;
        }
    }

    //调用相册获取图片
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
        //随机照片编号
        String key = new Random().nextLong() + ".jpeg";
        File photoFile = new File(fileDir, key);
        tempPhotoPath = photoFile.getAbsolutePath();
        imageUri = FileProvider7.getUriForFile(this, photoFile);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentToTakePhoto, RC_TAKE_PHOTO);
    }

    //处理照片并显示
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
                        //显示图片
                        System.out.println("path:" + filePath);
                        if (currentPic == logo) {
                            localUrl = filePath;
                            Glide.with(this)
                                    .load(filePath)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(logo);
                        } else {
                            licenseUrl = filePath;
                            warn6.setVisibility(View.GONE);
                            Glide.with(this)
                                    .load(filePath)
                                    .into(license);
                        }
                    }
                }
                break;
            case RC_TAKE_PHOTO:
                RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
                //显示图片
                if (currentPic == logo) {
                    localUrl = tempPhotoPath;
                    Glide.with(this)
                            .load(tempPhotoPath)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(logo);
                } else {
                    licenseUrl = tempPhotoPath;
                    warn6.setVisibility(View.GONE);
                    Glide.with(this)
                            .load(tempPhotoPath)
                            .into(license);
                }
                break;
        }
    }

    //上传图片到七牛云
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
        String key = "anzu/" + myApplication.getUid() + "-logo.jpg"; //在服务器的文件名
        upUrl = "http://yuan619.xyz/" + key;
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

    public void init() {
        //绑定组件
        back = (ImageView) findViewById(R.id.open_iv_back);
        logo = (ImageView) findViewById(R.id.open_iv_logo);
        license = (ImageView) findViewById(R.id.open_iv_license);
        commit = (Button) findViewById(R.id.open_btn_commit);
        holderName = (EditText) findViewById(R.id.open_et_holder);
        holderPhone = (EditText) findViewById(R.id.open_et_phone);
        shopName = (EditText) findViewById(R.id.open_et_shop_name);
        shopType = (Spinner) findViewById(R.id.open_sp_shop_type);
        address = (EditText) findViewById(R.id.open_et_shop_address);
        checkBox = (CheckBox) findViewById(R.id.open_check);
        warn1 = (ImageView) findViewById(R.id.open_iv_warn1);
        warn2 = (ImageView) findViewById(R.id.open_iv_warn2);
        warn3 = (ImageView) findViewById(R.id.open_iv_warn3);
        warn4 = (ImageView) findViewById(R.id.open_iv_warn4);
        warn5 = (ImageView) findViewById(R.id.open_iv_warn5);
        warn6 = (ImageView) findViewById(R.id.open_iv_warn6);
        warn7 = (ImageView) findViewById(R.id.open_iv_warn7);
        //初始化
        Glide.with(this)
                .load(R.drawable.ic_lease)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(logo);
        //设置属性
        String tipText = "我已阅读并同意《安租商家协议》";
        checkBox.setText(StringDesignUtil.getSpanned(tipText, "《安租商家协议》", "#FF7F00"));
    }
}
