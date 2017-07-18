package com.jey.jeydemo.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.jey.jeydemo.R;
import com.jey.jlibs.view.ImageCycleView;
import com.jey.jlibs.dataModel.BaseDataModel;
import com.jey.jlibs.interface_.FechRecyclerViewHolder;
import com.jey.jlibs.interface_.OnItemClickListener;
import com.jey.jlibs.interface_.OnLoadDataCompleteListener;

import java.util.List;
import java.util.Map;

/**
 * Created by jey on 2017/4/25.
 */

public class RefreshListView extends LinearLayout implements SwipeRefreshLayout.OnRefreshListener{
    private LinearLayout mContainer;
    private NormalRecyclerView baseListView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int REFRESH_COMPLETE = 0X110;

    public RefreshListView(Context context) {
        super(context);
        initView(context);
    }

    public RefreshListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RefreshListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_listview, null);
        baseListView = (NormalRecyclerView) mContainer.findViewById(R.id.baseListView);
        swipeRefreshLayout = (SwipeRefreshLayout) mContainer.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        baseListView.setPullRefreshEnabled(false);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);
    }

    public void setPara(Map<String, Object> para){
        baseListView.setPara(para);
    }

    public void setAction(String action){
        baseListView.setAction(action);
    }

    public void setViewHolder(Class modelClass, int resLayoutId, Object holder) {
        baseListView.setSingleLayout(modelClass,resLayoutId,holder);
    }

    public List<BaseDataModel> getDataSource() {
        return baseListView.getDataSource();
    }

    public void setFetchViewHodler(FechRecyclerViewHolder fetchViewHodler) {
        baseListView.setFetchViewHodler(fetchViewHodler);
    }

    public void showCycleImage(List<ImageCycleView.ImageInfo> mImageUrls,
                               ImageCycleView.OnPageClickListener onPageClickListener,
                               ImageCycleView.LoadImageCallBack loadImageCallBack) {
        baseListView.showCycleImage(mImageUrls, onPageClickListener, loadImageCallBack);
    }

    public void dismissCycleImage() {
        baseListView.dismissCycleImage();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        baseListView.setOnItemClickListener(onItemClickListener);
    }
    public void setOnLoadDataCompleteListener(OnLoadDataCompleteListener onLoadDataCompleteListener) {
        baseListView.setOnLoadDataCompleteListener(onLoadDataCompleteListener);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layout){
        baseListView.setLayoutManager(layout);
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    baseListView.setRefreshing(true);
                    swipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };
}
