package com.jey.jlibs.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jlibs.R;
import com.jey.jlibs.adapter.AlbumGridViewAdapter;
import com.jey.jlibs.utils.PhotoSelectUtil.AlbumHelper;
import com.jey.jlibs.utils.PhotoSelectUtil.Bimp;
import com.jey.jlibs.utils.PhotoSelectUtil.FileUtils;
import com.jey.jlibs.utils.PhotoSelectUtil.ImageBucket;
import com.jey.jlibs.utils.PhotoSelectUtil.ImageItem;
import com.jey.jlibs.utils.PhotoSelectUtil.PublicWay;
import com.jey.jlibs.utils.PhotoSelectUtil.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Description :进入相册显示所有图片的界面
 * Created by Jey on 2016/8/25.
 */
public class AlbumActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;
    // 相册
    private TextView tvAlbum;
    // 取消
    private ImageView tvLeftOperation;
    // 完成
    private TextView tvRightOperation;
    //  预览
    private TextView tvPreview;
    // 头像图片裁剪之后
    private File photoFile;

    //进入相册时已选择照片的张数
    private int size;

    private AlbumGridViewAdapter gridImageAdapter;
    private Intent intent;
    private Context mContext;
    private ArrayList<ImageItem> dataList;
    private List<ImageBucket> contentList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_album);
        PublicWay.activityList.add(this);
        // 注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(broadcastReceiver, filter);
        init();
        initListener();
        // 这个函数主要用来控制预览和完成按钮的状态
        isShowOkBt();
    }

    private void init() {
        mContext = AlbumActivity.this;
        AlbumHelper helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        size= Bimp.tempSelectBitmap.size();
        contentList = helper.getImagesBucketList(true);
        dataList = new ArrayList<>();
        for (int i = 0; i < contentList.size(); i++) {
            dataList.addAll(contentList.get(i).imageList);
        }
//        FolderAdapter folderAdapter = new FolderAdapter(mContext, contentList);
        Collections.sort(dataList, new Comparator<ImageItem>() {
            @Override
            public int compare(ImageItem lhs, ImageItem rhs) {
                return rhs.getPicDate().compareTo(lhs.getPicDate());
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.plugin_photo_pictrue));
        tvLeftOperation = (ImageView) findViewById(R.id.tv_left_operation);
        tvRightOperation = (TextView) findViewById(R.id.tv_right_operation);
//        tvRightOperation.setTextColor(Util.getColorById(mContext, R.color.colorAccent));
        tvRightOperation.setTextColor(getResources().getColor(R.color.plugin_camera_emphasize_color));
        tvPreview = (TextView) findViewById(R.id.tv_preview);
        tvAlbum = (TextView) findViewById(R.id.tv_album);
        TextView tv = (TextView) findViewById(R.id.myText);
        GridView gridView = (GridView) findViewById(R.id.myGrid);
        intent = getIntent();
        gridImageAdapter = new AlbumGridViewAdapter(this, dataList, Bimp.tempSelectBitmap);
        gridView.setAdapter(gridImageAdapter);
        if (dataList == null || dataList.size() == 0) {
            tv.setVisibility(View.VISIBLE);
            gridView.setEmptyView(tv);
        }
        tvRightOperation.setText(String.format(getString(R.string.plugin_photo_finish_format), Bimp.tempSelectBitmap.size() - Bimp.customPicCount, Bimp.max));
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            gridImageAdapter.notifyDataSetChanged();
        }
    };

    private void initListener() {
        gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
            @Override
            public void itemClickListener(int position) {
//                String path = Environment.getExternalStorageDirectory() + "/headPhotos" + System.currentTimeMillis();
//                photoFile = new File(path);
//                ImageItem imge = dataList.get(position);
//                Bimp.tempSelectBitmap.add(0, dataList.get(position));
//                Intent intent = new Intent();
//                intent.setAction("com.android.camera.action.CROP");
//                intent.setDataAndType(Uri.fromFile(new File(imge.getImagePath()))
//                        , "image/*");// mUri是已经选择的图片Uri
//                intent.putExtra("crop", "true");
//                intent.putExtra("aspectX", 1);// 裁剪框比例
//                intent.putExtra("aspectY", 1);
//                intent.putExtra("outputX", 150);// 输出图片大小
//                intent.putExtra("outputY", 150);
//                intent.putExtra("return-data", true);
//
//                startActivityForResult(intent, 200);
            }
        });
        gridImageAdapter.setCheckBoxChangeClickListener(new AlbumGridViewAdapter.OnCheckBoxChangeClickListener() {
            @Override
            public void onChange(CheckBox checkBox, View coverView, int position, boolean isChecked) {
                if (Bimp.tempSelectBitmap.size() - Bimp.customPicCount >= Bimp.max) {
                    checkBox.setChecked(false);
                    coverView.setBackgroundColor(Util.getColorById(mContext, R.color.none_color));
                    if (!removeOneData(dataList.get(position))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AlbumActivity.this);
                        builder.setTitle("温馨提示")
                                .setMessage(AlbumActivity.this.getString(R.string.plugin_photo_only_choose_num))
                                .setNegativeButton("确定", null)
                                .create()
                                .show();
                    }
                    isShowOkBt();
                    return;
                }
                if (dataList.get(position).getBitmap() == null || dataList.get(position).getBitmap().getWidth() == 0 ||
                        dataList.get(position).getBitmap().getHeight() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AlbumActivity.this);
                    builder.setTitle("温馨提示")
                            .setMessage("该图片已破损，请选择其它图片！")
                            .setNegativeButton("确定", null)
                            .create()
                            .show();
                    checkBox.setChecked(false);
                    return;
                }
                if (isChecked) {
                    coverView.setBackgroundColor(Util.getColorById(mContext, R.color.transparent_background_70));
                    Bimp.tempSelectBitmap.add(0, dataList.get(position));
                    tvRightOperation.setText(String.format(AlbumActivity.this.getString(R.string.plugin_photo_finish_format), Bimp.tempSelectBitmap.size() - Bimp.customPicCount, Bimp.max));
                } else {
                    coverView.setBackgroundColor(Util.getColorById(mContext, R.color.none_color));
                    Bimp.tempSelectBitmap.remove(dataList.get(position));
                    tvRightOperation.setText(String.format(AlbumActivity.this.getString(R.string.plugin_photo_finish_format), Bimp.tempSelectBitmap.size() - Bimp.customPicCount, Bimp.max));
                }
                isShowOkBt();
            }
        });

        tvLeftOperation.setOnClickListener(new CancelListener());
        tvRightOperation.setOnClickListener(new CompleteSelectionListener());
        tvAlbum.setOnClickListener(new AlbumListener());
        tvPreview.setOnClickListener(new PreviewListener());
    }

    // 预览的监听
    private class PreviewListener implements OnClickListener {
        public void onClick(View v) {
            if (Bimp.tempSelectBitmap.size() - Bimp.customPicCount > 0) {
                intent.putExtra("position", -1);
                intent.setClass(AlbumActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        }
    }

    // 完成选择的监听
    private class CompleteSelectionListener implements OnClickListener {
        public void onClick(View v) {
//            String path = Environment.getExternalStorageDirectory() + "/headPhotos" + System.currentTimeMillis();
//            photoFile = new File(path);
//            ImageItem imge = Bimp.tempSelectBitmap.get(0);
//            Intent intent = new Intent();
//            intent.setAction("com.android.camera.action.CROP");
//            intent.setDataAndType(Uri.fromFile(new File(imge.getImagePath()))
//                    , "image/*");// mUri是已经选择的图片Uri
//            intent.putExtra("crop", "true");
//            intent.putExtra("aspectX", 1);// 裁剪框比例
//            intent.putExtra("aspectY", 1);
//            intent.putExtra("outputX", 150);// 输出图片大小
//            intent.putExtra("outputY", 150);
//            intent.putExtra("return-data", true);
//
//            startActivityForResult(intent, 200);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
            setResult(Activity.RESULT_OK);
            PublicWay.finishActivitys();
        }
    }

    // 相册的监听
    private class AlbumListener implements OnClickListener {
        public void onClick(View v) {
            //直接进入一个新的Activity
            startActivityForResult(new Intent(AlbumActivity.this, PhotosFolderActivity.class), REQUEST_CODE);
            /*仿微信以popupWindow形式弹出相册文件夹
            @SuppressLint("InflateParams")
            RelativeLayout parentView = (RelativeLayout) getLayoutInflater().inflate(R.layout.popup_window_album, null);
            RelativeLayout parent = (RelativeLayout) parentView.findViewById(R.id.parent);
            ListView lvFolder = (ListView) parentView.findViewById(R.id.lv_album);
            lvFolder.setAdapter(folderAdapter);
            final PopupWindow popupWindow = new PopupWindow(parentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));//背景不为空但是完全透明
            popupWindow.setFocusable(true);
            View view = findViewById(R.id.v_bottom_line);
            int[] screen_pos = new int[2];
            view.getLocationOnScreen(screen_pos);
            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, screen_pos[0], screen_pos[1] - parentView.getMeasuredHeight());
            parent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
            lvFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (gridImageAdapter != null) {
                        dataList.clear();
                        dataList.addAll(contentList.get(position).imageList);
                        gridImageAdapter.notifyDataSetChanged();
                    }
                    popupWindow.dismiss();
                }
            });
        }*/
        }
    }

    // 取消按钮的监听
    private class CancelListener implements OnClickListener {
        public void onClick(View v) {
            if (Bimp.tempSelectBitmap.size()>size){
                int size1 = Bimp.tempSelectBitmap.size();
                for (int i=0;i<size1-size;i++){
                    Bimp.tempSelectBitmap.remove(0);
                }
            }
//            Bimp.tempSelectBitmap.clear();
            setResult(Activity.RESULT_CANCELED);
            PublicWay.finishActivitys();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == PhotosFolderActivity.RESULT_CODE) {
            int itemPosition = data.getIntExtra("position", -1);
            if (gridImageAdapter != null) {
                dataList.clear();
                dataList.addAll(contentList.get(itemPosition).imageList);
                gridImageAdapter.notifyDataSetChanged();
            }
        }else if (requestCode == 200){//裁剪之后的图片
            if (resultCode == RESULT_OK) {
                // 拿到剪切数据
                Bitmap bmap = data.getParcelableExtra("data");
                // 图像保存到文件中
                FileUtils.delFile(FileUtils.SDPATH+"jey.jpg");
                FileUtils.saveBitmap(bmap,"jey.jpg");

                Bimp.tempSelectBitmap.get(0).setImagePath(FileUtils.SDPATH+"jey.jpg");
                overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
                setResult(Activity.RESULT_OK);
                PublicWay.finishActivitys();
            }
        }
    }

    private boolean removeOneData(ImageItem imageItem) {
        if (Bimp.tempSelectBitmap.contains(imageItem)) {
            Bimp.tempSelectBitmap.remove(imageItem);
            tvRightOperation.setText(String.format(getString(R.string.plugin_photo_finish_format), Bimp.tempSelectBitmap.size() - Bimp.customPicCount, Bimp.max));
            tvPreview.setText(String.format(getString(R.string.plugin_photo_preview_format), Bimp.tempSelectBitmap.size() - Bimp.customPicCount, Bimp.max));
            return true;
        }
        return false;
    }

    public void isShowOkBt() {
        if (Bimp.tempSelectBitmap.size() - Bimp.customPicCount > 0) {
            tvRightOperation.setText(String.format(getString(R.string.plugin_photo_finish_format), Bimp.tempSelectBitmap.size() - Bimp.customPicCount, Bimp.max));
            tvPreview.setText(String.format(getString(R.string.plugin_photo_preview_format), Bimp.tempSelectBitmap.size() - Bimp.customPicCount, Bimp.max));
            tvPreview.setPressed(true);
//            tvRightOperation.setPressed(true);
            tvPreview.setClickable(true);
            tvRightOperation.setClickable(true);
            tvRightOperation.setTextColor(getResources().getColor(R.color.plugin_camera_white));
//            tvRightOperation.setTextColor(Util.getColorById(this, R.color.colorPrimary));
            tvPreview.setTextColor(Util.getColorById(this, R.color.colorPrimary));
        } else {
            tvRightOperation.setText(String.format(getString(R.string.plugin_photo_finish_format), Bimp.tempSelectBitmap.size() - Bimp.customPicCount, Bimp.max));
            tvPreview.setText(String.format(getString(R.string.plugin_photo_preview_format), Bimp.tempSelectBitmap.size() - Bimp.customPicCount, Bimp.max));
            tvPreview.setPressed(false);
            tvPreview.setClickable(false);
//            tvRightOperation.setPressed(false);
            tvRightOperation.setClickable(false);
            tvRightOperation.setTextColor(getResources().getColor(R.color.colorPrimary));
//            tvRightOperation.setTextColor(Util.getColorById(this, R.color.divider_line_color));
            tvPreview.setTextColor(Util.getColorById(this, R.color.plugin_camera_light_gray));
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Bimp.tempSelectBitmap.size()>size){
                int size1 = Bimp.tempSelectBitmap.size();
                for (int i=0;i<size1-size;i++){
                    Bimp.tempSelectBitmap.remove(0);
                }
            }
//            Bimp.tempSelectBitmap.clear();
            setResult(Activity.RESULT_CANCELED);
            PublicWay.finishActivitys();
        }
        return false;

    }

    @Override
    protected void onRestart() {
        isShowOkBt();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            this.unregisterReceiver(broadcastReceiver);
        }
        PublicWay.finishActivitys();
    }
}
