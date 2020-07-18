package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.CommissionDetailsBean;

import java.util.List;

public class CommissionDetailsAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<CommissionDetailsBean> mList;

    public CommissionDetailsAdapter(Context context, List<CommissionDetailsBean> list) {
        mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommissionDetailsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.view_commission_details_item, parent, false));
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommissionDetailsViewHolder viewHolder = (CommissionDetailsViewHolder) holder;
        CommissionDetailsBean commissionDetailsBean = mList.get(position);
        viewHolder.tv_time.setText(commissionDetailsBean.getCreateTime());
        viewHolder.tv_title.setText(commissionDetailsBean.getDescribe());
        viewHolder.tv_change.setText(commissionDetailsBean.getMoney());
//        viewHolder.tv_last.setText("余" + commissionDetailsBean.getCommission());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class CommissionDetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_time;
        private TextView tv_title;
        private TextView tv_change;
//        private TextView tv_last;

        public CommissionDetailsViewHolder(View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_change = itemView.findViewById(R.id.tv_change);
//            tv_last = itemView.findViewById(R.id.tv_last);
        }
    }
}
