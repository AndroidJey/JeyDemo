package com.jey.jeydemo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.appcompat.BuildConfig;

import com.jey.jlibs.activity.BaseActivity;
import com.jey.jlibs.activity.PopActivity;
import com.jey.jlibs.interface_.OnRequestPermissionsResultForAppCompatActivity;

/**
 * Created by sevnce on 16/9/26.
 */
public class PermissionUtil {

    public static final int FROM_BASE_ACTIVITY = 2354;
    public static final int FROM_POP_ACTIVITY = 2355;

    public static boolean checkBuildVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static void checkPermissionStatus(String[] permissions, AppCompatActivity appCompatActivity,
                                             final OnPermissionAllowedOrRefusedListener listener) {
        if (permissions == null || permissions.length == 0)
            throw new RuntimeException("permission String[] not be null");
        if (appCompatActivity == null)
            throw new RuntimeException("appCompatActivity not be null");
        for (int i = 0; i < permissions.length; i++) {
            int staus = ContextCompat.checkSelfPermission(appCompatActivity, permissions[i]);
            if (staus == PackageManager.PERMISSION_GRANTED) {
                if (listener != null)
                    listener.onPermissionAllowedOrRefused(new boolean[]{true}, null);
//                return true;
            } else {
                boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, permissions[0]);
                if (shouldShow) {
                    showShouldReminderForUserGetPermissionReasonDialog(appCompatActivity, permissions[0], listener);
                } else {
                    if (listener != null)
                        listener.onPermissionAllowedOrRefused(new boolean[]{false}, null);
//                    return false;
                }
            }
        }
//        if (listener != null)
//            listener.onPermissionAllowedOrRefused(new boolean[]{false}, null);
//        return false;
    }

    public static void getPermission(int fromWhereActivityTag, String[] permissions, int requestCode,
                                     AppCompatActivity appCompatActivity, final OnPermissionAllowedOrRefusedListener listener) {

        if (permissions == null || permissions.length == 0)
            throw new RuntimeException("permission String[] not be null");
        if (appCompatActivity == null) throw new RuntimeException("appCompatActivity not be null");
        ActivityCompat.requestPermissions(appCompatActivity, permissions, requestCode);
        if (fromWhereActivityTag == FROM_BASE_ACTIVITY) {
            BaseActivity.setOnRequestPermissionsResultForAppCompatActivity(
                    new OnRequestPermissionsResultForAppCompatActivity() {

                        @Override
                        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                               @NonNull int[] grantResults) {
                            switch (requestCode) {
                                case BaseActivity.REQUEST_PERMISSION_FOR_BASE_ACTIVITY:
                                    if (listener != null) {
                                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                            listener.onPermissionAllowedOrRefused(new boolean[]{true}, null);
                                        } else {
                                            listener.onPermissionAllowedOrRefused(new boolean[]{false}, null);
                                        }
                                    }
                                    break;
                            }
                        }
                    });
        } else if (fromWhereActivityTag == FROM_POP_ACTIVITY) {
            PopActivity.setOnRequestPermissionsResultForAppCompatActivity(
                    new OnRequestPermissionsResultForAppCompatActivity() {
                        @Override
                        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                               @NonNull int[] grantResults) {
                            switch (requestCode) {
                                case PopActivity.REQUEST_PERMISSION_FOR_POP_ACTIVITY:
                                    if (listener != null) {
                                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                            listener.onPermissionAllowedOrRefused(new boolean[]{true}, null);
                                        } else {
                                            listener.onPermissionAllowedOrRefused(new boolean[]{false}, null);
                                        }
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    public static void showShouldReminderForUserGetPermissionReasonDialog(final Context context, final String permission,
                                                                          final OnPermissionAllowedOrRefusedListener listener) {
//        final boolean[] isAllowed = {false};
        String permissionDetail = "只有当你同意该权限时,才能使用该功能哦!";
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                permissionDetail = "只有当你同意该权限时,才能使用该功能哦!";
                break;
            case Manifest.permission.CALL_PHONE:
                // TODO
                break;
        }
//        NormalReminderDialog.Builder builder = new NormalReminderDialog.Builder(context, 0);
//        builder
//                .setTitle("温馨提示")
//                .setMessage(permissionDetail)
//                .setLeftButtonText("取消")
//                .setRightButtonText("重新获取")
//                .setOnClickListener(new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == DialogInterface.BUTTON_NEGATIVE) {
////                            isAllowed[0] = false;
//                            listener.onPermissionAllowedOrRefused(new boolean[]{false}, new String[]{"取消"});
//                        } else if (which == DialogInterface.BUTTON_POSITIVE) {
//                            listener.onPermissionAllowedOrRefused(new boolean[]{false}, new String[]{"重新获取"});
////                            isAllowed[0] = true;
//                        }
//                    }
//                })
//                .create()
//                .show();
    }

    public interface OnPermissionAllowedOrRefusedListener {
        void onPermissionAllowedOrRefused(boolean[] isAllowed, String[] messageInfo);
    }

    /**
     * 通过尝试打开相机的方式判断有无拍照权限（在6.0以下使用拥有root权限的管理软件可以管理权限）
     *
     * @return
     */
    public static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    /**
     * 跳转到miui的权限管理页面
     */
    public static void gotoMiuiPermission(Activity activity) {
        Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        i.setComponent(componentName);
        i.putExtra("extra_pkgname", activity.getPackageName());
        try {
            activity.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
            gotoMeizuPermission(activity);
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    public static void gotoMeizuPermission(Activity activity) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            gotoHuaweiPermission(activity);
        }
    }

    /**
     * 华为的权限管理页面
     */
    public static void gotoHuaweiPermission(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            activity.startActivity(getAppDetailSettingIntent(activity));
        }
    }

    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    public static Intent getAppDetailSettingIntent(Activity activity) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
        }
        return localIntent;
    }

}
