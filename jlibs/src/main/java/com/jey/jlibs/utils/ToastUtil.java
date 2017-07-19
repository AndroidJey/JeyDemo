package com.jey.jlibs.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jlibs.R;

/**
 * Created by sevnce on 2016/8/20.
 */
public class ToastUtil {

    public static ToastUtil toastUtil;

    private static String msgs = "";

    private static long lastShowTime = 0;

    private Toast toast;

    public static void show(Context context, String msg) {
        if (StringUtils.isBlank(msg)) return;
        long t = System.currentTimeMillis();
        if (lastShowTime != 0) {
            if (Math.abs(t - lastShowTime) < 2000 && msg.equals(msgs)) {
                return;
            }
        }
        getInstance(context, msg).createToast(context, msg);
    }

    private static ToastUtil getInstance(Context context, String msg) {
        if (toastUtil == null) {
            synchronized (ToastUtil.class) {
                if (toastUtil == null) {
                    toastUtil = new ToastUtil(context, msg);
                }
            }
        }
        return toastUtil;
    }

    protected ToastUtil(Context context, String msg) {
        createToast(context, msg);
    }

    @SuppressLint("ShowToast")
    private void createToast(Context context, String msg) {
        long t = System.currentTimeMillis();
        if (lastShowTime != 0) {
            if (Math.abs(t - lastShowTime) < 2000 && msg.equals(msgs)) {
                return;
            } else {
                toast.cancel();
            }
        }
        lastShowTime = System.currentTimeMillis();
        msgs = msg;
        View layout = LayoutInflater.from(context).inflate(R.layout.my_toast_layout, null);
        TextView text = (TextView) layout.findViewById(R.id.my_toast_title);
        text.setText(msg);
        text.setTextColor(context.getResources().getColor(R.color.normal_title_text_color));
        toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, CommonFunction.dp2px(context, 80));
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
