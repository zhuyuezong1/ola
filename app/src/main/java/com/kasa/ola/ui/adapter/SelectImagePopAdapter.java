package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.ui.listener.OnItemClickListener;

import java.util.List;

public class SelectImagePopAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<TextBean> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SelectImagePopAdapter(Context context, List<TextBean> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SelectImagePopAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_select_image, viewGroup, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        SelectImagePopAdapterViewHolder holder = (SelectImagePopAdapterViewHolder) viewHolder;
        TextBean textBean = list.get(position);
        holder.tv_content.setText(textBean.getContent());
        holder.tv_content.setTextColor(context.getColor(textBean.getColor()));
        holder.ll_select_image_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }
    private class SelectImagePopAdapterViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_select_image_item;
        public TextView tv_content;
        public SelectImagePopAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
            ll_select_image_item = itemView.findViewById(R.id.ll_select_image_item);
        }
    }
}

