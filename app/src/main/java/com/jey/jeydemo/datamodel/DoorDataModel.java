package com.jey.jeydemo.datamodel;


import com.jey.jlibs.dataModel.BaseDataModel;

/**
 * Created by jey on 2017/3/10.
 */

public class DoorDataModel extends BaseDataModel {
    @Override
    public String getKeyName() {
        return "Name";
    }

    @Override
    public String getViewUrl() {
        return null;
    }

    @Override
    public String getUpdateUrl() {
        return null;
    }

    @Override
    public String getDeleteUrl() {
        return null;
    }
}
