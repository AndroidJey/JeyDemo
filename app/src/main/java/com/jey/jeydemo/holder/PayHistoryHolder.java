package com.jey.jeydemo.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jey.jlibs.dataModel.BaseDataModel;
import com.jey.jlibs.view.XRecyclerView.HolderInterface;
import com.jey.jeydemo.R;

import java.util.Hashtable;

/**
 * 费用历史Holder
 * <p>
 * <p>
 * create by         jey
 * edit last time by jey
 */
public class PayHistoryHolder implements HolderInterface {
    private Context context;

    public PayHistoryHolder(Context context) {
        this.context = context;
    }

    @Override
    public String setPhotoKey() {
        return null;
    }

    @Override
    public Hashtable<String, Integer> setViewFieldsMap() {
        Hashtable<String, Integer> fields = new Hashtable<String, Integer>();
        fields.put("date", R.id.tvPayDate);
        fields.put("time", R.id.tvPayTime);
//        fields.put("Name", R.id.tvPayShuoMing);
        fields.put("TotalPrice", R.id.tvPayShuliang);
        return fields;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindView(RecyclerView.Adapter adapter, Hashtable<String, View> fields, Object o, int position) {
        if (o == null) return;
        final BaseDataModel data = (BaseDataModel) o;
        if (data.getNameValues().size() == 0) return;
        TextView tvDate = (TextView) fields.get("date");
        TextView tvTime = (TextView) fields.get("time");
        TextView tvPrice = (TextView) fields.get("TotalPrice");
        String postDate = data.getValue("PostDate").toString();
        String date = postDate.split(" ")[0];
        String time = postDate.split(" ")[1];
        tvDate.setText(date);
        tvTime.setText(time);
        tvPrice.setText("" + data.getValue("TotalPrice") + "元");
    }
}
