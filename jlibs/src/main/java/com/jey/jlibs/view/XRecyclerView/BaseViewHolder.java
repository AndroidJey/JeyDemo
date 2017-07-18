package com.jey.jlibs.view.XRecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jey.jlibs.dataModel.BaseDataModel;
import com.jey.jlibs.interface_.OnItemClickListener;
import com.jey.jlibs.interface_.OnItemLongClickListener;

import java.util.Hashtable;

/**
 * Created by sevnce on 2016/10/23.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    protected Hashtable<String, Integer> viewMap;
    protected Hashtable<String, View> filedsMap;
    protected Object HolderClass;
    protected RecyclerView.Adapter adapter;

    public static BaseViewHolder createViewHolder(Context c, RecyclerView.Adapter adapter,
                                                  ViewGroup parent, int layoutId, Object HolderClass) {
        View v = LayoutInflater.from(c).inflate(layoutId, parent, false);
        BaseViewHolder b = new BaseViewHolder(v, HolderClass, adapter);
        return b;
    }

    public BaseViewHolder(View itemView, Object HolderClass, RecyclerView.Adapter adapter) {
        super(itemView);
        this.HolderClass = HolderClass;
        this.adapter = adapter;
        bindViewMap(itemView);
    }

    public Hashtable<String, Integer> setViewFieldsMap() {
        if (HolderClass instanceof HolderInterface)
            ((HolderInterface) HolderClass).setViewFieldsMap();
        return null;
    }

    private void bindViewMap(View v) {
        filedsMap = new Hashtable<>();
        if (HolderClass instanceof HolderInterface) {
            viewMap = ((HolderInterface) HolderClass).setViewFieldsMap();
        } else viewMap = null;
        if (viewMap != null)
            for (String key : viewMap.keySet()) {
                View view = v.findViewById(viewMap.get(key));
                if (!filedsMap.contains(view)) {
                    filedsMap.put(key, view);
                }
            }
    }

    @SuppressLint("SetTextI18n")
    public void onBindView(final BaseViewHolder holder, final Object o, final int position, final OnItemClickListener itemClickListener,
                           final OnItemLongClickListener itemLongClickListener) {
        if (itemClickListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClick(v, o, position);
                }
            });
        }
        if (itemLongClickListener != null) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemLongClickListener.OnItemLongClick(v, o, position);
                    return true;
                }
            });
        }
        BaseDataModel model = (BaseDataModel) o;
        for (String key : holder.filedsMap.keySet()) {
            Object value = model.getValue(key);
            if (value != null && (value + "").length() > 0) {
                View view = holder.filedsMap.get(key);
                if (view instanceof TextView) {
                    ((TextView) view).setText(value + "");
                }
            }
        }
        if (HolderClass instanceof HolderInterface) {
            ((HolderInterface) HolderClass).onBindView(adapter, holder.filedsMap, o, position);
        }
    }

}
