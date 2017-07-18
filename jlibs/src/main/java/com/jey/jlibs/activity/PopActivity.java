package com.jey.jlibs.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.example.jlibs.R;
import com.jey.jlibs.base.AsyHttp;
import com.jey.jlibs.base.BroadcastCenter;
import com.jey.jlibs.fragment.PageFragment;
import com.jey.jlibs.fragment.PopFragment;
import com.jey.jlibs.interface_.OnRequestPermissionsResultForAppCompatActivity;
import com.jey.jlibs.utils.CommonFunction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;

public class PopActivity extends AppCompatActivity implements BroadcastCenter.Reader {
    /**
     * permission default request code
     */
    public static final int REQUEST_PERMISSION_FOR_POP_ACTIVITY = 135;
    /**
     * permissionResult default activity
     */
    public static OnRequestPermissionsResultForAppCompatActivity onRequestPermissionsResultForAppCompatActivity;
    /**
     * first PopFragment
     */
    @SuppressLint("StaticFieldLeak")
    public static PopFragment popFragment = null;
    /**
     * default progressBar
     */
    private ProgressBar loading;
    /**
     * last PopFragment
     */
    private PopFragment mLastFragment;
    /**
     * fragmentManager
     */
    private FragmentManager fm;
    /**
     * all PopFragment List
     */
    private List<PopFragment> mAllPopFragmentList;

    private TimeCount timeCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        ButterKnife.bind(this);
        CommonFunction.setStatusTransparent(this);//设置状态栏透明
        init();
    }

    private void init() {
        fm = getSupportFragmentManager();
        mAllPopFragmentList = new LinkedList<>();
        if (popFragment != null) {
            popFragment(popFragment);
        }
        subscribeBroadcast();

        timeCount = new TimeCount(10000,1000);
        timeCount.start();
    }

    class TimeCount extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            new AsyHttp(getApplicationContext(), null, new AsyHttp.IPostCallback() {
                @Override
                public void onAsyHttpSuccess(JSONObject json) {
                    try {
                        JSONObject jsonObject = json.getJSONObject("list");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public Boolean onAsyHttpErr(JSONObject json) {

                    return null;
                }

                @Override
                public void onAsyHttpProgress(int percent) {

                }
            }).execute("/Gate/appScan.json");
            timeCount.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeCount.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timeCount.start();
    }

    private void subscribeBroadcast() {
        BroadcastCenter.subscribe(BroadcastCenter.TITLE.NAVIGATETOPAGE, this);

//        BroadcastCenter.subscribe(BroadcastCenter.TITLE.LOGINSUCCESS, new BroadcastCenter.Reader() {
//            @SuppressLint("CommitTransaction")
//            @Override
//            public void readNews(BroadcastCenter.TITLE title, Object content) {
//                int i = fm.getBackStackEntryCount();
//                if (i > 0) {
//                    for (int j = 0; j < i; j++) {
//                        if (fm.getBackStackEntryAt(j).getName().equals("com.chinacqsb.YouGoGo.fragment.LoginFragment")) {
//                            fm.popBackStackImmediate(null, 0);
//                            if (fm.getBackStackEntryCount() == 0)
//                                PopActivity.this.finish();
//                        }
//                    }
//                }
//            }
//        });

        BroadcastCenter.subscribe(BroadcastCenter.TITLE.BACK, new BroadcastCenter.Reader() {
            @Override
            public void readNews(BroadcastCenter.TITLE title, Object content) {
                if (content instanceof PopFragment) {
                    int i = fm.getBackStackEntryCount();
                    if (i > 0)
                        fm.popBackStackImmediate(null, 0);
                    popFragment((PopFragment) content);
                } else if (content instanceof PageFragment) {
                    PopActivity.this.finish();
                }
            }
        });
    }

    public void popFragment(PopFragment fragment) {
        try {
            if (fragment == null) return;
            mAllPopFragmentList.add(fragment);
            mLastFragment = fragment;
            FragmentTransaction t = fm.beginTransaction();
            t.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out,
                    R.anim.push_left_in, R.anim.push_right_out);
            t.add(R.id.popcontainer, fragment, fragment.getClass().toString());
            t.addToBackStack(fragment.getClass().getName());
            t.commitAllowingStateLoss();
            startPopFragmentLifeCycle(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPopFragmentLifeCycle(boolean isBack) {
        if (mAllPopFragmentList.size() >= 1) {
            final PopFragment mLastPopFragment = mAllPopFragmentList.get(mAllPopFragmentList.size() - 1);
            if (mLastPopFragment != null) {
                if (isBack) {
                    final View view = mLastPopFragment.getView();
                    mLastPopFragment.popOnPause(view);
                } else {
//                    mLastPopFragment.setOnAddFragmentLifeCycle(new PopFragment.OnAddFragmentLifeCycle() {
//                        @Override
//                        public void onResume() {
//                            final View view = mLastPopFragment.getView();
//                            mLastPopFragment.popOnResume(view);
//                        }
//
//                        @Override
//                        public void onPause() {
//                        }
//                    });
                }
            }
        }
        if (mAllPopFragmentList.size() > 1) {
            PopFragment mBeforePopFragment = mAllPopFragmentList.get(mAllPopFragmentList.size() - 2);
            if (mBeforePopFragment != null) {
                View view = mBeforePopFragment.getView();
                if (isBack) {
                    mBeforePopFragment.popOnResume(view);
                } else {
                    mBeforePopFragment.popOnPause(view);
                }
            }
        }
        if (isBack && mAllPopFragmentList.size() > 0) {
            PopFragment mLastPopFragment = mAllPopFragmentList.get(mAllPopFragmentList.size() - 1);
            if (mAllPopFragmentList.contains(mLastPopFragment)) {
                mAllPopFragmentList.remove(mLastPopFragment);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BroadcastCenter.unSubscribe(BroadcastCenter.TITLE.NAVIGATETOPAGE, this);
        popFragment = null;
    }

    @Override
    public void readNews(BroadcastCenter.TITLE title, Object content) {
        if (content instanceof PopFragment) {
            popFragment((PopFragment) content);
        } else if (content instanceof PageFragment) {
            this.finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (onRequestPermissionsResultForAppCompatActivity != null)
            onRequestPermissionsResultForAppCompatActivity.onRequestPermissionsResult(requestCode,
                    permissions, grantResults);
        onRequestPermissionsResultForAppCompatActivity = null;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            startPopFragmentLifeCycle(true);
            if (mLastFragment != null) {
                boolean tag = mLastFragment.onKeyDown(keyCode, event);
                if (tag)
                    return tag;
            }
            if (fm.getBackStackEntryCount() <= 1) {
                this.finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void setOnRequestPermissionsResultForAppCompatActivity(
            OnRequestPermissionsResultForAppCompatActivity listener) {
        onRequestPermissionsResultForAppCompatActivity = listener;
    }

    public FragmentManager getFm() {
        return fm;
    }
}
