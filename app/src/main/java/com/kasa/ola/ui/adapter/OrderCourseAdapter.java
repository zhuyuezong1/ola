package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.ClassBean;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class OrderCourseAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ClassBean> classBeans;
    private OnOrderClickListener onOrderClickListener;

    public OnOrderClickListener getOnOrderClickListener() {
        return onOrderClickListener;
    }

    public void setOnOrderClickListener(OnOrderClickListener onOrderClickListener) {
        this.onOrderClickListener = onOrderClickListener;
    }

    public OrderCourseAdapter(Context context, List<ClassBean> classBeans) {
        this.context = context;
        this.classBeans = classBeans;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new OrderCourseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order_course,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        OrderCourseViewHolder holder = (OrderCourseViewHolder) viewHolder;
        final ClassBean classBean = classBeans.get(position);
        if (classBean.getClassStatus().equals("1")){
            holder.tv_order_enter.setText(context.getString(R.string.cancel));
            holder.tv_order_enter.setBackgroundResource(R.drawable.bg_rectangle_deep_blue_corner);
        }else if (classBean.getClassStatus().equals("2")){
            holder.tv_order_enter.setText(context.getString(R.string.wait_for_cancel));
            holder.tv_order_enter.setBackgroundResource(R.drawable.bg_rectangle_grey);
        }else if (classBean.getClassStatus().equals("3")){
            holder.tv_order_enter.setText(context.getString(R.string.join));
            holder.tv_order_enter.setBackgroundResource(R.drawable.bg_rectangle_deep_blue_corner);
        }
        holder.tv_order_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onOrderClickListener!=null){
                    if (classBean.getClassStatus().equals("1")){
                        onOrderClickListener.onOrderCancel(position);
                    }else if (classBean.getClassStatus().equals("3")){
                        onOrderClickListener.onOrderClick(position);
                    }
                }
            }
        });
        holder.tv_have_join.setText("已报名"+classBean.getReservationNum()+"人，剩余"+classBean.getRemainingNum()+"人");
        ImageLoaderUtils.imageLoadRound(context,classBean.getClassLogo(),holder.iv_logo);
        holder.tv_time.setText(classBean.getClassTime());
        holder.tv_course_name.setText(classBean.getClassName());
    }

    @Override
    public int getItemCount() {
//        return 10;
        return classBeans==null?0:classBeans.size();
    }

    private class OrderCourseViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll_course_item;
        TextView tv_order_enter;
        TextView tv_time;
        TextView tv_course_name;
        TextView tv_have_join;
        ImageView iv_logo;
        public OrderCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_course_item = itemView.findViewById(R.id.ll_course_item);
            tv_order_enter = itemView.findViewById(R.id.tv_order_enter);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_course_name = itemView.findViewById(R.id.tv_course_name);
            tv_have_join = itemView.findViewById(R.id.tv_have_join);
            iv_logo = itemView.findViewById(R.id.iv_logo);
        }
    }
    public interface OnOrderClickListener {
        void onOrderClick(int position);
        void onOrderCancel(int position);
    }
}
