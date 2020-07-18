package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.utils.BitmapUtils;
import com.kasa.ola.utils.DisplayUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShopImageAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<File> list;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;
    public ShopImageAdapter(Context context, List<File> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PublishImagesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_shop_image, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        PublishImagesViewHolder  holder = (PublishImagesViewHolder)viewHolder;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels- DisplayUtils.dip2px(context,10)*2;
        ViewGroup.LayoutParams layoutParams = holder.rl_publish.getLayoutParams();
        layoutParams.width=widthPixels/5;
        if (isShowAddItem(position))
        {
            holder.iv_delete.setVisibility(View.GONE);
            holder.item_iv_image.setImageResource(R.mipmap.shop_image_add);
        }else{
            holder.iv_delete.setVisibility(View.VISIBLE);
            Bitmap smallBitmap = BitmapUtils.getSmallBitmap(list.get(position).getPath());
            holder.item_iv_image.setImageBitmap(smallBitmap);
        }
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.deleteItem(position);
            }
        });
        holder.item_iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
//        return 0;
        if(list==null) {
            return 1;
        }else if(list.size()==5){
            return 5;
        }else{
            return list.size()+1;
        }
    }
    public void setList(List<File> list){
        this.list = list;
        this.notifyDataSetChanged();
    }
    /**
     * 判断当前下标是否是最大值
     * @param position  当前下标
     * @return
     */
    private boolean isShowAddItem(int position)
    {
        int size = list == null ? 0 : list.size();
        return position == size;
    }
    private class PublishImagesViewHolder extends RecyclerView.ViewHolder{

        public ImageView item_iv_image;
        public ImageView iv_delete;
        public RelativeLayout rl_publish;

        public PublishImagesViewHolder(View itemView) {
            super(itemView);
            item_iv_image = itemView.findViewById(R.id.item_iv_image);
            rl_publish = itemView.findViewById(R.id.rl_publish);
            iv_delete = itemView.findViewById(R.id.iv_delete);
        }

    }
    public interface OnItemClickListener{
        void onItemClick(int position);
        void deleteItem(int position);
    }
}
