package com.jey.jeydemo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jey.jeydemo.R;
import com.jey.jeydemo.fragment.TabFragment1;
import com.jey.jeydemo.fragment.TabFragment2;
import com.jey.jeydemo.fragment.TabFragment3;
import com.jey.jeydemo.fragment.TabFragment4;
import com.jey.jeydemo.view.BottomTabView;

import java.util.ArrayList;
import java.util.List;

public class BottomTabActivity extends BottomTabBaseActivity {

    TabFragment1 tab1 = new TabFragment1();
    TabFragment2 tab2 = new TabFragment2();
    TabFragment3 tab3 = new TabFragment3();
    TabFragment4 tab4 = new TabFragment4();
    List<Fragment> fragments = new ArrayList<>();
    @Override
    protected List<BottomTabView.TabItemView> getTabViews() {
        List<BottomTabView.TabItemView> tabItemViews = new ArrayList<>();
        tabItemViews.add(new BottomTabView.TabItemView(this, "标题1", R.color.colorPrimary,
                R.color.colorAccent, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
        tabItemViews.add(new BottomTabView.TabItemView(this, "标题2", R.color.colorPrimary,
                R.color.colorAccent, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
        tabItemViews.add(new BottomTabView.TabItemView(this, "标题3", R.color.colorPrimary,
                R.color.colorAccent, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
        tabItemViews.add(new BottomTabView.TabItemView(this, "标题4", R.color.colorPrimary,
                R.color.colorAccent, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
        return tabItemViews;
    }

    @Override
    protected List<Fragment> getFragments() {
        if (fragments.size()==0){
            fragments.add(tab1);
            fragments.add(tab2);
            fragments.add(tab3);
            fragments.add(tab4);
        }
        return fragments;
    }

    @Override
    protected View getCenterView() {
        ImageView centerView = new ImageView(this);
        centerView.setImageResource(R.mipmap.ic_launcher_round);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        layoutParams.leftMargin = 30;
        layoutParams.rightMargin = 30;
        layoutParams.bottomMargin = 0;
        centerView.setLayoutParams(layoutParams);
        centerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToash("center被点了 *_*");
            }
        });
        return centerView;
    }

}
