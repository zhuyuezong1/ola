package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.AddressBean;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter  {
    private Context context;
    private ArrayList<AddressBean> addressModels;
    private AddrManagerListener addrManagerListener;
    private AddressBean tempBean;
    public AddressAdapter(Context context, ArrayList<AddressBean> addressModels) {
        this.context = context;
        this.addressModels = addressModels;
    }
    public void setAddrManagerListener(AddrManagerListener addrManagerListener) {
        this.addrManagerListener = addrManagerListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressViewHolder(LayoutInflater.from(context).inflate(R.layout.view_address_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AddressViewHolder viewHolder = (AddressViewHolder) holder;
        final AddressBean addressBean = addressModels.get(position);
        if (addressBean.getIsDefault()==1) {
            tempBean = addressBean;
        }
        viewHolder.tv_name.setText(addressBean.getName());
        viewHolder.tv_phone.setText(addressBean.getMobile());
        viewHolder.tv_address.setText(String.format("%s%s%s%s", addressBean.getProvince(), addressBean.getCity(), addressBean.getArea(), addressBean.getAddressDetail()));
        viewHolder.tv_default_flag.setVisibility(addressBean.getIsDefault()==1?View.VISIBLE:View.GONE);
        viewHolder.ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != addrManagerListener) {
                    addrManagerListener.onEditClick(addressBean);
                }
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != addrManagerListener) {
                    addrManagerListener.onItemClick(addressBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressModels!=null?addressModels.size():0;
    }
    public class AddressViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name;
        public TextView tv_phone;
        public TextView tv_default_flag;
        public TextView tv_address;
        public TextView tv_edit;
        public LinearLayout ll_edit;

        public AddressViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            ll_edit = itemView.findViewById(R.id.ll_edit);
            tv_default_flag = itemView.findViewById(R.id.tv_default_flag);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_edit = itemView.findViewById(R.id.tv_edit);
        }
    }

    public interface AddrManagerListener {
        void onDefaultClick(AddressBean addressBean);

        void onEditClick(AddressBean addressBean);

        void onDeleteClick(AddressBean addressBean);

        void onItemClick(AddressBean addressBean);
    }
}
