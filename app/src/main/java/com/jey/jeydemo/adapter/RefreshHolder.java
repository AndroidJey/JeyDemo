package com.jey.jeydemo.adapter;

import android.content.Context;
import android.view.View;

import com.jey.jlibs.base.ViewHolder;
import com.jey.jlibs.dataModel.BaseDataModel;

import java.util.Hashtable;

/**
 * Created by jey on 2017/4/25.
 */

public class RefreshHolder extends ViewHolder {
    protected RefreshHolder(Context context, int layoutId, Class dataClass) {
        super(context, layoutId, dataClass);
    }

    @Override
    public Hashtable<String, Integer> getFieldMap() {
        return super.getFieldMap();
    }

    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void handleData(BaseDataModel data, int position) throws Exception {
        super.handleData(data, position);
    }
}
