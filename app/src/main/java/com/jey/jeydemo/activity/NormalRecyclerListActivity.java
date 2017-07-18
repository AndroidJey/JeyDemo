package com.jey.jeydemo.activity;

import android.app.Activity;
import android.os.Bundle;
import com.jey.jeydemo.R;
import com.jey.jeydemo.datamodel.GateDataModel;
import com.jey.jeydemo.holder.RecordsHolder;
import com.jey.jeydemo.view.NormalRecyclerView;

import java.util.HashMap;
import java.util.Map;

public class NormalRecyclerListActivity extends Activity {
    private NormalRecyclerView recyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_recycler_list);
        recyclerView = (NormalRecyclerView) findViewById(R.id.recyclerView);
//        initRecyclerView();
    }

    private void initRecyclerView() {
        Map<String, Object> para = new HashMap<>();
        para.put("GateId", "3");
        recyclerView.setPara(para);
        recyclerView.setSingleLayout(GateDataModel.class,R.layout.item_records_view,new RecordsHolder(this));
        recyclerView.setAction("");
    }
}
