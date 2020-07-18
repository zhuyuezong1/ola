package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.MallProductBean;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.ui.ProductListActivity;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<MallProductBean> mallProductBeans;
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ProductAdapter(Context context, List<MallProductBean> mallProductBeans) {
        this.context = context;
        this.mallProductBeans = mallProductBeans;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new ProductAdapter.ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ProductViewHolder holder = (ProductViewHolder) viewHolder;
        MallProductBean mallProductBean = mallProductBeans.get(position);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int widthPixels = 0;
        widthPixels = metrics.widthPixels- /*DisplayUtils.dip2px(context,14)*2-*/ DisplayUtils.dip2px(context,5)*2;
        ViewGroup.LayoutParams layoutParams = holder.iv_product.getLayoutParams();
        layoutParams.width=widthPixels/2;
        layoutParams.height = widthPixels/2;
        if (!TextUtils.isEmpty(mallProductBean.getImageUrl())) {
            ImageLoaderUtils.imageLoad(context,mallProductBean.getImageUrl(),holder.iv_product);
        }
        if (!TextUtils.isEmpty(mallProductBean.getProductName())) {
            holder.tv_product_title.setText(mallProductBean.getProductName());
        }
        if (!TextUtils.isEmpty(mallProductBean.getUnit())){
            if (!TextUtils.isEmpty(mallProductBean.getPrice()) ) {
                holder.tv_price.setText("￥"+mallProductBean.getPrice()+"/"+mallProductBean.getUnit());
            }
        }else {
            if (!TextUtils.isEmpty(mallProductBean.getPrice()) ) {
                holder.tv_price.setText("￥"+mallProductBean.getPrice());
            }
        }

        holder.item_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });

    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
//        return 6;
        return mallProductBeans==null?0:mallProductBeans.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_product;
        public TextView tv_product_title;
        public TextView tv_price;
        public RelativeLayout item_product;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_product  = itemView.findViewById(R.id.iv_product);
            tv_product_title = itemView.findViewById(R.id.tv_product_title);
            tv_price = itemView.findViewById(R.id.tv_price);
            item_product = itemView.findViewById(R.id.item_product);

        }
    }
}
