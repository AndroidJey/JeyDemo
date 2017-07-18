package com.jey.jeydemo.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jey.jlibs.activity.AlbumActivity;
import com.jey.jlibs.interface_.ActionCompeleteListener;
import com.jey.jeydemo.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 获取照片的底部弹窗
 */
@SuppressLint("InlinedApi")
public class PhotoSelectPopupWindow implements PopupWindow.OnDismissListener, OnClickListener {
    private PopupWindow pop;
    private RelativeLayout containerRL;
    private Fragment fragment;
    private OnClickDeleteListener onClickDeleteListener;
    private boolean isShowDelete = false;
    /**
     * 从相册获取照片的请求码
     */
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x08;
    /**
     * 拍照
     */
    public static final int REQUEST_CODE_CAMERA = 0x09;
    /**
     * 外部返回码
     */
    private int result_album_code = 0;
    private int result_camera_code = 0;
    /**
     * 设置的拍照临时保存的路径
     */
    private String path;
    /**
     * 点击后是否自动消失PopupWindow
     */
    private boolean autoDismiss = true;


    public PhotoSelectPopupWindow(Fragment fragment, String path, boolean isShowDelete) {
        this.fragment = fragment;
        this.path = path;
        this.isShowDelete = isShowDelete;
        initPop();
    }

    @SuppressLint("InlinedApi")
    private void initPop() {
        pop = new PopupWindow(fragment.getContext());

        View view = LayoutInflater.from(fragment.getContext()).inflate(
                R.layout.item_popupwindows, null);

        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        containerRL = (RelativeLayout) view.findViewById(R.id.photo_select_container_rl);

        TextView btnCamera = (TextView) view.findViewById(R.id.item_popup_window_camera);
        TextView btnAlbum = (TextView) view.findViewById(R.id.item_popup_window_Photo);
        TextView btnDelete = (TextView) view.findViewById(R.id.item_popup_window_Delete);
        TextView btnCancel = (TextView) view.findViewById(R.id.item_popup_window_cancel);
        LinearLayout ll_delete = (LinearLayout) view.findViewById(R.id.ll_item_popup_window_delete);
        if (isShowDelete) {
            ll_delete.setVisibility(View.VISIBLE);
        } else {
            ll_delete.setVisibility(View.GONE);
        }
        containerRL.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        pop.setOnDismissListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_popup_window_camera:
                getCameraPermission(fragment.getActivity(), Manifest.permission.CAMERA, new ActionCompeleteListener() {
                    @Override
                    public void onResult(boolean isSuccessed, Object obj) {
                        if (isSuccessed) {
                            dismissPop();
                            photo();
                        }
                    }
                });
                break;
            case R.id.item_popup_window_Photo:
                @SuppressLint("InlinedApi") String permissionName = Manifest.permission.READ_EXTERNAL_STORAGE;
                getCameraPermission(fragment.getActivity(), permissionName, new ActionCompeleteListener() {
                    @Override
                    public void onResult(boolean isSuccessed, Object obj) {
                        if (isSuccessed) {
                            dismissPop();
                            toAlbum();
                        }
                    }
                });
                break;
            case R.id.item_popup_window_Delete:
                if (onClickDeleteListener != null) {
                    onClickDeleteListener.OnClick(PhotoSelectPopupWindow.this);
                }
                dismissPop();
                break;
            case R.id.photo_select_container_rl:
                dissmiss();
                break;
            case R.id.item_popup_window_cancel:
                dissmiss();
                break;
        }
    }

    private void dismissPop() {
        if (autoDismiss) {
            dissmiss();
        }
    }

    @Override
    public void onDismiss() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 250);
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        if (fragment == null) {
            return;
        }
        Activity a = fragment.getActivity();
        WindowManager.LayoutParams lp = a.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        a.getWindow().setAttributes(lp);
    }

    /**
     * 显示PopupWindow
     */
    public void show(View view) {
        backgroundAlpha(0.5f);
        pop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 消失PopupWindow
     */
    public void dissmiss() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
        }
    }

    /**
     * 设置是否点击后是否自动消失PopupWindow属性
     */
    public void setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
    }

    /**
     * 设置拍照返回码
     */
    public void setResult_camera_code(int result_camera_code) {
        this.result_camera_code = result_camera_code;
    }

    /**
     * 设置相册返回码
     */
    public void setResult_album_code(int result_album_code) {
        this.result_album_code = result_album_code;
    }

    /**
     * 获取拍照权限
     */
    private void getCameraPermission(Context context, String permissionName, ActionCompeleteListener listener) {
//        new CommonFunctionUtils(context).getPermissionByName(permissionName, "确定", true, listener);
    }

    /**
     * 相册
     */
    private void toAlbum() {
        if (result_album_code == 0) {
            result_album_code = REQUEST_CODE_TAKE_PICTURE;
        }
        Intent intent = new Intent(fragment.getActivity(), AlbumActivity.class);
        fragment.startActivityForResult(intent, result_album_code);
        fragment.getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    /**
     * 拍照
     */
    private void photo() {
        if (result_camera_code == 0) {
            result_camera_code = REQUEST_CODE_CAMERA;
        }
        getCameraPermission(fragment.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, new ActionCompeleteListener() {
            @Override
            public void onResult(boolean isSuccessed, Object obj) {
                if (isSuccessed) {
//                    XFileUtil.init();
//                    XFileUtil.startActionCaptureOfFragment(fragment, new File(path), result_camera_code);
                }
            }
        });
    }

    public void setOnClickDeleteListener(OnClickDeleteListener onClickDeleteListener) {
        this.onClickDeleteListener = onClickDeleteListener;
    }

    public interface OnClickDeleteListener {
        void OnClick(PhotoSelectPopupWindow pop);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    backgroundAlpha(1.0f);
                    break;
            }
        }
    };
}
