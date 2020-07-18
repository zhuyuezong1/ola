package com.kasa.ola.ui.adapter;

import android.app.Activity;
import android.content.Context;
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
import com.kasa.ola.ui.ProductCommentListActivity;
import com.kasa.ola.utils.CommonUtils;
import com.previewlibrary.GPreviewBuilder;

import java.util.ArrayList;
import java.util.List;

public class ProductCommentAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ProductCommentBean> list;
    public ProductCommentAdapter(Context context, List<ProductCommentBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductCommentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product_comment,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ProductCommentViewHolder holder = (ProductCommentViewHolder) viewHolder;
        ProductCommentBean productCommentBean = list.get(position);

        holder.tv_sku.setText(productCommentBean.getTime()+" "+productCommentBean.getSpe());
        holder.tv_comment_content.setText(productCommentBean.getComment());

        ArrayList<UserViewInfo> images = new ArrayList<>();
        if (productCommentBean.getImgArr()!=null && productCommentBean.getImgArr().size()>0){
            for (int i=0;i<productCommentBean.getImgArr().size();i++){
                UserViewInfo userViewInfo = new UserViewInfo(productCommentBean.getImgArr().get(i));
                images.add(userViewInfo);
            }

        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        holder.rv_comment_images.setLayoutManager(gridLayoutManager);
        CommentImagePreImageAdapter commentImagePreImageAdapter = new CommentImagePreImageAdapter(context, images);
        commentImagePreImageAdapter.setItemImageViewClickListener(new CommentImagePreImageAdapter.ItemImageClickListener<UserViewInfo>() {
            @Override
            public void onItemImageClick(Context context, ImageView imageView, int index, List<UserViewInfo> list) {
                CommonUtils.computeBoundsBackward(list,holder.rv_comment_images);//组成数据
                GPreviewBuilder.from((Activity) context)
                        .setData(list)
                        .setCurrentIndex(index)
                        .setType(GPreviewBuilder.IndicatorType.Dot)
                        .start();//启动
            }
        });
        holder.rv_comment_images.setAdapter(commentImagePreImageAdapter);

    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }

    private class ProductCommentViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_comment_head;
        private TextView tv_comment_user;
        private TextView tv_sku;
        private TextView tv_comment_content;
        private RecyclerView rv_comment_images;
        public ProductCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_comment_head = itemView.findViewById(R.id.iv_comment_head);
            tv_comment_user = itemView.findViewById(R.id.tv_comment_user);
            tv_sku = itemView.findViewById(R.id.tv_sku);
            tv_comment_content = itemView.findViewById(R.id.tv_comment_content);
            rv_comment_images = itemView.findViewById(R.id.rv_comment_images);
        }
    }
}
