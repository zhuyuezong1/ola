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
import com.kasa.ola.bean.entity.MessageBean;
import com.kasa.ola.ui.MyMessageActivity;

import org.w3c.dom.Text;

import java.util.List;

public class MyMessageAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<MessageBean> list;
    public MyMessageAdapter(Context context, List<MessageBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_my_message,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        MyMessageViewHolder holder = (MyMessageViewHolder) viewHolder;
        MessageBean messageBean = list.get(position);
        holder.tv_message_type.setText(messageBean.getTitle());
        holder.tv_message_content.setText(messageBean.getContent());
        holder.tv_message_time.setText(messageBean.getTime());
        holder.iv_delete.setVisibility(View.GONE);
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }
    private class MyMessageViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_message_type;
        private TextView tv_message_content;
        private TextView tv_message_time;
        private ImageView iv_delete;
        public MyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_message_type = itemView.findViewById(R.id.tv_message_type);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            tv_message_content = itemView.findViewById(R.id.tv_message_content);
            tv_message_time = itemView.findViewById(R.id.tv_message_time);
        }
    }
}
