package com.jey.jlibs.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jlibs.R;
import com.jey.jlibs.utils.PhotoSelectUtil.ImageBucket;

import java.text.MessageFormat;
import java.util.List;

/**
 * Description :
 * Created by wyd on 2016/8/3.
 */
public class FolderAdapter extends BaseAdapter {
    private Context context;
    private List<ImageBucket> datas;

    public FolderAdapter(Context mContect, List<ImageBucket> lists) {
        context = mContect;
        datas = lists;
    }

    @Override
    public int getCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas == null ? null : datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_album, null);
            viewHolder = new ViewHolder();
            viewHolder.imvFolder = (ImageView) convertView.findViewById(R.id.imv_folder);
            viewHolder.tvFolderName = (TextView) convertView.findViewById(R.id.tv_folder_name);
            viewHolder.tvPicCount = (TextView) convertView.findViewById(R.id.tv_pic_count);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imvFolder.setImageBitmap(datas.get(position).imageList.get(0).getBitmap());
        viewHolder.tvFolderName.setText(datas.get(position).bucketName);
        viewHolder.tvPicCount.setText(MessageFormat.format("({0})", String.valueOf(datas.get(position).count)));
        return convertView;
    }

    class ViewHolder {
        ImageView imvFolder;
        TextView tvFolderName;
        TextView tvPicCount;
    }
}
