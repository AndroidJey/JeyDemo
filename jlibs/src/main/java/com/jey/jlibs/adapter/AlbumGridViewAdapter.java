package com.jey.jlibs.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.jlibs.R;
import com.jey.jlibs.utils.PhotoSelectUtil.BitmapCache;
import com.jey.jlibs.utils.PhotoSelectUtil.ImageItem;
import com.jey.jlibs.utils.PhotoSelectUtil.Util;

import java.util.ArrayList;

/**
 * 九宫格显示图片所缩略图
 * <p/>
 * author: Kevin.Li
 * created at 2016/3/25 13:59
 */
public class AlbumGridViewAdapter extends BaseAdapter {
    final String TAG = getClass().getSimpleName();
    private Context mContext;

    private ArrayList<ImageItem> dataList;

    private ArrayList<ImageItem> selectedDataList;
    BitmapCache cache;

    public AlbumGridViewAdapter(Context c, ArrayList<ImageItem> dataList, ArrayList<ImageItem> selectedDataList) {
        mContext = c;
        cache = new BitmapCache();
        this.dataList = dataList;
        this.selectedDataList = selectedDataList;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    public int getCount() {
        return dataList.size();
    }

    public Object getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
        @Override
        public void imageLoad(ImageView imageView, Bitmap bitmap,
                              Object... params) {
            if (imageView != null && bitmap != null) {
                String url = (String) params[0];
                if (url != null && url.equals(imageView.getTag())) {
                    (imageView).setImageBitmap(bitmap);
                } else {
                    Log.e(TAG, "callback, bmp not match");
                }
            } else {
                Log.e(TAG, "callback, bmp null");
            }
        }
    };

    /**
     * 存放列表项控件句柄
     */
    private class ViewHolder {
        public ImageView imageView;
        public View coverView;
        public CheckBox chooseToggle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.plugin_camera_select_imageview, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            viewHolder.coverView = convertView.findViewById(R.id.cover_view);
            viewHolder.chooseToggle = (CheckBox) convertView.findViewById(R.id.choosedbt);
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,dipToPx(65));
//			lp.setMargins(50, 0, 50,0);
//			viewHolder.imageView.setLayoutParams(lp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String path;
        if (dataList != null && dataList.size() > position)
            path = dataList.get(position).imagePath;
        else
            path = "camera_default";
        viewHolder.imageView.setImageResource(R.mipmap.plugin_camera_no_pictures);
        if (path.contains("camera_default")) {
            viewHolder.imageView.setImageResource(R.mipmap.plugin_camera_no_pictures);
        } else {
            ImageItem item = dataList.get(position);
            viewHolder.imageView.setTag(R.id.image_tag, item.thumbnailPath);
            Glide.with(mContext)
                    .load(item.imagePath)
                    .crossFade()
                    .centerCrop()
                    .placeholder(R.mipmap.image_default)
                    .into(viewHolder.imageView);
        }
        viewHolder.coverView.setTag(position);
        viewHolder.coverView.setOnClickListener(new CoverViewClickListener());
        viewHolder.chooseToggle.setTag(position);
        viewHolder.chooseToggle.setOnClickListener(new CheckBoxClickListener(viewHolder.coverView));
        if (selectedDataList.contains(dataList.get(position))) {
            viewHolder.coverView.setBackgroundColor(Util.getColorById(mContext, R.color.transparent_background_70));
            viewHolder.chooseToggle.setChecked(true);
        } else {
            viewHolder.coverView.setBackgroundColor(Util.getColorById(mContext, R.color.none_color));
            viewHolder.chooseToggle.setChecked(false);
        }
        return convertView;
    }

    private class CoverViewClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            int position = (Integer) view.getTag();
            if (dataList != null && onItemClickListener != null && position < dataList.size()) {
                onItemClickListener.itemClickListener(position);
            }
        }
    }

    private class CheckBoxClickListener implements OnClickListener {
        View coverView;

        public CheckBoxClickListener(View coverView) {
            this.coverView = coverView;
        }

        @Override
        public void onClick(View view) {
            if (view instanceof CheckBox) {
                CheckBox chooseBt = (CheckBox) view;
                int position = (Integer) chooseBt.getTag();
                if (dataList != null && checkBoxChangeClickListener != null && position < dataList.size()) {
                    checkBoxChangeClickListener.onChange(chooseBt, coverView, position, chooseBt.isChecked());
                }
            }
        }
    }

    private OnCheckBoxChangeClickListener checkBoxChangeClickListener;

    public void setCheckBoxChangeClickListener(OnCheckBoxChangeClickListener checkBoxChangeClickListener) {
        this.checkBoxChangeClickListener = checkBoxChangeClickListener;
    }

    public interface OnCheckBoxChangeClickListener {
        /**
         * @param checkBox  事件监听的视图控件
         * @param coverView 覆盖视图
         * @param position  操作位置
         * @param isChecked 是否选中
         *                  <p/>
         *                  author: Kevin.Li
         *                  created at 2016/3/31 17:37
         */
        void onChange(CheckBox checkBox, View coverView, int position, boolean isChecked);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * @param position 操作位置
         *                 <p/>
         *                 author: Kevin.Li
         *                 created at 2016/3/31 17:37
         */
        void itemClickListener(int position);
    }
}
