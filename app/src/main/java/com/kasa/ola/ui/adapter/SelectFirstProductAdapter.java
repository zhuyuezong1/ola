package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.ui.SelectFirstProductActivity;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;

import org.w3c.dom.Text;

import java.util.List;

public class SelectFirstProductAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Object> list;
    public SelectFirstProductAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectFirstProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_select_first_product,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SelectFirstProductViewHolder viewHolder = (SelectFirstProductViewHolder) holder;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (position==0 || position==1){
            if (position%2==0){
                lp.setMargins(DisplayUtils.dip2px(context,12), -DisplayUtils.dip2px(context,88), DisplayUtils.dip2px(context,6), DisplayUtils.dip2px(context,6));
            }else {
                lp.setMargins(DisplayUtils.dip2px(context,6), -DisplayUtils.dip2px(context,88), DisplayUtils.dip2px(context,12), DisplayUtils.dip2px(context,6));
            }
        }else if(position>getItemCount()-3){
            if (position%2==0){
                lp.setMargins(DisplayUtils.dip2px(context,12), DisplayUtils.dip2px(context,6), DisplayUtils.dip2px(context,6), DisplayUtils.dip2px(context,12));
            }else {
                lp.setMargins(DisplayUtils.dip2px(context,6), DisplayUtils.dip2px(context,6), DisplayUtils.dip2px(context,12), DisplayUtils.dip2px(context,12));
            }
        } else{
            if (position%2==0){
                lp.setMargins(DisplayUtils.dip2px(context,12), DisplayUtils.dip2px(context,6), DisplayUtils.dip2px(context,6), DisplayUtils.dip2px(context,6));
            }else {
                lp.setMargins(DisplayUtils.dip2px(context,6), DisplayUtils.dip2px(context,6), DisplayUtils.dip2px(context,12), DisplayUtils.dip2px(context,6));
            }

        }
        viewHolder.ll_select_first_item.setLayoutParams(lp);
        ViewGroup.LayoutParams layoutParams = viewHolder.iv_product.getLayoutParams();
        int imageWidth = DisplayUtils.getScreenWidth(context)-DisplayUtils.dip2px(context,12)*3;
        layoutParams.width = imageWidth/2;
        layoutParams.height = imageWidth/2;
        ImageLoaderUtils.imageLoadRound(context,"",viewHolder.iv_product,R.mipmap.alipay,false,8);
    }

    @Override
    public int getItemCount() {
        return 20;
//        return list==null?0:list.size();
    }
    private class SelectFirstProductViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout ll_select_first_item;
        private ImageView iv_product;
        private TextView tv_watch_num;
        private TextView tv_product_name;
        public SelectFirstProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_select_first_item = itemView.findViewById(R.id.ll_select_first_item);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_watch_num = itemView.findViewById(R.id.tv_watch_num);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
        }
    }
}
