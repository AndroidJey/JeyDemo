package com.jey.jlibs.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jlibs.R;
import com.jey.jlibs.activity.PopActivity;
import com.jey.jlibs.base.BroadcastCenter;
import com.jey.jlibs.dataModel.BaseDataModel;
import com.jey.jlibs.dataModel.Database;
import com.jey.jlibs.dataModel.UserModel;
import com.jey.jlibs.utils.DialogMaker;
import com.jey.jlibs.utils.StringUtils;
import com.jey.jlibs.utils.ToastUtil;
import com.jey.jlibs.view.BadgeView;
import com.jey.jlibs.view.ObservableScrollView;

public abstract class PopFragment extends Fragment implements ObservableScrollView.ScrollViewListener {

    protected ViewGroup container;
    protected View view;
    protected String _title;
    protected RelativeLayout goback;
    protected ImageView leftButton;
    protected ImageView rightButton;
    protected RelativeLayout rightRL;
    protected TextView title;
    protected TextView rightText;
    protected LinearLayout containerLL;
    protected RelativeLayout parentLL;
    protected RelativeLayout lessRightRl;
    protected ImageView lessRightButton;
    protected TextView lessRightText;
    protected View statusBarView;
    protected boolean isConnected = true;
    protected boolean isWifiConnected = true;
    protected ObservableScrollView scrollView;


    /**
     * 手指向右滑动时的最小速度
     */
    private static final int XSPEED_MIN = 200;
    /**
     * 手指向右滑动时的最小距离
     */
    private static final int XDISTANCE_MIN = 200;
    /**
     * 记录手指按下时的横坐标
     */
    private float xDown;
    /**
     * 记录手指移动时的横坐标
     */
    private float xMove;
    /**
     * 用于计算手指滑动的速度
     */
    private VelocityTracker mVelocityTracker;


    /**
     * set PopFragment resource layout id
     */
    protected abstract int setLayoutResId();

    protected void initTitleBar() {
    }

    protected void initViews(View view) {
    }

    /**
     * @return ObservableScrollView when use it
     */
    protected ObservableScrollView returnObservableScrollView() {
        return null;
    }

    /**
     * @return XListView when use it
     */
//    protected XListView returnXListView() {
//        return null;
//    }

    /**
     * set go back method
     */
    protected void setGoBackMethod() {
    }

    /**
     * use it when saveBundleState
     */
    protected void setId(String id) {
    }

    /**
     * use it when saveBundleState
     *
     * @return BaseDataModel
     */
    protected BaseDataModel getItem() {
        return null;
    }

    /**
     * use it when saveBundleState
     *
     * @return Bundle
     */
    protected Bundle saveBundleState() {
        return null;
    }

    /**
     * use it when saveBundleState
     *
     * @return String
     */
    protected String saveBundleName() {
        return null;
    }

    /**
     * popFragment onResume lifecycle when FragmentTransaction.add();
     *
     * @param view
     */
    public void popOnResume(View view) {
    }

