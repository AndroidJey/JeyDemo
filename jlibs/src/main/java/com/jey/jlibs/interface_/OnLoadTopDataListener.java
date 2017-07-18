package com.jey.jlibs.interface_;


import com.jey.jlibs.dataModel.BaseDataModel;

import java.util.List;

/**
 * Created by 91238 on 2016/11/10.
 */

public interface OnLoadTopDataListener {
    void OnLoadTopDataComplete(List<BaseDataModel> dataModels);
}
