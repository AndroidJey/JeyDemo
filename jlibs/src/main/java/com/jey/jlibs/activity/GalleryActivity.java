package com.jey.jlibs.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jlibs.R;
import com.jey.jlibs.utils.CommonFunction;
import com.jey.jlibs.utils.PhotoSelectUtil.Bimp;
import com.jey.jlibs.utils.PhotoSelectUtil.ImageItem;
import com.jey.jlibs.utils.PhotoSelectUtil.PublicWay;
import com.jey.jlibs.utils.PhotoSelectUtil.Util;
import com.jey.jlibs.zoom.PhotoView;
import com.jey.jlibs.zoom.ViewPagerFixed;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JW.Lee
 * @ClassName: GalleryActivity
 * @Description: 这个是用于进行图片浏览时的界面
 * @date 2015-9-1 下午7:34:31
 */
public class GalleryActivity extends AppCompatActivity { // 发送按钮
    private Button btnFinish;
    // 当前的位置
    private int location = 0;

    private ArrayList<View> listViews = null;
    private ViewPagerFixed pager;
    private MyPageAdapter adapter;
    public List<String> drr;

    public GalleryActivity() {
        drr = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_gallery);// 切屏到主界面
        CommonFunction.setStatusTransparent(this);
        PublicWay.activityList.add(this);
        ImageView ivBack = (ImageView) findViewById(R.id.tv_left_operation);
        btnFinish = (Button) findViewById(R.id.btn_finish);
        btnFinish.setVisibility(View.GONE);// TODO: 2016/3/26 暂屏蔽此功能
        TextView btnDelete = (TextView) findViewById(R.id.tv_delete);
        ivBack.setOnClickListener(new BackListener());
        btnFinish.setOnClickListener(new GallerySendListener());
        btnDelete.setOnClickListener(new DelListener());

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", -1);
        if (position > -1) { // 预览全部照片
            btnDelete.setVisibility(View.GONE);
            btnFinish.setVisibility(View.GONE);
            ArrayList<ImageItem> dataList = (ArrayList<ImageItem>) intent.getSerializableExtra("dataList");
            for (ImageItem imageItem : dataList) {
                initListViews(imageItem.getBitmap());
            }

            pager = (ViewPagerFixed) findViewById(R.id.gallery_preview);
            pager.addOnPageChangeListener(pageChangeListener);
            pager.removeOnPageChangeListener(pageChangeListener);
            adapter = new MyPageAdapter(listViews);
            pager.setAdapter(adapter);
            pager.setPageMargin(1);
            pager.setCurrentItem(position);
        } else {// 只预览已选择的照片
            isShowOkBt();
            // 为发送按钮设置文字
            for (int i = 0; i < Bimp.tempSelectBitmap.size() - Bimp.customPicCount; i++) {
                initListViews(Bimp.tempSelectBitmap.get(i).getBitmap());
            }

            pager = (ViewPagerFixed) findViewById(R.id.gallery_preview);
//            pager.setOnPageChangeListener(pageChangeListener);
            pager.addOnPageChangeListener(pageChangeListener);
            pager.removeOnPageChangeListener(pageChangeListener);
            adapter = new MyPageAdapter(listViews);
            pager.setAdapter(adapter);
            pager.setPageMargin(1);
            int id = intent.getIntExtra("ID", 0);
            pager.setCurrentItem(id);
        }
    }

    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            location = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void initListViews(Bitmap bm) {
        if (listViews == null) {
            listViews = new ArrayList<>();
        }
        PhotoView img = new PhotoView(this);
        img.setBackgroundColor(Util.getColorById(this, R.color.transparent_background_70));
        img.setImageBitmap(bm);
        img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        listViews.add(img);
    }

    // 返回按钮添加的监听器
    private class BackListener implements OnClickListener {

        public void onClick(View v) {
            Intent intent = new Intent("data.broadcast.action");
            sendBroadcast(intent);
            finish();
        }
    }

    // 删除按钮添加的监听器
    private class DelListener implements OnClickListener {

        public void onClick(View v) {
            setResult(2);
            if (listViews.size() == 1) {
                Bimp.tempSelectBitmap.clear();
                btnFinish.setText(String.format(getString(R.string.plugin_photo_finish_format), Bimp.tempSelectBitmap.size() - Bimp.customPicCount, Bimp.max));
                Intent intent = new Intent("data.broadcast.action");
                sendBroadcast(intent);
                finish();
            } else {
                Bimp.tempSelectBitmap.remove(location);
                pager.removeAllViews();
                listViews.remove(location);
                adapter.setListViews(listViews);
                btnFinish.setText(String.format(getString(R.string.plugin_photo_finish_format), Bimp.tempSelectBitmap.size() - Bimp.customPicCount, Bimp.max));
                adapter.notifyDataSetChanged();
            }
        }
    }

    // 完成按钮的监听
    private class GallerySendListener implements OnClickListener {
        public void onClick(View v) {
//			PublicWay.finishActivitys();
        }
    }

    public void isShowOkBt() {
        if (Bimp.tempSelectBitmap.size() - Bimp.customPicCount > 0) {
            btnFinish.setText(String.format(getString(R.string.plugin_photo_finish_format), Bimp.tempSelectBitmap.size() - Bimp.customPicCount, Bimp.max));
            btnFinish.setPressed(true);
            btnFinish.setClickable(true);
            btnFinish.setTextColor(ContextCompat.getColor(this, R.color.plugin_camera_white));
        } else {
            btnFinish.setPressed(false);
            btnFinish.setClickable(false);
            btnFinish.setTextColor(ContextCompat.getColor(this, R.color.plugin_camera_light_gray));
        }
    }

    /**
     * 监听返回按钮
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {// TODO: 2016/3/26 暂不处理
            Intent intent = new Intent("data.broadcast.action");
            sendBroadcast(intent);
            finish();
        }
        return false;
    }

    class MyPageAdapter extends PagerAdapter {
        private ArrayList<View> listViews;
        private int size;

        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {
            return size;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
        }

        public void finishUpdate(View arg0) {
        }

        public Object instantiateItem(View arg0, int arg1) {
            try {
                ((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

            } catch (Exception e) {
            }
            return listViews.get(arg1 % size);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }
}