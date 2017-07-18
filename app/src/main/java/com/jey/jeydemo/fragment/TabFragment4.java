package com.jey.jeydemo.fragment;


import android.support.v4.app.Fragment;

import com.jey.jlibs.fragment.PageFragment;
import com.jey.jeydemo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment4 extends PageFragment {


    public TabFragment4() {
        // Required empty public constructor
    }

    @Override
    protected void initViews() {
        showToash("哈哈444");
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_tab_fragment4;
    }

}
