package com.kasa.ola.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.CommentBean;
import com.kasa.ola.bean.entity.UserViewInfo;
import com.kasa.ola.ui.MyCommentActivity;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.StringUtils;
import com.previewlibrary.GPreviewBuilder;

import java.util.ArrayList;
import java.util.List;

public class MyCommentsAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<CommentBean> list;
    private int type;
    public MyCommentsAdapter(Context context, List<CommentBean> list,int type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyCommentsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_my_comment,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final MyCommentsViewHolder holder = (MyCommentsViewHolder) viewHolder;
        CommentBean commentBean = list.get(position);
        if (type==0){
            holder.tv_spe.setVisibility(View.VISIBLE);
            holder.rv_comment_images.setVisibility(View.VISIBLE);
            holder.tv_spe.setText(commentBean.getSpe());
        }else if (type==1){
            holder.tv_spe.setVisibility(View.GONE);
            holder.rv_comment_images.setVisibility(View.GONE);
        }
        holder.tv_mobile.setText(StringUtils.getHideMiddleMobile(commentBean.getMobile()));
        holder.tv_name.setText(commentBean.getProductName());
        holder.tv_price.setText(commentBean.getProductPrice());
        holder.tv_comment_content.setText(commentBean.getComment());
        ImageLoaderUtils.imageLoad(context,commentBean.getProductImage(),holder.iv_product);

        ArrayList<UserViewInfo> images = new ArrayList<>();
        if (commentBean.getImgArr()!=null && commentBean.getImgArr().size()>0){
            for (int i=0;i<commentBean.getImgArr().size();i++){
                UserViewInfo userViewInfo = new UserViewInfo(commentBean.getImgArr().get(i));
                images.add(userViewInfo);
            }
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        holder.rv_comment_images.setLayoutManager(gridLayoutManager);
        CommentImageAdapter commentImageAdapter = new CommentImageAdapter(context, images);
        commentImageAdapter.setItemImageViewClickListener(new CommentImageAdapter.ItemImageClickListener<UserViewInfo>() {
            @Override
            public void onItemImageClick(Context context, ImageView imageView, int index, List<UserViewInfo> list) {
                computeBoundsBackward(list,holder.rv_comment_images);//组成数据
                GPreviewBuilder.from((Activity) context)
                        .setData(list)
                        .setCurrentIndex(index)
                        .setType(GPreviewBuilder.IndicatorType.Dot)
                        .start();//启动
            }
        });
        holder.rv_comment_images.setAdapter(commentImageAdapter);
        holder.rv_comment_images.setNestedScrollingEnabled(false);
        holder.rv_comment_images.setFocusableInTouchMode(false);
        holder.rv_comment_images.requestFocus();

    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }

    private class MyCommentsViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_product;
        TextView tv_mobile;
        TextView tv_spe;
        TextView tv_comment_content;
        TextView tv_name;
        TextView tv_price;
        RecyclerView rv_comment_images;
        public MyCommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_spe = itemView.findViewById(R.id.tv_spe);
            tv_comment_content = itemView.findViewById(R.id.tv_comment_content);
            rv_comment_images = itemView.findViewById(R.id.rv_comment_images);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }
    /**
     * 查找信息
     * @param list 图片集合
     * @param mNglContent
     */
    private void computeBoundsBackward(List<UserViewInfo> list, RecyclerView mNglContent) {
        if (mNglContent.getChildCount()>0){
            for (int i = 0;i < mNglContent.getChildCount(); i++) {
                View itemView = mNglContent.getChildAt(i);
                Rect bounds = new Rect();
                if (itemView != null) {
                    ImageView thumbView = (ImageView) itemView;
                    thumbView.getGlobalVisibleRect(bounds);
                }
                list.get(i).setBounds(bounds);
                list.get(i).setUrl(list.get(i).getUrl());
            }
        }
    }
}
