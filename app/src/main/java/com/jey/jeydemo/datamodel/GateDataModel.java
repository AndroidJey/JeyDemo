package com.jey.jeydemo.datamodel;

import com.jey.jlibs.dataModel.BaseDataModel;
import com.jey.jlibs.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by jey on 2017/3/10.
 */

public class GateDataModel extends BaseDataModel {

    @Override
    protected void init(JSONObject json) {
        super.init(json);
        Object photo = this.getNameValues().get("Gates");
        if (!StringUtils.isBlank(photo + "") && !StringUtils.isEmpty(photo + "") && (photo + "").length() > 2) {
            if (photo instanceof JSONArray) {
                parseJSONArray(DoorDataModel.class, (JSONArray) photo, "Gates");
            } else if (photo instanceof JSONObject) {
                parseJSONObject(DoorDataModel.class, (JSONObject) photo, "Gates");
            }
        } else {
            if (photo != null && this.getNameValues().containsValue(photo))
                this.getNameValues().put("Gates", "");
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
