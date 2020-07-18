package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.ui.ProductDetailsActivity;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.LogUtil;

import java.util.List;

public class ProductDetailsAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<String> list;
    public ProductDetailsAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductDetailsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product_detail_image,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductDetailsViewHolder viewHolder = (ProductDetailsViewHolder) holder;
        String imageUrl = list.get(position);
//        int widthPixels = DisplayUtils.getScreenWidth(context);
//        ViewGroup.LayoutParams layoutParams = viewHolder.iv_image.getLayoutParams();
//        layoutParams.width=widthPixels;
//        layoutParams.height=widthPixels;
//        ImageLoaderUtils.imageLoad(context,imageUrl,viewHolder.iv_image);
        ImageLoaderUtils.imageTrendsLoad(context,imageUrl,viewHolder.iv_image);
    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }

    private class ProductDetailsViewHolder extends RecyclerView.ViewHolder{

        private ImageView iv_image;

        public ProductDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
        }
    }
}
