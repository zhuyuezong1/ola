package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.DetailBean;

import java.util.List;

public class WalletDetailAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<DetailBean> list;
    public WalletDetailAdapter(Context context, List<DetailBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return  new DetailViewHolder(LayoutInflater.from(context).inflate(R.layout.item_wallet_detail, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        DetailViewHolder holder = (DetailViewHolder) viewHolder;
        DetailBean detailBean = list.get(position);
        holder.tv_name.setText(detailBean.getDescribe());
        holder.tv_time.setText(detailBean.getCreateTime());
        holder.tv_money.setText(detailBean.getMoney());
        if (position==list.size()){
            holder.divider.setVisibility(View.GONE);
        }else {
            holder.divider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }
    private class DetailViewHolder extends RecyclerView.ViewHolder {
        private View divider;
        private TextView tv_name;
        private TextView tv_time;
        private TextView tv_money;
        private DetailViewHolder(View itemView) {
            super(itemView);
            divider = itemView.findViewById(R.id.divider);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_money = itemView.findViewById(R.id.tv_money);
//
        }
    }
}
