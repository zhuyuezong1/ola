package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.FoundItemBean;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;

import java.util.List;

public class MyFindAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<FoundItemBean> list;
    public MyFindAdapter(Context context, List<FoundItemBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FindViewHolder(LayoutInflater.from(context).inflate(R.layout.item_my_find,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FindViewHolder viewHolder = (FindViewHolder) holder;
        FoundItemBean foundItemBean = list.get(position);
        ViewGroup.LayoutParams layoutParams = viewHolder.item_iv_find.getLayoutParams();
        int width = DisplayUtils.getScreenWidth(context) - DisplayUtils.dip2px(context, 12) * 3;
        layoutParams.width = width/2;
        layoutParams.height = width / 2;
        ViewGroup.LayoutParams layoutParams1 = viewHolder.rl_edit.getLayoutParams();
        layoutParams1.width = width/2;
        layoutParams1.height = width / 2;
        ImageLoaderUtils.imageLoad(context,foundItemBean.getImageUrl(),viewHolder.item_iv_find);
        viewHolder.item_tv_title.setText(foundItemBean.getContent());
        viewHolder.tv_read_num.setText(foundItemBean.getReadNum());
        viewHolder.tv_comment_num.setText(foundItemBean.getCommentNum());

        viewHolder.iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.rl_edit.setVisibility(View.VISIBLE);
            }
        });
        viewHolder.rl_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.rl_edit.setVisibility(View.GONE);
            }
        });
        viewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLongToast(context,"delete");
            }
        });
        viewHolder.tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLongToast(context,"edit");
            }
        });
    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }

    private class FindViewHolder extends RecyclerView.ViewHolder {
        TextView item_tv_title;
        TextView tv_shop_name;
        TextView tv_comment_num;
        TextView tv_read_num;
        TextView tv_edit;
        TextView tv_delete;
        TextView tv_check_status;
        ImageView item_iv_find;
        ImageView iv_more;
        RelativeLayout rl_edit;
        RelativeLayout rl_my_publish_top;
        private FindViewHolder(View itemView) {
            super(itemView);
            rl_my_publish_top = itemView.findViewById(R.id.rl_my_publish_top);
            item_tv_title = itemView.findViewById(R.id.item_tv_title);
            tv_shop_name = itemView.findViewById(R.id.tv_shop_name);
            tv_comment_num = itemView.findViewById(R.id.tv_comment_num);
            tv_read_num = itemView.findViewById(R.id.tv_read_num);
            item_iv_find = itemView.findViewById(R.id.item_iv_find);
            rl_edit = itemView.findViewById(R.id.rl_edit);
            tv_edit = itemView.findViewById(R.id.tv_edit);
            tv_delete = itemView.findViewById(R.id.tv_delete);
            tv_check_status = itemView.findViewById(R.id.tv_check_status);
            iv_more = itemView.findViewById(R.id.iv_more);
        }
    }
}
