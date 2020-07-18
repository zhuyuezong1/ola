package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.core.PoiItem;
import com.kasa.ola.R;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.ToastUtils;

import java.util.List;

public class MapAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<PoiItem> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MapAdapter(Context context, List<PoiItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MapViewHolder(LayoutInflater.from(context).inflate(R.layout.item_map_address,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MapViewHolder viewHolder = (MapViewHolder) holder;
        PoiItem poiItem = list.get(position);
        viewHolder.tvTitle.setText(poiItem.getTitle());
        viewHolder.tvContent.setText(poiItem.getSnippet());
        viewHolder.llAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    private class MapViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout llAddress;
        private TextView tvTitle;
        private TextView tvContent;

        public MapViewHolder(@NonNull View itemView) {
            super(itemView);
            llAddress = itemView.findViewById(R.id.llAddress);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
        }
    }
}
