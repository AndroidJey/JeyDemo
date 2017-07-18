package com.jey.jeydemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.jey.jeydemo.R;
import com.jey.jeydemo.adapter.listviewadapter;

import java.util.ArrayList;
import java.util.List;

public class StickHeaderActivity extends Activity {
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_header);
//        listView = (ListView) findViewById(R.id.listView);
//        List<String> data = new ArrayList<>();
//        data.add("第一个");
//        data.add("第2个");
//        data.add("第3个");
//        data.add("第4个");
//        data.add("第5个");
//        listView.setAdapter(new listviewadapter(data));
    }
}
