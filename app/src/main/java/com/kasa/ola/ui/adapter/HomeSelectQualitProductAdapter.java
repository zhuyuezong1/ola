package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.HomeBean;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class HomeSelectQualitProductAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<HomeBean.MallProductBean> list;
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public HomeSelectQualitProductAdapter(Context context, List<HomeBean.MallProductBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HomeSelectQualitProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_select_qualit,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        HomeSelectQualitProductViewHolder holder = (HomeSelectQualitProductViewHolder) viewHolder;
        HomeBean.MallProductBean mallProductBean = list.get(i);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int widthPixels = 0;
        if (list.size()==1){
            widthPixels = metrics.widthPixels- DisplayUtils.dip2px(context,14)*2- DisplayUtils.dip2px(context,5)*2;
        }else {
            widthPixels = metrics.widthPixels- DisplayUtils.dip2px(context,14)*2- DisplayUtils.dip2px(context,5)*2-DisplayUtils.dip2px(context,20)*2;
        }
        ViewGroup.LayoutParams layoutParams = holder.item_select_qualit_view.getLayoutParams();
        layoutParams.width=widthPixels;
        ViewGroup.LayoutParams layoutParams1 = holder.iv_select_qualit.getLayoutParams();
        layoutParams1.height=widthPixels*350/664;
        ImageLoaderUtils.imageLoad(context,mallProductBean.getImageUrl(),holder.iv_select_qualit);
        holder.tv_select_qualit_title.setText(mallProductBean.getProductName());
        holder.item_select_qualit_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(i);
                }
            }
        });
        holder.tv_select_qualit_title.setText(mallProductBean.getProductName());
        holder.tv_price.setText("￥"+mallProductBean.getPrice());
        holder.tv_describe.setText(TextUtils.isEmpty(mallProductBean.getDescribe())?"":mallProductBean.getDescribe());
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
//        return 20;
        return list==null?0:list.size();
    }

    public class HomeSelectQualitProductViewHolder extends RecyclerView.ViewHolder {

        public CardView item_select_qualit_view;
        public ImageView iv_select_qualit;
        public TextView tv_select_qualit_title;
        public TextView tv_price;
        public TextView tv_describe;

        public HomeSelectQualitProductViewHolder(@NonNull View itemView) {
            super(itemView);
            item_select_qualit_view = itemView.findViewById(R.id.item_select_qualit_view);
            iv_select_qualit = itemView.findViewById(R.id.iv_select_qualit);
            tv_select_qualit_title = itemView.findViewById(R.id.tv_select_qualit_title);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_describe = itemView.findViewById(R.id.tv_describe);
        }
    }

}
