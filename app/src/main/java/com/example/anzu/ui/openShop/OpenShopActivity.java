package com.example.anzu.ui.openShop;

import android.Manifest;
import android.app.Activity;
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
import com.example.anzu.ui.baiduMap.BaiduMapActivity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class OpenShopActivity extends AppCompatActivity implements View.OnClickListener {
    //AK P6Hy0f7PHEo0A13_ow3-0_OGvYdFibL8r4eEicIg
    //SK b4_OZ4LFs_wHwGoYVHPbVDZhsKGP_7HmzBqmdbZp
    //bucket yuan619

    public static final int RC_TAKE_PHOTO = 1; //拍照
    public static final int RC_CHOOSE_PHOTO = 2; //相册
    public static final int RC_LOCATION = 3; //位置权限
    private final int REQUEST_EXTERNAL_STORAGE = 4; //SD卡写入
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
    private ImageView openMap;
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
        //申请文件读取权限
        verifyStoragePermissions(this);

        //handler
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Constants.OK:
                        Intent intent = new Intent(OpenShopActivity.this, MainActivity.class);
                        startActivity(intent);
                        OpenShopActivity.this.finish();
                        break;
                    case Constants.FAIL:
                        Toast.makeText(OpenShopActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(OpenShopActivity.this, "提交成功，信息上传中，请稍候...", Toast.LENGTH_LONG).show();
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
        openMap.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(OpenShopActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION);
                    }
                }
        );
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

    @Override
    protected void onResume() {
        super.onResume();
        address.setText(Constants.location);
        warn5.setVisibility(View.GONE);
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

    // 底部弹出
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

    // 权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_TAKE_PHOTO:   //拍照权限申请回调
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //已授权
                    takePhoto();
                } else {
                    //未授权
                    Log.i("相机权限回调", "被拒绝了！");
                }
                break;
            case RC_CHOOSE_PHOTO:   //相册选择照片权限申请返回
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhoto();
                } else {
                    Log.i("相册权限回调", "被拒绝了！");
                }
                break;
            case RC_LOCATION:
                if(grantResults.length>0 && grantResults[0] == PERMISSION_GRANTED){
                    Intent intent = new Intent(OpenShopActivity.this, BaiduMapActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    //调用相册获取图片
    private void choosePhoto() {
        // 跳转到相册
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // 设置图片类型
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, RC_CHOOSE_PHOTO);
    }

    //调用拍照
    private void takePhoto() {
        // 跳转至相机
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 新建目录
        File fileDir = new File(Environment.getExternalStorageDirectory() + File.separator + "uploadImage" + File.separator);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        // 设置图片名
        File photoFile = new File(fileDir,  System.currentTimeMillis() + ".jpeg");
        tempPhotoPath = photoFile.getAbsolutePath();
        imageUri = FileProvider7.getUriForFile(this, photoFile);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentToTakePhoto, RC_TAKE_PHOTO);
    }

    //回调，处理图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case RC_CHOOSE_PHOTO:
                System.out.println("resultcode" + resultCode);
                if (resultCode == 0) {
                    return;
                } else {
                    Uri uri = data.getData(); //获取对应照片文件夹的uri
                    String filePath = FileUtil.getFilePathByUri(this, uri);
                    MyApplication myApplication = (MyApplication) getApplication();
                    myApplication.setLogoPath(filePath);
                    if (!TextUtils.isEmpty(filePath)) {
                        //设置页面上图片的载入状态图片和错误状态图片
                        RequestOptions requestOptions1 = new RequestOptions().placeholder(R.drawable.ic_lease).error(R.drawable.ic_lease);
                        RequestOptions requestOptions2 = new RequestOptions().placeholder(R.drawable.ic_plus).error(R.drawable.ic_plus);
                        if (currentPic == logo) { //显示logo在页面上
                            localUrl = filePath;
                            Glide.with(this)
                                    .load(filePath)
                                    .apply(requestOptions1.bitmapTransform(new CircleCrop())) //显示为圆形
                                    .into(logo);
                        } else { //显示营业执照在页面上
                            licenseUrl = filePath;
                            warn6.setVisibility(View.GONE);
                            Glide.with(this)
                                    .load(filePath)
                                    .apply(requestOptions2)
                                    .into(license);
                        }
                    }
                }
                break;
            case RC_TAKE_PHOTO:
                RequestOptions requestOptions1 = new RequestOptions().placeholder(R.drawable.ic_lease).error(R.drawable.ic_lease);
                RequestOptions requestOptions2 = new RequestOptions().placeholder(R.drawable.ic_plus).error(R.drawable.ic_plus);
                //显示图片
                if (resultCode == 0) {
                    return;
                } else {
                    if (currentPic == logo) {
                        localUrl = tempPhotoPath;
                        Glide.with(this)
                                .load(tempPhotoPath)
                                .apply(requestOptions1.bitmapTransform(new CircleCrop()))
                                .into(logo);
                    } else {
                        licenseUrl = tempPhotoPath;
                        warn6.setVisibility(View.GONE);
                        Glide.with(this)
                                .load(tempPhotoPath)
                                .apply(requestOptions2)
                                .into(license);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //上传图片到七牛云
    protected void uploadPic(String data) {
        //基础配置
        Configuration config = new Configuration.Builder()
                .useHttps(true)               // 是否使用https上传域名
                .zone(FixedZone.zone0)        // 设置区域。
                .build();

        UploadManager uploadManager = new UploadManager(config); // UploadManager对象只需要创建一次重复使用

        //设置上传文件的url
        MyApplication myApplication = (MyApplication) getApplication();
        String key = "anzu/" + myApplication.getUid() + "-logo.jpg"; //在服务器的文件名
        upUrl = "http://yuan619.xyz/" + key;
        // 用七牛云账户的AK SK生成上传凭证token
        String token = Auth.create("P6Hy0f7PHEo0A13_ow3-0_OGvYdFibL8r4eEicIg", "b4_OZ4LFs_wHwGoYVHPbVDZhsKGP_7HmzBqmdbZp").uploadToken("yuan619");
        /**
         * 调用put方法上传
         * 第一个参数 data：上传图片的路径
         * 第二个参数 key：字符串，图片在七牛云上的名称，具有唯一性
         * 第三个参数 token：根据开发者的 AK和SK 生成的token
         * 第四个参数：UpCompletionHandler的实例，有个回调方法
         */
        uploadManager.put(
                        data, key, token,
                        new UpCompletionHandler() {
                            /**
                             * 回调方法
                             * @param key 设置的 key 也就是文件名
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
        openMap = (ImageView)findViewById(R.id.open_location_map);
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

    //文件读取权限申请
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    public  void verifyStoragePermissions(Activity activity) {
        // 判断写入权限
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，则弹窗
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
