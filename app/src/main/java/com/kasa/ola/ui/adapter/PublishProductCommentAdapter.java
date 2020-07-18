package com.kasa.ola.ui.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.entity.MallOrderBean;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.PublishActivity;
import com.kasa.ola.ui.PublishProductCommentActivity;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.ui.popwindow.SelectImagePop;
import com.kasa.ola.utils.GlideLoader;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;
import com.lcw.library.imagepicker.ImagePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PublishProductCommentAdapter extends RecyclerView.Adapter {

    private Activity context;
    private List<MallOrderBean.ProductOrder> list;
    private List<List<File>> filesList = new ArrayList<>();
    private PublishImageAdapter publishImageAdapter;
    private ProductPublishImageListener productPublishImageListener;

    public void setProductPublishImageListener(ProductPublishImageListener productPublishImageListener) {
        this.productPublishImageListener = productPublishImageListener;
    }

    public PublishProductCommentAdapter(Activity context, List<MallOrderBean.ProductOrder> list, List<List<File>> filesList) {
        this.context = context;
        this.list = list;
        this.filesList = filesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PublishProductCommentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_publish_product_comment,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final PublishProductCommentViewHolder holder = (PublishProductCommentViewHolder) viewHolder;
        MallOrderBean.ProductOrder productOrder = list.get(i);
        ImageLoaderUtils.imageLoad(context,productOrder.getImageUrl(),holder.iv_product_image);
        holder.tv_product_name.setText(productOrder.getProductName());
        holder.tv_product_spe.setText(productOrder.getSpe());
        holder.et_publish_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
        holder.et_publish_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (productPublishImageListener!=null){
                    productPublishImageListener.commentContent(i,s.toString());
                }
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        holder.rv_images.setLayoutManager(gridLayoutManager);
        List<File> files = filesList.get(i);
        publishImageAdapter = new PublishImageAdapter(context, files);
        publishImageAdapter.setOnItemClickListener(new PublishImageAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int position) {
                if (productPublishImageListener!=null){
                    productPublishImageListener.onItemClick(i,position);
                }
            }

            @Override
            public void deleteItem(int position) {
                if (productPublishImageListener!=null){
                    productPublishImageListener.deleteItem(i,position);
                }
            }
        });
        holder.rv_images.setAdapter(publishImageAdapter);
    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }
    private class PublishProductCommentViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_product_image;
        private TextView tv_product_name;
        private TextView tv_product_spe;
        private EditText et_publish_content;
        private RecyclerView rv_images;

        public PublishProductCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_product_image = itemView.findViewById(R.id.iv_product_image);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_product_spe = itemView.findViewById(R.id.tv_product_spe);
            et_publish_content = itemView.findViewById(R.id.et_publish_content);
            rv_images = itemView.findViewById(R.id.rv_images);
        }
    }
    public interface ProductPublishImageListener{
        void onItemClick(int position,int imagePosition);
        void deleteItem(int position,int imagePosition);
        void commentContent(int position,String commentContent);
    }
}
