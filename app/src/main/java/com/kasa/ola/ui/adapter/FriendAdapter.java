package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.MemberBean;
import com.kasa.ola.ui.MyFriendActivity;
import com.kasa.ola.utils.StringUtils;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<MemberBean> list;
    public FriendAdapter(Context context, List<MemberBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FriendViewHolder(LayoutInflater.from(context).inflate(R.layout.item_friend,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        FriendViewHolder holder = (FriendViewHolder) viewHolder;
        MemberBean memberBean = list.get(position);
        holder.tv_mobile.setText(StringUtils.getHideMiddleMobile(memberBean.getMobile()));
    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }
    private class FriendViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_mobile;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
        }
    }
}
