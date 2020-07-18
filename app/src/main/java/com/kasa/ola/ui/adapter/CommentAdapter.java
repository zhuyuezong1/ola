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
import com.kasa.ola.bean.entity.ProductCommentBean;
import com.kasa.ola.bean.entity.UserViewInfo;
import com.kasa.ola.ui.ProductInfoActivity;
import com.kasa.ola.utils.StringUtils;
import com.previewlibrary.GPreviewBuilder;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter{
    private Context context;
    private List<ProductCommentBean> list;
    public CommentAdapter(Context context, List<ProductCommentBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_comment,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final CommentViewHolder holder = (CommentViewHolder) viewHolder;
        ProductCommentBean productCommentBean = list.get(position);

        holder.tv_mobile.setText(StringUtils.getHideMiddleMobile(productCommentBean.getMobile()));
        holder.tv_spe.setText(productCommentBean.getTime()+" "+productCommentBean.getSpe());
        holder.tv_comment_content.setText(productCommentBean.getComment());

        ArrayList<UserViewInfo> images = new ArrayList<>();
        if (productCommentBean.getImgArr()!=null && productCommentBean.getImgArr().size()>0){
            for (int i=0;i<productCommentBean.getImgArr().size();i++){
                UserViewInfo userViewInfo = new UserViewInfo(productCommentBean.getImgArr().get(i));
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

    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }
    private class CommentViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView rv_comment_images;
        private ImageView iv_head_image;
        private TextView tv_mobile;
        private TextView tv_spe;
        private TextView tv_comment_content;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_comment_images = itemView.findViewById(R.id.rv_comment_images);
            iv_head_image = itemView.findViewById(R.id.iv_head_image);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_spe = itemView.findViewById(R.id.tv_spe);
            tv_comment_content = itemView.findViewById(R.id.tv_comment_content);
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
