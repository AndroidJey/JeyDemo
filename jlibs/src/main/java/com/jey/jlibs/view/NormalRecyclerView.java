package com.jey.jlibs.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.jlibs.R;
import com.jey.jlibs.base.AsyHttp;
import com.jey.jlibs.base.BroadcastCenter;
import com.jey.jlibs.dataModel.BaseDataModel;
import com.jey.jlibs.interface_.FechRecyclerViewHolder;
import com.jey.jlibs.interface_.OnBaseListViewRefreshListener;
import com.jey.jlibs.interface_.OnChangedListener;
import com.jey.jlibs.interface_.OnItemClickListener;
import com.jey.jlibs.interface_.OnItemLongClickListener;
import com.jey.jlibs.interface_.OnListViewLoadListener;
import com.jey.jlibs.interface_.OnLoadDataCompleteListener;
import com.jey.jlibs.interface_.OnLoadTopDataListener;
import com.jey.jlibs.interface_.OnPullDownListener;
import com.jey.jlibs.utils.CommonFunction;
import com.jey.jlibs.utils.JCache;
import com.jey.jlibs.utils.ToastUtil;
import com.jey.jlibs.view.XRecyclerView.BaseViewHolder;
import com.jey.jlibs.view.XRecyclerView.ProgressStyle;
import com.jey.jlibs.view.XRecyclerView.XRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by sevnce on 2016/10/21.
 */

public class NormalRecyclerView extends XRecyclerView implements OnListViewLoadListener, OnPullDownListener {

    //获取list数据名
    public static String LIST = "list";
    //页码
    public static String PAGE = "page";
    //页数
    public static String PAGECOUNT = "pageCount";
    //总页数
    public static String TOTALNUMBER = "totalNumber";
    //当前访问第几页
    public static String SEVPAGESPAGE = "page";
    //当前访问页的数据条数
    public static String SEVPAGECOUNT = "pagesize";
    //缓存
    private JCache mCache;

    Map<String, Object> para;
    private int page = 0, pageSize = 20, pageCount, totalNumber;

    private String action;
    private BaseRecyclerAdapter adapter;
    private List<BaseDataModel> records;
    private List<BaseDataModel> currentRecords;
    private FechRecyclerViewHolder fetchViewHodler;
    private Class classModel;
    private DividerItemDecoration dividerItemDecoration;

    protected Hashtable<Integer, Integer> viewHolderMap;
    protected Hashtable<Integer, Object> faceMap;

    private View emptyView = null;
    private ImageCycleView lunboView = null;
    private View topBar = null;

    private boolean topBarGradient = false;
    private int cyclerViewHeight = 0;

    private OnChangedListener changeListener;
    private OnLoadDataCompleteListener onLoadDataCompleteListener;
    private OnBaseListViewRefreshListener onBaseListViewRefreshListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemClickListener onItemClickListener;
    private OnLoadTopDataListener onLoadTopDataListener;


    public NormalRecyclerView(Context context) {
        super(context, null);
        mCache = JCache.get(context);
    }

