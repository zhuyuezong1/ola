package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.CardBean;
import com.kasa.ola.ui.CardActivity;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.ArrayList;


public class CardAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<CardBean> cardModels;
    private CardBean tempCardBean;
    private CardListener cardListener;

    public void setCardListener(CardListener cardListener) {
        this.cardListener = cardListener;
    }

    public interface CardListener {
        void onDefaultClick(CardBean cardModel);

        void onDeleteClick(CardBean cardModel);

        void onItemClick(CardBean cardModel);
    }

    public CardAdapter(Context context, ArrayList<CardBean> cardModels) {
        this.context = context;
        this.cardModels = cardModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new CardViewHolder(LayoutInflater.from(context).inflate(R.layout.view_card_item, parent, false));
        if (viewType == 0) {
            return new CardViewHolder(LayoutInflater.from(context).inflate(R.layout.view_card_item, parent, false));
        } else {
            return new CardAddViewHolder(LayoutInflater.from(context).inflate(R.layout.view_empty_card, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == cardModels.size()) {
            CardAddViewHolder cardAddViewHolder = (CardAddViewHolder) holder;
            cardAddViewHolder.view_add_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, CardActivity.class));
                }
            });
        } else {
            final CardBean cardBean = cardModels.get(position);
            final CardViewHolder cardViewHolder = (CardViewHolder) holder;
            if (cardBean.getIsDefault()==1) {
                tempCardBean = cardBean;
            }
            cardViewHolder.view_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != cardListener) {
                        cardListener.onItemClick(cardBean);
                    }
                }
            });
            ImageLoaderUtils.imageLoadRound(context, cardBean.getBgImageUrl(), cardViewHolder.iv_bg_img, 16);
            ImageLoaderUtils.imageLoadRound(context, cardBean.getBankImageUrl(), cardViewHolder.iv_icon_img);
            cardViewHolder.tv_bank_name.setText(cardBean.getBankName());
            if (cardBean.getBankType() == 0) {
                cardViewHolder.tv_card_type.setText(context.getString(R.string.deposit_card));
            } else if (cardBean.getBankType() == 1) {
                cardViewHolder.tv_card_type.setText(context.getString(R.string.credit_card));
            }
            cardViewHolder.tv_card_num.setText(context.getString(R.string.bank_card_no,
                    cardBean.getBankNo().substring(cardBean.getBankNo().length() - 4)));
            Drawable checkDrawable = context.getResources().getDrawable(cardBean.getIsDefault()==1 ? R.mipmap.check_icon : R.mipmap.uncheck_icon);
            checkDrawable.setBounds(0, 0, checkDrawable.getIntrinsicWidth(), checkDrawable.getMinimumHeight());
            cardViewHolder.tv_set_default.setCompoundDrawables(checkDrawable, null, null, null);
            cardViewHolder.tv_set_default.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cardBean.getIsDefault()==0) {
                        cardBean.setIsDefault(1);
                        if (null != tempCardBean) {
                            tempCardBean.setIsDefault(0);
                            tempCardBean = cardBean;
                        }
                        Drawable drawable = context.getResources().getDrawable(R.mipmap.uncheck_icon);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getMinimumHeight());
                        cardViewHolder.tv_set_default.setCompoundDrawables(drawable, null, null, null);
                        if (null != cardListener) {
                            cardListener.onDefaultClick(cardBean);
                        }
                        notifyDataSetChanged();
                    }
                }
            });
            cardViewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardModels.remove(cardBean);
                    if (null != cardListener) {
                        cardListener.onDeleteClick(cardBean);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cardModels.size()+ 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == cardModels.size()) {
            return 1;
        } else {
            return 0;
        }
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_bg_img;
        public ImageView iv_icon_img;
        public TextView tv_bank_name;
        public TextView tv_card_type;
        public TextView tv_card_num;
        public TextView tv_set_default;
        public TextView tv_delete;
        public RelativeLayout view_container;

        public CardViewHolder(View itemView) {
            super(itemView);
            iv_bg_img = itemView.findViewById(R.id.iv_bg_img);
            iv_icon_img = itemView.findViewById(R.id.iv_icon_img);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            tv_card_type = itemView.findViewById(R.id.tv_card_type);
            tv_card_num = itemView.findViewById(R.id.tv_card_num);
            tv_set_default = itemView.findViewById(R.id.tv_set_default);
            tv_delete = itemView.findViewById(R.id.tv_delete);
            view_container = itemView.findViewById(R.id.view_container);
        }
    }

    public class CardAddViewHolder extends RecyclerView.ViewHolder {

        public View view_add_card;

        public CardAddViewHolder(View itemView) {
            super(itemView);
            view_add_card = itemView.findViewById(R.id.view_add_card);
        }
    }

}
