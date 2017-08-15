package com.jey.jeydemo;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.jey.jlibs.base.AsyHttp;
import com.jey.jlibs.utils.ToastUtil;
import com.jey.jeydemo.activity.BottomTabActivity;
import com.jey.jeydemo.activity.DataCacheActivity;
import com.jey.jeydemo.activity.LoginActivity;
import com.jey.jeydemo.activity.OkHttpActivity;
import com.jey.jeydemo.activity.RefreshActivity;
import com.jey.jeydemo.activity.StickHeaderActivity;
import com.jey.jeydemo.activity.VideoPlayerActivity;
import com.jey.jeydemo.activity.VinCodeActivity;
import com.jey.jeydemo.movie.MovieActivity;
import com.jey.jeydemo.rangbar.RangeBar;
import com.jey.jeydemo.utils.LocationUtils;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationUtils.OnResultMapListener,DistrictSearch.OnDistrictSearchListener {
    private RangeBar rangeBar;
    private Button btRecyclerView;
    private Button btStickHeader;
    private Button btHttp;
    private Button btRetrofit;
    private Button btVinScan;
    private Button btChat;
    private Button btRefresh;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button btBottomTab;
    private Button btVideoPlayer;

    public LocationClient mLocationClient = null;

    private LocationUtils mLocationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化地图SDK<最好放置在Application中>
        SDKInitializer.initialize(getApplicationContext());
        // 创建定位管理信息对象
        mLocationUtils = new LocationUtils(getApplicationContext());
        setContentView(R.layout.activity_main);
        // 开启定位
//        mLocationUtils.startLocation();
        mLocationUtils.registerOnResult(this);

        mLocationUtils.getLocation("重庆", "南岸区");//输入地址信息反编码得到经纬度

        AsyHttp.Host = "http://116.62.19.252:81";
        rangeBar = (RangeBar) findViewById(R.id.rangeBar);
//        rangeBar.setBarColor(R.color.colorAccent);
        btRecyclerView = (Button) findViewById(R.id.btRecyclerView);
        btStickHeader = (Button) findViewById(R.id.btStickHeader);
        btHttp = (Button) findViewById(R.id.btOkHttp);
        btRetrofit = (Button) findViewById(R.id.btRetrofit);
        btVinScan = (Button) findViewById(R.id.btVinScan);
        btChat = (Button) findViewById(R.id.btChat);
        btRefresh = (Button) findViewById(R.id.btRefresh);
        btBottomTab = (Button) findViewById(R.id.btBottomTab);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        btVideoPlayer = (Button) findViewById(R.id.btVideoPlayer);

        DistrictSearch search = new DistrictSearch(getApplicationContext());
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords("北京市");//传入关键字
        query.setShowBoundary(true);//是否返回边界值
        search.setQuery(query);
        search.setOnDistrictSearchListener(this);//绑定监听器
        search.searchDistrictAnsy();

        btRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                LemonBubble.showRight(MainActivity.this, "集成成功！", 2000);
//                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
//                Bimp.max = 2;
//                MainActivity.this.startActivityForResult(intent, 110);
//                MainActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
                Intent intent = new Intent(MainActivity.this, DataCacheActivity.class);
                startActivity(intent);
//                mLocationClient.start();
            }
        });
        btStickHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                LemonBubble.showError(MainActivity.this, "集成成功！", 2000);
                Intent intent = new Intent(MainActivity.this, StickHeaderActivity.class);
                startActivity(intent);
            }
        });
        btHttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                LemonBubble.showRoundProgress(MainActivity.this, "集成成功！");
                Intent intent = new Intent(MainActivity.this, OkHttpActivity.class);
                startActivity(intent);
            }
        });
        btRetrofit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取默认的正确信息的泡泡信息对象
//                LemonBubbleInfo myInfo = LemonBubble.getRightBubbleInfo();
//// 设置图标在左侧，标题在右侧
//                myInfo.setLayoutStyle(LemonBubbleLayoutStyle.ICON_LEFT_TITLE_RIGHT);
//// 设置泡泡控件在底部
//                myInfo.setLocationStyle(LemonBubbleLocationStyle.BOTTOM);
//// 设置泡泡控件的动画图标颜色为蓝色
//                myInfo.setIconColor(Color.BLUE);
//// 设置泡泡控件的尺寸，单位dp
//                myInfo.setBubbleSize(200, 80);
//// 设置泡泡控件的偏移比例为整个屏幕的0.01,
//                myInfo.setProportionOfDeviation(0.01f);
//// 设置泡泡控件的标题
//                myInfo.setTitle("自定义泡泡控件");
//// 展示自定义的泡泡控件，并显示2s后关闭
//                LemonBubble.showBubbleInfo(MainActivity.this, myInfo, 2000);
                Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                startActivity(intent);
            }
        });
        btVinScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VinCodeActivity.class);
                startActivity(intent);
            }
        });
        btChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RefreshActivity.class);
                startActivity(intent);
            }
        });
        btBottomTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ToastUtil.show(MainActivity.this,"吐司消息");
                Intent intent = new Intent(MainActivity.this, BottomTabActivity.class);
                startActivity(intent);
            }
        });
        btVideoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
                startActivity(intent);
            }
        });

        setRefreshLayoutListener();
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);//停止刷新
                    }
                }, 6000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onReverseGeoCodeResult(Map<String, Object> map) {
        Log.i("data", "result====>" + map.toString());
    }

    @Override
    public void onGeoCodeResult(Map<String, Object> map) {
        Log.i("data", "result====>" + map.toString());
//        mLocationUtils.getAddress(Double.parseDouble(map.get("latitude").toString()),
//                Double.parseDouble(map.get("lontitude").toString()));
    }


    //查询结果监听
    @Override
    public void onDistrictSearched(DistrictResult districtResult) {
        int errocode = districtResult.getAMapException().getErrorCode();//errroCode如果是1000，则查询成功
        ArrayList<DistrictItem> district = districtResult.getDistrict();
        if (district.size()>0){
            DistrictItem districtItem = district.get(0);
            String code = districtItem.getAdcode();
            ToastUtil.show(getApplicationContext(),code);
        }
    }
}
