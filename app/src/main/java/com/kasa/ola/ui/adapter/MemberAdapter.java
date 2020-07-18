package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.MemberBean;

import java.util.ArrayList;

public class MemberAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<MemberBean> list;

    public MemberAdapter(Context context, ArrayList<MemberBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemberViewHolder(LayoutInflater.from(context).inflate(R.layout.item_member, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MemberBean memberBean = list.get(position);
        MemberViewHolder viewHolder = (MemberViewHolder) holder;
        if (!TextUtils.isEmpty(memberBean.getMobile()) && memberBean.getMobile().length() > 7) {
            StringBuilder sb = new StringBuilder(memberBean.getMobile());
            sb.replace(3, 7, "****");
            viewHolder.tvMemberNum.setText(sb);
        }
//        viewHolder.tvTime.setText(memberBean.getDistributionTime());
//        viewHolder.tvActive.setVisibility(memberBean.getIsActivate()==0 ? View.GONE : View.VISIBLE);
        viewHolder.tvExpenditure.setText(context.getString(R.string.expenditure, memberBean.getYongJin()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, MemberRewardActivity.class);
//                intent.putExtra(Const.MEMBER_REWARD_KEY, memberBean.getMobile());
//                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    private class MemberViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgvIcon;
        private TextView tvMemberNum;
//        private TextView tvTime;
        private TextView tvExpenditure;
//        private TextView tvActive;

        private MemberViewHolder(View itemView) {
            super(itemView);
            imgvIcon = itemView.findViewById(R.id.imageView);
            tvMemberNum = itemView.findViewById(R.id.textView_number);
//            tvTime = itemView.findViewById(R.id.textView_text);
            tvExpenditure = itemView.findViewById(R.id.textView_Expenditure);
//            tvActive = itemView.findViewById(R.id.tv_active);
        }
    }
}
