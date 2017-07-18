package com.jey.jlibs.utils.PhotoSelectUtil;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static String SDPATH = Environment.getExternalStorageDirectory() + "/WestCar/";

    public static boolean saveBitmap(Bitmap bm, String picName) {
        boolean tag = false;
        File folder = new File(SDPATH);
        if (!folder.exists()) {//判断是否已存在该文件夹
            folder.mkdir();
        }
        File file = new File(SDPATH, picName);
        if (!file.exists()) {//判断是否已存在该文件
            try {
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                tag = true;
            } catch (IOException e) {
                e.printStackTrace();
                tag = false;
            }
        }
        return tag;
    }

    /**
     * 根据文件地址删除文件
     */
    public static void delFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除文件夹
     */
    public static void deleteDir() {
        File dir = new File(SDPATH);
        if (!dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir();
        }
        dir.delete();
    }


    /**
     * 文件压缩
     */
    public static void compress(File f, Bitmap bm) {
        try {
            FileOutputStream out = new FileOutputStream(f);
            compressBitmap(bm);
            bm.compress(Bitmap.CompressFormat.JPEG, 75, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片压缩
     */
    public static void compressBitmap(Bitmap bitmap) {
        bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 100 && options > 0) {// 循环判断如果压缩后图片字节流大于80kb，继续压缩
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            if (options > 10) {
                options -= 10;
            } else {
                options = 80;
            }
        }
    }


    public static Bitmap imageZoom(Bitmap bitmap) {
        //图片允许最大空间   单位：KB
        double maxSize = 400.00;
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length / 1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i), bitmap.getHeight() / Math.sqrt(i));
        }
        return bitmap;
    }


    /***
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return 压缩后的bitmap
     */
    private static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
    }

}
