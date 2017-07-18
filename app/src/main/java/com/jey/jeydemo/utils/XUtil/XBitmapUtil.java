package com.jey.jeydemo.utils.XUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;


import com.jey.jlibs.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Bitmap操作工具类
 */
public class XBitmapUtil {

    public XBitmapUtil() {
    }

    public CompressFileList CompressFileListBackgroud(int maxSize, int compressValue, CompressListFileResult result) {
        return new CompressFileList(maxSize, compressValue, result);
    }

    public class CompressFileList extends AsyncTask<List<File>, Integer, List<File>> {

        int maxSize;
        int compressValue;
        CompressListFileResult result;

        public CompressFileList(int maxSize, int compressValue, CompressListFileResult result) {
            this.maxSize = maxSize;
            this.compressValue = compressValue;
            this.result = result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<File> doInBackground(List<File>... params) {
            return compressListFile(params[0], maxSize, compressValue);
        }

        @Override
        protected void onPostExecute(List<File> files) {
            super.onPostExecute(files);
            if (result != null) {
                result.onCompressResult(files);
            }
        }
    }

    public CompressFile CompressFileBackgroud(int maxSize, int compressValue, CompressFileResult result) {
        return new CompressFile(maxSize, compressValue, result);
    }

    public class CompressFile extends AsyncTask<File, Integer, File> {

        int maxSize;
        int compressValue;
        CompressFileResult result;

        public CompressFile(int maxSize, int compressValue, CompressFileResult result) {
            this.maxSize = maxSize;
            this.compressValue = compressValue;
            this.result = result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(File... params) {
            return compressFile(params[0], maxSize, compressValue);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (result != null) {
                result.onCompressResult(file);
            }
        }
    }

    public interface CompressListFileResult {

        void onCompressResult(List<File> list);
    }

    public interface CompressFileResult {

        void onCompressResult(File file);
    }

    /**
     * 存储Bitmap到指定文件名得文件中
     *
     * @param bitmap                   需要存储的文件
     * @param path                     文件路径（不包含文件名）
     * @param name                     文件名
     * @param compressNumber           压缩的比例 0～100 ； 100 为不压缩
     * @param deleteExistsSameNameFile 是否删除已经存在的同名文件后重新存储
     * @return 是否存储成功
     */
    public static boolean saveBitmapToFile(Bitmap bitmap, String path, String name, int compressNumber,
                                           boolean deleteExistsSameNameFile) {

        if (bitmap == null || StringUtils.isEmpty(name)) return false;
        File folder = new File(path);
        if (!folder.exists()) {
            XFileUtil.createFolder(path);
        }
        File file = new File(path, name);
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) return false;
                else {
                    if (deleteExistsSameNameFile)
                        if (!file.delete()) return false;
                }
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressNumber, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 存储Bitmap到指定文件名得文件中
     *
     * @param bitmap                   需要存储的文件
     * @param path                     文件路径（包含文件名）
     * @param compressNumber           压缩的比例 0～100 ； 100 为不压缩
     * @param deleteExistsSameNameFile 是否删除已经存在的同名文件后重新存储
     * @return 是否存储成功
     */
    public static boolean saveBitmapToFile(Bitmap bitmap, String path, int compressNumber, boolean deleteExistsSameNameFile) {
        if (bitmap == null || StringUtils.isEmpty(path)) return false;
        File file = new File(path);
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) return false;
                else {
                    if (deleteExistsSameNameFile)
                        if (!file.delete()) return false;
                }
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressNumber, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据分辨率压缩图片
     *
     * @param filePath 图片路径
     * @return 压缩后的bitmap对象
     */
    public static Bitmap getBitmapFromPath(String filePath) {
        final BitmapFactory.Options o = new BitmapFactory.Options();
        // 这一句，将不会将bitmap放入内存中
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        // Calculate inSampleSize
        o.inSampleSize = calculateInSampleSize(o, 480, 800);

        // Decode bitmap with inSampleSize set
        o.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, o);
    }

