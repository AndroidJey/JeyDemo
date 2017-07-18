package com.jey.jlibs.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.jlibs.R;


/**
 * 对app的所有对话框进行管理
 *
 * @author blue
 */
@SuppressLint("InflateParams")
public class DialogMaker {

    public interface DialogCallBack {
        /**
         * 对话框按钮点击回调
         *
         * @param position 点击Button的索引.
         * @param tag
         */
        public void onButtonClicked(Dialog dialog, int position, Object tag);

        /**
         * 当对话框消失的时候回调
         *
         * @param tag
         */
        public void onCancelDialog(Dialog dialog, Object tag);
    }

    public static final String BIRTHDAY_FORMAT = "%04d-%02d-%02d";


    public static Dialog showCommenWaitDialog(Context context, String msg, final DialogCallBack callBack, boolean isCanCancelabel, final Object tag) {
        final Dialog dialog = new Dialog(context, R.style.DialogNoTitleRoundCornerStyle);
        dialog.setOwnerActivity((Activity) context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_wait_common_layout, null);
        TextView contentTv = (TextView) contentView.findViewById(R.id.dialog_content_tv);
        if (StringUtils.isNotBlank(msg)) {
            contentTv.setText(msg);
        }
        dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                if (null != callBack) {
                    callBack.onCancelDialog((Dialog) dialog, tag);
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.show();
        return dialog;

    }
}
