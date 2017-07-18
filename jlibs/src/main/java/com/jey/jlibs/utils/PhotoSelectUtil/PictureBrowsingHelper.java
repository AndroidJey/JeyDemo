package com.jey.jlibs.utils.PhotoSelectUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jey.jlibs.dataModel.PictureThumbEntity;
import com.example.jlibs.R;
import com.jey.jlibs.utils.StringUtils;
import com.jey.jlibs.utils.ToastUtil;
import com.jey.jlibs.activity.PictureBrowsingActivity;
import com.jey.jlibs.base.AsyHttp;
import com.jey.jlibs.utils.CommonFunction;
import com.jey.jlibs.interface_.OnProgressBarListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 图片浏览帮助类
 */
public class PictureBrowsingHelper {

    public static final int PICTURE_BROWSING_CODE = 8978;

    private Activity mActivity;

    private PictureThumbEntity entity;

    public PictureBrowsingHelper(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 获取图片的位置坐标以及图片长宽
     *
     * @param view
     * @return 存放获取到的信息bean文件内
     */
    private static PictureThumbEntity getViewCoordinatesBaseData(View view) {
        PictureThumbEntity entity = new PictureThumbEntity();
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        if (l[0] != 0)
            entity.setLeft(l[0]);
        if (l[1] != 0)
            entity.setTop(l[1]);
        int w = CommonFunction.getViewWidth(view);
        if (w != 0)
            entity.setViewWidth(w);
        int h = CommonFunction.getViewHeight(view);
        if (h != 0)
            entity.setViewHeight(h);
        return entity;
    }

    public static PictureThumbEntity createPictureBrowsingData(View view, String url, boolean isClickPostion) {
        if (view == null) return null;
        final PictureThumbEntity entity = getViewCoordinatesBaseData(view);
        if (!StringUtils.isEmpty(url))
            entity.setUrl(url);
        entity.setClickPosition(isClickPostion);
        return entity;
    }

    public static void toStartActivity(Activity activity, Class cls, List<PictureThumbEntity> datas,
                                       int clickPosition, boolean allowSavePicture) {
        Intent intent = new Intent(activity, cls);
        Bundle b = new Bundle();
        b.putSerializable("data", (Serializable) datas);
        b.putInt("position", clickPosition);
        b.putBoolean("AllowSavePicture", allowSavePicture);
        intent.putExtras(b);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public static void toStartActivityForResult(Activity activity, Class cls, List<PictureThumbEntity> datas,
                                                int clickPosition, boolean allowSavePicture,
                                                boolean allowReplacePicture) {
        Intent intent = new Intent(activity, cls);
        Bundle b = new Bundle();
        b.putSerializable("data", (Serializable) datas);
        b.putInt("position", clickPosition);
        b.putBoolean("AllowSavePicture", allowSavePicture);
        b.putBoolean("AllowReplacePicture", allowReplacePicture);
        intent.putExtras(b);
        activity.startActivityForResult(intent, PICTURE_BROWSING_CODE);
        activity.overridePendingTransition(0, 0);
    }

    public static void toStartActivityForResultFromFragment(Fragment fragment, Class cls,
                                                            List<PictureThumbEntity> datas,
                                                            int clickPosition, int holderPosition,
                                                            boolean allowSavePicture,
                                                            boolean allowReplacePicture) {
        Intent intent = new Intent(fragment.getActivity(), cls);
        Bundle b = new Bundle();
        b.putSerializable("data", (Serializable) datas);
        b.putInt("position", clickPosition);
        b.putInt("holderPosition", holderPosition);
        b.putBoolean("AllowSavePicture", allowSavePicture);
        b.putBoolean("AllowReplacePicture", allowReplacePicture);
        intent.putExtras(b);
        fragment.startActivityForResult(intent, PICTURE_BROWSING_CODE);
        fragment.getActivity().overridePendingTransition(0, 0);
    }

    public static void disPlayImageWithUrlFitCenterAndNoAnimation(final Context context, final ImageView imageView,
                                                                  String url, final OnProgressBarListener listener) {
        if (StringUtils.isEmpty(url)) return;
        if (url.startsWith("/") && !url.startsWith("/storage")) {
            url = AsyHttp.Host + url;
        }
        listener.onStart();
        imageView.setTag(R.id.image_tag, url);
        Glide.with(context)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                               boolean isFirstResource) {
                        if (!StringUtils.isEmpty(e.getMessage()) && !e.getMessage().startsWith("Failed to load model")) {
                            listener.onFailed(e);
                            ToastUtil.show(context, "图片加载失败，请稍后重试！");
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        listener.onCompleted();
                        return false;
                    }
                })
                .dontAnimate()
                .dontTransform()
                .fitCenter()
                .placeholder(R.mipmap.image_default)
                .error(R.color.image_view_default_bg_color)
                .into(imageView);

    }

