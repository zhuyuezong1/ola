package com.kasa.ola.widget.slidingtab;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kasa.ola.R;


public class SlidingTab extends LinearLayout implements OnClickListener {

	private int SELECTED_COLOR = Color.parseColor("#2CA8AF");
	private int NORMAL_COLOR = Color.parseColor("#666666");
	private int DEVIDER_COLOR = Color.parseColor("#666666");
	private String[] mItems;
	private int parent_width = 0;
	private int item_width = 0;
	private int currentIndex = 0;
	private View view_mask;
	private OnSlidingTabItemSelectListener mOnSlidingTabItemSelectListener = null;
	private int itemLabelPadding = 0;
	private int itemMaskPadding = 0;
	private float labelTextSize = 0;
	private int defaultMaskHeight = 0;

	public SlidingTab(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	public SlidingTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public SlidingTab(Context context) {
		super(context);
		init(null);
	}

	private void init(AttributeSet attrs) {
		setOrientation(LinearLayout.VERTICAL);
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SlidingTab);
		itemLabelPadding = a.getDimensionPixelSize(R.styleable.SlidingTab_labelPadding, 0);
		itemMaskPadding = a.getDimensionPixelSize(R.styleable.SlidingTab_maskPadding, 0);
		defaultMaskHeight = a.getDimensionPixelSize(R.styleable.SlidingTab_maskHeight, 0);
		SELECTED_COLOR = a.getColor(R.styleable.SlidingTab_selectColor, android.R.color.black);
		NORMAL_COLOR = a.getColor(R.styleable.SlidingTab_normalColor, android.R.color.white);
		DEVIDER_COLOR = a.getColor(R.styleable.SlidingTab_deviderColor, android.R.color.white);
		labelTextSize = a.getDimensionPixelSize(R.styleable.SlidingTab_labelTextSize, 16);
        item_width = a.getDimensionPixelSize(R.styleable.SlidingTab_tabItemWidth, 0);
		a.recycle();
	}

	public void setLabels(String[] items) {
		this.mItems = items;
		removeAllViews();
		parent_width = getResources().getDisplayMetrics().widthPixels;
		if (null != mItems && item_width == 0) {
			item_width = parent_width / mItems.length;
		}
		initLabels();
	}

	private void initLabels() {
		if (null != mItems) {
			LinearLayout mLinearLayout = new LinearLayout(getContext());
			mLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
			for (int i = 0; i < mItems.length; i++) {
				String title = mItems[i];
				TextView mTextView = new TextView(getContext());
				mTextView.setLayoutParams(
						new LayoutParams(item_width, LayoutParams.WRAP_CONTENT));
				mTextView.setSingleLine(true);
				mTextView.setPadding(0, itemLabelPadding, 0, itemLabelPadding);
				mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, labelTextSize, getResources().getDisplayMetrics()));
				mTextView.setGravity(Gravity.CENTER);
				if (i == currentIndex) {
					mTextView.setTextColor(SELECTED_COLOR);
				} else {
					mTextView.setTextColor(NORMAL_COLOR);
				}
				mTextView.setText(title);
				mTextView.setTag(i);
				mTextView.setOnClickListener(this);
				mLinearLayout.addView(mTextView);
			}
			addView(mLinearLayout);
			initMask();
			initDevider();
		}
	}

	private void initMask() {
		view_mask = new View(getContext());
		LayoutParams lp = new LayoutParams(item_width - itemMaskPadding * 2,
				defaultMaskHeight);
		lp.leftMargin = itemMaskPadding;
		view_mask.setLayoutParams(lp);
		view_mask.setBackgroundColor(SELECTED_COLOR);
		addView(view_mask);
	}

	private void initDevider() {
		View view = new View(getContext());
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
		view.setBackgroundColor(DEVIDER_COLOR);
		addView(view);
	}

	public void setViewDrawable(int position, Drawable drawable) {
        TextView textView = (TextView) findViewWithTag(position);
        if (null != textView)  {
            textView.setCompoundDrawables(null, null, drawable, null);
            textView.setPadding((int) (itemLabelPadding * 3.5), itemLabelPadding, (int) (itemLabelPadding * 3.5), itemLabelPadding);
        }
    }

	public void setCurrentItem(final int pos) {
		if (currentIndex != pos) {
			ValueAnimator mValueAnimator = ValueAnimator.ofFloat((float) item_width * currentIndex,
					(float) item_width * pos);
			mValueAnimator.setInterpolator(new DecelerateInterpolator());
			mValueAnimator.setDuration(100);
			mValueAnimator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					if (null != view_mask) {
						view_mask.setTranslationX((Float) animation.getAnimatedValue());
					}
				}
			});
			mValueAnimator.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
					View view_last = findViewWithTag(currentIndex);
					if (view_last != null) {
						((TextView) view_last).setTextColor(NORMAL_COLOR);
					}
				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					View view_now = findViewWithTag(pos);
					if (view_now != null) {
						((TextView) view_now).setTextColor(SELECTED_COLOR);
					}
				}

				@Override
				public void onAnimationCancel(Animator animation) {

				}
			});
			mValueAnimator.start();
			currentIndex = pos;
		}
	}

	public void setOnSlidingTabItemSelectListener(OnSlidingTabItemSelectListener l) {
		this.mOnSlidingTabItemSelectListener = l;
	}

	public interface OnSlidingTabItemSelectListener {
		void onSlidingTabItemSelect(int pos);
	}

	@Override
	public void onClick(View v) {
		if (v != null) {
			int pos = (Integer) v.getTag();
			setCurrentItem(pos);
			if (mOnSlidingTabItemSelectListener != null) {
				mOnSlidingTabItemSelectListener. onSlidingTabItemSelect(pos);
			}
		}
	}
}
