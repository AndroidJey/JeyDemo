package com.jey.jlibs.interface_;

import com.jey.jlibs.dataModel.BaseDataModel;

import org.json.JSONObject;

import java.util.Hashtable;

/**
 * Created by sevnce on 2016/10/24.
 */
public interface FechRecyclerViewHolder {

    int getViewType(BaseDataModel data);

    Class getModelClass(JSONObject json);

    Hashtable<Integer, Integer> getViewLayoutResourseIdMap();

    Hashtable<Integer, Object> geteViewHolderMap();
}
