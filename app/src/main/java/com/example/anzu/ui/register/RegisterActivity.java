package com.example.anzu.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anzu.Constants;
import com.example.anzu.MyApplication;
import com.example.anzu.R;
import com.example.anzu.bean.ShopUser;
import com.example.anzu.query.RegisterQuery;
import com.example.anzu.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button registerBtn;
    private Button codeBtn;
    private EditText cellphone;
    private EditText code;
    private EditText password;
    private TextView toLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

    }
    //初始化
    public void init() {
        //绑定组件
        registerBtn = (Button) findViewById(R.id.register_btn_register);
        codeBtn = (Button) findViewById(R.id.register_btn_code);
        cellphone = (EditText) findViewById(R.id.register_et_cellphone);
        code = (EditText) findViewById(R.id.register_et_code);
        password = (EditText) findViewById(R.id.register_et_password);
        toLogin = (TextView) findViewById(R.id.register_tv_login);

        //handler
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Constants.OK:
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        RegisterActivity.this.finish();
                        break;
                    case Constants.FAIL:
                        Toast.makeText(RegisterActivity.this, "该手机号已注册，请直接登录", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.NET:
                        Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        //注册
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterQuery registerQuery = new RegisterQuery(handler,
                        cellphone.getText().toString(),
                        code.getText().toString(),
                        password.getText().toString());
                Thread loginThread = new Thread(registerQuery);
                loginThread.start();
            }
        });
        //获取验证码
        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeBtn.setEnabled(false);
                codeBtn.setText("已发送");
                codeBtn.setBackgroundResource(R.drawable.background_button_disable);
                codeBtn.getBackground().setAlpha(150);
            }
        });
        //去登录
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
