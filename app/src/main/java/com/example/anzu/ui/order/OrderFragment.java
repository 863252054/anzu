package com.example.anzu.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.anzu.R;
import com.example.anzu.ui.order.orderTab.FinishTabFragment;
import com.example.anzu.ui.order.orderTab.ProblemTabFragment;
import com.example.anzu.ui.order.orderTab.UnderwayTabFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class OrderFragment extends Fragment {

    private TabLayout orderTabLayout;  //订单页的TabLayout
    private ViewPager orderViewPager;  //订单页的ViewPager
    private String[] orderTitles = new String[]{"进行中 1","已完成 1","问题单 1"};  //订单页的菜单标题
    private ArrayList<Fragment> orderFragments = new ArrayList<>();  //订单页的页面列表容器
    private OrderPagerAdapter orderPagerAdapter;  //订单页的pagerAdapter

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_order, container, false);
        orderTabLayout = (TabLayout) root.findViewById(R.id.order_tablayout);
        orderViewPager = (ViewPager) root.findViewById(R.id.order_viewpager);

        for(int i=0;i<orderTitles.length;i++){
            orderTabLayout.addTab(orderTabLayout.newTab());
        }
        orderFragments.add(new UnderwayTabFragment()); //进行中页
        orderFragments.add(new FinishTabFragment()); //已完成页
        orderFragments.add(new ProblemTabFragment());  //问题单页



        //展示页的设置
        orderTabLayout.setupWithViewPager(orderViewPager,false);
        orderPagerAdapter = new OrderPagerAdapter(orderFragments, getChildFragmentManager());
        orderViewPager.setAdapter(orderPagerAdapter);

        for(int i=0;i<orderTitles.length;i++){
            orderTabLayout.getTabAt(i).setText(orderTitles[i]);
        }

        return root;
    }


}
