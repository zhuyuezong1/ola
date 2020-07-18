package com.kasa.ola.widget.banner;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kasa.ola.R;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.util.BannerUtils;

import java.util.List;

/**
 * 自定义布局,多个不同UI切换
 */
public class MultipleTypesAdapter extends BannerAdapter<BannerDataBean, RecyclerView.ViewHolder> {
    private Context context;

    public MultipleTypesAdapter(Context context, List<BannerDataBean> mDatas) {
        super(mDatas);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                return new ImageHolder(BannerUtils.getView(parent, R.layout.banner_image));
            case 2:
                return new VideoHolder(BannerUtils.getView(parent, R.layout.banner_video));
            case 3:
                return new TitleHolder(BannerUtils.getView(parent, R.layout.banner_title));
        }
        return new ImageHolder(BannerUtils.getView(parent, R.layout.banner_image));
    }

    @Override
    public int getItemViewType(int position) {
        return getData(getRealPosition(position)).viewType;
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, BannerDataBean data, int position, int size) {
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case 1:
                ImageHolder imageHolder = (ImageHolder) holder;
//                imageHolder.imageView.setImageResource(data.imageRes);
                Glide.with(imageHolder.itemView)
                        .load(data.imageUrl)
//                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                        .into(imageHolder.imageView);
                break;
            case 2:
                VideoHolder videoHolder = (VideoHolder) holder;
                videoHolder.player.setUp(data.imageUrl, true, null);
                videoHolder.player.getBackButton().setVisibility(View.GONE);
                //增加封面
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageResource(R.mipmap.education_home_banner);
                videoHolder.player.setThumbImageView(imageView);
                videoHolder.player.getFullscreenButton().setVisibility(View.GONE);
//                videoHolder.player.startPlayLogic();
                break;
            case 3:
                TitleHolder titleHolder = (TitleHolder) holder;
                titleHolder.title.setText(data.title);
                titleHolder.title.setBackgroundColor(Color.parseColor(BannerDataBean.getRandColor()));
                break;
        }
    }


}
