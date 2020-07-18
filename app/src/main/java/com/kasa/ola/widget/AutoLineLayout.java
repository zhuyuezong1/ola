package com.kasa.ola.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.kasa.ola.R;


/**
 * 自动换行ViewGroup
 * Created by guan on 2018/6/7.
 */

public class AutoLineLayout extends ViewGroup {
    private int rowHeight;
    private boolean centerInRow;
    public AutoLineLayout(Context context) {
        this(context, null);
    }

    public AutoLineLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AutoLineLayout);
        rowHeight = typedArray.getDimensionPixelSize(R.styleable.AutoLineLayout_row_height, 100);
        centerInRow = typedArray.getBoolean(R.styleable.AutoLineLayout_center_in_row, true);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int count = getChildCount();
        int row = 1;
        int widthSpace = width;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidthSpec;
            int childHeightSpec;
            if (lp.width > 0) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(Math.min(width, lp.width), MeasureSpec.EXACTLY);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            }
            if (lp.height > 0) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(Math.min(rowHeight, lp.height), MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(rowHeight, MeasureSpec.AT_MOST);
            }
            child.measure(childWidthSpec, childHeightSpec);
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            if (childWidth <= widthSpace) {
                child.setTag(row);
                widthSpace -= childWidth;
            } else {
                row++;
                child.setTag(row);
                widthSpace = width - childWidth;
            }
        }
        int heightSpec = MeasureSpec.makeMeasureSpec(rowHeight * row + getPaddingTop() + getPaddingBottom(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            View preChild = getChildAt(i - 1);
            MarginLayoutParams childLP = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            int tempTop = centerInRow ? (rowHeight - childHeight) / 2 : childLP.topMargin;
            int childRow = (int) child.getTag();
            int left;
            int top;
            int right;
            int bottom;
            if (preChild != null && (int) preChild.getTag() == childRow) {
                MarginLayoutParams preChildLP = (MarginLayoutParams) preChild.getLayoutParams();
                left = preChild.getRight() + preChildLP.rightMargin + childLP.leftMargin;
            } else {
                left = getPaddingLeft() + childLP.leftMargin;
            }
            right = left + childWidth;
            top = getPaddingTop() + rowHeight * (childRow - 1) + tempTop;
            bottom = top + childHeight;
            child.layout(left, top, right, bottom);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
