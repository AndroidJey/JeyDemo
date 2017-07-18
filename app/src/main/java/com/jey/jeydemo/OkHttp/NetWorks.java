package com.jey.jeydemo.OkHttp;

import android.util.JsonReader;

import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jey on 2017/3/29.
 */

public class NetWorks extends RetrofitUtils {
    //创建实现接口调用
    protected static final NetService service = getRetrofit().create(NetService.class);

    //设缓存有效期为1天
    protected static final long CACHE_STALE_SEC = 60 * 60 * 24 * 1;
    //查询缓存的Cache-Control设置，使用缓存
    protected static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    //查询网络的Cache-Control设置。不使用缓存
    protected static final String CACHE_CONTROL_NETWORK = "max-age=0";

    private interface NetService {

        //POST请求
        @FormUrlEncoded
        @POST("bjws/app.user/login")
        Observable<String> getVerfcationCodePost(@Field("tel") String tel, @Field("password") String pass);

        //POST请求
        @FormUrlEncoded
        @POST("bjws/app.user/login")
        Observable<String> getVerfcationCodePostMap(@FieldMap Map<String, Object> map);

        //GET请求
        @GET("bjws/app.user/login")
        Observable<String> getVerfcationGet(@Query("tel") String tel, @Query("password") String pass);


        //GET请求，设置缓存
        @Headers("Cache-Control: public," + CACHE_CONTROL_CACHE)
        @GET("bjws/app.user/login")
        Observable<String> getVerfcationGetCache(@Query("tel") String tel, @Query("password") String pass);


//        @Headers("Cache-Control: public," + CACHE_CONTROL_NETWORK)
        @GET("Verification/getPublicKey.json")
        Observable<Object> getMainMenu();

    }

    //POST请求
    public static void verfacationCodePost(String tel, String pass,Observer<String> observer){
        setSubscribe(service.getVerfcationCodePost(tel, pass),observer);
    }


    //POST请求参数以map传入
    public static void verfacationCodePostMap(Map<String, Object> map,Observer<String> observer) {
        setSubscribe(service.getVerfcationCodePostMap(map),observer);
    }

    //Get请求设置缓存
    public static void verfacationCodeGetCache(String tel, String pass,Observer<String> observer) {
        setSubscribe(service.getVerfcationGetCache(tel, pass),observer);
    }

    //Get请求
    public static void verfacationCodeGet(String tel, String pass,Observer<String> observer) {
        setSubscribe(service.getVerfcationGet(tel, pass),observer);
    }

    //Get请求
    public static void verfacationCodeGetsub(String tel, String pass, Observer<String> observer) {
        setSubscribe(service.getVerfcationGet(tel, pass),observer);
    }

    //Get请求
    public static void Getcache( Observer<Object> observer) {
        setSubscribe(service.getMainMenu(),observer);
    }

    public static <T> void setSubscribe(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())//子线程访问网络
                .observeOn(AndroidSchedulers.mainThread())//回调到主线程
                .subscribe(observer);
    }
}
