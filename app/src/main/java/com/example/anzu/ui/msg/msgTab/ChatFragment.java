package com.example.anzu.ui.msg.msgTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anzu.R;
import com.example.anzu.ui.goods.goodsTab.goodsData.GoodsAdapter;
import com.example.anzu.ui.goods.goodsTab.goodsData.GoodsItem;
import com.example.anzu.ui.msg.msgTab.chatData.ChatAdapter;
import com.example.anzu.ui.msg.msgTab.chatData.ChatItem;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private ChatAdapter adapter;
    private List<ChatItem> chatList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msg_chat, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        initData(); //初始化商品列表
        adapter = new ChatAdapter(getActivity(),chatList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void initData(){
        for(int i = 0 ; i < 12; i++){
            chatList.add(new ChatItem(R.drawable.my_head,"山水之间","游戏机里的游戏多吗","20:22",R.drawable.ic_1_pupple));
        }
    }
}