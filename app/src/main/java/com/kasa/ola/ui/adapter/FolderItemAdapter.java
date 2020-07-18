package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dai on 2017/6/6.
 */

public class FolderItemAdapter extends RecyclerView.Adapter {
    public interface FolderItemClickListener {
        void onItemClick(int position);
    }

    private Context context;
    private ArrayList<File> allImageList;
    private ArrayList<ArrayList<File>> folderImages;
    private ArrayList<File> folderList;
    private int selectPosition;
    private FolderItemClickListener folderItemClickListener;

    public FolderItemAdapter(Context context, ArrayList<File> allImageList, ArrayList<ArrayList<File>> folderImages, ArrayList<File> folderList, int selectPosition) {
        this.context = context;
        this.allImageList = allImageList;
        this.folderImages = folderImages;
        this.folderList = folderList;
        this.selectPosition = selectPosition;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FolderViewHolder(LayoutInflater.from(context).inflate(R.layout.img_folder_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        FolderViewHolder viewHolder = (FolderViewHolder) holder;
        viewHolder.selectView.setVisibility(selectPosition == position ? View.VISIBLE : View.GONE);
        if (position == 0) {
            if (!allImageList.isEmpty()) {
                ImageLoaderUtils.imageLoadFile(context, allImageList.get(0), viewHolder.coverImg);
                viewHolder.folderNameText.setText("所有图片");
                viewHolder.folderCountText.setText(allImageList.size() + "张");
            }
        } else {
            ImageLoaderUtils.imageLoadFile(context, folderImages.get(position - 1).get(0), viewHolder.coverImg);
            viewHolder.folderNameText.setText(folderList.get(position - 1).getName());
            viewHolder.folderCountText.setText(folderImages.get(position - 1).size() + "张");
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition = position;
                notifyDataSetChanged();
                if (null != folderItemClickListener) {
                    folderItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderList.size() + 1;
    }

    public void setFolderItemClickListener(FolderItemClickListener folderItemClickListener) {
        this.folderItemClickListener = folderItemClickListener;
    }

    private class FolderViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private ImageView coverImg;
        private TextView folderNameText;
        private TextView folderCountText;
        private View selectView;

        public FolderViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            coverImg = (ImageView) itemView.findViewById(R.id.cover_img);
            folderNameText = (TextView) itemView.findViewById(R.id.folder_name_text);
            folderCountText = (TextView) itemView.findViewById(R.id.folder_count_text);
            selectView = itemView.findViewById(R.id.select_img);
        }
    }
}
