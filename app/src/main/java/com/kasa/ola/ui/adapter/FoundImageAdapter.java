package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.utils.BitmapUtils;
import com.kasa.ola.utils.DisplayUtils;

import java.io.File;
import java.util.List;

public class FoundImageAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<File> list;
    private int maxCount = 4;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    private OnItemClickListener onItemClickListener;
    public FoundImageAdapter(Context context, List<File> list) {
        this.context = context;
        this.list = list;
    }
    public FoundImageAdapter(Context context, List<File> list,int maxCount) {
        this.context = context;
        this.list = list;
        this.maxCount = maxCount;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PublishImagesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_found_image, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        PublishImagesViewHolder  holder = (PublishImagesViewHolder)viewHolder;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels- DisplayUtils.dip2px(context,8)*7;
        ViewGroup.LayoutParams layoutParams = holder.rl_publish.getLayoutParams();
        layoutParams.width=widthPixels/4;
        layoutParams.height=widthPixels/4;
        ViewGroup.LayoutParams layoutParams1 = holder.rl_upload.getLayoutParams();
        layoutParams1.width=widthPixels/4;
        layoutParams1.height=widthPixels/4;
        if (isShowAddItem(position))
        {
            holder.iv_delete.setVisibility(View.GONE);
            holder.item_iv_image.setImageResource(R.mipmap.shop_image_add);
            holder.item_iv_image.setVisibility(View.GONE);
            holder.rl_upload.setVisibility(View.VISIBLE);
        }else{
            holder.iv_delete.setVisibility(View.VISIBLE);
            holder.item_iv_image.setVisibility(View.VISIBLE);
            Bitmap smallBitmap = BitmapUtils.getSmallBitmap(list.get(position).getPath());
            holder.item_iv_image.setImageBitmap(smallBitmap);
            holder.rl_upload.setVisibility(View.GONE);
        }
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.deleteItem(position);
                }
            }
        });
        holder.item_iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
        holder.rl_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
//        return 0;
        if (maxCount==1){
            return 1;
        }else {
            if(list==null) {
                return 1;
            }else if(list.size()==4){
                return 4;
            }else{
                return list.size()+1;
            }
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
        public RelativeLayout rl_upload;

        public PublishImagesViewHolder(View itemView) {
            super(itemView);
            item_iv_image = itemView.findViewById(R.id.item_iv_image);
            rl_publish = itemView.findViewById(R.id.rl_publish);
            rl_upload = itemView.findViewById(R.id.rl_upload);
            iv_delete = itemView.findViewById(R.id.iv_delete);
        }

    }
    public interface OnItemClickListener{
        void onItemClick(int position);
        void deleteItem(int position);
    }
}
