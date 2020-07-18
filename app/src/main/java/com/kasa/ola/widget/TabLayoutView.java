package com.kasa.ola.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.core.content.res.ResourcesCompat;

import com.kasa.ola.R;

import java.util.ArrayList;
import java.util.List;

public class TabLayoutView extends LinearLayout {

    private Context context;
    private String[] titles; //要显示的标题
    private int[] imgs; //图标
    private int imgwidth;
    private int imgheight;
    private int txtsize; //标题大小
    private int txtColor; //标题未选中颜色
    private int txtSelectedColor; //选择颜色

    private List<TextView> textViews; //保存标题
    private List<ImageView> imageViews; //保存图片
    private List<TextView> tvDots;//保存圆点
    private int currentIndex = 0;

    private OnItemOnclickListener onItemOnclickListener;

    public TabLayoutView(Context context) {
        this(context, null);
    }

    public TabLayoutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setOnItemOnclickListener(OnItemOnclickListener onItemOnclickListener) {
        this.onItemOnclickListener = onItemOnclickListener;
    }

    /**
     * 设置标题、图片和当前选中的条目
     *
     * @param tabtxts
     * @param imgs
     * @param currentIndex
     */
    public void setDataSource(String[] tabtxts, int[] imgs, int currentIndex) {
        this.titles = tabtxts;
        this.imgs = imgs;
        this.currentIndex = currentIndex;
    }
    /**
     * 设置图标大小
     *
     * @param imgwidth
     * @param imgheight
     */
    public void setImageStyle(int imgwidth, int imgheight) {
        this.imgwidth = dip2px(context, imgwidth);
        this.imgheight = dip2px(context, imgheight);
    }

    /**
     * 设置标题颜色
     *
     * @param txtsize
     * @param txtColor
     * @param txtSelectedColor
     */
    public void setTextStyle(int txtsize, int txtColor, int txtSelectedColor) {
        this.txtsize = txtsize;
        this.txtColor = txtColor;
        this.txtSelectedColor = txtSelectedColor;
    }

    /**
     * 动态布局
     * 1、外层为横向线下布局
     * 2、动态添加相对布局，平分父布局，使宽度一致，添加到横向布局中
     * 3、总线布局添加图标和标题，并添加到相对布局中
     * 4、添加圆点到相对布局中，并设置在3的右上角
     */
    public void initDatas() {
        textViews = new ArrayList<>();
        imageViews = new ArrayList<>();
        tvDots = new ArrayList<>();

        setOrientation(HORIZONTAL);
        LayoutParams lp = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;

        LayoutParams imglp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imglp.gravity = Gravity.CENTER;

        LayoutParams txtlp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txtlp.gravity = Gravity.CENTER;

        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        int size = titles.length;
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(imglp);
            imageView.setImageResource(imgs[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER);

            TextView textView = new TextView(context);
            textView.setText(titles[i]);
            textView.setLayoutParams(txtlp);
            textView.setTextSize(txtsize);

            LinearLayout cly = new LinearLayout(context);
            cly.setId(i + 100);
            cly.setGravity(Gravity.CENTER);
            cly.setOrientation(VERTICAL);
            cly.setLayoutParams(imglp);
            cly.addView(imageView);

            RelativeLayout prl = new RelativeLayout(context);

            RelativeLayout.LayoutParams rlDot = new RelativeLayout.LayoutParams(dip2px(context, 16), dip2px(context, 16));
            rlDot.addRule(RelativeLayout.RIGHT_OF, cly.getId());
            rlDot.addRule(RelativeLayout.ABOVE, cly.getId());
            rlDot.setMargins(-dip2px(context, 8), 0, 0, -dip2px(context, 15));

            TextView dot = new TextView(getContext());
            dot.setTextColor(Color.WHITE);
            dot.setBackgroundColor(Color.BLUE);
            dot.setGravity(Gravity.CENTER);
            dot.setIncludeFontPadding(false);
            dot.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.textsize_12));
            dot.setSingleLine();
            Drawable dot_drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.apsts_tips, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                dot.setBackground(dot_drawable);
            } else {
                dot.setBackgroundDrawable(dot_drawable);
            }
