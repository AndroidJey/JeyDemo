package com.jey.jlibs.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jey.jlibs.activity.BaseActivity;
import com.jey.jlibs.base.BroadcastCenter;
import com.jey.jlibs.utils.DialogMaker;
import com.jey.jlibs.utils.StringUtils;
import com.jey.jlibs.utils.ToastUtil;

public abstract class PageFragment extends Fragment {
    ViewGroup container;
    public View view;
    protected Dialog dialog;
    private boolean isConnected = true;
    private boolean isWifiConnected = true;

    @Override
    public void onAttach(Context context) {
        onConnectionChangeListener();
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.container = container;
        view = inflater.inflate(setLayoutId(), container, false);
        initViews();
        return view;
    }

    protected void destroyAllFragments() {
        BaseActivity activity = (BaseActivity) getActivity();
        FragmentManager fm = activity.getFm();
        if (fm == null) return;
        fm.popBackStackImmediate(null, 1);
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

    public void showCommonDialog(String title, String mess) {
        new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(mess)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }
    protected void showToash(String msg) {
        if (StringUtils.isEmpty(msg)) return;
        ToastUtil.show(getActivity(), msg);
    }
    protected abstract void initViews();

    protected abstract int setLayoutId();

}
