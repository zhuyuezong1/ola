package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class ImageShowAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<String> list;
    private ItemImageClickListener itemImageViewClickListener;
    public void setItemImageViewClickListener(ItemImageClickListener itemImageViewClickListener) {
        this.itemImageViewClickListener = itemImageViewClickListener;
    }

    public ImageShowAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ImageShowViewHolder(LayoutInflater.from(context).inflate(R.layout.item_image_show,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final ImageShowViewHolder holder = (ImageShowViewHolder) viewHolder;
        String imageUrl = list.get(position);
        ViewGroup.LayoutParams layoutParams = holder.iv_comment_image.getLayoutParams();
        int width = DisplayUtils.getScreenWidth(context) - DisplayUtils.dip2px(context, 12) * 7;
        layoutParams.width = width/4;
        layoutParams.height = width/4;
        ImageLoaderUtils.imageLoadRound(context,imageUrl,holder.iv_comment_image);
        holder.iv_comment_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemImageViewClickListener!=null){
                    itemImageViewClickListener.onItemImageClick(context,holder.iv_comment_image,position,list);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
//        return 4;
        return list==null?0:list.size();
    }
    private class ImageShowViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_comment_image;
        public ImageShowViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_comment_image = itemView.findViewById(R.id.iv_comment_image);
        }
    }
    public interface ItemImageClickListener<T> {
        void onItemImageClick(Context context, ImageView imageView, int index, List<T> list);
    }
}
