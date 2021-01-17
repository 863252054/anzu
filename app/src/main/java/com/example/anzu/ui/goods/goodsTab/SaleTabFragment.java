package com.example.anzu.ui.goods.goodsTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anzu.R;
import com.example.anzu.ui.goods.goodsTab.goodsData.GoodsAdapter;
import com.example.anzu.ui.goods.goodsTab.goodsData.GoodsItem;

import java.util.ArrayList;
import java.util.List;

public class SaleTabFragment extends Fragment {
    private GoodsAdapter adapter;
    private List<GoodsItem> goodsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goods_tab_sale, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        initData(); //初始化商品列表
        adapter = new GoodsAdapter(getActivity(),goodsList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void initData(){
        for(int i = 0 ; i < 6; i++){
            goodsList.add(new GoodsItem("http://yuan619.xyz/anzu/123123-goodsCover.jpg","任天堂游戏机switch家庭游戏亲子游戏机","编码：2178A","￥ 34.00/天","库存： 68"));
        }
    }
}
