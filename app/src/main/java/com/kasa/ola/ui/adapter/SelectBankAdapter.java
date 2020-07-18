package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.CardBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class SelectBankAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<CardBean> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SelectBankAdapter(Context context, List<CardBean> cardList) {
        this.context = context;
        this.list = cardList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectCardHolder(LayoutInflater.from(context).inflate(R.layout.view_select_card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SelectCardHolder viewHolder = (SelectCardHolder) holder;
        CardBean cardBean = list.get(position);
        if (cardBean.getIsSelected()==0){
            viewHolder.iv_select.setImageResource(R.mipmap.icon_unselected);
        }else if (cardBean.getIsSelected()==1){
            viewHolder.iv_select.setImageResource(R.mipmap.icon_selected);
        }
        viewHolder.tv_bank_name.setText(cardBean.getBankName()+"("+cardBean.getBankNo()+")");
        viewHolder.rl_select_bank_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<list.size();i++){
                    list.get(i).setIsSelected(0);
                }
                cardBean.setIsSelected(1);
                notifyDataSetChanged();

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
    public class SelectCardHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_select_bank_item;
        private TextView tv_bank_name;
        private ImageView iv_select;
        public SelectCardHolder(@NonNull View itemView) {
            super(itemView);
            rl_select_bank_item = itemView.findViewById(R.id.rl_select_bank_item);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            iv_select = itemView.findViewById(R.id.iv_select);
        }
    }
}
