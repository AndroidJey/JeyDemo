package com.jey.jlibs.dataModel;


/**
 * Created by H on 2015/11/2.
 */
public class UserModel extends BaseDataModel {
    private static final long serialVersionUID = -6833515588565861693L;

    @Override
    public String getKeyName() {
        return "Guid";
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
