/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kasa.ola.widget.slidingtab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.kasa.ola.R;
import com.kasa.ola.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.Locale;

public class PagerSlidingTabStrip extends HorizontalScrollView implements ViewPager.OnPageChangeListener {

    public interface IconTabProvider {

        public int getPageIconResId(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener itemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{android.R.attr.textSize, android.R.attr.textColor};
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;

    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    private int indicatorColor = 0xbd6aff;
    private int underlineColor = 0xbd6aff;
    private int dividerColor = 0x00000000;

    private boolean shouldExpand = false;
    private boolean textAllCaps = true;

    private int scrollOffset = 52;
    private int indicatorHeight = 4;
    private int indicatorWidth = 0;
    private int underlineHeight = 1;
    private int dividerPadding = 12;
    private int tabPadding = 24;
    private int dividerWidth = 1;
    private int tabWidth = 0;

    private int tabTextSize = 12;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.BOLD;

    private int lastScrollX = 0;

    private int tabBackgroundResId = R.drawable.bg_tab;

    private int checkedTextColor = 0xff2ca8af;
    private int unCheckedTextColor = 0xff999999;
    private int disableColor = 0xff999999;
    private ArrayList<Integer> disablePositions = new ArrayList<>();

    private Locale locale;

    public PagerSlidingTabStrip(Context context) {
        this(context, null);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        tabWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabWidth, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        indicatorWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorWidth, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

        indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor);
        underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor);
        checkedTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsCheckColor, checkedTextColor);
        unCheckedTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnCheckColor, unCheckedTextColor);
        disableColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDisableColor, disableColor);
        indicatorHeight = a
                .getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight);
        indicatorWidth = a
                .getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorWidth, indicatorWidth);
        underlineHeight = a
                .getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, tabPadding);
        tabBackgroundResId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground, tabBackgroundResId);
        shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset);
        tabWidth = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabWidth, tabWidth);
        textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(tabWidth > 0 ? tabWidth : LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        notifyDataSetChanged();
    }

    public void setSinglePage(int currentPosition, ArrayList<String> titles) {
        this.tabCount = titles.size();
        this.currentPosition = currentPosition;
        for (int i = 0; i < tabCount; i++) {
            addTextTab(i, titles.get(i));
        }
        updateTabStyles();
        setToItem(currentPosition);
    }

    public void setDisablePositions(ArrayList<Integer> disablePositions) {
        this.disablePositions = disablePositions;
    }

    public void notifyDataSetChanged() {
        if (null != pager) {
            tabsContainer.removeAllViews();

            tabCount = pager.getAdapter().getCount();

            for (int i = 0; i < tabCount; i++) {

                if (pager.getAdapter() instanceof IconTabProvider) {
                    addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
                } else {
                    addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
                }

            }

            updateTabStyles();

            getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @SuppressWarnings("deprecation")
                @SuppressLint("NewApi")
                @Override
                public void onGlobalLayout() {

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                    currentPosition = pager.getCurrentItem();
                    setChooseTabViewTextColor(currentPosition);
                    scrollToChild(currentPosition, 0);
                }
            });
        }
    }

    public void setToItem(int position) {
        currentPosition = position;
        setChooseTabViewTextColor(currentPosition);
        scrollToChild(currentPosition, 0);
    }

    private void addTextTab(final int position, String title) {
        RelativeLayout tab = new RelativeLayout(getContext());

        RelativeLayout.LayoutParams txtParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        txtParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        TextView tabText = new TextView(getContext());
        tabText.setText(title);
        tabText.setGravity(Gravity.CENTER);
        tabText.setSingleLine();
        tabText.setTag(position);
        tabText.setId(Integer.MAX_VALUE - 1000);
        tabText.setIncludeFontPadding(false);
        tabText.setLayoutParams(txtParams);

        TextView dot = new TextView(getContext());
        dot.setTextColor(Color.WHITE);
        dot.setBackgroundColor(Color.BLUE);
        dot.setGravity(Gravity.CENTER);
        dot.setIncludeFontPadding(false);
        dot.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.psts_dot_txt_size));
        dot.setSingleLine();
        Drawable dot_drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.apsts_tips, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            dot.setBackground(dot_drawable);
        } else {
            dot.setBackgroundDrawable(dot_drawable);
        }
        RelativeLayout.LayoutParams dotParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dotParams.height = DisplayUtils.dip2px(getContext(), 13);
        dotParams.width = DisplayUtils.dip2px(getContext(), 13);
        dotParams.bottomMargin = DisplayUtils.dip2px(getContext(), -7);
        dotParams.addRule(RelativeLayout.RIGHT_OF, tabText.getId());
        dotParams.addRule(RelativeLayout.ABOVE, tabText.getId());
        dot.setLayoutParams(dotParams);

        tab.addView(tabText);
        tab.addView(dot);
        dot.setVisibility(GONE);

        addTab(position, tab);
    }

    private void setChooseTabViewTextColor(int position) {
        TextView textView;
        for (int i = 0; i < tabCount; i++) {
            View v = tabsContainer.findViewWithTag(i);
            if (v instanceof TextView) {
                textView = (TextView) v;
                if (disablePositions.contains(i)) {
                    textView.setTextColor(disableColor);
                } else if (i == position) {
                    textView.setTextColor(checkedTextColor);
                } else {
                    textView.setTextColor(unCheckedTextColor);
                }
            }
        }
    }

    public void setTextViewDrawable(int position, Drawable drawable) {
        View view = tabsContainer.findViewWithTag(position);
        if (null != view && view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setCompoundDrawablePadding((int) (DisplayUtils.dip2px(getContext(), 6)));
            textView.setCompoundDrawables(null, null, drawable, null);
        }
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);
        tab.setTag(position);

        addTab(position, tab);

    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!disablePositions.contains(position)) {
                    currentPosition = position;
                    if (pager != null) {
                        pager.setCurrentItem(position);
                    } else {
                        scrollToChild(position, 0);
                        setChooseTabViewTextColor(currentPosition);
                    }
                    if (null != itemClickListener) {
                        itemClickListener.onItemClick(currentPosition);
                    }
                }
            }
        });

        tab.setPadding(tabPadding, 0, tabPadding, 0);
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            View vv = tabsContainer.findViewWithTag(i);
            if (vv instanceof TextView) {

                TextView tab = (TextView) vv;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);

                if (textAllCaps) {
                    tab.setAllCaps(true);
                }
            }
        }

    }

    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        int width = tabWidth == 0 ? tabsContainer.getChildAt(0).getWidth() : tabWidth;

        View view = tabsContainer.getChildAt(position);
        int newScrollX = view.getLeft() - width + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator line

        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates
        // between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        if (indicatorWidth > 0) {
            float tabWidthOffOffset = (lineRight - lineLeft - indicatorWidth) / 2;
            canvas.drawRect(lineLeft + tabWidthOffOffset, height - indicatorHeight, lineRight - tabWidthOffOffset, height, rectPaint);
        } else {
            canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);
        }

        // draw underline

        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

        // draw divider

        dividerPaint.setColor(dividerColor);
        for (int i = 0; i < tabCount - 1; i++) {
            View tab = tabsContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
        }
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public void setCheckedTextColorResource(int resId) {
        this.checkedTextColor = resId;
        invalidate();
    }

    public void setTextColor(int checkedColor, int uncheckedColor) {
        this.checkedTextColor = checkedColor;
        this.unCheckedTextColor = uncheckedColor;
        setChooseTabViewTextColor(currentPosition);
    }

    public int getTextSize() {
        return tabTextSize;
    }


    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {

        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentPosition = position;
        currentPositionOffset = positionOffset;
        scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            scrollToChild(pager.getCurrentItem(), 0);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setChooseTabViewTextColor(position);
    }

    public void showDot(int index) {
        RelativeLayout tab = (RelativeLayout) tabsContainer.getChildAt(index);
        TextView dot_layout = (TextView) tab.getChildAt(tab.getChildCount() - 1);
        dot_layout.setText("");
        dot_layout.setVisibility(View.VISIBLE);
    }

    public void showDot(int index, int dotTxt) {
        RelativeLayout tab = (RelativeLayout) tabsContainer.getChildAt(index);
        TextView dot_layout = (TextView) tab.getChildAt(tab.getChildCount() - 1);
        if (dotTxt > 0) {
            if (dotTxt > 99) {
                dot_layout.setText("···");
            } else {
                dot_layout.setText(dotTxt + "");
            }
            dot_layout.setVisibility(View.VISIBLE);
        } else {
            dot_layout.setVisibility(GONE);
        }
    }

    public void hideDot(int index) {
        RelativeLayout tab = (RelativeLayout) tabsContainer.getChildAt(index);
        TextView dot_layout = (TextView) tab.getChildAt(tab.getChildCount() - 1);
        if (dot_layout.getVisibility() == VISIBLE) {
            dot_layout.setVisibility(View.GONE);
        }
    }

}
