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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class BottomDialog extends Dialog{
    public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;
    public static final int VERTICAL = OrientationHelper.VERTICAL;

    public static final int LINEAR = 0;
    public static final int GRID = 1;
    public static int padding;
    public static int topPadding;
    public static int leftPadding;
    public static int topIcon;
    public static int leftIcon;

//    private CustomDialog customDialog;
    private LinearLayout view;

//    public BottomDialog(Context context) {
//        customDialog = new CustomDialog(context);
//    }
    public BottomDialog(@NonNull Context context) {
        super(context, R.style.BottomDialog);
        padding = context.getResources().getDimensionPixelSize(R.dimen.dp_10);
        topPadding = context.getResources().getDimensionPixelSize(R.dimen.dp_4);
        leftPadding= context.getResources().getDimensionPixelSize(R.dimen.dp_10);
        topIcon= context.getResources().getDimensionPixelSize(R.dimen.dp_38);
        leftIcon = context.getResources().getDimensionPixelSize(R.dimen.dp_32);
    }

    public BottomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
    public static class Builder{
        private Context context;
        private String title;
        private int bgRes;
        private int menu;
        private int layout;
        private int orientation;
        private DialogAdapter adapter;
        private OnItemClickListener onItemClickListener;
        private BottomDialog bottomDialog;


        public Builder(Context context) {
            this.context = context;
        }
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        public Builder background(int res) {
            this.bgRes = res;
            return this;
        }
        public Builder inflateMenu(int menu, OnItemClickListener onItemClickListener) {
            this.menu = menu;
            this.onItemClickListener = onItemClickListener;
            return this;
        }
        public Builder layout(int layout) {
            this.layout = layout;
            return this;
        }
        public Builder orientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public BottomDialog create() {

            LinearLayout background;
            LinearLayout container;
            TextView titleView;

            bottomDialog = new BottomDialog(context);
            bottomDialog.setContentView(R.layout.bottom_dialog);
            bottomDialog.setCancelable(true);
            bottomDialog.setCanceledOnTouchOutside(true);
            bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
            bottomDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            background = (LinearLayout) bottomDialog.findViewById(R.id.background);
            titleView = (TextView) bottomDialog.findViewById(R.id.title);
            container = (LinearLayout) bottomDialog.findViewById(R.id.container);
            bottomDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomDialog.dismiss();
                }
            });
            if (adapter != null) adapter.setLayout(layout);
            if (adapter != null) adapter.setOrientation(orientation);
            if (!TextUtils.isEmpty(title)){
                titleView.setText(title);
                titleView.setVisibility(View.VISIBLE);
            }
            if (bgRes!=0) {
                background.setBackgroundResource(bgRes);
            }
            if (menu!=0){
                MenuInflater menuInflater = new SupportMenuInflater(context);
                MenuBuilder menuBuilder = new MenuBuilder(context);
                menuInflater.inflate(menu, menuBuilder);
                List<Item> items = new ArrayList<>();
                for (int i = 0; i < menuBuilder.size(); i++) {
                    MenuItem menuItem = menuBuilder.getItem(i);
                    items.add(new Item(menuItem.getItemId(), menuItem.getTitle().toString(), menuItem.getIcon()));
                }
                if (onItemClickListener!=null){
                    if (layout!=0 && orientation!=0){
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        RecyclerView.LayoutManager manager;

                        adapter = new DialogAdapter(items, layout, orientation);
                        adapter.setItemClick(onItemClickListener);

                        if (layout == LINEAR)
                            manager = new LinearLayoutManager(context, orientation, false);
                        else if (layout == GRID)
                            manager = new GridLayoutManager(context, 5, orientation, false);
                        else manager = new LinearLayoutManager(context, orientation, false);

                        RecyclerView recyclerView = new RecyclerView(context);
                        recyclerView.setLayoutParams(params);
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setAdapter(adapter);

                        container.addView(recyclerView);
                    }

                }
            }

            return bottomDialog;
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
                            if (itemClickListener != null) itemClickListener.click(item,bottomDialog);
                        }
                    });
                } else if (orientation == HORIZONTAL) {
                    topHolder = (TopHolder) holder;

                    topHolder.item.setText(item.getTitle());
                    topHolder.item.setCompoundDrawablesWithIntrinsicBounds(null, topHolder.icon(item.getIcon()), null, null);
                    topHolder.item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (itemClickListener != null) itemClickListener.click(item,bottomDialog);
                        }
                    });
                } else {
                    leftHolder = (LeftHolder) holder;

                    leftHolder.item.setText(item.getTitle());
                    leftHolder.item.setCompoundDrawablesWithIntrinsicBounds(leftHolder.icon(item.getIcon()), null, null, null);
                    leftHolder.item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (itemClickListener != null) itemClickListener.click(item,bottomDialog);
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
                    params.width = DisplayUtils.getScreenWidth(context) / 5;

                    item = new TextView(view.getContext());
                    item.setLayoutParams(params);
                    item.setMaxLines(1);
                    item.setEllipsize(TextUtils.TruncateAt.END);
                    item.setGravity(Gravity.CENTER);
                    item.setTextColor(ContextCompat.getColor(view.getContext(),R.color.COLOR_FF646464));
                    item.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.textsize_10));
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
                        @SuppressWarnings("SuspiciousNameCombination") Drawable resizeIcon = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, topIcon, topIcon, true));
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
                    item.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.textsize_14));
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
                        @SuppressWarnings("SuspiciousNameCombination") Drawable resizeIcon = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, leftIcon, leftIcon, true));
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
