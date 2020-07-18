package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.CostDetailsBean;

import java.util.List;

public class CostDetailsAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<CostDetailsBean> mList;

    public CostDetailsAdapter(Context context, List<CostDetailsBean> list) {
        mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CostDetailsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.view_cost_details_item, parent, false));
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CostDetailsViewHolder viewHolder = (CostDetailsViewHolder) holder;
        CostDetailsBean costDetailsBean = mList.get(position);
        viewHolder.tv_time.setText(costDetailsBean.getCreateTime());
        viewHolder.tv_title.setText(costDetailsBean.getDescribe());
        viewHolder.tv_change.setText(costDetailsBean.getMoney());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class CostDetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_time;
        private TextView tv_title;
        private TextView tv_change;
        public CostDetailsViewHolder(View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_change = itemView.findViewById(R.id.tv_change);
        }
    }
}