    public NormalRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
        mCache = JCache.get(context);
    }

    public NormalRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mCache = JCache.get(context);
    }

    private void init(Context context) {
        setLoadingListener(this);
        setOnPullDownListener(this);
        setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        viewHolderMap = new Hashtable<Integer, Integer>();
        faceMap = new Hashtable<Integer, Object>();
        records = new ArrayList<>();
        currentRecords = new ArrayList<>();
        BroadcastCenter.subscribe(BroadcastCenter.TITLE.DATAADDED, new BroadcastCenter.Reader() {
            public void readNews(BroadcastCenter.TITLE title, Object content) {
                if (content == null || classModel == null) return;
                if (content.getClass().equals(classModel)) {
                    page = 1;
                    load();
                }
            }
        });
        BroadcastCenter.subscribe(BroadcastCenter.TITLE.DATADELETED, new BroadcastCenter.Reader() {
            public void readNews(BroadcastCenter.TITLE title, Object content) {
                if (content == null || classModel == null) return;
                if (content.getClass().equals(classModel)) {
                    BaseDataModel data = (BaseDataModel) content;
                    boolean deleted = false;
                    for (BaseDataModel item : records) {
                        if (data.getId().equalsIgnoreCase(item.getId())) {
                            deleted = true;
                            break;
                        }
                    }
                    if (deleted) {
                        records.remove(data);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public void setAction(String action) {
        page = 1;
        this.action = action;
//        if (isPullRefreshEnabled()) {
//            setRefreshing(true);
//        } else {
            JSONObject testJsonObject = mCache.getAsJSONObject(CommonFunction.MD5(action+page));
            if (testJsonObject == null) {
                load();
            }else {
                ToastUtil.show(getContext(),"获取缓存数据");
                initJson(testJsonObject);
            }
//        }
    }

    public void setPara(Map<String, Object> para) {
        page = 1;
        this.para = para;
    }

    public void setDividerItemDecoration(DividerItemDecoration dividerItemDecoration) {
        this.dividerItemDecoration = dividerItemDecoration;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public View getLunboView() {
        return lunboView;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public List<BaseDataModel> getDataSource() {
        return records;
    }

    public void setDataSource(List<BaseDataModel> records) {
        this.records = records;
        RecyclerView.Adapter d = getAdapter();
//        if (adapter != null) {
        adapter = new BaseRecyclerAdapter(getContext(), this.records, viewHolderMap, faceMap);
        setAdapter(adapter);
//        }
    }

    public RecyclerView.Adapter<BaseViewHolder> getAdapter() {
        return adapter;
    }

    public boolean isTopBarGradient() {
        return topBarGradient;
    }

    public void setTopBarGradient(boolean topBarGradient) {
        this.topBarGradient = topBarGradient;
        if (topBarGradient) {
            addOnScrollListener(onScrollListener);
        } else {
            removeOnScrollListener(onScrollListener);
        }
    }

    public void setGradientView(View topBar) {
        this.topBar = topBar;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setClassModel(Class classModel) {
        this.classModel = classModel;
    }

    public void setFetchViewHodler(FechRecyclerViewHolder fetchViewHodler) {
        this.fetchViewHodler = fetchViewHodler;
    }

    public void setViewHolderMap(Hashtable<Integer, Integer> viewHolderMap) {
        this.viewHolderMap = viewHolderMap;
    }

    public void setSingleLayout(Class modelClass, int resLayoutId, Object holder) {
        classModel = modelClass;
        viewHolderMap.put(0, resLayoutId);
        faceMap.put(0, holder);
    }

    private void setLayoutManagers() {
        if (getLayoutManager() == null) {
            LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            if (dividerItemDecoration != null)
                addItemDecoration(dividerItemDecoration);
            setLayoutManager(manager);
        }
    }

    private void load() {
        new AsyHttp(getContext(), para, new AsyHttp.IPostCallback() {
            public void onAsyHttpProgress(int percent) {
            }

            public void onAsyHttpSuccess(JSONObject json) {
                if (json.has(LIST)){
                    if (page == 1)//缓存recyclerView的第一页数据
                        mCache.put(CommonFunction.MD5(action+page),json,JCache.TIME_HOUR);
                }
                initJson(json);
            }

            public Boolean onAsyHttpErr(JSONObject json) {
                AsyHttp.loading.setVisibility(INVISIBLE);
                reset();
                return false;
            }
        }).execute(action + "?" + SEVPAGESPAGE + "=" + page + "&" + SEVPAGECOUNT + "=" + pageSize);
    }

    /**
     * 访问接口或数据缓存拿到的json数据
     * @param json
     */
    private void initJson(JSONObject json){
        try {
            if (json.has(LIST)) {
                if (json.has(PAGE)) {
                    page = json.getInt(PAGE);
                    pageCount = json.getInt(PAGECOUNT);
                    totalNumber = json.getInt(TOTALNUMBER);
                    if (changeListener != null) {
                        changeListener.onChanged(json.getInt(TOTALNUMBER));
                    }
                }
                JSONArray list = json.getJSONArray(LIST);
                if (list.length() > 0) {
                    if (page > 1)
                        append(list);
                    else
                        refresh(list);
                    return;
                } else {
                    if (onLoadDataCompleteListener != null && page == 1)
                        onLoadDataCompleteListener.onLoadDataComplete(true, false);
                    if (page == 1) {
                        if (emptyView == null) {
                            View view = LayoutInflater.from(getContext()).inflate(R.layout.emty_list_view, null);
                            ((ViewGroup) getParent()).addView(view);
                            setEmptyView(view);
                        } else {
                            ((ViewGroup) getParent()).addView(emptyView);
                            setEmptyView(emptyView);
                        }
                    } else {
                        setNoMore(true);
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
//                    reset();
        }
    }

    private void refresh(JSONArray array) throws JSONException {
        records = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            createModel(array.getJSONObject(i));
        }
//        if (adapter == null) {
        if (fetchViewHodler != null) {
            viewHolderMap = fetchViewHodler.getViewLayoutResourseIdMap();
            faceMap = fetchViewHodler.geteViewHolderMap();
        }
        if (viewHolderMap.size() == 0 || faceMap.size() == 0) {
            throw new IllegalArgumentException("ViewHolder not be null");
        }
        setLayoutManagers();
        //Jey改动的
        if (records.get(0).getValue("IsTop").toString().equals("")) {
            adapter = new BaseRecyclerAdapter(getContext(), records, viewHolderMap, faceMap);
            setAdapter(adapter);
            if (onLoadDataCompleteListener != null)
                onLoadDataCompleteListener.onLoadDataComplete(true, true);
        } else {
            List<BaseDataModel> topRecords = new ArrayList<>();
            List<BaseDataModel> listRecords = new ArrayList<>();
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i).getValue("IsTop").toString().equals("true")) {
                    topRecords.add(records.get(i));
                } else {
                    listRecords.add(records.get(i));
                }
            }
            adapter = new BaseRecyclerAdapter(getContext(), listRecords, viewHolderMap, faceMap);
            setAdapter(adapter);
            if (onLoadTopDataListener != null) {
                onLoadTopDataListener.OnLoadTopDataComplete(topRecords);
            }
            if (onLoadDataCompleteListener != null)
                onLoadDataCompleteListener.onLoadDataComplete(true, true);
        }
//            adapter = new BaseRecyclerAdapter(getContext(), records, viewHolderMap, faceMap);
//            setAdapter(adapter);
//            if (onLoadDataCompleteListener != null)
//                onLoadDataCompleteListener.onLoadDataComplete(true, true);
//        } else
//            adapter.notifyDataSetChanged();
        refreshComplete();
    }

    private void append(final JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            createModel(array.getJSONObject(i));
        }
        if (onLoadDataCompleteListener != null)
            onLoadDataCompleteListener.onLoadDataComplete(true, false);
        adapter.notifyDataSetChanged();
        loadMoreComplete();
    }

    private void createModel(JSONObject json) {
        if (fetchViewHodler != null) {
            this.records.add(BaseDataModel.getModelByJson(fetchViewHodler.getModelClass(json), json));
        } else {
            this.records.add(BaseDataModel.getModelByJson(classModel, json));
        }
    }

    @Override
    public void onRefresh() {//下拉刷新
        if (onBaseListViewRefreshListener != null)
            onBaseListViewRefreshListener.onBaseListViewRefresh();
        page = 1;
//        JSONObject testJsonObject = mCache.getAsJSONObject("testJsonObject1");
//        if (testJsonObject == null) {
            load();
//        }else {
//            ToastUtil.show(getContext(),"获取缓存数据");
//            initJson(testJsonObject);
//        }
    }

    @Override
    public void onLoadMore() {//上拉加载下一页
        page++;
        load();
    }

    private int getScollYDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        if (isPullRefreshEnabled()) {
            position = position + 1;
        }
        View firstVisiableChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        int height = (position) * itemHeight - firstVisiableChildView.getTop();
        return height;
    }


    private boolean isShowCycleImage = false;

    public void showCycleImage(final List<ImageCycleView.ImageInfo> mImageUrls,
                               ImageCycleView.OnPageClickListener onPageClickListener,
                               ImageCycleView.LoadImageCallBack loadImageCallBack) {
        if (mImageUrls == null) return;
        if (lunboView != null && getmHeaderViews().contains(lunboView)) {
            lunboView.setData(mImageUrls);
            return;
        }
        isShowCycleImage = true;
        lunboView = (ImageCycleView) LayoutInflater.from(getContext()).inflate(R.layout.image_lun_bo_layout, null);
        lunboView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                CommonFunction.dp2px(getContext(), 180)));
        cyclerViewHeight = CommonFunction.getViewHeight(lunboView);
        lunboView.setOnPageClickListener(onPageClickListener);
        lunboView.loadData(mImageUrls, loadImageCallBack);
        addHeaderView(lunboView);
    }

    public void showCycleImage(final List<ImageCycleView.ImageInfo> mImageUrls,
                               ImageCycleView.OnPageClickListener onPageClickListener,
                               ImageCycleView.LoadImageCallBack loadImageCallBack, boolean showTitleAndBrief, String keyTitle, String keyBrief) {
        if (mImageUrls == null) return;
        if (lunboView != null && getmHeaderViews().contains(lunboView)) {
            lunboView.setData(mImageUrls);
            return;
        }
        isShowCycleImage = true;
        lunboView = (ImageCycleView) LayoutInflater.from(getContext()).inflate(R.layout.image_lun_bo_layout, null);
        lunboView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                CommonFunction.dp2px(getContext(), 180)));
        cyclerViewHeight = CommonFunction.getViewHeight(lunboView);
        lunboView.setOnPageClickListener(onPageClickListener);
        lunboView.setmShowTitleAndBrief(showTitleAndBrief);
        lunboView.setKeyTitle(keyTitle);
        lunboView.setKeyBrief(keyBrief);
        lunboView.loadData(mImageUrls, loadImageCallBack);
        addHeaderView(lunboView);
    }

    public void dismissCycleImage() {
        if (lunboView == null || !isShowCycleImage || !getmHeaderViews().contains(lunboView))
            return;
        getmHeaderViews().remove(lunboView);
        lunboView = null;
        isShowCycleImage = false;
    }

    public void setOnLoadDataCompleteListener(OnLoadDataCompleteListener onLoadDataCompleteListener) {
        this.onLoadDataCompleteListener = onLoadDataCompleteListener;
    }

    public void setOnLoadTopDataListener(OnLoadTopDataListener onLoadTopDataListener) {
        this.onLoadTopDataListener = onLoadTopDataListener;
    }

    public void setOnBaseListViewRefreshListener(OnBaseListViewRefreshListener onBaseListViewRefreshListener) {
        this.onBaseListViewRefreshListener = onBaseListViewRefreshListener;
    }

    public void setOnChangeListener(OnChangedListener listener) {
        this.changeListener = listener;
    }

    public class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
        protected Context context;
        protected List<BaseDataModel> datas;
        protected Hashtable<Integer, Integer> viewHolderMap;
        protected Hashtable<Integer, Object> faceMap;

        public BaseRecyclerAdapter(Context c, List<BaseDataModel> datas, Hashtable<Integer, Integer> map,
                                   Hashtable<Integer, Object> faceMap) {
            context = c;
            this.datas = datas;
            viewHolderMap = map;
            this.faceMap = faceMap;
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewHolderMap == null || viewHolderMap.size() == 0)
                throw new IllegalArgumentException("the Hashtable of RecyclerView.ViewHolder not bu null");
            for (Integer key : viewHolderMap.keySet()) {
                if (viewType == key) {
                    return BaseViewHolder.createViewHolder(context, adapter, parent, viewHolderMap.get(key), faceMap.get(key));
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            holder.onBindView(holder, datas.get(position), position, onItemClickListener, onItemLongClickListener);
        }

        @Override
        public int getItemViewType(int position) {
            if (position >= 0 && position < datas.size() && fetchViewHodler != null)
                return fetchViewHodler.getViewType(records.get(position));
            else return 0;
        }

        @Override
        public int getItemCount() {
            return datas == null ? 0 : datas.size();
        }
    }

    @Override
    public void onTopPullDown(boolean tag, int dy) {
        if (topBar == null) return;
        if (tag && topBarGradient) {
            topBar.setVisibility(GONE);
        } else {
            topBar.setVisibility(VISIBLE);
        }
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int y) {
            y = getScollYDistance();
            if (topBar == null) return;
            if (y <= 0) {
                topBar.setVisibility(VISIBLE);
                topBar.setBackgroundColor(getResources().getColor(R.color.transparent));
            } else {
                if (y > 0 && !isOnTop()) {
                    topBar.setVisibility(VISIBLE);
                    if (cyclerViewHeight == 0)
                        cyclerViewHeight = CommonFunction.dp2px(getContext(), 200);
                    if (y <= cyclerViewHeight) {
                        float scale = (float) y / cyclerViewHeight;
                        float alpha = (255 * scale);
                        topBar.setBackgroundColor(Color.argb((int) alpha, 42, 43, 43));
                    } else
                        topBar.setBackgroundColor(Color.argb((int) 245, 42, 43, 43));
                } else {
                    topBar.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            }
        }
    };
}