    /**
     * 生成List<PictureThumbEntity>去控制缩放动画
     *
     * @param manager    RecyclerView.LayoutManager
     * @param parentView ViewHolder的布局根布局
     * @param clickView  点击的View
     * @param position   点击的position
     * @param mDatas     图片urlList
     * @return List<PictureThumbEntity>
     */
    public static List<PictureThumbEntity> getChildImageView(RecyclerView.LayoutManager manager, ViewGroup parentView,
                                                             View clickView, final int position, List<String> mDatas) {

        if (manager == null || parentView == null || clickView == null || position == -1 || mDatas == null)
            return null;

        /** 新生成的list集合 **/
        List<PictureThumbEntity> list = new LinkedList<>();
        /** 最后一项下标 **/
        int finalIndex = 0;
        /** 最后一项的ImageView **/
        ImageView lastImageView = null;
        /** 可见项第一项的ImageView **/
        ImageView firstImageView = null;
        /** manager中的可见项个数 **/
        int count = manager.getChildCount();
        /** 点击项在可见项中的的下标 **/
        int currentIndex = 0;

        /** 先确定点击项的下标位置 **/
        for (int k = 1; k < count; k++) {
            ViewGroup group = (ViewGroup) manager.getChildAt(k);
            if (group == parentView) {
                currentIndex = k;
                break;
            }
        }

        /** 点击项的position和可见项中的点击下标差值代表可见项前面有多少不可见项 **/
        int chaBeforeCount = position - currentIndex;
        /** 可见项后面额不可见项的差值 **/
        int chaAfterCount = mDatas.size() - count;

        addLastPictureThumbEntity(list, mDatas, 0, chaBeforeCount, clickView);

        int realAdapterPosition = 0;
        for (int k = 0; k < count; k++) {
            ViewGroup group = (ViewGroup) manager.getChildAt(k);
            if (group != null) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    View childView = group.getChildAt(i);
                    if (childView != null) {
                        if (childView instanceof ImageView) {
                            if (k == count - 1) {
                                lastImageView = (ImageView) childView;
                                finalIndex = k + 1;
                            }
                            realAdapterPosition = k + chaBeforeCount;
                            PictureThumbEntity e;
                            if (childView == clickView) {
                                e = PictureBrowsingHelper.createPictureBrowsingData(
                                        childView, mDatas.get(realAdapterPosition), true);
                            } else {
                                e = PictureBrowsingHelper.createPictureBrowsingData(
                                        childView, mDatas.get(realAdapterPosition), false);
                            }
                            list.add(e);
                        }
                    }
                }
            }
        }

        addLastPictureThumbEntity(list, mDatas, count + chaBeforeCount, mDatas.size(), lastImageView);

        return list;
    }

    /**
     * 将layoutManager中的不可见项加入到List中
     *
     * @param list          List<PictureThumbEntity>
     * @param urlList       图片url地址List
     * @param startPosition 开始Position
     * @param endPosition   结束的Position
     * @param clickView     点击的View
     */
    private static void addLastPictureThumbEntity(
            List<PictureThumbEntity> list,
            List<String> urlList,
            final int startPosition,
            final int endPosition,
            View clickView) {

        for (int i = startPosition; i < endPosition; i++) {
            PictureThumbEntity e = PictureBrowsingHelper.createPictureBrowsingData(
                    clickView, urlList.get(i), false);
            list.add(e);
        }
    }

    /**
     * 显示提示对话框
     *
     * @param context         上下文
     * @param title           title
     * @param msg             提示消息
     * @param positiveStr     右边按钮文字
     * @param negativeStr     左边按钮文字
     * @param cancleEnable    是否可以取消
     * @param onClickListener 按钮监听
     * @return AlertDialog
     */
    public static AlertDialog showNormalAlertDialog(Context context,
                                                                           String title,
                                                                           String msg,
                                                                           String positiveStr,
                                                                           String negativeStr,
                                                                           boolean cancleEnable,
                                                                           DialogInterface.OnClickListener onClickListener) {

        if (context == null) return null;
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        if (!StringUtils.isEmpty(title))
            b.setTitle(title);
        if (!StringUtils.isEmpty(msg))
            b.setMessage(msg);
        if (!StringUtils.isEmpty(positiveStr))
            b.setPositiveButton(positiveStr, onClickListener);
        if (!StringUtils.isEmpty(negativeStr))
            b.setNegativeButton(negativeStr, onClickListener);

        b.setCancelable(cancleEnable);
        AlertDialog dialog = b.create();
        dialog.show();
        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (context != null) {
            btnNegative.setTextColor(ContextCompat.getColor(context, R.color.gray_text));
            btnPositive.setTextColor(ContextCompat.getColor(context, R.color.lib_blue_login_press));
        }
        return dialog;
    }

    /**
     * 下载图片，并保存到相册
     *
     * @param context  上下文
     * @param url      图片地址
     * @param savePath 图片保存路径
     */
    public static void savePictureToAlbum(final Context context, String url, final String savePath) {
        new AsyHttp(context, new AsyHttp.IGetCallback() {
            @Override
            public void onAsyHttpSuccess(File file) {
                if (context instanceof PictureBrowsingActivity)
                    ((PictureBrowsingActivity) context).dismissLoadingDialog();
                try {
                    MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            file.getAbsolutePath(), file.getName() + "", null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                /** 最后通知图库更新 **/
                ToastUtil.show(context, "已成功保存到相册");
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));
            }

            @Override
            public void onAsyHttpProgress(int percent) {

            }
        }).execute(url);
    }


    public static boolean checkBuildVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean checkPermissionStatus(String[] permissions, AppCompatActivity appCompatActivity) {
        if (permissions == null || permissions.length == 0)
            throw new RuntimeException("permission String[] not be null");
        if (appCompatActivity == null)
            throw new RuntimeException("appCompatActivity not be null");
        for (int i = 0; i < permissions.length; i++) {
            int staus = ContextCompat.checkSelfPermission(appCompatActivity, permissions[i]);
            if (staus == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, Manifest.permission.READ_CONTACTS);
                if (shouldShow) {
                    return showShouldReminderForUserGetPermissionReasonDialog(appCompatActivity, permissions[0]);
                } else
                    return false;
            }
        }
        return false;
    }

    private static boolean showShouldReminderForUserGetPermissionReasonDialog(Context context, String permission) {
        final boolean[] isAllowed = {false};
        String permissionDetail = "只有当你同意该权限时,才能使用该功能哦!";
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                permissionDetail = "只有当你同意该权限时,才能使用该功能哦!";
                break;
            case Manifest.permission.CALL_PHONE:
                // TODO
                break;
        }
        showNormalAlertDialog(context, "温馨提示", permissionDetail, "重新获取", "取消", true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    isAllowed[0] = true;
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    isAllowed[0] = false;
                }
            }
        });
        return isAllowed[0];
    }

    public interface OnPermissionAllowedOrRefusedListener {
        void onPermissionAllowedOrRefused(boolean[] isAllowed, String[] messageInfo);
    }

}
