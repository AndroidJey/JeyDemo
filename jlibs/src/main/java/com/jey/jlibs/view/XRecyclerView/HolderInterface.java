package com.jey.jlibs.view.XRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Hashtable;

/**
 * Created by sevnce on 2016/10/23.
 */

public interface HolderInterface {

    void onBindView(RecyclerView.Adapter adapter, Hashtable<String, View> fields, Object o, int position);


    Hashtable<String, Integer> setViewFieldsMap();

    String setPhotoKey();
}
