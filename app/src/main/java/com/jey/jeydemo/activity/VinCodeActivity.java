package com.jey.jeydemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.jey.jlibs.base.AsyHttp;
import com.jey.jeydemo.R;
import com.jey.jeydemo.adapter.VinData;
import com.jey.jeydemo.adapter.listviewadapter;
import com.jey.jeydemo.http.Fault;
import com.jey.jeydemo.http.HttpLoader;
import com.jey.jeydemo.movie.MovieActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

public class VinCodeActivity extends Activity {
    private RecyclerView recyclerView;
    private listviewadapter mMovieAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vin_code);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        initListView();
    }

    private void initListView() {
        recyclerView.addItemDecoration(new MovieActivity.MovieDecoration());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        mMovieAdapter = new listviewadapter();
        recyclerView.setAdapter(mMovieAdapter);
//        getVinList();
        getHttpList();
    }

    private void getVinList() {
        Map<String, Object> para = new HashMap<>();
        para.put("GateId", "10");
        new AsyHttp(this, para, new AsyHttp.IPostCallback() {
            @Override
            public void onAsyHttpSuccess(JSONObject json) {
                try {
                    JSONArray jsonArray = json.getJSONArray("list");
                    List<VinData> dataList = new ArrayList<VinData>();
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject jo = (JSONObject) jsonArray.get(i);
                        String photo = jo.getString("PhotoUrl");
                        String num = jo.getString("Num");
                        VinData data = new VinData(num,photo);
                        dataList.add(data);
                    }
                    mMovieAdapter.setData(dataList);
                    mMovieAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public Boolean onAsyHttpErr(JSONObject json) {
                return null;
            }

            @Override
            public void onAsyHttpProgress(int percent) {

            }
        }).execute("/Gate/listEnterSiteViewByPage.json");
    }
    private void getHttpList(){
        new HttpLoader().getHttp("Gate/listEnterSiteViewByPage.json",null).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                mMovieAdapter.setData((List<VinData>) o);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("TAG","error message:"+throwable.getMessage());
                if(throwable instanceof Fault){
                    Fault fault = (Fault) throwable;
                    if(fault.getErrorCode() == 404){
                        //错误处理
                    }else if(fault.getErrorCode() == 500){
                        //错误处理
                    }else if(fault.getErrorCode() == 501){
                        //错误处理
                    }
                }
            }
        });
    }
}
