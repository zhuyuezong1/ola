package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;

import java.util.List;

public class ShopRecommendAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Object> list;
    public ShopRecommendAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShopRecommendViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home_shop_grid,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ShopRecommendViewHolder viewHolder = (ShopRecommendViewHolder) holder;
    }

    @Override
    public int getItemCount() {
        return 4;
//        return list==null?0:list.size();
    }
    private class ShopRecommendViewHolder extends RecyclerView.ViewHolder{
        public ShopRecommendViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
