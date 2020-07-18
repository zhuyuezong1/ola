package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.AddressBean;

import java.util.ArrayList;

public class AddrManagerAdapter extends RecyclerView.Adapter  {
    private Context context;
    private ArrayList<AddressBean> addressModels;
    private AddrManagerListener addrManagerListener;
    private AddressBean tempBean;
    public AddrManagerAdapter(Context context, ArrayList<AddressBean> addressModels) {
        this.context = context;
        this.addressModels = addressModels;
    }
    public void setAddrManagerListener(AddrManagerListener addrManagerListener) {
        this.addrManagerListener = addrManagerListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddrManagerViewHolder(LayoutInflater.from(context).inflate(R.layout.view_addr_manager_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AddrManagerViewHolder viewHolder = (AddrManagerViewHolder) holder;
        final AddressBean addressBean = addressModels.get(position);
        if (addressBean.getIsDefault()==1) {
            tempBean = addressBean;
        }
        viewHolder.tv_name.setText(addressBean.getName());
        viewHolder.tv_tel.setText(addressBean.getMobile());
        viewHolder.tv_addr.setText(String.format("%s%s%s%s", addressBean.getProvince(), addressBean.getCity(), addressBean.getArea(), addressBean.getAddressDetail()));
        Drawable checkDrawable = context.getResources().getDrawable(addressBean.getIsDefault()==1 ? R.mipmap.check_icon : R.mipmap.uncheck_icon);
        checkDrawable.setBounds(0, 0, checkDrawable.getIntrinsicWidth(), checkDrawable.getMinimumHeight());
        viewHolder.tv_set_default.setCompoundDrawables(checkDrawable, null, null, null);
        viewHolder.tv_set_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressBean.getIsDefault()==0) {
                    addressBean.setIsDefault(1);
                    if (null != tempBean) {
                        tempBean.setIsDefault(0);
                        tempBean = addressBean;
                    }
                    Drawable drawable = context.getResources().getDrawable(R.mipmap.check_icon);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getMinimumHeight());
                    viewHolder.tv_set_default.setCompoundDrawables(drawable, null, null, null);
                    if (null != addrManagerListener) {
                        addrManagerListener.onDefaultClick(addressBean);
                    }
                    notifyDataSetChanged();
                }
            }
        });
        viewHolder.tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != addrManagerListener) {
                    addrManagerListener.onEditClick(addressBean);
                }
            }
        });
        viewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != addrManagerListener) {
                    addrManagerListener.onDeleteClick(addressBean);
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
    public class AddrManagerViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name;
        public TextView tv_tel;
        public TextView tv_addr;
        public TextView tv_set_default;
        public TextView tv_change;
        public TextView tv_delete;

        public AddrManagerViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_tel = itemView.findViewById(R.id.tv_id_number);
            tv_addr = itemView.findViewById(R.id.tv_addr);
            tv_set_default = itemView.findViewById(R.id.tv_set_default);
            tv_change = itemView.findViewById(R.id.tv_change);
            tv_delete = itemView.findViewById(R.id.tv_delete);
        }
    }

    public interface AddrManagerListener {
        void onDefaultClick(AddressBean addressBean);

        void onEditClick(AddressBean addressBean);

        void onDeleteClick(AddressBean addressBean);

        void onItemClick(AddressBean addressBean);
    }
}
