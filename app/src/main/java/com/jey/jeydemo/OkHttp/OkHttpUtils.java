package com.jey.jeydemo.OkHttp;

import com.jey.jeydemo.application.Application;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by jey on 2017/3/29.
 */

public class OkHttpUtils {
    private static OkHttpClient mOkHttpClient;

    //设置缓存目录
    private static File cacheDirectory = new File(Application.context.getCacheDir().getAbsolutePath(), "JeyCache");
    private static Cache cache = new Cache(cacheDirectory, 10 * 1024 * 1024);


    /**
     * 获取OkHttpClient对象
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient() {
        if (null == mOkHttpClient) {
            mOkHttpClient = new OkHttpClient.Builder()
                    //.addInterceptor(new MyIntercepter())
                    //.addNetworkInterceptor(new CookiesInterceptor(MyApplication.getInstance().getApplicationContext()))
                    //设置请求读写的超时时间
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(cache)
                    .build();
        }
        return mOkHttpClient;
    }
}
