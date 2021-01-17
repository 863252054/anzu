package com.example.anzu.ui.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.anzu.Constants;
import com.example.anzu.MainActivity;
import com.example.anzu.MyApplication;
import com.example.anzu.R;
import com.example.anzu.bean.Shop;
import com.example.anzu.query.GetShopQuery;
import com.example.anzu.ui.login.LoginActivity;
import com.example.anzu.ui.register.RegisterActivity;

import java.util.Random;

public class MyFragment extends Fragment {
    private TextView name;
    private ImageView head;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root1 = inflater.inflate(R.layout.fragment_my, container, false);
        //绑定组件
        head = (ImageView) root1.findViewById(R.id.iv_my_head);
        name = (TextView) root1.findViewById(R.id.tv_username);
        Glide.with(getActivity())
                .load(Constants.shop.getShopLogo())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(head);
        name.setText(Constants.shop.getShopName());
        return root1;
    }
}