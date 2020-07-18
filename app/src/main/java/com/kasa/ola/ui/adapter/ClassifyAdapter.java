package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.MallClassBean;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class ClassifyAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<MallClassBean> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ClassifyAdapter(Context context, List<MallClassBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ClassifyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_grid_classify,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ClassifyViewHolder holder = (ClassifyViewHolder) viewHolder;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int widthPixels = 0;
        widthPixels = metrics.widthPixels*1/4- DisplayUtils.dip2px(context,10)*2;
        ViewGroup.LayoutParams layoutParams = holder.iv_classify.getLayoutParams();
        layoutParams.width=widthPixels;
        layoutParams.height = widthPixels;
        MallClassBean mallClassBean = list.get(position);
        ImageLoaderUtils.imageLoad(context,mallClassBean.getClassImg(),holder.iv_classify);
        holder.tv_second_tab_name.setText(mallClassBean.getClassName());
        holder.ll_second_tab.setOnClickListener(new View.OnClickListener() {
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
//        return 9;
        return list==null?0:list.size();
    }

    private class ClassifyViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout ll_second_tab;
        public ImageView iv_classify;
        public TextView tv_second_tab_name;
        public ClassifyViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_second_tab = itemView.findViewById(R.id.ll_second_tab);
            iv_classify = itemView.findViewById(R.id.iv_classify);
            tv_second_tab_name = itemView.findViewById(R.id.tv_second_tab_name);
        }
    }
}
