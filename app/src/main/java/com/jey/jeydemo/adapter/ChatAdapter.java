package com.jey.jeydemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jey.jeydemo.R;
import com.jey.jeydemo.entity.ChatModel;
import com.jey.jeydemo.entity.ItemModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by WangChang on 2016/4/28.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.BaseAdapter> {

    private ArrayList<ItemModel> dataList = new ArrayList<>();

    /**
     * 替换adapter数据
     * @param list
     */
    public void replaceAll(ArrayList<ItemModel> list) {
//        dataList.clear();
//        if (list != null && list.size() > 0) {
//            dataList.addAll(list);
//        }
//        notifyDataSetChanged();
        if (dataList != null && list != null) {
            dataList.addAll(list);
            notifyItemRangeChanged(dataList.size(),list.size());
        }
    }

    /**
     * 设置adapter数据
     * @param list
     */
    public void addAll(ArrayList<ItemModel> list) {
        if (dataList != null && list != null) {
            dataList.addAll(list);
//            notifyItemRangeChanged(dataList.size(),list.size());
            notifyDataSetChanged();
        }
    }

    @Override
    public BaseAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemModel.CHAT_A:
                return new ChatAViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_detail_from, parent, false));
            case ItemModel.CHAT_B:
                return new ChatBViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_detail_to, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseAdapter holder, int position) {
        holder.setData(dataList.get(position).object);
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public class BaseAdapter extends RecyclerView.ViewHolder {

        public BaseAdapter(View itemView) {
            super(itemView);
        }

        void setData(Object object) {

        }
    }

    private class ChatAViewHolder extends BaseAdapter {
//        private RoundImageView ic_user;
        private TextView tv;

        public ChatAViewHolder(View view) {
            super(view);
//            ic_user = (RoundImageView) itemView.findViewById(R.id.iv_userhead);
            tv = (TextView) itemView.findViewById(R.id.tv_chatcontent);
        }

        @Override
        void setData(Object object) {
            super.setData(object);
            ChatModel model = (ChatModel) object;
//            Picasso.with(itemView.getContext())
//                    .load(model.getIcon())
//                    .placeholder(R.mipmap.ic_launcher)
//                    .into(ic_user);
            tv.setText(model.getContent());
        }
    }

    private class ChatBViewHolder extends BaseAdapter {
//        private RoundImageView ic_user;
        private TextView tv;

        public ChatBViewHolder(View view) {
            super(view);
//            ic_user = (RoundImageView) itemView.findViewById(R.id.iv_userhead);
            tv = (TextView) itemView.findViewById(R.id.tv_chatcontent);

        }

        @Override
        void setData(Object object) {
            super.setData(object);
            ChatModel model = (ChatModel) object;
//            Picasso.with(itemView.getContext()).load(model.getIcon()).placeholder(R.mipmap.ic_launcher).into(ic_user);
            tv.setText(model.getContent());
        }
    }
}
