package com.jey.jlibs.view.XRecyclerView;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.jlibs.R;
import com.jey.jlibs.interface_.OnListViewLoadListener;
import com.jey.jlibs.interface_.OnPullDownListener;
import com.jey.jlibs.utils.CommonFunction;
import com.jey.jlibs.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class XRecyclerView extends RecyclerView implements ArrowRefreshHeader.ArrowRefreshHeaderRefreshCompletedListener {
    private boolean isLoadingData = false;
    private boolean isNoMore = false;
    private int mRefreshProgressStyle = ProgressStyle.BallPulse;
    private int mLoadingMoreProgressStyle = ProgressStyle.SysProgress;
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFooterViews = new ArrayList<>();
    private WrapAdapter mWrapAdapter;
    private float mLastY = -1;
    private static final float DRAG_RATE = 3;
    private OnListViewLoadListener mLoadingListener;
    private ArrowRefreshHeader mRefreshHeader;
    private boolean pullRefreshEnabled = true;
    private boolean loadingMoreEnabled = true;
    private boolean showDividerEnabled = true;
    private boolean isDragTop = false;
    private DividerItemDecoration dividerItemDecoration = null;
    //下面的ItemViewType是保留值(ReservedItemViewType),如果用户的adapter与它们重复将会强制抛出异常。不过为了简化,我们检测到重复时对用户的提示是ItemViewType必须小于10000
    private static final int TYPE_REFRESH_HEADER = 10000;//设置一个很大的数字,尽可能避免和用户的adapter冲突
    private static final int TYPE_FOOTER = 10001;
    private static final int HEADER_INIT_INDEX = 10002;
    private static final int FOOTER_INIT_INDEX = 11003;
    private static List<Integer> sHeaderTypes = new ArrayList<>();//每个header必须有不同的type,不然滚动的时候顺序会变化
    private static List<Integer> sFooterTypes = new ArrayList<>();//每个footer必须有不同的type,不然滚动的时候顺序会变化
    private int mPageCount = 0;
    //adapter没有数据的时候显示,类似于listView的emptyView
    private View mEmptyView;
    private View mFootView;
    private final AdapterDataObserver mDataObserver = new DataObserver();
    private AppBarStateChangeListener.State appbarState = AppBarStateChangeListener.State.EXPANDED;
    private OnPullDownListener onPullDownListener;

    public XRecyclerView(Context context) {
        this(context, null);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (pullRefreshEnabled) {
            mRefreshHeader = new ArrowRefreshHeader(getContext());
            mRefreshHeader.setProgressStyle(mRefreshProgressStyle);
        }
        setItemAnimator(null);
        LoadingMoreFooter footView = new LoadingMoreFooter(getContext());
        footView.setProgressStyle(mLoadingMoreProgressStyle);
        mFootView = footView;
        mFootView.setVisibility(GONE);
        mRefreshHeader.setArrowRefreshHeaderRefreshCompletedListener(this);
    }

    public void setShowDividerEnabled(boolean showDividerEnabled) {
        this.showDividerEnabled = showDividerEnabled;
    }

    public void setDividerItemDecoration(DividerItemDecoration dividerItemDecoration) {
        this.dividerItemDecoration = dividerItemDecoration;
    }

    public void addHeaderView(View view) {
        sHeaderTypes.add(HEADER_INIT_INDEX + mHeaderViews.size());
        mHeaderViews.add(view);
    }

    public void addFooterView(View view) {
        sFooterTypes.add(FOOTER_INIT_INDEX + mFooterViews.size());
        mFooterViews.add(view);
    }

    public ArrayList<View> getmHeaderViews() {
        return mHeaderViews;
    }

    public ArrayList<View> getmFooterViews() {
        return mFooterViews;
    }

    public void setOnPullDownListener(OnPullDownListener onPullDownListener) {
        this.onPullDownListener = onPullDownListener;
    }


    //根据header的ViewType判断是哪个header
    private View getHeaderViewByType(int itemType) {
        if (!isHeaderType(itemType)) {
            return null;
        }
        return mHeaderViews.get(itemType - HEADER_INIT_INDEX);
    }

    //判断一个type是否为HeaderType
    private boolean isHeaderType(int itemViewType) {
        return mHeaderViews.size() > 0 && sHeaderTypes.contains(itemViewType);
    }

    private View getFooterViewByType(int itemType) {
        if (!isFooterType(itemType)) {
            return null;
        }
        return mFooterViews.get(itemType - FOOTER_INIT_INDEX);
    }

    private boolean isFooterType(int itemViewType) {
        return mFooterViews.size() > 0 && sFooterTypes.contains(itemViewType);
    }

    //判断是否是XRecyclerView保留的itemViewType
    private boolean isReservedItemViewType(int itemViewType) {
        if (itemViewType == TYPE_REFRESH_HEADER || itemViewType == TYPE_FOOTER || sHeaderTypes.contains(itemViewType) ||
                sFooterTypes.contains(itemViewType)) {
            return true;
        } else {
            return false;
        }
    }

    public void setFootView(final View view) {
        mFootView = view;
    }

    public void loadMoreComplete() {
        isLoadingData = false;
        if (mFootView instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) mFootView).setState(LoadingMoreFooter.STATE_COMPLETE);
        } else {
            mFootView.setVisibility(View.GONE);
        }
    }

    public void setNoMore(boolean noMore) {
        isLoadingData = false;
        isNoMore = noMore;
        if (mFootView instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) mFootView).setState(isNoMore ? LoadingMoreFooter.STATE_NOMORE : LoadingMoreFooter.STATE_COMPLETE);
        } else {
            mFootView.setVisibility(View.GONE);
        }
    }

    public void reset() {
        setNoMore(false);
        loadMoreComplete();
        refreshComplete();
    }

    public void refreshComplete() {
        mRefreshHeader.refreshComplete();
        setNoMore(false);
    }

    public void setRefreshHeader(ArrowRefreshHeader refreshHeader) {
        mRefreshHeader = refreshHeader;
    }

    public void setPullRefreshEnabled(boolean enabled) {
        pullRefreshEnabled = enabled;
    }

    public void setLoadingMoreEnabled(boolean enabled) {
        loadingMoreEnabled = enabled;
        if (!enabled) {
            if (mFootView instanceof LoadingMoreFooter) {
                ((LoadingMoreFooter) mFootView).setState(LoadingMoreFooter.STATE_COMPLETE);
            }
        }
    }

    public boolean isPullRefreshEnabled() {
        return pullRefreshEnabled;
    }

    public boolean isLoadingMoreEnabled() {
        return loadingMoreEnabled;
    }

    public void setRefreshProgressStyle(int style) {
        mRefreshProgressStyle = style;
        if (mRefreshHeader != null) {
            mRefreshHeader.setProgressStyle(style);
        }
    }

    public void setLoadingMoreProgressStyle(int style) {
        mLoadingMoreProgressStyle = style;
        if (mFootView instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) mFootView).setProgressStyle(style);
        }
    }

    public void setArrowImageView(int resId) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setArrowImageView(resId);
        }
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        mDataObserver.onChanged();
    }

    public View getEmptyView() {
        return mEmptyView;
    }


    private void setLayoutManagers() {
        if (getLayoutManager() != null) return;
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        if (showDividerEnabled) {
            if (dividerItemDecoration == null) {
                dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL,
                        CommonFunction.dp2px(getContext(), 5), getContext().getResources().getColor(R.color.white));
            }
            addItemDecoration(dividerItemDecoration);
        }
        setLayoutManager(manager);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        setLayoutManagers();
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingListener != null && !isLoadingData && loadingMoreEnabled) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount() && !isNoMore && mRefreshHeader.getState() < ArrowRefreshHeader.STATE_REFRESHING) {
                isLoadingData = true;
                if (mFootView instanceof LoadingMoreFooter) {
                    ((LoadingMoreFooter) mFootView).setState(LoadingMoreFooter.STATE_LOADING);
                } else {
                    mFootView.setVisibility(View.VISIBLE);
                }
                mLoadingListener.onLoadMore();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop() && pullRefreshEnabled && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    isDragTop = true;
                    String sy = deltaY + "";
                    if (onPullDownListener != null && !sy.equals("0.0")) {
                        onPullDownListener.onTopPullDown(true, (int) deltaY);
                    }
                    mRefreshHeader.onMove(deltaY / DRAG_RATE);
                    if (mRefreshHeader.getVisibleHeight() > 0 && mRefreshHeader.getState() < ArrowRefreshHeader.STATE_REFRESHING) {
                        return false;
                    }
                }
                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && pullRefreshEnabled && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    resetDragTopStatus();
                    if (mRefreshHeader.releaseAction()) {
                        if (mLoadingListener != null) {
                            mLoadingListener.onRefresh();
                            mFootView.setVisibility(GONE);
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void resetDragTopStatus() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 500);
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    protected boolean isOnTop() {
        if (mRefreshHeader.getParent() != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onHeaderRefreshComleted(boolean tag) {
        resetDragTopStatus();
    }

    private class DataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null && mEmptyView != null) {
                int emptyCount = 0;
                if (pullRefreshEnabled) {
                    emptyCount++;
                }
                if (loadingMoreEnabled) {
                    emptyCount++;
                }
                if (adapter.getItemCount() == emptyCount) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    XRecyclerView.this.setVisibility(View.GONE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    XRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
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
    }

    public class WrapAdapter extends Adapter<ViewHolder> {

        private Adapter adapter;

        public WrapAdapter(Adapter adapter) {
            this.adapter = adapter;
        }

        public boolean isHeader(int position) {
            if (pullRefreshEnabled) {
                return position >= 1 && position < mHeaderViews.size() + 1;
            } else {
                return false;
            }
        }

        public boolean isLoadMoreFooter(int position) {
            if (loadingMoreEnabled) {
                return position == getItemCount() - 1;
            } else {
                return false;
            }
        }

        public boolean isFooter(int position) {
            if (loadingMoreEnabled) {
                int c = getItemCount();
                int b = c - mFooterViews.size() - 1;
                return position >= b && position < c;
            } else {
                return false;
            }
        }

        private int getFooterPosition(int position) {
            int adjPosition;
            if (pullRefreshEnabled) {
                adjPosition = position - adapter.getItemCount() - getHeadersCount() - 1;
            } else {
                adjPosition = position - adapter.getItemCount() - getHeadersCount();
            }
            return adjPosition;
        }

        public boolean isRefreshHeader(int position) {
            if (pullRefreshEnabled) {
                return position == 0;
            } else {
                return false;
            }
        }

        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        private int adjPosition(int position) {
            int adjPosition;
            if (pullRefreshEnabled) {
                adjPosition = position - (getHeadersCount() + 1);
            } else {
                adjPosition = position - getHeadersCount();
            }
            return adjPosition;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                return new SimpleViewHolder(mRefreshHeader);
            } else if (viewType == TYPE_FOOTER) {
                return new SimpleViewHolder(mFootView);
            } else if (isHeaderType(viewType)) {
                return new SimpleViewHolder(getHeaderViewByType(viewType));
            } else if (isFooterType(viewType)) {
                return new SimpleViewHolder(getFooterViewByType(viewType));
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (isHeader(position) || isRefreshHeader(position)) {
                return;
            }
            if (isFooter(position) || isLoadMoreFooter(position)) {
                return;
            }

//            int adjPosition = position - (getHeadersCount() + 1);
            int adjPosition = adjPosition(position);
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                    return;
                }
            }
        }

        @Override
        public int getItemCount() {
            if (loadingMoreEnabled) {
                if (adapter != null) {
                    if (pullRefreshEnabled) {
                        return getHeadersCount() + adapter.getItemCount() + 2;
                    } else {
                        return getHeadersCount() + adapter.getItemCount() + 1;
                    }
                } else {
                    if (pullRefreshEnabled)
                        return getHeadersCount() + 2;
                    else
                        return getHeadersCount() + 1;
                }
            } else {
                if (adapter != null) {
                    if (pullRefreshEnabled)
                        return getHeadersCount() + adapter.getItemCount() + 1;
                    else
                        return getHeadersCount() + adapter.getItemCount();
                } else {
                    if (pullRefreshEnabled)
                        return getHeadersCount() + 1;
                    else
                        return getHeadersCount();
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
//            int adjPosition = position - (getHeadersCount() + 1);
            int adjPosition = adjPosition(position);
            if (isReservedItemViewType(adapter.getItemViewType(adjPosition))) {
                throw new IllegalStateException("XRecyclerView require itemViewType in adapter should be less than 10000 ");
            }
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position)) {
                position = position - 1;
                return sHeaderTypes.get(position);
            }
            if (isLoadMoreFooter(position)) {
                return TYPE_FOOTER;
            }
            if (isFooter(position)) {
                position = getFooterPosition(position);
                return sFooterTypes.get(position);
            }

            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemViewType(adjPosition);
                }
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            if (pullRefreshEnabled) {
                if (adapter != null && position >= getHeadersCount() + 1) {
                    int adjPosition = position - (getHeadersCount() + 1);
                    if (adjPosition < adapter.getItemCount()) {
                        return adapter.getItemId(adjPosition);
                    }
                }
            } else {
                if (adapter != null && position >= getHeadersCount()) {
                    int adjPosition = position - getHeadersCount();
                    if (adjPosition < adapter.getItemCount()) {
                        return adapter.getItemId(adjPosition);
                    }
                }
            }
//            if (adapter != null && position >= getHeadersCount() + 1) {
//                int adjPosition = position - (getHeadersCount() + 1);
//                if (adjPosition < adapter.getItemCount()) {
//                    return adapter.getItemId(adjPosition);
//                }
//            }
            return -1;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isLoadMoreFooter(position) || isRefreshHeader(position) ||
                                isFooter(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isRefreshHeader(holder.getLayoutPosition()) ||
                    isLoadMoreFooter(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }

        private class SimpleViewHolder extends ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    public void setLoadingListener(OnListViewLoadListener listener) {
        mLoadingListener = listener;
    }

    public interface LoadingListener {

        void onRefresh();

        void onLoadMore();
    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing && pullRefreshEnabled && mLoadingListener != null) {
            mRefreshHeader.setState(ArrowRefreshHeader.STATE_REFRESHING);
            mRefreshHeader.onMove(mRefreshHeader.getMeasuredHeight());
            mFootView.setVisibility(GONE);
            mLoadingListener.onRefresh();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //解决和CollapsingToolbarLayout冲突的问题
        AppBarLayout appBarLayout = null;
        ViewParent p = getParent();
        while (p != null) {
            if (p instanceof CoordinatorLayout) {
                break;
            }
            p = p.getParent();
        }
        if (p instanceof CoordinatorLayout) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) p;
            final int childCount = coordinatorLayout.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = coordinatorLayout.getChildAt(i);
                if (child instanceof AppBarLayout) {
                    appBarLayout = (AppBarLayout) child;
                    break;
                }
            }
            if (appBarLayout != null) {
                appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        appbarState = state;
                    }
                });
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (onPullDownListener != null && mRefreshHeader.getState() != BaseRefreshHeader.STATE_REFRESHING) {
                        onPullDownListener.onTopPullDown(false, 0);
                    }
                    isDragTop = false;
                    break;
            }
        }
    };
}