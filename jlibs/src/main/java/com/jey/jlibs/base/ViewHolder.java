package com.jey.jlibs.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jey.jlibs.dataModel.BaseDataModel;
import com.jey.jlibs.interface_.IBindField;
import com.jey.jlibs.view.BadgeView;

import java.util.Hashtable;

public class ViewHolder implements DataBindCenter.Requester, IBindField {
    protected View convertView;
    protected BaseDataModel item;
    Class _dataClass;
    public Hashtable<String, View> fields = new Hashtable<String, View>();

    // TODO 我
    public Hashtable<String, View> getFields() {
        return fields;
    }

    private ViewHolder() {
    }

    protected ViewHolder(final Context context, int layoutId, Class dataClass) {
        _dataClass = dataClass;
        convertView = LayoutInflater.from(context).inflate(layoutId, null);
        convertView.setTag(this);
        Hashtable<String, Integer> fieldMap = getFieldMap();//得到服务器取到的数据对应的键值 和  值是  本地对应的控件对象  的绑定
        for (String key : fieldMap.keySet()) {
            View v = convertView.findViewById(fieldMap.get(key));
            if (v != null) fields.put(key, v);
        }

    }

    protected ViewHolder(final Context context, View view, Class dataClass) {
        _dataClass = dataClass;
        convertView = view;
        convertView.setTag(this);
        Hashtable<String, Integer> fieldMap = getFieldMap();//得到服务器取到的数据对应的键值 和  值是  本地对应的控件对象  的绑定
        for (String key : fieldMap.keySet()) {
            fields.put(key, convertView.findViewById(fieldMap.get(key)));
        }
    }

    public ViewHolder(final Context context, View view, Class dataClass, Hashtable<String, Object> fieldMap) {
        _dataClass = dataClass;
        convertView = view;
        convertView.setTag(this);
        for (String key : fieldMap.keySet()) {
            Object v = fieldMap.get(key);
            if (v instanceof View) fields.put(key, (View) v);
            else fields.put(key, convertView.findViewById((int) v));
        }
    }

    public Hashtable<String, Integer> getFieldMap() {
        return null;
    }

    public Class getDataClass() {
        return _dataClass;
    }

    public View getView() {
        return convertView;
    }

    public void setData(final BaseDataModel item,int position) {
        if (this.item != null) DataBindCenter.unBind(this.item, this);
        this.item = item;
        if (this.item != null) DataBindCenter.bind(this.item, this,position);
    }


    /**
     * 进行数据的绑定
     */

    public void handleData(BaseDataModel data,int position) throws Exception {
        for (String key : fields.keySet()) {
            Object value = data.getValue(key);
            if (value == null) continue;
            View field = fields.get(key);

            if (field instanceof TextView) {
                if (field instanceof BadgeView) {
                    if (Integer.parseInt(value.toString()) > 0) {
                        ((BadgeView) field).setText(value.toString());
                        ((BadgeView) field).show();
                    } else {
                        ((BadgeView) field).hide();
                    }

                    continue;

                }
                ((TextView) field).setText(value.toString());
                continue;
            }

//            if (field instanceof AsyLoadImage) {
//                ((AsyLoadImage) field).setImageURI(value.toString());
//                continue;
//            }
        }
    }
}
