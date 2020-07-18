package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.MessageBean;
import com.kasa.ola.ui.MessageActivity;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<MessageBean> messages;
    public MessageAdapter(Context context, List<MessageBean> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.view_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MessageViewHolder holder = (MessageViewHolder) viewHolder;
        MessageBean messageBean = messages.get(position);
        holder.tv_title.setText(messageBean.getTitle());
        holder.tv_message.setText(messageBean.getContent());
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
//        return 20;
        return messages==null?0:messages.size();
    }
    private class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_title;
        public TextView tv_message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_message = itemView.findViewById(R.id.tv_message);

        }
    }

}
