package com.example.anzu.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anzu.Constants;
import com.example.anzu.MainActivity;
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

    private String realCellphone; //保存真实的手机号，防止用户获取验证码后再次修改手机号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

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

        //监听注册按钮
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code.getText().toString().length() != 6) {
                    Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                } else {
                    RegisterQuery registerQuery = new RegisterQuery(handler,
                            realCellphone,
                            code.getText().toString(),
                            password.getText().toString());
                    Thread loginThread = new Thread(registerQuery);
                    loginThread.start();
                }
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
                    registerBtn.setEnabled(false);
                    registerBtn.setAlpha((float) 0.6);
                } else {
                    if (code.getText().toString().length() != 0
                            && password.getText().toString().length() != 0) {
                        registerBtn.setEnabled(true);
                        registerBtn.setAlpha((float) 1);
                    }
                }
                //是否到11位可发送验证码
                if (cellphone.getText().toString().length() != 11) {
                    codeBtn.setEnabled(false);
                    codeBtn.setAlpha((float) 0.6);
                } else {
                    codeBtn.setEnabled(true);
                    codeBtn.setAlpha((float) 1);
                }
            }
        });

        //监听获取验证码按钮
        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeBtn.setEnabled(false);
                codeBtn.setAlpha((float) 0.6);
                //60s重试倒计时
                timer.start();
                Toast.makeText(RegisterActivity.this, "短信验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
                realCellphone = cellphone.getText().toString();
            }
        });

        //监听验证码输入
        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (code.getText().toString().length() == 0) {
                    registerBtn.setEnabled(false);
                    registerBtn.setAlpha((float) 0.6);
                } else {
                    if (password.getText().toString().length() != 0
                        && cellphone.getText().toString().length() != 0) {
                        registerBtn.setEnabled(true);
                        registerBtn.setAlpha((float) 1);
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
                    registerBtn.setEnabled(false);
                    registerBtn.setAlpha((float) 0.6);
                } else {
                    if (code.getText().toString().length() != 0
                            && cellphone.getText().toString().length() != 0) {
                        registerBtn.setEnabled(true);
                        registerBtn.setAlpha((float) 1);
                    }
                }
            }
        });

        //监听去登录
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

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
    }

    //验证码发送后倒计时
    CountDownTimer timer = new CountDownTimer(60000, 1000) {
        public void onTick(long millisUntilFinished) {
            codeBtn.setText(millisUntilFinished / 1000 + "秒后重试");
        }
        @Override
        public void onFinish() {
            codeBtn.setText("获取验证码");
            if (cellphone.getText().toString().length() != 11) {
                codeBtn.setEnabled(false);
                codeBtn.setAlpha((float) 0.6);
            } else {
                codeBtn.setEnabled(true);
                codeBtn.setAlpha((float) 1);
            }
        }
    };

    @Override
    public void onClick(View v) {

    }
}