//            RelativeLayout.LayoutParams dotParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            dotParams.height = DisplayUtils.dip2px(getContext(), 16);
//            dotParams.width = DisplayUtils.dip2px(getContext(), 16);
//            dot.setLayoutParams(dotParams);
            dot.setVisibility(GONE);

            final int index = i;
            prl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
//                    setSelectStyle(index);
                    if (onItemOnclickListener != null) {
                        onItemOnclickListener.onItemClick(index);
                    }
                }
            });

            cly.addView(textView);
            prl.addView(cly, rlp);
            prl.addView(dot, rlDot);
            addView(prl, lp);

            textViews.add(textView);
            imageViews.add(imageView);
            tvDots.add(dot);
        }
        setSelectStyle(currentIndex);
    }

    public void initDatasVertical() {
        textViews = new ArrayList<>();
        imageViews = new ArrayList<>();
        tvDots = new ArrayList<>();

        setOrientation(VERTICAL);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;

        LayoutParams imglp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imglp.gravity = Gravity.CENTER;

        LayoutParams txtlp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txtlp.gravity = Gravity.CENTER;

        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        int size = titles.length;
        for (int i = 0; i < size; i++) {
//            ImageView imageView = new ImageView(context);
//            imageView.setLayoutParams(imglp);
//            imageView.setImageResource(imgs[i]);
//            imageView.setScaleType(ImageView.ScaleType.CENTER);


            TextView textView = new TextView(context);
            textView.setText(titles[i]);
            textView.setLayoutParams(txtlp);
            textView.setTextSize(txtsize);
            textView.setPadding(0,40,0,40);
            textView.setGravity(Gravity.CENTER);


            RelativeLayout prl = new RelativeLayout(context);

            final int index = i;
            prl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
//                    setSelectStyle(index);
                    if (onItemOnclickListener != null) {
                        onItemOnclickListener.onVerticalItemClick(index,view);
                    }
                }
            });
            prl.addView(textView);
            addView(prl, lp);
            textViews.add(textView);
        }
        setSelectStyleVertical(currentIndex);
    }

    public void setSelectStyleVertical(int index) {
        int size = titles.length;
        for (int i = 0; i < size; i++) {
            if (i == index) {
                textViews.get(i).setTextColor(context.getResources().getColor(txtSelectedColor));
                textViews.get(i).setBackgroundResource(R.drawable.shape_select_mall_tab);
                textViews.get(i).setSelected(true);
            } else {
                textViews.get(i).setTextColor(context.getResources().getColor(txtColor));
                textViews.get(i).setBackgroundResource(R.drawable.shape_unselect_mall_tab);
                textViews.get(i).setSelected(false);
            }
        }
    }
    public void setSelectStyleVertical(int index,int selectBg,int unselectBg) {
        int size = titles.length;
        for (int i = 0; i < size; i++) {
            if (i == index) {
                textViews.get(i).setTextColor(context.getResources().getColor(txtSelectedColor));
                textViews.get(i).setBackgroundResource(selectBg);
                textViews.get(i).setSelected(true);
            } else {
                textViews.get(i).setTextColor(context.getResources().getColor(txtColor));
                textViews.get(i).setBackgroundResource(unselectBg);
                textViews.get(i).setSelected(false);
            }
        }
    }
    public void setSelectStyle(int index) {
        int size = titles.length;
        for (int i = 0; i < size; i++) {
            if (i == index) {
                textViews.get(i).setTextColor(context.getResources().getColor(txtSelectedColor));
                imageViews.get(i).setSelected(true);
            } else {
                textViews.get(i).setTextColor(context.getResources().getColor(txtColor));
                imageViews.get(i).setSelected(false);
            }
        }
    }

    /**
     * 设置圆点
     *
     * @param index 圆点索引
     * @param count 圆点个数
     */
    public void setDotsCount(int index, int count) {
        if (tvDots == null || index > tvDots.size() - 1)
            return;
        if (count > 0) {
            tvDots.get(index).setVisibility(VISIBLE);
            if (count > 99) {
                tvDots.get(index).setText("···");
            } else {
                tvDots.get(index).setText(count + "");
            }
        } else {
            tvDots.get(index).setVisibility(GONE);
        }
    }

    public interface OnItemOnclickListener {
        void onItemClick(int index);
        void onVerticalItemClick(int index,View view);
    }

    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
