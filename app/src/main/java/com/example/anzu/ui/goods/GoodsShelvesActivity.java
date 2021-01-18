package com.example.anzu.ui.goods;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.anzu.Constants;
import com.example.anzu.MainActivity;
import com.example.anzu.MyApplication;
import com.example.anzu.R;
import com.example.anzu.TestQuery;
import com.example.anzu.query.UpGoodsQuery;
import com.example.anzu.query.UpShopQuery;
import com.example.anzu.ui.components.TakePhotoPopWin;
import com.example.anzu.ui.openShop.OpenShopActivity;
import com.example.anzu.utils.FileProvider7;
import com.example.anzu.utils.FileUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.util.Auth;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GoodsShelvesActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,View.OnClickListener {

    public static final int RC_TAKE_PHOTO = 1; //拍照
    public static final int RC_CHOOSE_PHOTO = 2; //相册
    private String tempPhotoPath;
    private Uri imageUri;
    private String localUrl = "";
    private String upUrl = "";
    private String goodsCoverUrl = "";
    private boolean flag = false;
    private boolean selected = false;
    private boolean checked = false;

    //图像处理组件
    private TakePhotoPopWin takePhotoPopWin;
    private ImageView currentPic; //当前选中的图片组件
    private ImageView goodsCover; //logo

    //其他组件
    private EditText goodsMain;
    private EditText goodsSub;
    private Spinner mGoodsCategorySpinner;
    private EditText goodsDetail;
    private EditText goodsPriceContent;
    private Spinner mGoodsPriceDateSpinner = null;
    private EditText goodsInventory;
    private EditText goodsRules;
    private String goodsMode = "";
    private CheckBox modeOne;
    private CheckBox modeTwo;
    private CheckBox modeThree;
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
        setContentView(R.layout.activity_goods_shelves);
        //初始化
        init();

        //handler
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Constants.OK:
                        Intent intent = new Intent(GoodsShelvesActivity.this, MainActivity.class);
                        startActivity(intent);
                        GoodsShelvesActivity.this.finish();
                        break;
                    case Constants.FAIL:
                        Toast.makeText(GoodsShelvesActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.NET:
                        Toast.makeText(GoodsShelvesActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        //申请文件读取权限
        verifyStoragePermissions(this);

        //点击按钮事件
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;
                if (goodsMain.getText().toString().length() == 0) {
                    warn1.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (goodsSub.getText().toString().length() == 0) {
                    warn2.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (goodsDetail.getText().toString().length() == 0) {
                    warn3.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (goodsPriceContent.getText().toString().length() == 0) {
                    warn4.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (goodsInventory.getText().toString().length() == 0) {
                    warn5.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (goodsRules.getText().toString().length() == 0) {
                    warn6.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (goodsCoverUrl =="") {
                    warn7.setVisibility(View.VISIBLE);
                    flag = 1;
                }
                if (flag == 1) {
                    Toast.makeText(GoodsShelvesActivity.this, "提交失败，请完善或修改红标信息", Toast.LENGTH_SHORT).show();
                } else {
                    uploadPic(localUrl);
                    MyApplication myApplication = (MyApplication) getApplication();
                    Map<String, String> params = new HashMap<>();
                    params.put("uid", Constants.uid);
                    params.put("goodsMain", goodsMain.getText().toString());
                    params.put("goodsSub", goodsSub.getText().toString());
                    params.put("goodsType", mGoodsCategorySpinner.getSelectedItem().toString());
                    params.put("goodsDetail", goodsDetail.getText().toString());
                    params.put("goodsPriceContent", goodsPriceContent.getText().toString());
                    params.put("goodsPriceDate", mGoodsPriceDateSpinner.getSelectedItem().toString());
                    params.put("goodsRule", goodsRules.getText().toString());
                    params.put("goodsInventory", goodsInventory.getText().toString());
                    if(modeOne.isChecked()){goodsMode +="1";}
                    if(modeTwo.isChecked()){goodsMode +="2";}
                    if(modeThree.isChecked()){goodsMode +="3";}
                    params.put("goodsMode", goodsMode);
                    params.put("goodsCover", upUrl);
                    UpGoodsQuery upGoodsQuery = new UpGoodsQuery(handler, params);
                    Thread upGoodsThread = new Thread(upGoodsQuery);
                    upGoodsThread.start();
                }
            }
        });

        //感叹号的隐藏与出现
        goodsMain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (goodsMain.getText().toString().length() != 0) {
                    warn1.setVisibility(View.GONE);
                } else {
                    warn1.setVisibility(View.VISIBLE);
                }
            }
        });

        goodsSub.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (goodsSub.getText().toString().length() != 0) {
                    warn2.setVisibility(View.GONE);
                } else {
                    warn2.setVisibility(View.VISIBLE);
                }
            }
        });

        goodsDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (goodsDetail.getText().toString().length() != 0) {
                    warn3.setVisibility(View.GONE);
                } else {
                    warn3.setVisibility(View.VISIBLE);
                }
            }
        });

        goodsPriceContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (goodsPriceContent.getText().toString().length() != 0) {
                    warn4.setVisibility(View.GONE);
                } else {
                    warn4.setVisibility(View.VISIBLE);
                }
            }
        });

        goodsInventory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (goodsInventory.getText().toString().length() != 0) {
                    warn5.setVisibility(View.GONE);
                } else {
                    warn5.setVisibility(View.VISIBLE);
                }
            }
        });

        goodsRules.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (goodsRules.getText().toString().length() != 0) {
                    warn6.setVisibility(View.GONE);
                } else {
                    warn6.setVisibility(View.VISIBLE);
                }
            }
        });

        goodsCover.setOnClickListener(this);


    }


    //点击图片后的响应事件
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.goods_cover){
            currentPic = goodsCover;
            showPopFormBottom(v);
        }
    }

    //底部弹出
    public void showPopFormBottom(View view) {
        takePhotoPopWin = new TakePhotoPopWin(GoodsShelvesActivity.this, onClickListener);
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
                    if (ContextCompat.checkSelfPermission(GoodsShelvesActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //未授权，申请授权
                        ActivityCompat.requestPermissions(GoodsShelvesActivity.this, new String[]{Manifest.permission.CAMERA}, RC_TAKE_PHOTO);
                    } else {
                        //已授权，拍照
                        takePhoto();
                    }
                    break;
                case R.id.btn_pick_photo:
                    takePhotoPopWin.dismiss();
                    System.out.println("choosePhoto");
                    if (ContextCompat.checkSelfPermission(GoodsShelvesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        //未授权，申请授权
                        ActivityCompat.requestPermissions(GoodsShelvesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_CHOOSE_PHOTO);
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
            case RC_TAKE_PHOTO:   //拍照权限申请返回
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
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
                if (resultCode == 0) {
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
                            goodsCoverUrl = filePath;
                            localUrl = filePath;
                            warn7.setVisibility(View.GONE);
                            Glide.with(this)
                                    .load(filePath)
                                    .into(goodsCover);

                    }
                }
                break;
            case RC_TAKE_PHOTO:
                RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_lease);
                if (resultCode == 0) {
                    return;
                }else{
                    goodsCoverUrl = tempPhotoPath;
                    localUrl = tempPhotoPath;
                    warn7.setVisibility(View.GONE);
                    Glide.with(this)
                            .load(tempPhotoPath)
                            .apply(requestOptions)
                            .into(goodsCover);
                }
                break;
        }
    }

    //spinner选中后显示Toast
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String content = parent.getItemAtPosition(position).toString();
        switch (parent.getId()) {
            case R.id.goods_category_content:
                Toast.makeText(GoodsShelvesActivity.this, "您选择的商品类别是：" + content, Toast.LENGTH_SHORT).show();
                break;
            case R.id.goods_price_date:
                Toast.makeText(GoodsShelvesActivity.this, "您选择按" + content + "出租", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    public void init(){
        //初始化两个spinner
        mGoodsCategorySpinner = (Spinner) findViewById(R.id.goods_category_content);
        mGoodsPriceDateSpinner = (Spinner) findViewById(R.id.goods_price_date);
        String[] arr1 = {"电子数码", "服装饰品", "居家生活", "电子娱乐", "其他类别"};
        String[] arr2 = {"天", "周", "月", "季", "年"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr2);
        mGoodsCategorySpinner.setAdapter(adapter1);
        mGoodsPriceDateSpinner.setAdapter(adapter2);
        mGoodsCategorySpinner.setOnItemSelectedListener(this);
        mGoodsPriceDateSpinner.setOnItemSelectedListener(this);

        goodsMain = (EditText)findViewById(R.id.goods_main_content);
        goodsSub = (EditText)findViewById(R.id.goods_sub_content);
        goodsDetail = (EditText)findViewById(R.id.goods_detail_content);
        goodsPriceContent = (EditText)findViewById(R.id.goods_price_content);
        goodsInventory = (EditText)findViewById(R.id.goods_inventory_content);
        goodsRules = (EditText)findViewById(R.id.goods_rules_content);
        modeOne = (CheckBox) findViewById(R.id.goods_modes_one);
        modeTwo = (CheckBox) findViewById(R.id.goods_modes_two);
        modeThree = (CheckBox) findViewById(R.id.goods_modes_three);
        goodsCover = (ImageView) findViewById(R.id.goods_cover);
        commit = (Button)findViewById(R.id.goods_btn_commit);
        warn1 = (ImageView) findViewById(R.id.goods_iv_warn1);
        warn2 = (ImageView) findViewById(R.id.goods_iv_warn2);
        warn3 = (ImageView) findViewById(R.id.goods_iv_warn3);
        warn4 = (ImageView) findViewById(R.id.goods_iv_warn4);
        warn5 = (ImageView) findViewById(R.id.goods_iv_warn5);
        warn6 = (ImageView) findViewById(R.id.goods_iv_warn6);
        warn7 = (ImageView) findViewById(R.id.goods_iv_warn7);
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
        String key = "anzu/" + Constants.uid +Math.random()+ "-goodsCover.jpg"; //在服务器的文件名
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

    //文件读取权限申请
    private final int REQUEST_EXTERNAL_STORAGE = 4;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    public  void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}