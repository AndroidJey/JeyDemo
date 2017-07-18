package com.jey.jlibs.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jlibs.R;
import com.jey.jlibs.adapter.PictureBrowsingAdapter;
import com.jey.jlibs.dataModel.PictureThumbEntity;
import com.jey.jlibs.interface_.ActionCompeleteListener;
import com.jey.jlibs.utils.DialogMaker;
import com.jey.jlibs.utils.PhotoSelectUtil.PictureBrowsingHelper;

import java.util.ArrayList;

/**
 * 图集activty
 */
public class PictureBrowsingActivity extends AppCompatActivity {

    public static final int STORAGE_RESQUEST_CODE = 4355;

    private FrameLayout parentFL;

    private ViewPager mViewPager;

    private ImageView ibBack;

    private TextView tvPage;
    private TextView tvReplace;

    private PictureBrowsingAdapter mAdapter;

    private ArrayList<PictureThumbEntity> mDataList;

    private Dialog dialog;

    private int currentPosition = 0;
    private int holderPosition = -1;
    private boolean allowSavePicture = false;
    private boolean allowReplacePicture = false;

    private ActionCompeleteListener listener;

    private PictureBrowsingHelper.OnPermissionAllowedOrRefusedListener permissionListner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_browsing);
        getIntentData();
        initViews();
    }

    /**
     * 获取启动该界面所传入的数据源
     */
    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle b = intent.getExtras();
            if (b != null) {
                mDataList = new ArrayList<>();
                mDataList = (ArrayList<PictureThumbEntity>) b.getSerializable("data");
                currentPosition = b.getInt("position");
                holderPosition = b.getInt("holderPosition");
                allowSavePicture = b.getBoolean("AllowSavePicture");
                allowReplacePicture = b.getBoolean("AllowReplacePicture");
            }
        }
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        parentFL = (FrameLayout) findViewById(R.id.picture_browsing_parent_fl);
        mViewPager = (ViewPager) findViewById(R.id.picture_browsing_view_pager);
        ibBack = (ImageView) findViewById(R.id.picture_browsing_back_ib);
        tvPage = (TextView) findViewById(R.id.picture_browsing_page_tv);
        tvReplace = (TextView) findViewById(R.id.picture_browsing_replace_tv);
        mAdapter = new PictureBrowsingAdapter(this, mDataList, currentPosition, allowSavePicture);
        mViewPager.addOnPageChangeListener(mAdapter);
        setPermissionListner(mAdapter);
        mViewPager.setAdapter(mAdapter);
        if (currentPosition != 0)
            mViewPager.setCurrentItem(currentPosition);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.onResult(true, null);
            }
        });
        if (allowReplacePicture) {
            tvReplace.setVisibility(View.VISIBLE);
            tvReplace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.putExtra("holderPosition", holderPosition);
                    i.putExtra("position", currentPosition);
                    setResult(Activity.RESULT_OK, i);
                    mAdapter.onResult(true, null);
                }
            });
        }
        setPageText(currentPosition, mAdapter.getCount());
    }

    public FrameLayout getParentFL() {
        return parentFL;
    }

    public void setActionCompeleteListener(ActionCompeleteListener listener) {
        this.listener = listener;
    }

    public void setPermissionListner(PictureBrowsingHelper.OnPermissionAllowedOrRefusedListener permissionListner) {
        this.permissionListner = permissionListner;
    }

    public void showLoadingDialog() {
        dialog = DialogMaker.showCommenWaitDialog(this, "正在下载...", null, true, null);
    }

    public void setPageText(int currentPage, int allPageCounts) {
        tvPage.setText((currentPage + 1) + "/" + allPageCounts);
    }

    public void dismissLoadingDialog() {
        if (dialog != null) dialog.dismiss();
    }

    public boolean requestPermission() {
        if (PictureBrowsingHelper.checkBuildVersion()) {
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            if (!PictureBrowsingHelper.checkPermissionStatus(permissions, this)) {
                ActivityCompat.requestPermissions(this, permissions, STORAGE_RESQUEST_CODE);
                return false;
            } else
                return true;
        } else
            return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_RESQUEST_CODE:
                if (permissionListner != null) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionListner.onPermissionAllowedOrRefused(new boolean[]{true}, null);
                    } else {
                        permissionListner.onPermissionAllowedOrRefused(new boolean[]{false}, null);
                    }
                }
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (listener != null) {
                listener.onResult(true, null);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
