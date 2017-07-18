package com.jey.jlibs.utils.PhotoSelectUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Bimp {
    public static int max = 0;
    public static ArrayList<ImageItem> tempSelectBitmap = new ArrayList<>(); // 选择的图片的临时列表
    /* 选择图片后面跟着的自定义图片数量，ex:加号（无需上传服务器） */
    public static int customPicCount = 0;

    /**
     * 图片修正
     * <p/>
     * author: Kevin.Li
     * created at 2016/3/25 13:58
     */
    public static Bitmap revisionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap;
        while (true) {
            if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
                in = new BufferedInputStream(new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }
}