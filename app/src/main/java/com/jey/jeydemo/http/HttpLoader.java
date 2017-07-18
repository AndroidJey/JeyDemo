package com.jey.jeydemo.http;

import com.google.gson.JsonObject;
import com.jey.jeydemo.movie.MovieLoader;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by jey on 2017/3/30.
 */

public class HttpLoader extends ObjectLoader {
    private HttpLoader.HttpService mMovieService;
    public HttpLoader(){
        mMovieService = RetrofitServiceManager.getInstance().create(HttpLoader.HttpService.class);
    }

    /**
     * 获取电影列表
     * @param url
     * @return
     */
    public Observable<JsonObject> getHttp(String url, Map<String,Object> param){
        return observe(mMovieService.getdata(url, param))
                .map(new Func1<JsonObject, JsonObject>() {
                    @Override
                    public JsonObject call(JsonObject dateRes) {
                        return dateRes;
                    }
                });
    }

    public interface HttpService{
        @FormUrlEncoded
        @POST
        Observable<JsonObject> getdata(@Url String url,@FieldMap Map<String, Object> names/*@Query("start") int start, @Query("count") int count*/);
    }
}
