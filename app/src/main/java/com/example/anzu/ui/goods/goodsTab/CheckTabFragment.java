package com.example.anzu.ui.goods.goodsTab;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.anzu.Constants;
import com.example.anzu.R;
import com.example.anzu.bean.Goods;
import com.example.anzu.bean.Shop;
import com.example.anzu.query.GetGoodsQuery;
import com.example.anzu.query.GetShopQuery;
import com.example.anzu.ui.goods.goodsTab.goodsData.GoodsAdapter;
import com.example.anzu.ui.goods.goodsTab.goodsData.GoodsItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class CheckTabFragment extends Fragment {
    private GoodsAdapter adapter;
    private List<GoodsItem> goodsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goods_tab_sale, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        initData(); //初始化商品列表
        //handler
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<Goods> list = (ArrayList<Goods>) msg.obj;
                for (int i = 0; i < list.size(); i++) {
                    Goods item = list.get(i);
                    String key = "?v=" + new Random().nextLong();
                    goodsList.add(new GoodsItem(item.getGoodsCover() + key,
                            item.getGoodsMain() + item.getGoodsSub(),
                            item.getGoodsDetail(),
                            "￥" + item.getGoodsPriceContent() + "/" + item.getGoodsPriceDate(),
                            "库存：" + item.getGoodsInventory()
                            ));
                }
                adapter = new GoodsAdapter(getActivity(), goodsList);
                recyclerView.setAdapter(adapter);
            }
        };
        GetGoodsQuery getGoodsQuery = new GetGoodsQuery(handler, Constants.uid);
        Thread getGoodsThread = new Thread(getGoodsQuery);
        getGoodsThread.start();

        return view;
    }

    public void initData() {
        for (int i = 0; i < 6; i++) {
            goodsList.add(new GoodsItem("http://yuan619.xyz/anzu/123123-goodsCover.jpg", "任天堂游戏机switch家庭游戏亲子游戏机", "编码：2178A", "￥ 500.00/月", "库存： 100"));
        }
    }
}
