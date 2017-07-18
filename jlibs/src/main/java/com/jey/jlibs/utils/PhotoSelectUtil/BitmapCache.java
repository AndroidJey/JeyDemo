package com.jey.jlibs.utils.PhotoSelectUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

public class BitmapCache extends Activity {
    public static Bitmap addBitmap;// 缓存类加号图片

    public Handler h = new Handler();
    public final String TAG = getClass().getSimpleName();
    private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<>();

    public void put(String path, Bitmap bmp) {
        if (!TextUtils.isEmpty(path) && bmp != null) {
            imageCache.put(path, new SoftReference<>(bmp));
        }
    }

    public void displayBmp(final ImageView iv, final String thumbPath,
                           final String sourcePath, final ImageCallback callback) {
        if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(sourcePath)) {
            Log.e(TAG, "no paths pass in");
            return;
        }
        final String path;
        final boolean isThumbPath;
        if (!TextUtils.isEmpty(thumbPath)) {
            path = thumbPath;
            isThumbPath = true;
        } else if (!TextUtils.isEmpty(sourcePath)) {
            path = sourcePath;
            isThumbPath = false;
        } else {
//             iv.setImageBitmap(null);
            return;
        }

        if (imageCache.containsKey(path)) {
            SoftReference<Bitmap> reference = imageCache.get(path);
            Bitmap bmp = reference.get();
            if (bmp != null) {
                if (callback != null) {
                    callback.imageLoad(iv, bmp, sourcePath);
                }
                iv.setImageBitmap(bmp);
                Log.d(TAG, "hit cache");
                return;
            }
        }
        iv.setImageBitmap(null);

        new Thread() {
            Bitmap thumb;

            public void run() {

                try {
                    if (isThumbPath) {
                        thumb = BitmapFactory.decodeFile(thumbPath);
                        if (thumb == null) {
                            thumb = Bimp.revisionImageSize(sourcePath);
                        }
                    } else {
                        thumb = Bimp.revisionImageSize(sourcePath);
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                try {
                    if (thumb == null) {
                        if (addBitmap == null) {
                            throw new Exception();
                        }
                        thumb = addBitmap;
                    }
                    Log.i(TAG, "-------thumb------" + thumb);
                    put(path, thumb);
                    if (callback != null) {
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.imageLoad(iv, thumb, sourcePath);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }.start();
    }


    public interface ImageCallback {
        void imageLoad(ImageView imageView, Bitmap bitmap, Object... params);
    }
}