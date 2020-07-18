package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.bean.model.PayTypeModel;
import com.kasa.ola.ui.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class PayPopAdapter extends RecyclerView.Adapter {
    private ArrayList<PayTypeModel> list;
    private PayTypeModel tempModel;
    private Context context;
    private PayPopAdapter.PayTypeListener payTypeListener;

    public interface PayTypeListener {
        void onItemClick(int payType);
    }

    public void setPayTypeListener(PayPopAdapter.PayTypeListener payTypeListener) {
        this.payTypeListener = payTypeListener;
    }

    public PayPopAdapter(Context context, ArrayList<PayTypeModel> list) {
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PayPopViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pop_pay, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final PayPopViewHolder viewHolder = (PayPopViewHolder) holder;
        final PayTypeModel payTypeModel = list.get(position);
        viewHolder.imgv_logo.setBackgroundResource(payTypeModel.iconId);
        viewHolder.tv_payType.setText(payTypeModel.payTypeDetails);
        viewHolder.tv_explain.setText(payTypeModel.explain);
//        if (payTypeModel.payType==3){
//            viewHolder.tv_explain.setTextColor(context.getResources().getColor(R.color.red));
//        }
        if (payTypeModel.status == 1) {
            viewHolder.imgv_status.setBackgroundResource(R.mipmap.icon_selected);
            tempModel = payTypeModel;
        } else if (payTypeModel.status == 0) {
            viewHolder.imgv_status.setBackgroundResource(R.mipmap.uncheck_icon);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payTypeModel.status == 0) {
                    viewHolder.imgv_status.setBackgroundResource(R.mipmap.icon_selected);
                    payTypeModel.status = 1;
                    if (null != tempModel) {
                        tempModel.status = 0;
                    }
                    tempModel = payTypeModel;
                    if (null != payTypeListener) {
                        payTypeListener.onItemClick(payTypeModel.payType);
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    private class PayPopViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgv_logo;
        private TextView tv_payType;
        private TextView tv_explain;
        private ImageView imgv_status;

        public PayPopViewHolder(View itemView) {
            super(itemView);
            imgv_logo = itemView.findViewById(R.id.imageView_payType);
            tv_payType = itemView.findViewById(R.id.textView_payType);
            tv_explain = itemView.findViewById(R.id.textView_explain);
            imgv_status = itemView.findViewById(R.id.select_status);
        }
    }
}

