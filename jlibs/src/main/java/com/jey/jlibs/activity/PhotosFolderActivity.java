package com.jey.jlibs.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jlibs.R;
import com.jey.jlibs.adapter.FolderAdapter;
import com.jey.jlibs.utils.CommonFunction;
import com.jey.jlibs.utils.PhotoSelectUtil.AlbumHelper;
import com.jey.jlibs.utils.PhotoSelectUtil.ImageBucket;
import com.jey.jlibs.utils.PhotoSelectUtil.PublicWay;

import java.util.List;

/**
 * 系统相册列表
 */
public class PhotosFolderActivity extends AppCompatActivity {
    public static final int RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_photos_folder);
        CommonFunction.setStatusTransparent(this);
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        PublicWay.activityList.add(this);
        init();
    }

    private void init() {
        Context mContext = PhotosFolderActivity.this;
        ListView lvPhotosFolder = (ListView) findViewById(R.id.lv_photos_folder);
        ImageView tvLeft = (ImageView) findViewById(R.id.tv_left_operation);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        TextView tvRight = (TextView) findViewById(R.id.tv_right_operation);
        tvRight.setVisibility(View.GONE);
        tvTitle.setText(R.string.plugin_photo_photo);
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        AlbumHelper helper = AlbumHelper.getHelper();
        helper.init(mContext);
        List<ImageBucket> folderList = helper.getImagesBucketList(true);
        FolderAdapter folderAdapter = new FolderAdapter(mContext, folderList);
        lvPhotosFolder.setAdapter(folderAdapter);
        lvPhotosFolder.setOnItemClickListener(mItemClickListener);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long longId) {
            setResult(RESULT_CODE, new Intent().putExtra("position", position));
            finish();
        }
    };

}
