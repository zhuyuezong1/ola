package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.VacanciesDetailsBean;

import java.util.List;

public class VacanciesDetailsAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<VacanciesDetailsBean> mList;

    public VacanciesDetailsAdapter(Context context, List<VacanciesDetailsBean> list) {
        mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VacanciesDetailsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.view_vacancies_details_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VacanciesDetailsViewHolder viewHolder = (VacanciesDetailsViewHolder) holder;
        VacanciesDetailsBean vacanciesDetailsBean = mList.get(position);
        viewHolder.tv_time.setText(vacanciesDetailsBean.getCreateTime());
        viewHolder.tv_title.setText(vacanciesDetailsBean.getDescribe());
        viewHolder.tv_change.setText(vacanciesDetailsBean.getMoney());
//        viewHolder.tv_last.setText("余" + vacanciesDetailsBean.getBalanceYuFu());
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    private class VacanciesDetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_time;
        private TextView tv_title;
        private TextView tv_change;
//        private TextView tv_last;

        public VacanciesDetailsViewHolder(View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_change = itemView.findViewById(R.id.tv_change);
//            tv_last = itemView.findViewById(R.id.tv_last);
        }
    }
}
