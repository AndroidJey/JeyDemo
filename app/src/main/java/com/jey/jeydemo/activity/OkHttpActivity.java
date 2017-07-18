package com.jey.jeydemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.jey.jeydemo.OkHttp.NetWorks;
import com.jey.jeydemo.R;
import com.jey.jeydemo.adapter.VinData;
import com.jey.jeydemo.http.Fault;
import com.jey.jeydemo.http.HttpLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.functions.Action1;

public class OkHttpActivity extends Activity implements View.OnClickListener {

    private Button btGetData;
    private TextView tvNetData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http);
        btGetData = (Button) findViewById(R.id.btGetNetData);
        tvNetData = (TextView) findViewById(R.id.tvNetData);
        btGetData.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btGetNetData:
//                sendHttp();
                getHttpList();
                break;
        }
    }

    private void getHttpList(){
        Map<String,Object> para = new HashMap<>();
        para.put("Price","30000-400000");
        new HttpLoader().getHttp("Client/CarMessage/listTotalNewCarView.json",para).subscribe(new Action1<JsonObject>() {
            @Override
            public void call(JsonObject o) {
                String a = o + "";
//                LinkedTreeMap<String,Object> map = (LinkedTreeMap<String, Object>) o;
//                LinkedTreeMap<String,Object> dataMap = (LinkedTreeMap<String, Object>) map.get("data");
//                List<LinkedTreeMap<String,String>> listMap = (List<LinkedTreeMap<String,String>>) dataMap.get("list");
//                tvNetData.setText(listMap.get(1).get("Engine"));
//                for (int i=0; i<listMap.size();i++){
//
//                }
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

    private void sendHttp(){
        NetWorks.Getcache(new Observer<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                tvNetData.setText(e.getLocalizedMessage());
                Log.e("MAIN2",e.getLocalizedMessage()+"--"+e.getMessage());
            }

            @Override
            public void onNext(Object verification) {
                tvNetData.setText(verification.toString());
            }
        });
    }

}