    /**
     * popFragment onPause lifecycle when FragmentTransaction.add();
     *
     * @param view
     */
    public void popOnPause(View view) {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            if (!StringUtils.isEmpty(saveBundleName()) && getItem() != null) {
                outState = new Bundle();
                outState.putSerializable(saveBundleName(), getItem());
            } else {
                throw new RuntimeException("abstract PopFragment saveBundleName " +
                        "method or getItem method not can be null neither");
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        onConnectionChangeListener();
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        view = inflater.inflate(setLayoutResId(), null);
        if (view == null) {
            throw new IllegalArgumentException("the layout resourse id must set");
        }
        containerLL = (LinearLayout) view.findViewById(R.id.title_bar_container_ll);
        parentLL = (RelativeLayout) view.findViewById(R.id.abc);
        if (parentLL == null) {
            throw new IllegalArgumentException("your fragment must contain @layout/titlebar");
        }
        leftButton = (ImageView) view.findViewById(R.id.title_GoBackButton);
        title = (TextView) view.findViewById(R.id.title_TextView);
        goback = (RelativeLayout) view.findViewById(R.id.regoback);
        rightRL = (RelativeLayout) view.findViewById(R.id.title_RightLayout);
        lessRightRl = (RelativeLayout) view.findViewById(R.id.title_lessRightLayout);
        rightText = (TextView) view.findViewById(R.id.title_RightTextView);
        rightButton = (ImageView) view.findViewById(R.id.imgRight);
        lessRightButton = (ImageView) view.findViewById(R.id.imgLessRight);
        lessRightText = (TextView) view.findViewById(R.id.title_lessRightTextView);
        statusBarView = view.findViewById(R.id.pop_status_bar);
        initTitleBar();
        initViews(view);
        setTitle(_title);
        setLeftButtonClickEvent();
        if (returnObservableScrollView() != null) {
            scrollView = returnObservableScrollView();
            scrollView.setScrollViewListener(this);
        }
        setShoppingCartCount();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Bundle b = saveBundleState();
        if (b != null) {
            onSaveInstanceState(b);
        }
    }

    public void navTo(PopFragment fragment) {
        try {
            FragmentTransaction t = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            t.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out,
                    R.anim.push_left_in, R.anim.push_right_out);
            t.replace(container.getId(), fragment);
            t.addToBackStack(null);
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitle(String title) {
        if (title == null) return;
        _title = title;
        if (this.title != null) this.title.setText(title);
    }


    private void setShoppingCartCount() {
        UserModel user = Database.getUser();
        if (user == null) return;
        String count = user.getValue("CartCount") + "";
        if (StringUtils.isEmpty(count)) return;
        BadgeView badgeView = new BadgeView(getActivity(), lessRightButton);
        badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badgeView.setText(count);
        badgeView.setBadgeBackgroundColor(getResources().getColor(R.color.red_light));
        badgeView.setTextColor(getResources().getColor(R.color.white));
        badgeView.setTextSize(10);
    }

    private void setLeftButtonClickEvent() {
        if (goback == null) return;
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGoBackMethod();
            }
        });
    }

    protected void goBack() {
        if (((PopActivity) getActivity()).getFm().getBackStackEntryCount() <= 1) {
            getActivity().finish();
        } else {
            PopActivity popActivity = (PopActivity) getActivity();
            popActivity.startPopFragmentLifeCycle(true);
            popActivity.getFm().popBackStack();
        }
    }

    protected void close() {
        getActivity().finish();
    }

    protected void doFavority() {
        if (Database.favorityData(getItem())) {
//			action.setBackgroundResource(R.drawable.favoritied);
        } else {
//			action.setBackgroundResource(R.drawable.favority);
        }
    }

    protected void showToash(String pMsg) {
        ToastUtil.show(getActivity(), pMsg);
    }

    protected Dialog dialog;

    public Dialog showWaitDialog(String msg, boolean isCanCancelabel, Object tag) {
        if (null == dialog || !dialog.isShowing()) {
            try {
                dialog = DialogMaker.showCommenWaitDialog(getActivity(), msg, null, isCanCancelabel, tag);
                return dialog;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void dismissDialog() {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void onConnectionChangeListener() {
        BroadcastCenter.subscribe(BroadcastCenter.TITLE.NETWORKCHANGED, new BroadcastCenter.Reader() {
            @Override
            public void readNews(BroadcastCenter.TITLE title, Object content) {
                if (content != null) {
                    int type = (int) content;
                    if (type == -1) {
                        isConnected = false;
                    } else {
                        isConnected = true;
                        if (type == 1) {
                            isWifiConnected = false;
                        } else if (type == 2) {
                            isWifiConnected = true;
                        }
                    }
                }
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * 显示提示对话框
     *
     * @param title
     * @param mess
     */
    public void showCommonDialog(String title, String mess, String negativeStr, final DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder b =
                new AlertDialog.Builder(getActivity());
        b.setTitle(title).setMessage(mess);
        if (negativeStr != null) {
            b.setNegativeButton(negativeStr, onClickListener == null ? null : onClickListener);
        }
        b.setPositiveButton("确定", onClickListener == null ? null : onClickListener);
        b.show();
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
//        if (returnXListView() == null) return;
//        int imageHeight = 0;
//        XListView xListView = returnXListView();
//        if (xListView instanceof BaseListView) {
//            imageHeight = ((BaseListView) xListView).getTopicImageViewHeight();
//        } else if (xListView instanceof BasePinnedListView) {
//            imageHeight = ((BasePinnedListView) xListView).getTopicImageViewHeight();
//        } else {
//            imageHeight = CommonFunction.dp2px(getActivity(), 200);
//        }
//        if (imageHeight == 0) return;
//        if (y <= 0) {
//            if (statusBarView != null)
//                statusBarView.setBackgroundColor(getResources().getColor(R.color.transparent));
//            if (parentLL != null)
//                parentLL.setBackgroundColor(getResources().getColor(R.color.transparent));
//        } else if (y > 0 && y <= imageHeight) {
//            float scale = (float) y / imageHeight;
//            float alpha = (255 * scale);
//            if (statusBarView != null)
//                statusBarView.setBackgroundColor(Color.argb((int) alpha, 42, 43, 43));
//            if (parentLL != null)
//                parentLL.setBackgroundColor(Color.argb((int) alpha, 42, 43, 43));
//        } else {
//            if (statusBarView != null)
//                statusBarView.setBackgroundColor(Color.argb(245, 42, 43, 43));
//            if (parentLL != null)
//                parentLL.setBackgroundColor(Color.argb(245, 42, 43, 43));
//        }
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

}
