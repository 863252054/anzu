package com.example.anzu.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anzu.Constants;
import com.example.anzu.MyApplication;
import com.example.anzu.MainActivity;
import com.example.anzu.R;
import com.example.anzu.bean.ShopUser;
import com.example.anzu.query.LoginQuery;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginBtn;
    private EditText cellphone;
    private EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

    }
    //初始化
    public void init() {
        //绑定组件
        loginBtn = (Button) findViewById(R.id.login_btn_login);
        cellphone = (EditText) findViewById(R.id.login_et_cellphone);
        password = (EditText) findViewById(R.id.login_et_password);

        //handler
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Constants.OK:
                        MyApplication myApplication = (MyApplication) getApplication();
                        myApplication.setUid(((ShopUser) msg.obj).getUid());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        break;
                    case Constants.FAIL:
                        Toast.makeText(LoginActivity.this, "手机号或密码错误", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.NET:
                        Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        //登录
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginQuery loginQuery = new LoginQuery(cellphone.getText().toString(),
                        password.getText().toString(), handler);
                Thread loginThread = new Thread(loginQuery);
                loginThread.start();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
    //点击事件
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.login_btn_login:
//                LoginQuery loginQuery = new LoginQuery(cellphone.getText().toString(),
//                        password.getText().toString(), handler);
//                Thread loginThread = new Thread(loginQuery);
//                loginThread.start();
//        }
//    }
}
