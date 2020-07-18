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
import com.kasa.ola.bean.model.PayTypeModel;

import java.util.ArrayList;


public class PayTypeAdapter extends RecyclerView.Adapter {

    private ArrayList<PayTypeModel> payTypeList;
    private PayTypeModel tempModel;
    private Context context;
    private PayTypeListener payTypeListener;

    public interface PayTypeListener {
        void onItemClick(int payType);
    }

    public void setPayTypeListener(PayTypeListener payTypeListener) {
        this.payTypeListener = payTypeListener;
    }

    public PayTypeAdapter(Context context, ArrayList<PayTypeModel> payTypeList) {
        this.payTypeList = payTypeList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PayTypeViewHolder(LayoutInflater.from(context).inflate(R.layout.sub_paytype, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final PayTypeViewHolder viewHolder = (PayTypeViewHolder) holder;
        final PayTypeModel payTypeModel = payTypeList.get(position);
        viewHolder.imgv_logo.setBackgroundResource(payTypeModel.iconId);
        viewHolder.tv_payType.setText(payTypeModel.payTypeDetails);
        viewHolder.tv_explain.setText(payTypeModel.explain);
//        if (payTypeModel.payType==3){
//            viewHolder.tv_explain.setTextColor(context.getResources().getColor(R.color.red));
//        }
        if (payTypeModel.status == 1) {
            viewHolder.imgv_status.setBackgroundResource(R.mipmap.check_icon);
            tempModel = payTypeModel;
        } else if (payTypeModel.status == 0) {
            viewHolder.imgv_status.setBackgroundResource(R.mipmap.uncheck_icon);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payTypeModel.status == 0) {
                    viewHolder.imgv_status.setBackgroundResource(R.mipmap.check_icon);
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
        return payTypeList.size();
    }

    private class PayTypeViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgv_logo;
        private TextView tv_payType;
        private TextView tv_explain;
        private ImageView imgv_status;

        public PayTypeViewHolder(View itemView) {
            super(itemView);
            imgv_logo = itemView.findViewById(R.id.imageView_payType);
            tv_payType = itemView.findViewById(R.id.textView_payType);
            tv_explain = itemView.findViewById(R.id.textView_explain);
            imgv_status = itemView.findViewById(R.id.select_status);
        }
    }
}
