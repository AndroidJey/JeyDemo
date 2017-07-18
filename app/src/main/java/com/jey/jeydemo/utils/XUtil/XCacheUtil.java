package com.jey.jeydemo.utils.XUtil;

import android.content.Context;

import java.io.File;
import java.math.BigDecimal;

/**
 * 缓存类工具（包括清楚缓存，清楚本应用所有数据等）
 */
public class XCacheUtil {


    /**
     * 清除本应用所有的数据 * *
     *
     * @param context  上下文
     * @param filepath 文件目录
     */
    public static void cleanApplicationData(Context context, String... filepath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        if (filepath == null) {
            return;
        }
        for (String filePath : filepath) {
            cleanCustomCache(filePath);
        }
    }

    /**
     * 清除全部缓存，不包括下载的图片，音视频，以及日志文件，只有缓存的图片文件
     *
     * @param context 上下文
     */
    public static void clearAllCache(Context context) {
        XFileUtil.deleteFolderFile(context.getCacheDir().getAbsolutePath(), false);
        XFileUtil.deleteFolderFile(XFileUtil.CACHE_PATH_NAME, false);
        if (XFileUtil.checkExternalStorageExist() && context.getExternalCacheDir() != null) {
            XFileUtil.deleteFolderFile(context.getExternalCacheDir().getAbsolutePath(), false);
        }
    }

    /**
     * 获取所有缓存文件的大小
     *
     * @param context 上下文
     * @return 已经格式化后的大小
     */
    public static String getTotalFormatCacheSize(Context context) {
        long cacheSize = XFileUtil.getFileSize(context.getCacheDir());
        cacheSize += XFileUtil.getFileSize(new File(XFileUtil.CACHE_PATH_NAME));
        cacheSize += XFileUtil.getFileSize(new File(XFileUtil.PICTURE_PATH_NAME));
        if (XFileUtil.checkExternalStorageExist()) {
            cacheSize += XFileUtil.getFileSize(context.getExternalCacheDir());
        }
        return getFormatSizeToString(cacheSize);
    }

    /**
     * 获取所有缓存文件的大小
     *
     * @param context 上下文
     * @return 未格式化后的大小
     */
    public static long getTotalCacheSize(Context context) {
        long cacheSize = XFileUtil.getFileSize(context.getCacheDir());
        cacheSize += XFileUtil.getFileSize(new File(XFileUtil.CACHE_PATH_NAME));
        cacheSize += XFileUtil.getFileSize(new File(XFileUtil.PICTURE_PATH_NAME));
        if (XFileUtil.checkExternalStorageExist()) {
            cacheSize += XFileUtil.getFileSize(context.getExternalCacheDir());
        }
        return cacheSize;
    }

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
     *
     * @param context 上下文
     */
    public static void cleanInternalCache(Context context) {
        XFileUtil.deleteFolderFile(context.getCacheDir().getAbsolutePath(), false);
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * *
     *
     * @param context 上下文
     */
    public static void cleanDatabases(Context context) {
        XFileUtil.deleteFolderFile("/data/data/" + context.getPackageName() + "/databases", false);
    }

    /**
     * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
     *
     * @param context 上下文
     */
    public static void cleanSharedPreference(Context context) {
        XFileUtil.deleteFolderFile("/data/data/" + context.getPackageName() + "/shared_prefs", false);
    }

    /**
     * 按名字清除本应用数据库 * *
     *
     * @param context 上下文
     * @param dbName  需要清除的数据库名称
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容 * *
     *
     * @param context 上下文
     */
    public static void cleanFiles(Context context) {
        XFileUtil.deleteFolderFile(context.getFilesDir().getAbsolutePath(), false);
    }

    /**
     * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context 上下文
     */
    public static void cleanExternalCache(Context context) {
        if (XFileUtil.checkExternalStorageExist() && context.getExternalCacheDir() != null) {
            XFileUtil.deleteFolderFile(context.getExternalCacheDir().getAbsolutePath(), false);
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * *
     *
     * @param filePath 文件目录
     */
    public static boolean cleanCustomCache(String filePath) {
        return XFileUtil.deleteDir(new File(filePath), false);
    }


    /**
     * 格式化单位
     *
     * @param size 文件大小
     * @return 格式化后的文件大小
     */
    public static String getFormatSizeToString(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0KB";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }
}
