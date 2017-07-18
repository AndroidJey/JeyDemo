package com.jey.jeydemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jey.jeydemo.R;
import com.jey.jeydemo.entity.UnReadMsgModel;

import java.util.List;

/**
 * Created by jey on 2017/4/19.
 */

public class ChatHistoryAdapter extends BaseAdapter {
    private List<UnReadMsgModel> datamodel;
    private Context context;

    public ChatHistoryAdapter(Context context, List<UnReadMsgModel> datamodel){
        this.context = context;
        this.datamodel = datamodel;
    }

    @Override
    public int getCount() {
        return datamodel.size();
    }

    @Override
    public Object getItem(int i) {
        return datamodel.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_history_view, null);
            viewHolder = new ViewHolder();
            viewHolder.userName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.message = (TextView) convertView.findViewById(R.id.tvMessage);
            viewHolder.userName.setTag(i);
            convertView.setTag(viewHolder);
        }
        viewHolder.userName.setText(datamodel.get(i).getUserName());
        viewHolder.message.setText(datamodel.get(i).getMessage());
        return convertView;
    }

    public class ViewHolder {
        TextView userName;
        TextView message;
    }
}
