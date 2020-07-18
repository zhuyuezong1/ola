package com.kasa.ola.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.CommentBean;
import com.kasa.ola.bean.entity.UserViewInfo;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.MyCommentNewActivity;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.widget.RoundImageView;
import com.previewlibrary.GPreviewBuilder;

import java.util.ArrayList;
import java.util.List;

public class CommentManagerAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<CommentBean> list;
    public CommentManagerAdapter(Context context, List<CommentBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentManagerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_comment_manager,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final CommentManagerViewHolder holder = (CommentManagerViewHolder) viewHolder;
        CommentBean commentBean = list.get(position);
        holder.tv_comment_user.setText(commentBean.getMobile());
        holder.tv_comment_time.setText(commentBean.getTime());
        holder.tv_product_name.setText(commentBean.getProductName());
        holder.tv_product_desc.setText(commentBean.getSpe());
        holder.tv_product_price.setText(context.getString(R.string.commission_value,commentBean.getProductPrice()));
        holder.tv_comment_content.setText(commentBean.getComment());
        String headImage = LoginHandler.get().getMyInfo().optString("headImage");
        ImageLoaderUtils.imageLoad(context,headImage,holder.iv_comment_head);
        ImageLoaderUtils.imageLoadRound(context,commentBean.getProductImage(),holder.iv_product,4);

        ArrayList<UserViewInfo> images = new ArrayList<>();
        if (commentBean.getImgArr()!=null && commentBean.getImgArr().size()>0){
            for (int i=0;i<commentBean.getImgArr().size();i++){
                UserViewInfo userViewInfo = new UserViewInfo(commentBean.getImgArr().get(i));
                images.add(userViewInfo);
            }
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        holder.rv_comment_images.setLayoutManager(gridLayoutManager);
        CommentManagerImageAdapter commentManagerImageAdapter = new CommentManagerImageAdapter(context, images);
        commentManagerImageAdapter.setItemImageViewClickListener(new CommentManagerImageAdapter.ItemImageClickListener<UserViewInfo>() {
            @Override
            public void onItemImageClick(Context context, ImageView imageView, int index, List<UserViewInfo> list) {
                GPreviewBuilder.from((Activity) context)
                        .setData(list)
                        .setCurrentIndex(index)
                        .setType(GPreviewBuilder.IndicatorType.Dot)
                        .start();//启动
            }
        });
        holder.rv_comment_images.setAdapter(commentManagerImageAdapter);
        holder.rv_comment_images.setNestedScrollingEnabled(false);
        holder.rv_comment_images.setFocusableInTouchMode(false);
        holder.rv_comment_images.requestFocus();
    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }
    private class CommentManagerViewHolder extends RecyclerView.ViewHolder{
        RecyclerView rv_comment_images;
        TextView tv_product_name;
        TextView tv_product_desc;
        TextView tv_product_price;
        TextView tv_comment_content;
        RoundImageView iv_comment_head;
        ImageView iv_product;
        TextView tv_comment_user;
        TextView tv_comment_time;
        public CommentManagerViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_comment_images = itemView.findViewById(R.id.rv_comment_images);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_product_desc = itemView.findViewById(R.id.tv_product_desc);
            tv_product_price = itemView.findViewById(R.id.tv_product_price);
            tv_comment_content = itemView.findViewById(R.id.tv_comment_content);
            iv_comment_head = itemView.findViewById(R.id.iv_comment_head);
            tv_comment_user = itemView.findViewById(R.id.tv_comment_user);
            tv_comment_time = itemView.findViewById(R.id.tv_comment_time);
            iv_product = itemView.findViewById(R.id.iv_product);
        }
    }
}
