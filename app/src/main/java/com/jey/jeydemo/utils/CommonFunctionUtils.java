//package com.jey.jeydemo.utils;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Build;
//import android.support.annotation.IntegerRes;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
//import android.support.v4.os.CancellationSignal;
//import android.support.v4.view.ViewCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.drawable.GlideDrawable;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
//import com.jey.jeydemo.R;
//import com.jey.jeydemo.utils.XUtil.XFileUtil;
//import com.jey.jeydemo.view.PhotoSelectPopupWindow;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * 公用方法工具类
// */
//public class CommonFunctionUtils {
//
//    private Context context;
//    protected Dialog dialog;
//
//
//    public CommonFunctionUtils(Context context) {
//        this.context = context;
//    }
//
//    /**
//     * 等待对话框
//     *
//     * @author blue
//     */
//    public Dialog showWaitDialog(String msg, boolean isCanCancelabel, Object tag) {
//        if (null == dialog || !dialog.isShowing()) {
//            try {
//                dialog = DialogMaker.showCommenWaitDialog(context, msg, null, isCanCancelabel, tag);
//                return dialog;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 关闭对话框
//     *
//     * @author blue
//     */
//    public void dismissDialog() {
//        if (null != dialog && dialog.isShowing()) {
//            dialog.dismiss();
//        }
//    }
//
//    /**
//     * Glide加载图片，图片ScaleType为CenterCrop
//     *
//     * @param imageView 图片View
//     * @param url       图片Url地址
//     */
//    public void disPlayImageWithUrl(ImageView imageView, String url) {
//        if (url.startsWith("/") && !url.startsWith("/storage")) {
//            url = AsyHttp.Host + url;
//        }
//        imageView.setTag(R.id.image_tag, url);
//        if (imageView.getTag(R.id.image_tag) != null && url == imageView.getTag(R.id.image_tag))
//            Glide.with(context)
//                    .load(url)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            if (e != null && !StringUtils.isEmpty(e.getMessage()) &&
//                                    !e.getMessage().startsWith("Failed to load model"))
//                                ToastUtil.show(context, "图片加载失败，请稍后重试！");
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
//                                                       boolean isFromMemoryCache, boolean isFirstResource) {
//                            return false;
//                        }
//                    })
//                    .crossFade()
//                    .centerCrop()
//                    .placeholder(R.mipmap.image_default)
//                    .error(R.mipmap.image_default)
//                    .into(imageView);
//    }
//
//    /**
//     * Glide加载图片
//     *
//     * @param imageView   图片View
//     * @param url         图片Url地址
//     * @param holderResId 默认图片资源Id
//     * @param errorResId  加载错误图片资源Id
//     */
//    public void disPlayImageWithUrl(ImageView imageView, String url, @IntegerRes int holderResId, @IntegerRes int errorResId) {
//        if (url.startsWith("/") && !url.startsWith("/storage")) {
//            url = AsyHttp.Host + url;
//        }
//        imageView.setTag(R.id.image_tag, url);
//        if (imageView.getTag(R.id.image_tag) != null && url == imageView.getTag(R.id.image_tag))
//            Glide.with(context)
//                    .load(url)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            if (!StringUtils.isEmpty(e.getMessage()) && !e.getMessage().startsWith("Failed to load model"))
//                                ToastUtil.show(context, "图片加载失败，请稍后重试！");
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
//                                                       boolean isFromMemoryCache, boolean isFirstResource) {
//                            return false;
//                        }
//                    })
//                    .crossFade()
//                    .fitCenter()
//                    .placeholder(holderResId)
//                    .error(errorResId)
//                    .into(imageView);
//    }
//
//    /**
//     * Glide加载图片，图片ScaleType为CenterCrop，带有加载状态监听
//     *
//     * @param imageView 图片View
//     * @param url       图片Url地址
//     */
//    public void disPlayImageWithUrl(ImageView imageView, String url, final OnProgressBarListener listener) {
//        if (url.startsWith("/") && !url.startsWith("/storage")) {
//            url = AsyHttp.Host + url;
//        }
//        imageView.setTag(R.id.image_tag, url);
//        if (imageView.getTag(R.id.image_tag) != null && url == imageView.getTag(R.id.image_tag))
//            Glide.with(context)
//                    .load(url)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            if (!StringUtils.isEmpty(e.getMessage()) && !e.getMessage().startsWith("Failed to load model"))
//                                ToastUtil.show(context, "图片加载失败，请稍后重试！");
//                            listener.onFailed(e);
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
//                                                       boolean isFromMemoryCache, boolean isFirstResource) {
//                            listener.onCompleted();
//                            return false;
//                        }
//                    })
//                    .crossFade()
//                    .centerCrop()
//                    .placeholder(R.mipmap.image_default)
//                    .error(R.mipmap.image_default)
//                    .into(imageView);
//
//    }
//
//    /**
//     * Glide指定图片尺寸加载图片，图片ScaleType为CenterCrop
//     *
//     * @param imageView 图片View
//     * @param url       图片Url地址
//     * @param width     图片宽度
//     * @param height    图片高度
//     */
//    public void disPlayImageWithUrlWithWidthAndHeight(ImageView imageView, String url, int width, int height) {
//        if (url.startsWith("/") && !url.startsWith("/storage")) {
//            url = AsyHttp.Host + url;
//        }
//        imageView.setTag(R.id.image_tag, url);
//        if (imageView.getTag(R.id.image_tag) != null && url == imageView.getTag(R.id.image_tag))
//            Glide.with(context)
//                    .load(url)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
//                                                   boolean isFirstResource) {
//                            if (e != null && !StringUtils.isEmpty(e.getMessage()) &&
//                                    !e.getMessage().startsWith("Failed to load model"))
//                                ToastUtil.show(context, "图片加载失败，请稍后重试！");
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
//                                                       boolean isFromMemoryCache, boolean isFirstResource) {
//                            return false;
//                        }
//                    })
//                    .override(width, height)
//                    .crossFade()
//                    .centerCrop()
//                    .placeholder(R.mipmap.image_default)
//                    .error(R.mipmap.image_default)
//                    .into(imageView);
//    }
//
//    /**
//     * Glide加载图片，图片ScaleType为FitCenter
//     *
//     * @param imageView 图片View
//     * @param url       图片Url地址
//     */
//    public void disPlayImageWithUrlFitCenter(ImageView imageView, String url) {
//        if (url.startsWith("/") && !url.startsWith("/storage")) {
//            url = AsyHttp.Host + url;
//        }
//        imageView.setTag(R.id.image_tag, url);
//        if (imageView.getTag(R.id.image_tag) != null && url == imageView.getTag(R.id.image_tag))
//            Glide.with(context)
//                    .load(url)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            if (!StringUtils.isEmpty(e.getMessage()) && !e.getMessage().startsWith("Failed to load model"))
//                                ToastUtil.show(context, "图片加载失败，请稍后重试！");
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
//                                                       boolean isFromMemoryCache, boolean isFirstResource) {
//                            return false;
//                        }
//                    })
//                    .crossFade()
//                    .fitCenter()
//                    .placeholder(R.mipmap.image_default)
//                    .error(R.mipmap.image_default)
//                    .into(imageView);
//    }
//
//    /**
//     * Glide加载高斯模糊图片
//     *
//     * @param imageView 图片View
//     * @param url       图片Url地址
//     */
//    public void disPlayImageWithUrlAndBlur(ImageView imageView, String url) {
//        if (url.startsWith("/") && !url.startsWith("/storage")) {
//            url = AsyHttp.Host + url;
//        }
//        imageView.setTag(R.id.image_tag, url);
//        if (imageView.getTag(R.id.image_tag) != null && url == imageView.getTag(R.id.image_tag))
//            Glide.with(context)
//                    .load(url)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            if (!StringUtils.isEmpty(e.getMessage()) && !e.getMessage().startsWith("Failed to load model"))
//                                ToastUtil.show(context, "图片加载失败，请稍后重试！");
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
//                                                       boolean isFromMemoryCache, boolean isFirstResource) {
//                            return false;
//                        }
//                    })
//                    .crossFade()
//                    .fitCenter()
////                    .bitmapTransform(new BlurTransformation(context, 23, 4)) // “23”：设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”。
//                    .placeholder(R.mipmap.image_default)
//                    .error(R.mipmap.image_default)
//                    .into(imageView);
//    }
//
//
//    /**
//     * Glide加载圆形图片，无任何动画
//     *
//     * @param imageView 图片View
//     * @param url       图片Url地址
//     */
//    public void disPlayImageWithUrlNoAnimation(ImageView imageView, String url) {
//        if (url.startsWith("/") && !url.startsWith("/storage")) {
//            url = AsyHttp.Host + url;
//        }
//        imageView.setTag(R.id.image_tag, url);
//        if (imageView.getTag(R.id.image_tag) != null && url == imageView.getTag(R.id.image_tag))
//            Glide.with(context)
//                    .load(url)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            if (!StringUtils.isEmpty(e.getMessage()) && !e.getMessage().startsWith("Failed to load model"))
//                                ToastUtil.show(context, "图片加载失败，请稍后重试！");
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
//                                                       boolean isFromMemoryCache, boolean isFirstResource) {
//                            return false;
//                        }
//                    })
//                    .dontTransform()
//                    .dontAnimate()
//                    .fitCenter()
//                    .placeholder(R.mipmap.image_default)
//                    .error(R.mipmap.image_default)
//                    .into(imageView);
//    }
//
//    /**
//     * 6.0及以上版本获取权限
//     *
//     * @param permissionName   权限名称
//     * @param msg              提示信息
//     * @param isCancelable     是否可以取消
//     * @param onResultListener 授权结果监听
//     */
//    public void getPermissionByName(String permissionName, final String msg, final boolean isCancelable,
//                                    final ActionCompeleteListener onResultListener) {
//        if (StringUtils.isEmpty(permissionName)) {
//            if (onResultListener != null)
//                onResultListener.onResult(false, null);
//        }
//        if (PermissionUtil.checkBuildVersion()) {
//            final String[] arrPermissions = new String[]{permissionName};
//
//            if (!(context instanceof AppCompatActivity))
//                throw new RuntimeException("The Context must be AppCompatActivity");
//
//            PermissionUtil.checkPermissionStatus(arrPermissions, (AppCompatActivity) context,
//                    new PermissionUtil.OnPermissionAllowedOrRefusedListener() {
//                        @Override
//                        public void onPermissionAllowedOrRefused(boolean[] isAllowed, String[] messageInfo) {
//                            if (isAllowed[0]) {
//                                if (onResultListener != null) onResultListener.onResult(true, null);
//                            } else {
//                                if (messageInfo == null || (messageInfo != null && messageInfo[0].equals("重新获取"))) {
//                                    int whereTag = 0;
//                                    int requestCode = 0;
//                                    if (context instanceof BaseActivity) {
//                                        whereTag = PermissionUtil.FROM_BASE_ACTIVITY;
//                                        requestCode = BaseActivity.REQUEST_PERMISSION_FOR_BASE_ACTIVITY;
//                                    } else if (context instanceof PopActivity) {
//                                        whereTag = PermissionUtil.FROM_POP_ACTIVITY;
//                                        requestCode = PopActivity.REQUEST_PERMISSION_FOR_POP_ACTIVITY;
//                                    }
//                                    if (whereTag == 0 || requestCode == 0)
//                                        return;
//                                    PermissionUtil.getPermission(whereTag, arrPermissions, requestCode, (AppCompatActivity) context,
//                                            new PermissionUtil.OnPermissionAllowedOrRefusedListener() {
//
//                                                @Override
//                                                public void onPermissionAllowedOrRefused(boolean[] isAllowed, String[] messageInfo) {
//                                                    if (isAllowed[0]) {
//                                                        if (onResultListener != null)
//                                                            onResultListener.onResult(true, null);
//                                                    } else {
//                                                        NormalReminderDialog.Builder b = new NormalReminderDialog.Builder(context, 0);
//                                                        b
//                                                                .setTitle("温馨提示")
//                                                                .setMessage("由于你拒绝了该权限，无法进行下一步")
//                                                                .setOnlyOneButtonText(msg)
//                                                                .setCancleableTouchOutside(false)
//                                                                .setCancelable(isCancelable)
//                                                                .create()
//                                                                .show();
//                                                        if (onResultListener != null)
//                                                            onResultListener.onResult(false, null);
//                                                    }
//                                                }
//                                            });
//                                }
//                            }
//                        }
//                    });
//        } else {
//            if (onResultListener != null)
//                onResultListener.onResult(true, null);
//        }
//    }
//
//    /********************************************************************************************************************************************************************
//     * ******************************************************************************************************************************************************************
//     ********************************************************************************************************************************************************************/
//
//    public static AlertDialog showNormalAlertDialog(Context context,
//                                                    String title,
//                                                    String msg,
//                                                    String positiveStr,
//                                                    String negativeStr,
//                                                    boolean cancleEnable,
//                                                    DialogInterface.OnClickListener onClickListener) {
//
//        if (context == null) return null;
//        AlertDialog.Builder b = new AlertDialog.Builder(context);
//        if (!StringUtils.isEmpty(title))
//            b.setTitle(title);
//        if (!StringUtils.isEmpty(msg))
//            b.setMessage(msg);
//        if (!StringUtils.isEmpty(positiveStr))
//            b.setPositiveButton(positiveStr, onClickListener);
//        if (!StringUtils.isEmpty(negativeStr))
//            b.setNegativeButton(negativeStr, onClickListener);
//
//        b.setCancelable(cancleEnable);
//        AlertDialog dialog = b.create();
//        dialog.show();
//        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//        if (context != null) {
//            btnNegative.setTextColor(ContextCompat.getColor(context, com.sevnce.yhlib.R.color.gray_text));
//            btnPositive.setTextColor(ContextCompat.getColor(context, com.sevnce.yhlib.R.color.lib_blue_login_press));
//        }
//        return dialog;
//    }
//
//    /**
//     * 显示自定义普通提示对话框
//     *
//     * @param context         上下文
//     * @param title           标题
//     * @param msg             提示信息
//     * @param positiveStr     右边按钮文字
//     * @param negativeStr     左边按钮文字
//     * @param cancelable      是否可以点击外部取消
//     * @param onClickListener 按钮监听
//     * @return NormalReminderDialog 对象
//     */
//    public static NormalReminderDialog showNormalReminderDialog(Context context, int theme, String title, String msg,
//                                                                String negativeStr, String positiveStr,
//                                                                boolean cancelable, boolean canCancelTouchOutsideEnable,
//                                                                DialogInterface.OnClickListener onClickListener) {
//
//        if (theme < 0) theme = 0;
//        NormalReminderDialog.Builder b = new NormalReminderDialog.Builder(context, theme);
//        if (!StringUtils.isEmpty(title)) {
//            b.setTitle(title);
//        } else {
//            b.setTitle("温馨提示");
//        }
//
//        if (!StringUtils.isEmpty(msg)) {
//            b.setMessage(msg);
//        }
//
//        if (!StringUtils.isEmpty(negativeStr)) {
//            b.setLeftButtonText(negativeStr);
//        } else {
//            b.setLeftButtonText("取消");
//        }
//
//        if (!StringUtils.isEmpty(positiveStr)) {
//            b.setRightButtonText(positiveStr);
//        } else {
//            b.setRightButtonText("确定");
//        }
//
//        b.setCancelable(cancelable);
//        b.setCancleableTouchOutside(canCancelTouchOutsideEnable);
//        b.setOnClickListener(onClickListener == null ? null : onClickListener);
//        NormalReminderDialog dialog = b.create();
//        dialog.show();
//        return dialog;
//    }
//
//    /**
//     * 弹出图片来源选择Dialog
//     *
//     * @param maxSelectCount 指定对打选择图片数目
//     * @param fragment       Fragment对象
//     * @param path           拍照后图片的存储路径
//     * @return 返回一个最新的保存图片的路径
//     */
//    public static void showSelectedPhotoDialog(final int maxSelectCount, final Fragment fragment, final String path,
//                                               final boolean showDeleteItem, final View view, final int resultCameraCode,
//                                               final int resultAblumCode, final ActionCompeleteListener compeleteListener,
//                                               final PhotoSelectPopupWindow.OnClickDeleteListener listener) {
//        new CommonFunctionUtils(fragment.getActivity()).getPermissionByName(Manifest.permission.READ_EXTERNAL_STORAGE,
//                "确定", true,
//                new ActionCompeleteListener() {
//                    @Override
//                    public void onResult(boolean isSuccessed, Object obj) {
//                        if (!isSuccessed) return;
//                        XFileUtil.init();
//                        Bimp.max = maxSelectCount;
//                        String pictureSavePath = path;
//                        if (StringUtils.isEmpty(path)) {
//                            pictureSavePath = XFileUtil.PICTURE_PATH_NAME + "currentPhoto" + System.currentTimeMillis() + ".jpg";
//                        }
//                        if (compeleteListener != null)
//                            compeleteListener.onResult(true, pictureSavePath);
//                        PhotoSelectPopupWindow pop = new PhotoSelectPopupWindow(fragment, pictureSavePath, showDeleteItem);
//                        if (resultCameraCode != 0) pop.setResult_camera_code(resultCameraCode);
//                        if (resultAblumCode != 0) pop.setResult_album_code(resultAblumCode);
//                        pop.setOnClickDeleteListener(listener);
//                        pop.show(view);
//                    }
//                });
//    }
//
//
//
//    /**
//     * Holder中显示公里数和上牌日期的方法
//     *
//     * @param data
//     */
//    @SuppressLint("SetTextI18n")
//    public static void holderShowMileageAndYear(TextView textView, BaseDataModel data, String mileageKey, String yearKey) {
//        if (textView == null || data == null || data.getNameValues().size() == 0 || StringUtils.isEmpty(mileageKey) ||
//                StringUtils.isEmpty(yearKey)) return;
//
//        String mileage = data.getValue(mileageKey) + "";
//        String split = "";
//        if (!StringUtils.isEmpty(mileage)) {
//            mileage += "万公里";
//            split = " | ";
//        }
//        String yearType = data.getValue(yearKey) + "";
//        if (!StringUtils.isEmpty(yearType)) {
//            yearType = split + "首次上牌" + yearType;
//        }
//        textView.setText(mileage + yearType);
//    }
//
//    /**
//     * 创建 List<BaseDataModel>
//     *
//     * @param cls
//     * @param json
//     * @return
//     */
//    public static List<BaseDataModel> createBaseDataModelList(Class cls, JSONObject json) {
//        if (cls == null || json == null || json.length() == 0) return null;
//        List<BaseDataModel> l = new ArrayList<>();
//        if (json.has("list")) {
//            try {
//                JSONArray array = json.getJSONArray("list");
//                for (int i = 0; i < array.length(); i++) {
//                    l.add(createBaseDataModel(cls, array.getJSONObject(i)));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return l;
//    }
//
//
//    public static BaseDataModel createBaseDataModel(Class cls, JSONObject json) {
//        if (cls == null || json == null) return null;
//        return BaseDataModel.getModelByJson(cls, json);
//    }
//
//    public static void getHeaderView(final String url, final ImageView imageView, final int width, final int height) {
//        if (StringUtils.isEmpty(url)) return;
//        imageView.setTag(url);
//        new AsyHttp(null, new AsyHttp.IGetCallback() {
//            @Override
//            public void onAsyHttpSuccess(File file) {
//                try {
//                    Bitmap bitmap = BitMapUtil.getBitmap(file.getAbsolutePath(), width, height);
//                    if (bitmap == null) {
//                        try {
//                            if (file.exists()) {
//                                file.delete();
//                            }
//                        } catch (Exception e) {
//                        }
//                        return;
//                    }
//                    loadBitmapImage(url, imageView, bitmap, width, height);
//                } catch (OutOfMemoryError e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onAsyHttpProgress(int percent) {
//
//            }
//        }).execute(url);
//    }
//
//    public static void loadBitmapImage(String uriTag, ImageView imageView, Bitmap bitmap, int width, int height) {
//        if (!imageView.getTag().equals(uriTag)) return;
//        try {
//            int w = bitmap.getWidth();// 获取真实宽高
//            int h = bitmap.getHeight();
//            if (width > 0) {
//                h = width * h / w;
//                w = width;
//            } else if (height > 0) {
//                width = height * w / h;
//                h = height;
//            }
//            if (width != -1 && height != -1) {
//                ViewGroup.LayoutParams lp = imageView.getLayoutParams();
//                if (lp == null) lp = new ViewGroup.LayoutParams(w, h);
//                else {
//                    lp.height = h;
//                    lp.width = w;
//                }
//                imageView.setLayoutParams(lp);
//            }
//            imageView.setImageBitmap(bitmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 保持到小数点后多少位
//     *
//     * @param price
//     * @param count
//     * @return
//     */
//    public static String keepCountAfterPoint(String price, int count) {
//        if (StringUtils.isEmpty(price) || count <= 0) return price;
//        price = getStringOutE(price);
//        String[] b = price.split("\\.");
//        if (b.length > 1 && b[1].length() > count) {
//            b[1] = b[1].substring(0, count);
//            price = b[0] + "." + b[1];
//        }
//        return price;
//    }
//
//    /**
//     * 转换成以  万元  为单位，并只显示小数点后一位
//     *
//     * @param price 价钱
//     * @return
//     */
//    public static String changeToWanYuanAndKeepOneCountAfterPoint(String price, int countAfterPoint) {
//        if (StringUtils.isEmpty(price)) return "";
//        price = price.replaceAll(",", "");
//        String a = (Double.parseDouble(getStringOutE(price)) / 10000) + "";
//        a = keepCountAfterPoint(a, countAfterPoint);
//        a = addComma(a);
//        return "￥" + a + " 万元";
//    }
//
//    /**
//     * 去除科学计数法
//     *
//     * @param str 需要去除的字符串
//     * @return 去除后的字符串
//     */
//    public static String getStringOutE(String str) {
//        if (str == null) return "";
//        BigDecimal bd = new BigDecimal(str);
//        return bd.toPlainString();
//    }
//
//    /**
//     * 设置状态栏黑色字体图标(modify status's color)
//     * 适配4.4以上版本Android
//     * 传递activity和需要改变的颜色值
//     */
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public static void modifyStatusColor(Activity activity, int color) {
//        Window window = activity.getWindow();
//        if (Build.VERSION.SDK_INT >= 21) {
//            //设置透明状态栏,这样才能让 ContentView 向上
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            //设置状态栏颜色
//
//            window.setStatusBarColor(activity.getResources().getColor(color));
//            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
//            View mChildView = mContentView.getChildAt(0);
//            if (mChildView != null) {
//                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
//                ViewCompat.setFitsSystemWindows(mChildView, true);
//            }
//        } else if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT <= 21) {
//            //不做处理
//        }
//    }
//
//    /**
//     * 设置状态栏黑色字体图标，
//     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
//     *
//     * @param activity
//     * @return 1:MIUUI 2:Flyme 3:android6.0
//     */
//    public static int statusBarLightMode(Activity activity) {
//        int result = 0;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//                result = 3;
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 透明状态栏
//     * 适配4.4以上版本Android
//     *
//     * @param activity
//     */
//    public static void setStatusTransparent(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = activity.getWindow();
//            // Translucent status bar
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
//    }
//
//    /**
//     * 拨打电话方法，调用该方法前需要进行权限检测
//     *
//     * @param context
//     * @param phoneNumber
//     */
//    public static void call(Context context, String phoneNumber) {
//        if (StringUtils.isEmpty(phoneNumber)) return;
//        Uri uriPhone = Uri.parse("tel:" + phoneNumber);
//        Intent intent = new Intent(Intent.ACTION_DIAL, uriPhone);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        context.startActivity(intent);
//    }
//
//    /**
//     * 将每三个数字加上逗号处理（通常使用金额方面的编辑）
//     *
//     * @param str 无逗号的数字
//     * @return 加上逗号的数字
//     */
//    public static String addComma(String str) {
//        // 将传进数字反转
//        if (StringUtils.isEmpty(str)) return null;
//        str = str.replaceAll(",", "");
//        str = getStringOutE(str);
//        String[] a = str.split("\\.");
//        String after = "";
//        if (a.length > 1) {
//            int afterInt = Integer.parseInt(a[1] + "");
//            if (afterInt != 0) {
//                after = "." + a[1];
//            }
//            str = a[0];
//        }
//        String reverseStr = new StringBuilder(str).reverse().toString();
//        String strTemp = "";
//        for (int i = 0; i < reverseStr.length(); i++) {
//            if (i * 3 + 3 > reverseStr.length()) {
//                strTemp += reverseStr.substring(i * 3, reverseStr.length());
//                break;
//            }
//            strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ",";
//        }
//        // 将 【789,456,】 中最后一个【,】去除
//        if (strTemp.endsWith(",")) {
//            strTemp = strTemp.substring(0, strTemp.length() - 1);
//        }
//        // 将数字重新反转
//        String resultStr = new StringBuilder(strTemp).reverse().toString();
//        resultStr += after;
//        return resultStr;
//    }
//
//    /**
//     * 转换成以  元  为单位，并只显示小数点后两位
//     *
//     * @param price 价钱
//     * @return
//     */
//    public static String changeToYuanAndKeepTwoCountAfterPoint(String price) {
//        if (StringUtils.isEmpty(price)) return "";
//        String a = getStringOutE(price) + "";
//        a = keepCountAfterPoint(a, 2);
//        a = addComma(a);
//        return a;
//    }
//
//    /**
//     * 使身份证中间显示为 XXXXXX********XXXX
//     *
//     * @param idCard
//     * @return
//     */
//    public static String hideIDCardCenter(String idCard) {
//        if (!StringUtils.isEmpty(idCard) && idCard.length() >= 17) {
//            String regex = "(\\w{1})(.*)(\\w{1})";
//            Matcher m = Pattern.compile(regex).matcher(idCard);
//            if (m.find()) {
//                String rep = m.group(2);
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < rep.length(); i++) {
//                    sb.append("*");
//                }
//                idCard = idCard.replaceAll(rep, sb.toString());
//            }
//        }
//        return idCard;
//    }
//
//    /**
//     * 使身份证中间显示为 XXX********XXXX
//     *
//     * @param phone
//     * @return
//     */
//    public static String hidePhoneNumberCenter(String phone) {
//        if (!StringUtils.isEmpty(phone) && phone.length() >= 11 && phone.length() <= 14) {
//            String regex = "(\\w{3})(.*)(\\w{4})";
//            if (phone.startsWith("0") && (phone.indexOf(3) == 1)) { // 含有区号
//                regex = "(\\w{6})(.*)(\\w{4})";
//            }
//            Matcher m = Pattern.compile(regex).matcher(phone);
//            if (m.find()) {
//                String rep = m.group(2);
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < rep.length(); i++) {
//                    sb.append("*");
//                }
//                phone = phone.replaceAll(rep, sb.toString());
//            }
//        }
//        return phone;
//    }
//
//    /**
//     * 安装下载后的apk文件
//     * 兼容7.0
//     */
//    public static void instanllAPK(File file, Context context) {
//        if (context == null || file == null || !file.exists()) {
//            return;
//        }
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        String type = "application/vnd.android.package-archive";
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        } else {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
//        intent.setDataAndType(XFileUtil.getUriForFile(context, file), type);
//        context.startActivity(intent);
//    }
//
//}
