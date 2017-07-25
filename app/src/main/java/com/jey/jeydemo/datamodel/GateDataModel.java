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

        Object carMessage = this.getNameValues().get("CarMessage");
        if (!StringUtils.isBlank(carMessage + "") && !StringUtils.isEmpty(carMessage + "") && (carMessage + "").length() > 2) {
            if (carMessage instanceof JSONArray) {
                parseJSONArray(GateDataModel.class, (JSONArray) carMessage, "CarMessage");
            } else if (carMessage instanceof JSONObject) {
                JSONObject jobj = (JSONObject) carMessage;
                if (jobj.has("Guid")) {
                    parseJSONObject(GateDataModel.class, (JSONObject) carMessage, "CarMessage");
                } else {
                    parseJSONObject(GateDataModel.class, (JSONObject) carMessage, "CarMessage");
                }
            }
        } else {
            if (carMessage != null && this.getNameValues().containsValue(carMessage))
                this.getNameValues().put("CarMessage", "");
        }

        Object p = this.getNameValues().get("Photos");
        if (!StringUtils.isBlank(p + "") && !StringUtils.isEmpty(p + "") && (p + "").length() > 2) {
            if (p instanceof JSONArray) {
                parseJSONArray(GateDataModel.class, (JSONArray) p, "Photos");
            } else if (p instanceof JSONObject) {
                parseJSONObject(GateDataModel.class, (JSONObject) p, "Photos");
            }
        } else {
            if (p != null && this.getNameValues().containsValue(p))
                this.getNameValues().put("Photos", "");
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
