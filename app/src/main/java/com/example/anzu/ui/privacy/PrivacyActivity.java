package com.example.anzu.ui.privacy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anzu.Constants;
import com.example.anzu.R;
import com.example.anzu.ui.login.LoginActivity;

public class PrivacyActivity extends AppCompatActivity {
    private ImageView back;
    private TextView contentTile;
    private TextView contentP1;
    private TextView contentP2;
    private TextView contentP3;
    private TextView contentP4;
    private TextView contentP5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        //组件绑定
        back = (ImageView) findViewById(R.id.privacy_iv_back);
        contentTile = (TextView) findViewById(R.id.privacy_content_title);
        contentP1 = (TextView) findViewById(R.id.privacy_p1_content);
        contentP2 = (TextView) findViewById(R.id.privacy_p2_content);
        contentP3 = (TextView) findViewById(R.id.privacy_p3_content);
        contentP4 = (TextView) findViewById(R.id.privacy_p4_content);
        contentP5 = (TextView) findViewById(R.id.privacy_p5_content);

        //初始化内容
        contentTile.setText(Constants.privacyTitle);
        contentP1.setText(Constants.privacyP1);
        contentP2.setText(Constants.privacyP2);
        contentP3.setText(Constants.privacyP3);
        contentP4.setText(Constants.privacyP4);
        contentP5.setText(Constants.privacyP5);

        //返回监听
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivacyActivity.this.finish();
            }
        });
    }
}
