package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.FoundItemBean;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class FindAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<FoundItemBean> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public FindAdapter(Context context, List<FoundItemBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FindViewHolder(LayoutInflater.from(context).inflate(R.layout.item_find,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FindViewHolder viewHolder = (FindViewHolder) holder;
        FoundItemBean foundItemBean = list.get(position);
        ViewGroup.LayoutParams layoutParams = viewHolder.item_iv_find.getLayoutParams();
        int width = DisplayUtils.getScreenWidth(context)-DisplayUtils.dip2px(context,12)*3;
        layoutParams.width = width/2;
        layoutParams.height = width/2;
        ImageLoaderUtils.imageLoad(context,foundItemBean.getImageUrl(),viewHolder.item_iv_find);
        viewHolder.tv_flag.setVisibility(TextUtils.isEmpty(foundItemBean.getSuppliersName())?View.GONE:View.VISIBLE);
        viewHolder.item_tv_title.setText(foundItemBean.getContent());
        viewHolder.tv_shop_name.setText(foundItemBean.getSuppliersName());
        viewHolder.tv_read_num.setText(foundItemBean.getReadNum());
        viewHolder.tv_comment_num.setText(foundItemBean.getCommentNum());
        viewHolder.item_product.setOnClickListener(new View.OnClickListener() {
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
//        return 10;
        return list==null?0:list.size();
    }

    private class FindViewHolder extends RecyclerView.ViewHolder {
        CardView item_product;
        TextView item_tv_title;
        TextView tv_shop_name;
        TextView tv_comment_num;
        TextView tv_read_num;
        TextView tv_flag;
        ImageView item_iv_find;
        private FindViewHolder(View itemView) {
            super(itemView);
            item_tv_title = itemView.findViewById(R.id.item_tv_title);
            tv_shop_name = itemView.findViewById(R.id.tv_shop_name);
            tv_comment_num = itemView.findViewById(R.id.tv_comment_num);
            tv_flag = itemView.findViewById(R.id.tv_flag);
            tv_read_num = itemView.findViewById(R.id.tv_read_num);
            item_iv_find = itemView.findViewById(R.id.item_iv_find);
            item_product = itemView.findViewById(R.id.item_product);
        }
    }
}
