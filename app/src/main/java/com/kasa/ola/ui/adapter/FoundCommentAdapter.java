package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.CommentBean;
import com.kasa.ola.bean.entity.FoundItemBean;
import com.kasa.ola.ui.FoundDetailActivity;
import com.kasa.ola.widget.RoundImageView;

import java.util.List;

public class FoundCommentAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<CommentBean> list;
    public FoundCommentAdapter(Context context, List<CommentBean> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoundCommentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_found_comment,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
//        return list==null?0:list.size();
    }
    private class FoundCommentViewHolder extends RecyclerView.ViewHolder {
        private RoundImageView image_head;
        private TextView tv_name;
        private TextView tv_time;
        private TextView tv_content;
        public FoundCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            image_head = itemView.findViewById(R.id.image_head);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }


}
