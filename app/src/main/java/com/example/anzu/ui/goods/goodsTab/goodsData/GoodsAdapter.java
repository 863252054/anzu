package com.example.anzu.ui.goods.goodsTab.goodsData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.anzu.R;

import java.util.List;

public class GoodsAdapter extends RecyclerView.Adapter <GoodsAdapter.MyHolder>{
    Context context;
    private List<GoodsItem> list;

    public GoodsAdapter(Context context, List<GoodsItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.goods_list_item_layout,parent,false);
        MyHolder holder = new MyHolder (view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        GoodsItem item = list.get (position);
        Glide.with(context).
                load(item.getImageSource()).
                into(holder.img);
        holder.title.setText (item.getTitle ());
        holder.typeName.setText (item.getTypeName ());
        holder.priceNum.setText (item.getPriceNum ());
        holder.saveCount.setText (item.getSaveCount ());
    }

    @Override
    public int getItemCount() {
        return list.size ();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        TextView typeName;
        TextView priceNum;
        TextView saveCount;

        public MyHolder(View itemView) {
            super (itemView);
            img = itemView.findViewById (R.id.goods_img);
            title = itemView.findViewById (R.id.goods_title);
            typeName = itemView.findViewById (R.id.goods_type_name);
            priceNum = itemView.findViewById (R.id.goods_price_num);
            saveCount = itemView.findViewById (R.id.goods_save_count);
        }
    }
}