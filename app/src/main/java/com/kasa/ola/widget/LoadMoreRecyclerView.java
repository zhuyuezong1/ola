package com.kasa.ola.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.widget.xrecyclerview.LoadingMoreFooter;

import java.util.ArrayList;

/**
 * 加载更多RecyclerView
 */
public class LoadMoreRecyclerView extends RecyclerView {

    private boolean isLoadingData = false;
    private boolean isNoMore = false;
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private LoadingMoreFooter footView;
    private Adapter mWrapAdapter;
    private LoadingListener mLoadingListener;
    private boolean loadingMoreEnabled = true;
    private final int HEAD_TYPE = 1 << 27;
    private final int FOOTER_TYPE = 1 << 29;

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        footView = new LoadingMoreFooter(context);
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
    }

    public void loadMoreComplete(boolean noMore) {
        isLoadingData = false;
        isNoMore = noMore;
        if (null != footView) {
            footView.setState(noMore ? LoadingMoreFooter.STATE_NOMORE : LoadingMoreFooter.STATE_COMPLETE);
        }
    }
    public void setNoFooter(boolean nofooter){
        if (null != footView) {
            footView.setState(nofooter ? LoadingMoreFooter.NO_FOOTER : LoadingMoreFooter.STATE_NOMORE);
        }
    }

    public void loadComplete() {
        isLoadingData = false;
        isNoMore = true;
        if (null != footView) {
            footView.setState(LoadingMoreFooter.STATE_COMPLETE);
        }
    }

    public void setFootViewBottom(int bottom) {
        if (null != footView) {
            footView.setPadding(0, 6, 0, 6 + bottom);
        }
    }

    public void setLoadingMoreEnabled(boolean enabled) {
        loadingMoreEnabled = enabled;
        if (null != footView) {
            if (enabled) {
                footView.setState(LoadingMoreFooter.STATE_COMPLETE);
            } else {
                footView.setVisibility(GONE);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (null != adapter) {
            mWrapAdapter = new WrapAdapter(mHeaderViews, footView, adapter);
            super.setAdapter(mWrapAdapter);
            adapter.registerAdapterDataObserver(mDataObserver);
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingListener != null && !isLoadingData && loadingMoreEnabled && !isNoMore) {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                if (layoutManager.getChildCount() > 0
                        && lastVisibleItemPosition >= layoutManager.getItemCount() - 1) {

                    isLoadingData = true;
                    if (null != footView) {
                        footView.setState(LoadingMoreFooter.STATE_LOADING);
                    }
                    mLoadingListener.onLoadMore();
                }
            }
        }
    }

    private final AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            mWrapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };

    private class WrapAdapter extends Adapter<ViewHolder> {

        private Adapter adapter;

        private ArrayList<View> mHeaderViews;

        private View mFootView;

        public WrapAdapter(ArrayList<View> headerViews, View footView, Adapter adapter) {
            this.adapter = adapter;
            this.mHeaderViews = headerViews;
            this.mFootView = footView;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if(manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position)||  isFooter(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }


        public boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        public boolean isFooter(int position) {
            return position == getItemCount() - 1;
        }

        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if ((viewType & HEAD_TYPE) == HEAD_TYPE) {
                int headPosition = viewType & ~HEAD_TYPE;
                return new SimpleViewHolder(mHeaderViews.get(headPosition));
            } else if ((viewType & FOOTER_TYPE) == FOOTER_TYPE) {
                return new SimpleViewHolder(mFootView);
            } else {
                return adapter.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(position) || isFooter(position)) {
                return;
            }
            int adjPosition = position - getHeadersCount();
            if (adapter != null) {
                int adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                    return;
                }
            }
        }

        @Override
        public int getItemCount() {
            if (adapter != null) {
                return getHeadersCount() + 1 + adapter.getItemCount();
            } else {
                return getHeadersCount() + 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (isHeader(position)) {
                return HEAD_TYPE | position;
            } else if (isFooter(position)) {
                return FOOTER_TYPE | position;
            } else {
                return adapter.getItemViewType(position - getHeadersCount());
            }
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= getHeadersCount()) {
                int adjPosition = position - getHeadersCount();
                int adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(observer);
            }
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.registerAdapterDataObserver(observer);
            }
        }

        private class SimpleViewHolder extends ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    public interface LoadingListener {

        void onLoadMore();
    }


}
