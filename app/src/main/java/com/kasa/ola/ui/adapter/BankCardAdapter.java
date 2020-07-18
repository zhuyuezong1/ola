package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.CardBean;

import java.util.List;

public class BankCardAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<CardBean> list;
    private OnEditBtnClickListener onEditBtnClickListener;

    public void setOnEditBtnClickListener(OnEditBtnClickListener onEditBtnClickListener) {
        this.onEditBtnClickListener = onEditBtnClickListener;
    }

    public BankCardAdapter(Context context, List<CardBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new BankCardViewHolder(LayoutInflater.from(context).inflate(R.layout.view_bank_card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BankCardViewHolder viewHolder = (BankCardViewHolder) holder;
        viewHolder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditBtnClickListener!=null){
                    onEditBtnClickListener.onEditBtnClick(position,viewHolder.iv_edit);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }
    public class BankCardViewHolder extends RecyclerView.ViewHolder {
        TextView tv_bank_name;
        TextView tv_default_flag;
        TextView tv_bank_card_num;
        ImageView iv_edit;
        public BankCardViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            tv_default_flag = itemView.findViewById(R.id.tv_default_flag);
            tv_bank_card_num = itemView.findViewById(R.id.tv_bank_card_num);
            iv_edit = itemView.findViewById(R.id.iv_edit);
        }
    }
    public interface OnEditBtnClickListener{
        void onEditBtnClick(int position,View view);
    }
}
