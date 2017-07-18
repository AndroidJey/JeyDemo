package com.jey.jeydemo.entity;


import com.jey.jlibs.dataModel.BaseDataModel;
import com.jey.jlibs.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by jey on 2016/12/26.
 */

public class PayDataModel extends BaseDataModel {
    @Override
    protected void init(JSONObject json) {
        super.init(json);
        Object car = this.getNameValues().get("Car");
        if (!StringUtils.isBlank(car + "") && !StringUtils.isEmpty(car + "") && (car + "").length() > 2) {
            if (car instanceof JSONArray) {
                parseJSONArray(PayDataModel.class, (JSONArray) car, "Car");
            } else if (car instanceof JSONObject) {
                parseJSONObject(PayDataModel.class, (JSONObject) car, "Car");
            }
        } else {
            if (car != null && this.getNameValues().containsValue(car))
                this.getNameValues().put("Car", "");
        }
    }

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