    /**
     * 根据指定最大大小缩放bitmap
     *
     * @param bitmap  bitmap源图
     * @param maxSize 图片允许最大空间   单位：KB
     * @return 缩放后的bitmap
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, double maxSize) {
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
            // 获取这个图片的宽和高
            float width = bitmap.getWidth();
            float height = bitmap.getHeight();
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 计算宽高缩放率
            float scaleWidth = ((float) (bitmap.getWidth() / Math.sqrt(i))) / width;
            float scaleHeight = ((float) (bitmap.getHeight() / Math.sqrt(i))) / height;
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) width, (int) height, matrix, true);
            return bitmap;
        } else
            return bitmap;
    }

    /**
     * 计算图片的缩放值
     *
     * @param o         BitmapFactory.Options
     * @param reqWidth  需要缩放到得宽度
     * @param reqHeight 需要缩放到得高度
     * @return 缩放值
     */
    public static int calculateInSampleSize(BitmapFactory.Options o, int reqWidth, int reqHeight) {
        final int height = o.outHeight;
        final int width = o.outWidth;
        int inSampleSize = 1;

        if (width > reqWidth || height > reqHeight) {
            final int heightRatio = Math.round((float) (height / reqHeight));
            final int widthRatio = Math.round((float) (width / reqWidth));
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 通过图片路径判断图片的角度
     *
     * @param filePath 图片路径
     * @return 角度
     */
    public static int checkPictureDegree(String filePath) {
        int degree = 0;
        try {
            ExifInterface ex = new ExifInterface(filePath);
            int orientation = ex.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 对bitmap对象进行旋转
     *
     * @param bitmap bitmap源文件
     * @param degree 旋转角度
     * @return 处理后得bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        if (bitmap != null) {
            Matrix x = new Matrix();
            x.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), x, true);
            return bitmap;
        } else
            return bitmap;
    }

    public static Bitmap getImage(String absPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(absPath);
        return bitmap;
    }

    /**
     * 批量压缩图片
     *
     * @param resourceFileList List<File>
     * @return 压缩后的图片
     */
    public static List<File> compressListFile(List<File> resourceFileList, int maxSize, int compressValue) {
        List<File> resultFileList = new ArrayList<>();
        if (resourceFileList != null) {
            for (File file : resourceFileList) {
                String path = file.getAbsolutePath();
                String savePath = path;
                if (!path.contains(XFileUtil.PICTURE_PATH_NAME)) {
                    savePath = XFileUtil.PICTURE_PATH_NAME + System.currentTimeMillis() + ".jpg";
                }
                long size = XFileUtil.getFileSize(file);
                boolean tag;
                if (size > 200) {
                    tag = saveBitmapToFile(zoomBitmap(getImage(path), maxSize), savePath, compressValue, true);
                } else {
                    tag = saveBitmapToFile(getImage(path), savePath, 100, true);

                }
                if (tag) {
                    resultFileList.add(new File(savePath));
                }
            }
        }
        return resultFileList;
    }

    /**
     * 单个图片压缩压缩
     *
     * @param file file
     * @return 压缩后的图片
     */
    public static File compressFile(File file, int maxSize, int compressValue) {
        if (file == null || !file.exists()) return null;
        String path = file.getAbsolutePath();
        String savePath = path;
        if (!path.contains(XFileUtil.PICTURE_PATH_NAME)) {
            savePath = XFileUtil.PICTURE_PATH_NAME + System.currentTimeMillis() + ".jpg";
        }
        long size = XFileUtil.getFileSize(file);
        boolean tag;
        if (size > 200) {
            tag = saveBitmapToFile(zoomBitmap(getImage(path), maxSize), savePath, compressValue, true);
        } else {
            tag = saveBitmapToFile(getImage(path), savePath, 100, true);

        }
        if (tag) {
            file = new File(savePath);
        }
        return file;
    }


}
