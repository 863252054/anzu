package com.example.anzu.ui.msg.msgTab.chatData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anzu.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter <ChatAdapter.MyHolder>{
    Context context;
    private List<ChatItem> list;

    public ChatAdapter(Context context, List<ChatItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.chat_list_item_layout,parent,false);
        MyHolder holder = new MyHolder (view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ChatItem item = list.get (position);
        holder.img.setImageResource (item.getImageId());
        holder.title.setText (item.getTitle ());
        holder.content.setText (item.getContent ());
        holder.time.setText (item.getTime ());
        holder.msgCount.setImageResource (item.getMsgNum ());
    }

    @Override
    public int getItemCount() {
        return list.size ();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        TextView content;
        TextView time;
        ImageView msgCount;

        public MyHolder(View itemView) {
            super (itemView);
            img = itemView.findViewById (R.id.chat_head);
            title = itemView.findViewById (R.id.chat_name);
            content = itemView.findViewById (R.id.chat_content);
            time = itemView.findViewById (R.id.chat_time);
            msgCount = itemView.findViewById (R.id.chat_msg_num);
        }
    }
}