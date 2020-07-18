package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 图片选择适配器
 * Created by GXH on 2018/3/12.
 */

public class ImageSelectAdapter extends RecyclerView.Adapter {
    public interface SelectAdapterListener {
//        void onCameraClick();

        void onItemClick(File selectFile);
    }

    private Context context;
    private ArrayList<File> imageList = new ArrayList<>();
    private ArrayList<File> selectImageList = new ArrayList<>();
    private SelectAdapterListener selectAdapterListener;

    public ImageSelectAdapter(Context context) {
        this.context = context;
    }

    public void setImageList(ArrayList<File> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    public ArrayList<File> getSelectImageList() {
        return selectImageList;
    }

//    public void setSelectImageList(ArrayList<File> selectImageList) {
//        this.selectImageList = selectImageList;
//    }

    public void setSelectAdapterListener(SelectAdapterListener selectAdapterListener) {
        this.selectAdapterListener = selectAdapterListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*if (viewType == 0) {
            return new CameraViewHolder(LayoutInflater.from(context).inflate(R.layout.image_camera_item, parent, false));
        } else {*/
            return new ImageSelectViewHolder(LayoutInflater.from(context).inflate(R.layout.image_select_item, parent, false));
//        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        /*if (position == 0) {
            CameraViewHolder viewHolder = (CameraViewHolder) holder;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != selectAdapterListener) {
                        selectAdapterListener.onCameraClick();
                    }
                }
            });
        } else {*/
            final ImageSelectViewHolder viewHolder = (ImageSelectViewHolder) holder;
            final File file = imageList.get(position);
            ImageLoaderUtils.imageLoadFile(context, file, viewHolder.showImg);
            final boolean isSelect = selectImageList.contains(file);
            viewHolder.checkImg.setImageResource(isSelect ? R.mipmap.selected_icon : R.mipmap.ico_picchoice_dis);
            viewHolder.checkImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isSelect) {
                        viewHolder.checkImg.setImageResource(R.mipmap.selected_icon);
                        selectImageList.clear();
                        selectImageList.add(file);
                    } else {
                        selectImageList.clear();
                    }
                    notifyDataSetChanged();
                }
            });

            viewHolder.showImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != selectAdapterListener) {
                        selectAdapterListener.onItemClick(file);
                    }
                }
            });
//        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    private class CameraViewHolder extends RecyclerView.ViewHolder {
        public CameraViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class ImageSelectViewHolder extends RecyclerView.ViewHolder {
        private ImageView showImg, checkImg;

        public ImageSelectViewHolder(View itemView) {
            super(itemView);
            showImg = itemView.findViewById(R.id.show_img);
            checkImg = itemView.findViewById(R.id.check_view);
        }
    }

}

