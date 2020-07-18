package com.kasa.ola.widget.bottomdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BottomDialog1 {
    public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;
    public static final int VERTICAL = OrientationHelper.VERTICAL;

    public static final int LINEAR = 0;
    public static final int GRID = 1;

    private CustomDialog customDialog;
    private LinearLayout view;

    public BottomDialog1(Context context) {
        customDialog = new CustomDialog(context);
    }

    public BottomDialog1 title(String title) {
        customDialog.title(title);
        return this;
    }

    public BottomDialog1 title(int title) {
        customDialog.title(title);
        return this;
    }

    public BottomDialog1 background(int res) {
        customDialog.background(res);
        return this;
    }

    public BottomDialog1 inflateMenu(int menu, OnItemClickListener onItemClickListener) {
        customDialog.inflateMenu(menu, onItemClickListener);
        return this;
    }

    public BottomDialog1 layout(int layout) {
        customDialog.layout(layout);
        return this;
    }

    public BottomDialog1 orientation(int orientation) {
        customDialog.orientation(orientation);
        return this;
    }

    public BottomDialog1 addItems(List<Item> items, OnItemClickListener onItemClickListener) {
        customDialog.addItems(items, onItemClickListener);
        return this;
    }

    /**
     * @deprecated
     */
    public BottomDialog1 itemClick(OnItemClickListener listener) {
        customDialog.setItemClick(listener);
        return this;
    }

    public void show() {
        customDialog.show();
    }

    public class CustomDialog extends Dialog {
        private LinearLayout background;
        private LinearLayout container;
        private TextView titleView;

        private DialogAdapter adapter;

        private int padding;
        private int topPadding;
        private int leftPadding;
        private int topIcon;
        private int leftIcon;

        private int orientation;
        private int layout;

        CustomDialog(Context context) {
            super(context, R.style.BottomDialog);
            init();
        }
        public void close(){
            if (customDialog!=null){
                dismiss();
            }
        }
        private void init() {
            padding = getContext().getResources().getDimensionPixelSize(R.dimen.dp_10);
            topPadding = getContext().getResources().getDimensionPixelSize(R.dimen.dp_4);
            leftPadding = getContext().getResources().getDimensionPixelSize(R.dimen.dp_10);
            topIcon = getContext().getResources().getDimensionPixelSize(R.dimen.dp_38);
            leftIcon = getContext().getResources().getDimensionPixelSize(R.dimen.dp_32);

            setContentView(R.layout.bottom_dialog);
            setCancelable(true);
            setCanceledOnTouchOutside(true);
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            view = findViewById(R.id.view);
            background = (LinearLayout) findViewById(R.id.background);
            titleView = (TextView) findViewById(R.id.title);
            container = (LinearLayout) findViewById(R.id.container);
            findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

        void addItems(List<Item> items, OnItemClickListener onItemClickListener) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RecyclerView.LayoutManager manager;

            adapter = new DialogAdapter(items, layout, orientation);
            adapter.setItemClick(onItemClickListener);

            if (layout == LINEAR)
                manager = new LinearLayoutManager(getContext(), orientation, false);
            else if (layout == GRID)
                manager = new GridLayoutManager(getContext(), 5, orientation, false);
            else manager = new LinearLayoutManager(getContext(), orientation, false);

            RecyclerView recyclerView = new RecyclerView(getContext());
            recyclerView.setLayoutParams(params);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);

            container.addView(recyclerView);
        }

        public void title(int title) {
            title(getContext().getString(title));
        }

        public void title(String title) {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        }

        public void layout(int layout) {
            this.layout = layout;
            if (adapter != null) adapter.setLayout(layout);
        }

        public void orientation(int orientation) {
            this.orientation = orientation;
            if (adapter != null) adapter.setOrientation(orientation);
        }

        public void background(int res) {
            background.setBackgroundResource(res);
        }

        void inflateMenu(int menu, OnItemClickListener onItemClickListener) {
            MenuInflater menuInflater = new SupportMenuInflater(getContext());
            MenuBuilder menuBuilder = new MenuBuilder(getContext());
            menuInflater.inflate(menu, menuBuilder);
            List<Item> items = new ArrayList<>();
            for (int i = 0; i < menuBuilder.size(); i++) {
                MenuItem menuItem = menuBuilder.getItem(i);
                items.add(new Item(menuItem.getItemId(), menuItem.getTitle().toString(), menuItem.getIcon()));
            }
            addItems(items, onItemClickListener);
        }

        void setItemClick(OnItemClickListener onItemClickListener) {
            adapter.setItemClick(onItemClickListener);
        }

        /**
         * recycler view adapter, provide HORIZONTAL and VERTICAL item style
         */
        private class DialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

            private List<Item> mItems = Collections.emptyList();
            private OnItemClickListener itemClickListener;

            private int orientation;
            private int layout;

            DialogAdapter(List<Item> mItems, int layout, int orientation) {
                setList(mItems);
                this.layout = layout;
                this.orientation = orientation;
            }

            private void setList(List<Item> items) {
                mItems = items == null ? new ArrayList<Item>() : items;
            }

            void setItemClick(OnItemClickListener onItemClickListener) {
                this.itemClickListener = onItemClickListener;
            }

            public void setOrientation(int orientation) {
                this.orientation = orientation;
                notifyDataSetChanged();
            }

            public void setLayout(int layout) {
                this.layout = layout;
                notifyDataSetChanged();
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (layout == GRID)
                    return new TopHolder(new LinearLayout(parent.getContext()));
                else if (orientation == HORIZONTAL)
                    return new TopHolder(new LinearLayout(parent.getContext()));
                else return new LeftHolder(new LinearLayout(parent.getContext()));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                final Item item = mItems.get(position);

                TopHolder topHolder;
                LeftHolder leftHolder;

                if (layout == GRID) {
                    topHolder = (TopHolder) holder;

                    topHolder.item.setText(item.getTitle());
                    topHolder.item.setCompoundDrawablesWithIntrinsicBounds(null, topHolder.icon(item.getIcon()), null, null);
                    topHolder.item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            if (itemClickListener != null) itemClickListener.click(item,customDialog);
                        }
                    });
                } else if (orientation == HORIZONTAL) {
                    topHolder = (TopHolder) holder;

                    topHolder.item.setText(item.getTitle());
                    topHolder.item.setCompoundDrawablesWithIntrinsicBounds(null, topHolder.icon(item.getIcon()), null, null);
                    topHolder.item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            if (itemClickListener != null) itemClickListener.click(item,customDialog);
                        }
                    });
                } else {
                    leftHolder = (LeftHolder) holder;

                    leftHolder.item.setText(item.getTitle());
                    leftHolder.item.setCompoundDrawablesWithIntrinsicBounds(leftHolder.icon(item.getIcon()), null, null, null);
                    leftHolder.item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            if (itemClickListener != null) itemClickListener.click(item,customDialog);
                        }
                    });
                }
            }

            @Override
            public int getItemCount() {
                return mItems.size();
            }

            /**
             * horizontal item adapter
             */
            class TopHolder extends RecyclerView.ViewHolder {
                private TextView item;

                TopHolder(View view) {
                    super(view);

                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.width = DisplayUtils.getScreenWidth(getContext()) / 5;

                    item = new TextView(view.getContext());
                    item.setLayoutParams(params);
                    item.setMaxLines(1);
                    item.setEllipsize(TextUtils.TruncateAt.END);
                    item.setGravity(Gravity.CENTER);
                    item.setTextColor(ContextCompat.getColor(view.getContext(),R.color.COLOR_FF646464));
                    item.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.textsize_10));
                    item.setCompoundDrawablePadding(topPadding);
                    item.setPadding(0, padding, 0, padding);

                    TypedValue typedValue = new TypedValue();
                    view.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
                    item.setBackgroundResource(typedValue.resourceId);

                    ((LinearLayout) view).addView(item);
                }

                private Drawable icon(Drawable drawable) {
                    if (drawable != null) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        @SuppressWarnings("SuspiciousNameCombination") Drawable resizeIcon = new BitmapDrawable(getContext().getResources(), Bitmap.createScaledBitmap(bitmap, topIcon, topIcon, true));
                        Drawable.ConstantState state = resizeIcon.getConstantState();
                        resizeIcon = DrawableCompat.wrap(state == null ? resizeIcon : state.newDrawable().mutate());
                        return resizeIcon;
                    }
                    return null;
                }
            }

            class LeftHolder extends RecyclerView.ViewHolder {
                private TextView item;

                LeftHolder(View view) {
                    super(view);

                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    view.setLayoutParams(params);
                    item = new TextView(view.getContext());
                    item.setLayoutParams(params);
                    item.setMaxLines(1);
                    item.setEllipsize(TextUtils.TruncateAt.END);
                    item.setGravity(Gravity.CENTER_VERTICAL);
                    item.setTextColor(ContextCompat.getColor(view.getContext(), R.color.black));
                    item.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.textsize_14));
                    item.setCompoundDrawablePadding(leftPadding);
                    item.setPadding(padding, padding, padding, padding);

                    TypedValue typedValue = new TypedValue();
                    view.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
                    item.setBackgroundResource(typedValue.resourceId);

                    ((LinearLayout) view).addView(item);
                }

                private Drawable icon(Drawable drawable) {
                    if (drawable != null) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        @SuppressWarnings("SuspiciousNameCombination") Drawable resizeIcon = new BitmapDrawable(getContext().getResources(), Bitmap.createScaledBitmap(bitmap, leftIcon, leftIcon, true));
                        Drawable.ConstantState state = resizeIcon.getConstantState();
                        resizeIcon = DrawableCompat.wrap(state == null ? resizeIcon : state.newDrawable().mutate());
                        return resizeIcon;
                    }
                    return null;
                }
            }
        }
    }
}
