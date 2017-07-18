package com.jey.jeydemo.utils.XUtil;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.jey.jlibs.activity.BaseActivity;
import com.jey.jeydemo.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 文件操作类
 */
public class XFileUtil {

    public static boolean isInit = false;

    /**
     * 应用所有文件存放的根文件名
     **/
    public static String FOLDER_NAME = "";
    /**
     *
     */
    public static String ROOT_DIR_NAME = "";
    /**
     *
     */
    public static String APP_DATA_NAME = "";

    /**
     * 音频类文件存放的根文件名
     */
    public static String MEDIA_PATH_NAME = "";

    /**
     * 日志类文件存放的根文件名
     */
    public static String LOG_PATH_NAME = "";

    /**
     * 图片类文件存放的根文件名
     */
    public static String PICTURE_PATH_NAME = "";

    /**
     * 缓存类文件存放的根文件名
     */
    public static String CACHE_PATH_NAME = "";


    /**
     * XFileUtil 初始化，需要在 MainActivity 或者 Application 中调用
     * 注意 ： 手机OS版本大于等于23时，需要去申请权限成功后才能调用该方法
     */
    public static void init() {
        if (checkExternalStorageExist()) {
            FOLDER_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                    "WestCarCustomer" + File.separator;
            ROOT_DIR_NAME = FOLDER_NAME;
        } else {
            FOLDER_NAME = Environment.getRootDirectory() + File.separator + "WestCarCustomer" + File.separator;
            ROOT_DIR_NAME = Environment.getDataDirectory().getAbsolutePath() + File.separator + "data"
                    + File.separator + "WestCarCustomer" + File.separator;
        }
        MEDIA_PATH_NAME = FOLDER_NAME + "Media" + File.separator;
        LOG_PATH_NAME = FOLDER_NAME + "Log" + File.separator;
        PICTURE_PATH_NAME = FOLDER_NAME + "Picture" + File.separator;
        CACHE_PATH_NAME = FOLDER_NAME + "Cache" + File.separator;
        createFolder(FOLDER_NAME);
        createFolder(ROOT_DIR_NAME);
        createFolder(MEDIA_PATH_NAME);
        createFolder(LOG_PATH_NAME);
        createFolder(PICTURE_PATH_NAME);
        createFolder(CACHE_PATH_NAME);
        if (BaseActivity.context != null) {
            XBitmapUtil.saveBitmapToFile(BitmapFactory.decodeResource(BaseActivity.context.getResources(),
                    R.mipmap.image_default), XFileUtil.LOG_PATH_NAME + "Logo.png", 100, true);
        }
        isInit = true;
    }

    /**
     * 检测SD卡是否挂载
     *
     * @return true 为 已挂载 ； false 为 未挂载
     */
    public static boolean checkExternalStorageExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 创建文件夹
     *
     * @param path 文件夹路径
     * @return 创建成功与否
     */
    public static boolean createFolder(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return f.mkdirs();
        }
        return true;
    }

    /**
     * 将byte[] 写入到文件中
     *
     * @param bytes    要写入的内容
     * @param fileName 要存储的文件名
     * @return 存储后的文件
     */
    public static File writeToFile(byte[] bytes, String fileName) {
        File f = new File(fileName);
        try {
            if (!f.exists())
                f.createNewFile();
            FileOutputStream s = new FileOutputStream(f);
            s.write(bytes);
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return f;
    }

    /**
     * 通过文件名来获取该文件
     *
     * @param fileName                  文件名
     * @param createNewFileWhenNotExist 当该文件名的文件不存在时是否创建同名文件
     * @return
     */
    public static File getFile(String fileName, boolean createNewFileWhenNotExist) {
        File f = new File(fileName);
        if (!f.mkdirs() && !f.exists()) return null;

        if (f.exists()) return f;

        if (createNewFileWhenNotExist) {
            try {
                f.createNewFile();
                return f;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 获取文件大小
     *
     * @param dir 文件或文件夹路径
     * @return 总大小
     */
    public static long getFileSize(File dir) {
        long size = 0;
        try {
            if (dir != null) {
                if (dir.isFile() && dir.exists()) {
                    size = dir.length();
                } else {
                    File[] fileList = dir.listFiles();
                    if (fileList != null) {
                        for (int i = 0; i < fileList.length; i++) {
                            // 如果下面还有文件
                            if (fileList[i].isDirectory()) {
                                size = size + getFileSize(fileList[i]);
                            } else {
                                size = size + fileList[i].length();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 根据文件路径删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deletedFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 批量根据文件路径删除文件
     *
     * @param filePathList 文件路径
     * @return 是否删除成功
     */
    public static boolean deletedFileByPath(List<String> filePathList) {
        if (filePathList == null || filePathList.size() == 0) return true;
        boolean[] tag = new boolean[filePathList.size()];
        for (int i = 0; i < filePathList.size(); i++) {
            tag[i] = deletedFile(filePathList.get(i));
        }
        boolean returnBoolean = true;
        for (int i = 0; i < tag.length; i++) {
            if (!tag[i]) {
                returnBoolean = false;
                break;
            }
        }
        return returnBoolean;
    }

    /**
     * 批量根据文件路径删除文件
     *
     * @param filePathList 文件路径
     * @return 是否删除成功
     */
    public static boolean deletedFileByFile(List<File> filePathList) {
        if (filePathList == null || filePathList.size() == 0) return true;
        boolean[] tag = new boolean[filePathList.size()];
        for (int i = 0; i < filePathList.size(); i++) {
            tag[i] = deletedFile(filePathList.get(i).getAbsolutePath());
        }
        boolean returnBoolean = true;
        for (int i = 0; i < tag.length; i++) {
            if (!tag[i]) {
                returnBoolean = false;
                break;
            }
        }
        return returnBoolean;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param filePath       文件目录
     * @param deleteThisPath 是否该文件存在的目录
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 如果下面还有文件
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteDir(File dir, boolean deleteThisPath) {
        if (dir == null) {
            return false;
        }
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]), deleteThisPath);
                if (!success) {
                    return false;
                }
            }
        }
        if (dir.isFile()) {
            return dir.delete();
        } else {
            if (deleteThisPath) {
                return dir.delete();
            } else {
                return true;
            }
        }
    }

    /**
     * 根据路径去检测该文件是否存在
     *
     * @param filePaht 文件路径
     * @return 存在与否
     */
    public static boolean checkFileExits(String filePaht) {
        File f = new File(filePaht);
        if (!f.exists())
            return false;
        return true;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     */
    public static boolean copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()));
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开文件
     * 兼容7.0
     *
     * @param context     activity
     * @param file        File
     * @param contentType 文件类型如：文本（text/html）
     *                    当手机中没有一个app可以打开file时会抛ActivityNotFoundException
     */
    public static void startActionFile(Context context, File file, String contentType) throws ActivityNotFoundException {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//增加读写权限
        intent.setDataAndType(getUriForFile(context, file), contentType);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 打开相机   针对Activity
     * 兼容7.0
     *
     * @param activity    Activity
     * @param file        File
     * @param requestCode result requestCode
     */
    public static void startActionCaptureOfActivity(Activity activity, File file, int requestCode) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(activity, file));
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开相机  针对Fragment
     * 兼容7.0
     *
     * @param fragment    Fragment
     * @param file        File
     * @param requestCode result requestCode
     */
    public static void startActionCaptureOfFragment(Fragment fragment, File file, int requestCode) {
        if (fragment == null) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(fragment.getActivity(), file));
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取设置的Uri
     * 兼容7.0
     *
     * @param context
     * @param file
     * @return
     */
    public static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }


}
