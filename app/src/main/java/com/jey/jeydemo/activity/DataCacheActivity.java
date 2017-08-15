package com.jey.jeydemo.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.jey.jeydemo.utils.CommonFunctionUtils;
import com.jey.jeydemo.utils.PermissionUtil;
import com.jey.jlibs.activity.AlbumActivity;
import com.jey.jlibs.interface_.ActionCompeleteListener;
import com.jey.jlibs.utils.CommonFunction;
import com.jey.jlibs.utils.PhotoSelectUtil.Bimp;
import com.jey.jlibs.utils.ToastUtil;
import com.jey.jeydemo.R;
import com.jey.jeydemo.datamodel.GateDataModel;
import com.jey.jeydemo.holder.PageHomeGroup4RVHolder;
import com.jey.jlibs.view.NormalRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DataCacheActivity extends AppCompatActivity {
    @BindView(R.id.page_home_recycler_view)
    public NormalRecyclerView recyclerView;
    @BindView(R.id.button)
    public Button button;
    @BindView(R.id.regoback)
    public RelativeLayout regoback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_cache);
        CommonFunction.setStatusTransparent(this);
        ButterKnife.bind(this);
        // 初始化控件
        initView();

        initRefreshView();
    }

    private void initRefreshView() {
//        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
//        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//                refreshlayout.finishRefresh(2000);
//            }
//        });
//        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
//            @Override
//            public void onLoadmore(RefreshLayout refreshlayout) {
//                refreshlayout.finishLoadmore(2000);
//            }
//        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
//        Map<String, Object> para = new HashMap<>();
//        para.put("pageSize", 10);//只获取10条数据
//        recyclerView.setPara(para);
        if (recyclerView == null) {
            ToastUtil.show(getApplicationContext(),"Butterknife绑定失败!");
            return;
        }

        recyclerView.setSingleLayout(GateDataModel.class, R.layout.item_home_page_group4_rv,
                new PageHomeGroup4RVHolder(this, 0));
        recyclerView.setAction("http://116.62.19.252/Client/Home/newCarListForHome.json");

        regoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick({R.id.button})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.button:
                new CommonFunctionUtils(this).getPermissionByName(Manifest.permission.CAMERA, "确定", false,
                        new ActionCompeleteListener() {
                            @Override
                            public void onResult(boolean isSuccessed, Object obj) {
                                if (isSuccessed) {
                                    if (!PermissionUtil.cameraIsCanUse()) {
                                        ToastUtil.show(getApplicationContext(),"请前往设置获取相机权限");
                                        return;
                                    }
                                    Intent intent = new Intent(DataCacheActivity.this, AlbumActivity.class);
                                    Bimp.max = 2;
                                    DataCacheActivity.this.startActivityForResult(intent, 110);
                                }
                            }
                        });

                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
