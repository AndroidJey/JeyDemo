package com.jey.jeydemo.activity;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jey.jeydemo.R;
import com.jey.jeydemo.progressImagePlayer.ProgressImageView;
import com.jey.jeydemo.progressImagePlayer.ProgressModelLoader;
import com.jey.jeydemo.utils.DownLoadImage;
import com.jey.jeydemo.view.NormalRecyclerView;

import java.lang.ref.WeakReference;

public class RefreshActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {
    private NormalRecyclerView recyclerView;
    private SwipeRefreshLayout refreshSwipe;
    private ProgressImageView imageProgress;
    private Button btStartLoad;

    private static final int REFRESH_COMPLETE = 0X110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        recyclerView = (NormalRecyclerView) findViewById(R.id.refreshListView);
        refreshSwipe = (SwipeRefreshLayout) findViewById(R.id.refreshSwipe);
        imageProgress = (ProgressImageView) findViewById(R.id.imageProgress);
        btStartLoad = (Button) findViewById(R.id.btStartLoad);
//        imageView.setImageURI("http://192.168.1.130/images/history/1496332804/1496372317.jpg");

        btStartLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(RefreshActivity.this).using(new ProgressModelLoader(new ProgressHandler(RefreshActivity.this, imageProgress))).
                        load("http://image2.sina.com.cn/dy/o/2004-11-10/1100077821_2laygS.jpg")
                        .diskCacheStrategy(DiskCacheStrategy.NONE).into(imageProgress.getImageView());
            }
        });

//        new DownLoadImage(imageView).execute("http://192.168.1.130/images/history/1495468801/1495509200.jpg");
//        refreshSwipe.setOnRefreshListener(this);
//        Map<String, Object> para = new HashMap<>();
//        para.put("MerchantId", "c08f8c76526e489285dcbc5bdaf924eb");
//        para.put("pageSize", 5);
//        recyclerView.setPullRefreshEnabled(false);
//        recyclerView.setLoadingMoreEnabled(true);
//        recyclerView.setPara(para);
//        recyclerView.setAction("/PayLog/listPayLogByMerchant.json");
//        recyclerView.setLayoutManager(new FullyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        recyclerView.setSingleLayout(PayDataModel.class, R.layout.item_pay_history, new PayHistoryHolder(this));
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
//                    recyclerView.onRefresh();
//                    Toast toast = Toast.makeText(getApplicationContext(),"刷新成功",Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.TOP,0,0);
//                    toast.show();
                    refreshSwipe.setRefreshing(false);
                    break;
            }
        }
    };

    private static class ProgressHandler extends Handler {

        private final WeakReference<Activity> mActivity;
        private final ProgressImageView mProgressImageView;

        public ProgressHandler(Activity activity, ProgressImageView progressImageView) {
            super(Looper.getMainLooper());
            mActivity = new WeakReference<>(activity);
            mProgressImageView = progressImageView;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Activity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        int percent = msg.arg1*100/msg.arg2;
                        if (percent != 100) mProgressImageView.setProgress(percent);
                        else mProgressImageView.removeProgress();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
