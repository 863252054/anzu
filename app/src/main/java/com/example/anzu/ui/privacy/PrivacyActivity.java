package com.example.anzu.ui.privacy;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anzu.Constants;
import com.example.anzu.R;

public class PrivacyActivity extends AppCompatActivity {
    private TextView contentTile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        contentTile = (TextView) findViewById(R.id.privacy_content_title);

        contentTile.setText(Constants.privacyTitle);
    }
}
