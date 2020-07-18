package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.AppointmentBean;
import com.kasa.ola.ui.AppointmentDetailsActivity;
import com.kasa.ola.utils.DisplayUtils;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<AppointmentBean> list;
    private OnAppointmentListListener onAppointmentListListener;

    public void setOnAppointmentListListener(OnAppointmentListListener onAppointmentListListener) {
        this.onAppointmentListListener = onAppointmentListListener;
    }

    public AppointmentAdapter(Context context, List<AppointmentBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AppointmentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_appointment,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        AppointmentViewHolder holder = (AppointmentViewHolder) viewHolder;
        final AppointmentBean appointmentBean = list.get(position);
        if (appointmentBean.getReservationStatus().equals("1")){
            holder.rl_left.setBackgroundColor(context.getResources().getColor(R.color.COLOR_FF47D174));
            holder.tv_appointment_status.setText(context.getString(R.string.appointment_success));
            DisplayUtils.setViewDrawableTop(holder.tv_appointment_status,R.mipmap.successfulappointment_icon);
            holder.tv_appointment_tips.setText(context.getString(R.string.appointment_successful_tips));
            holder.tv_submit.setText(context.getString(R.string.cancel_appointment));
            holder.tv_submit.setBackgroundResource(R.drawable.bg_rectangle_blue_corner_1);
            holder.tv_submit.setEnabled(true);
        }else if (appointmentBean.getReservationStatus().equals("2")){
            holder.rl_left.setBackgroundColor(context.getResources().getColor(R.color.COLOR_FF429BF6));
            holder.tv_appointment_status.setText(context.getString(R.string.appointment_wait));
            DisplayUtils.setViewDrawableTop(holder.tv_appointment_status,R.mipmap.waitingforconfirmation);
            holder.tv_appointment_tips.setText(context.getString(R.string.appointment_wait_confirm_tips));
            holder.tv_submit.setVisibility(View.INVISIBLE);
            holder.tv_submit.setEnabled(false);
        }else if (appointmentBean.getReservationStatus().equals("3")){
            holder.rl_left.setBackgroundColor(context.getResources().getColor(R.color.COLOR_FF858585));
            holder.tv_appointment_status.setText(context.getString(R.string.cancel_success));
            DisplayUtils.setViewDrawableTop(holder.tv_appointment_status,R.mipmap.cancellationofsuccess);
            holder.tv_appointment_tips.setText(context.getString(R.string.cancel_success_tips));
            holder.tv_submit.setVisibility(View.INVISIBLE);
            holder.tv_submit.setEnabled(false);
        }else if (appointmentBean.getReservationStatus().equals("4")){
            holder.rl_left.setBackgroundColor(context.getResources().getColor(R.color.COLOR_FF858585));
            holder.tv_appointment_status.setText(context.getString(R.string.have_break_appointment));
            DisplayUtils.setViewDrawableTop(holder.tv_appointment_status,R.mipmap.alreadybroke);
            holder.tv_appointment_tips.setText(context.getString(R.string.have_break_appointment));
            holder.tv_submit.setVisibility(View.INVISIBLE);
            holder.tv_submit.setEnabled(false);
        }else if (appointmentBean.getReservationStatus().equals("5")){
            holder.rl_left.setBackgroundColor(context.getResources().getColor(R.color.COLOR_FF858585));
            holder.tv_appointment_status.setText(context.getString(R.string.have_ask_for_leave));
            DisplayUtils.setViewDrawableTop(holder.tv_appointment_status,R.mipmap.haveaskedforleave);
            holder.tv_appointment_tips.setText(context.getString(R.string.have_ask_for_leave_tips));
            holder.tv_submit.setVisibility(View.INVISIBLE);
            holder.tv_submit.setEnabled(false);
        }else if (appointmentBean.getReservationStatus().equals("6")){
            holder.rl_left.setBackgroundColor(context.getResources().getColor(R.color.COLOR_FF858585));
            holder.tv_appointment_status.setText(context.getString(R.string.appointment_finish));
            DisplayUtils.setViewDrawableTop(holder.tv_appointment_status,R.mipmap.alreadycompleted);
            holder.tv_appointment_tips.setText(context.getString(R.string.appointment_finish_tips));
            holder.tv_submit.setText(context.getString(R.string.discuss));
            holder.tv_submit.setBackgroundResource(R.drawable.bg_rectangle_blue_corner_1);
            holder.tv_submit.setEnabled(true);
        }else if (appointmentBean.getReservationStatus().equals("7")){
            holder.rl_left.setBackgroundColor(context.getResources().getColor(R.color.COLOR_FF858585));
            holder.tv_appointment_status.setText(context.getString(R.string.comment_finish));
            DisplayUtils.setViewDrawableTop(holder.tv_appointment_status,R.mipmap.havecommented);
            holder.tv_appointment_tips.setText(context.getString(R.string.comment_finish_tips));
            holder.tv_submit.setVisibility(View.INVISIBLE);
            holder.tv_submit.setEnabled(true);
        }
        holder.tv_lesson_name.setText(appointmentBean.getLessonName());
        holder.tv_organ_name.setText(appointmentBean.getEducationName());
        holder.tv_time.setText(appointmentBean.getReservationTime());
        holder.tv_address.setText(appointmentBean.getAddress());
        holder.tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appointmentBean.getReservationStatus().equals("1")){
                    if (onAppointmentListListener!=null){
                        onAppointmentListListener.onCancel(position);
                    }
                }else if (appointmentBean.getReservationStatus().equals("6")){
                    if (onAppointmentListListener!=null){
                        onAppointmentListListener.onComment(position);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }

    private class AppointmentViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout rl_left;
        public TextView tv_appointment_status;
        public TextView tv_appointment_tips;
        public ImageView iv_organ_logo;
        public TextView tv_lesson_name;
        public TextView tv_organ_name;
        public TextView tv_time;
        public TextView tv_address;
        public TextView tv_submit;
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            rl_left = itemView.findViewById(R.id.rl_left);
            tv_appointment_status = itemView.findViewById(R.id.tv_appointment_status);
            tv_appointment_tips = itemView.findViewById(R.id.tv_appointment_tips);
            iv_organ_logo = itemView.findViewById(R.id.iv_organ_logo);
            tv_lesson_name = itemView.findViewById(R.id.tv_lesson_name);
            tv_organ_name = itemView.findViewById(R.id.tv_organ_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_submit = itemView.findViewById(R.id.tv_submit);
        }
    }
    public interface OnAppointmentListListener{
        void onCancel(int position);
        void onComment(int position);
    }
}
