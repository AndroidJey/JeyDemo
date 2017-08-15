package com.jey.jlibs.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.example.jlibs.R;
import com.jey.jlibs.base.AsyHttp;
import com.jey.jlibs.base.BroadcastCenter;
import com.jey.jlibs.dataModel.Database;
import com.jey.jlibs.fragment.PageFragment;
import com.jey.jlibs.fragment.PopFragment;
import com.jey.jlibs.interface_.OnRequestPermissionsResultForAppCompatActivity;
import com.jey.jlibs.utils.CommonFunction;
import com.jey.jlibs.utils.ToastUtil;

import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {
    /**
     * permission default request code
     */
    public static final int REQUEST_PERMISSION_FOR_BASE_ACTIVITY = 134;
    /**
     * permissionResult default activity
     */
    public static OnRequestPermissionsResultForAppCompatActivity onRequestPermissionsResultForAppCompatActivity;
    /**
     * boolean is background
     */
    public static boolean isForeground = false;
    /**
     * Context
     */
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    /**
     * last PageFragment
     */
    @SuppressLint("StaticFieldLeak")
    public static PageFragment mLastPageFragment;
    /**
     * first PopFragment
     */
    private FragmentManager fm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        CommonFunction.setStatusTransparent(this);//设置状态栏透明
        Database.init(this);
        initApplicationData();
        initData();
    }

    private void initApplicationData() {
        context = getApplicationContext();
        AsyHttp.setNetworkConnnected(CommonFunction.isConnect(this));
        AsyHttp.LoginDialogClass = "com.sevnce.yhlib.Dialog.LoginDialog";

        BroadcastCenter.subscribe(BroadcastCenter.TITLE.NAVIGATETOPAGE, new BroadcastCenter.Reader() {
            Object lastContent = null;

            public void readNews(BroadcastCenter.TITLE title, Object content) {
                if (lastContent != null && lastContent.equals(content))
                    return;
                lastContent = content;
                if (content instanceof PopFragment) {
//                    if (!((PopFragment) content).getClass().getName().equals("com.chinacqsb.ShangYouCJ.fragment.LoginFragment")) {
                    if (PopActivity.popFragment == null) {
                        PopActivity.popFragment = (PopFragment) content;
                        Intent intent = new Intent(BaseActivity.this, PopActivity.class);
                        startActivity(intent);
                    }
//                    }
                } else if (content instanceof PageFragment)
                    NavFragment((PageFragment) content);
//
            }
        });

        BroadcastCenter.subscribe(BroadcastCenter.TITLE.BACK, new BroadcastCenter.Reader() {
            @Override
            public void readNews(BroadcastCenter.TITLE title, Object content) {
                if (content instanceof PageFragment) {
                    NavFragment((PageFragment) content);
                }
            }
        });

        BroadcastCenter.subscribe(BroadcastCenter.TITLE.LOGINSUCCESS, new BroadcastCenter.Reader() {
            public void readNews(BroadcastCenter.TITLE title, Object content) {
                startlogin();
            }
        });

        BroadcastCenter.subscribe(BroadcastCenter.TITLE.PASSLOGIN, new BroadcastCenter.Reader() {
            public void readNews(BroadcastCenter.TITLE title, Object content) {
                passLogin();
            }
        });

        if (mLastPageFragment != null)
            NavFragment(mLastPageFragment);
    }

    private void initData() {
        fm = getSupportFragmentManager();
    }

    public void passLogin() {

    }

    public void startlogin() {

    }

    protected void showToash(String pMsg) {
        ToastUtil.show(this, pMsg);
    }

    public FragmentManager getFm() {
        return fm;
    }

    protected void NavFragment(PageFragment fragment) {
        try {
            if (fragment == null) return;
            mLastPageFragment = fragment;
            FragmentTransaction t = fm.beginTransaction();
//            t.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
            t.replace(R.id.maincontainer, fragment, fragment.getClass().getName());
            t.addToBackStack(fragment.getClass().getName());
            t.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (onRequestPermissionsResultForAppCompatActivity != null)
            onRequestPermissionsResultForAppCompatActivity.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResultForAppCompatActivity = null;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static void setOnRequestPermissionsResultForAppCompatActivity(OnRequestPermissionsResultForAppCompatActivity listener) {
        onRequestPermissionsResultForAppCompatActivity = listener;
    }

    private long exitTime = 0;

    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.show(BaseActivity.this, "再按一次退出应用");
            exitTime = System.currentTimeMillis();
        } else {
//            android.os.Process.killProcess(android.os.Process.myPid());//退出应用主进程
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(broadcastReceiver);
    }
}
