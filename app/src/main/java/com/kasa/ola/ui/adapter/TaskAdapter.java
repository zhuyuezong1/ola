package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.ui.CityServiceActivity;
import com.kasa.ola.ui.listener.OnItemClickListener;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Object> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public TaskAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(context).inflate(R.layout.item_task,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskViewHolder viewHolder = (TaskViewHolder) holder;
        viewHolder.cv_item.setOnClickListener(new View.OnClickListener() {
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
        return 10;
//        return list==null?0:list.size();
    }

    private class TaskViewHolder extends RecyclerView.ViewHolder{
        CardView cv_item;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
           cv_item = itemView.findViewById(R.id.cv_item);
        }
    }
}
