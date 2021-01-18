package com.example.anzu.ui.goods;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.anzu.Constants;
import com.example.anzu.R;
import com.example.anzu.bean.Goods;
import com.example.anzu.query.GetGoodsQuery;
import com.example.anzu.ui.goods.goodsTab.CheckTabFragment;
import com.example.anzu.ui.goods.goodsTab.SaleTabFragment;
import com.example.anzu.ui.goods.goodsTab.SoldoutTabFragment;
import com.example.anzu.ui.goods.goodsTab.goodsData.GoodsAdapter;
import com.example.anzu.ui.goods.goodsTab.goodsData.GoodsItem;
import com.example.anzu.ui.login.LoginActivity;
import com.example.anzu.ui.openShop.OpenShopActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Random;

public class GoodsFragment extends Fragment {

    private TabLayout goodsTabLayout;  //订单页的TabLayout
    private ViewPager goodsViewPager;  //订单页的ViewPager

    private String[] goodsTitles = new String[]{"销售中 6","审核中 6","已下架 6"};  //订单页的菜单标题
    private ArrayList<Fragment> goodsFragments = new ArrayList<>();  //订单页的页面列表容器
    private GoodsPagerAdapter goodsPagerAdapter;  //订单页的pagerAdapter

    private Button deliver;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_goods, container, false);
        goodsTabLayout = (TabLayout) root.findViewById(R.id.goods_tablayout);
        goodsViewPager = (ViewPager) root.findViewById(R.id.goods_viewpager);

        for(int i=0;i<goodsTitles.length;i++){
            goodsTabLayout.addTab(goodsTabLayout.newTab());
        }
        goodsFragments.add(new SaleTabFragment()); //进行中页
        goodsFragments.add(new CheckTabFragment()); //已完成页
        goodsFragments.add(new SoldoutTabFragment());  //问题单页

        //展示页的设置
        goodsTabLayout.setupWithViewPager(goodsViewPager,false);
        goodsPagerAdapter = new GoodsPagerAdapter(goodsFragments, getChildFragmentManager());
        goodsViewPager.setAdapter(goodsPagerAdapter);

        //更新分组数字信息
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int count;
                ArrayList<Goods> list = (ArrayList<Goods>) msg.obj;
                count = list.size()+6;
                goodsTitles[1]=new String("审核中 "+ count);
                for(int i=0;i<goodsTitles.length;i++){
                    goodsTabLayout.getTabAt(i).setText(goodsTitles[i]);
                }
            }
        };
        GetGoodsQuery getGoodsQuery = new GetGoodsQuery(handler, Constants.uid);
        Thread getGoodsThread = new Thread(getGoodsQuery);
        getGoodsThread.start();


        //点击上架新商品后跳转
        deliver = (Button)root.findViewById(R.id.goods_deliver_button);
        deliver.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), GoodsShelvesActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
        );

        return root;
    }
}