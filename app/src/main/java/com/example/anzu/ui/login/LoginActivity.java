package com.example.anzu.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anzu.Constants;
import com.example.anzu.MyApplication;
import com.example.anzu.MainActivity;
import com.example.anzu.R;
import com.example.anzu.bean.ShopUser;
import com.example.anzu.query.GetShopQuery;
import com.example.anzu.query.LoginQuery;
import com.example.anzu.ui.openShop.OpenShopActivity;
import com.example.anzu.ui.privacy.PrivacyActivity;
import com.example.anzu.ui.register.RegisterActivity;
import com.example.anzu.utils.StringDesignUtil;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private EditText cellphone;
    private EditText password;
    private TextView toRegister;
    private TextView loginTip;
    private TextView privacy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //初始化
        init();

        //handler
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Constants.OK:
                        MyApplication myApplication = (MyApplication) getApplication();
                        myApplication.setUid(((ShopUser) msg.obj).getUid());
                        Constants.uid = ((ShopUser) msg.obj).getUid();
                        break;
                    case Constants.FAIL:
                        Toast.makeText(LoginActivity.this, "手机号或密码错误", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.OPENED:
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        LoginActivity.this.finish();
                        startActivity(intent);
                        break;
                    case Constants.NO:
                        Intent intent2 = new Intent(LoginActivity.this, OpenShopActivity.class);
                        LoginActivity.this.finish();
                        startActivity(intent2);
                        break;
                }
            }
        };

        //监听登录按钮
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginQuery loginQuery = new LoginQuery(cellphone.getText().toString(),
                        password.getText().toString(), handler);
                Thread loginThread = new Thread(loginQuery);
                loginThread.start();
            }
        });

        //监听去注册按钮
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //监听点击《隐私政策》
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PrivacyActivity.class);
                startActivity(intent);
            }
        });

        //监听手机号输入
        cellphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (cellphone.getText().toString().length() == 0) {
                    loginBtn.setEnabled(false);
                    loginBtn.setAlpha((float) 0.6);
                } else {
                    if (password.getText().toString().length() != 0) {
                        loginBtn.setEnabled(true);
                        loginBtn.setAlpha((float) 1);
                    }
                }
            }
        });

        //监听密码输入
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (password.getText().toString().length() == 0) {
                    loginBtn.setEnabled(false);
                    loginBtn.setAlpha((float) 0.6);
                } else {
                    if (cellphone.getText().toString().length() != 0) {
                        loginBtn.setEnabled(true);
                        loginBtn.setAlpha((float) 1);
                    }
                }
            }
        });

    }
    //初始化
    public void init() {
        //绑定组件
        loginBtn = (Button) findViewById(R.id.login_btn_login);
        cellphone = (EditText) findViewById(R.id.login_et_cellphone);
        password = (EditText) findViewById(R.id.login_et_password);
        toRegister = (TextView) findViewById(R.id.login_tv_register);
        loginTip = (TextView) findViewById(R.id.login_tv_tip);
        privacy = (TextView) findViewById(R.id.login_tv_link);

        //设置属性
        String tipText = "登录即代表阅读并同意《用户服务协议》";
        loginTip.setText(StringDesignUtil.getSpanned(tipText, "《用户服务协议》", "#FF7F00"));
    }
}
