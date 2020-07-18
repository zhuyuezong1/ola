package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.SelfMentionPointBean;
import com.kasa.ola.ui.listener.OnItemClickListener;

import java.util.List;

public class SelfMentionPointAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<SelfMentionPointBean> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SelfMentionPointAdapter(Context context, List<SelfMentionPointBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SelfMentionPointViewHolder(LayoutInflater.from(context).inflate(R.layout.pop_item_self_mention_point,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        SelfMentionPointViewHolder holder = (SelfMentionPointViewHolder) viewHolder;
        SelfMentionPointBean selfMentionPointBean = list.get(position);
        holder.tv_name.setText(selfMentionPointBean.getName());
        holder.tv_address.setText(selfMentionPointBean.getProvince()+selfMentionPointBean.getCity()+selfMentionPointBean.getArea()+selfMentionPointBean.getAddressDetail());
        holder.ll_self_mention_point_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private class SelfMentionPointViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout ll_self_mention_point;
        public LinearLayout ll_self_mention_point_pop;
        public ImageView iv_self_mention_point;
        public TextView tv_name;
        public TextView tv_address;

        public SelfMentionPointViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_self_mention_point = itemView.findViewById(R.id.ll_self_mention_point);
            ll_self_mention_point_pop = itemView.findViewById(R.id.ll_self_mention_point_pop);
            iv_self_mention_point = itemView.findViewById(R.id.iv_self_mention_point);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_address = itemView.findViewById(R.id.tv_address);
        }
    }
}
