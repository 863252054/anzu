package com.example.anzu.ui.goods;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.anzu.R;
import com.example.anzu.ui.goods.goodsTab.CheckTabFragment;
import com.example.anzu.ui.goods.goodsTab.SaleTabFragment;
import com.example.anzu.ui.goods.goodsTab.SoldoutTabFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class GoodsFragment extends Fragment {

    private TabLayout goodsTabLayout;  //订单页的TabLayout
    private ViewPager goodsViewPager;  //订单页的ViewPager
    private String[] goodsTitles = new String[]{"销售中 6","审核中 6","已下架 6"};  //订单页的菜单标题
    private ArrayList<Fragment> goodsFragments = new ArrayList<>();  //订单页的页面列表容器
    private GoodsPagerAdapter goodsPagerAdapter;  //订单页的pagerAdapter

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

        for(int i=0;i<goodsTitles.length;i++){
            goodsTabLayout.getTabAt(i).setText(goodsTitles[i]);
        }
        return root;
    }
}