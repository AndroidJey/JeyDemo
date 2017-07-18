package com.jey.jeydemo.movie;


import com.jey.jeydemo.http.ObjectLoader;
import com.jey.jeydemo.http.RetrofitServiceManager;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;

/**
 *
 *
 * Created by zhouwei on 16/11/10.
 */

public class MovieLoader extends ObjectLoader {
    private MovieService mMovieService;

    public MovieLoader(){
        mMovieService = RetrofitServiceManager.getInstance().create(MovieService.class);
    }

    /**
     * 获取电影列表
      * @param start
     * @param count
     * @return
     */
    public Observable<List<Movie>> getMovie(int start, int count){
        return observe(mMovieService.getTop250(start,count))
                .map(new Func1<MovieSubject, List<Movie>>() {
            @Override
            public List<Movie> call(MovieSubject movieSubject) {
                return movieSubject.subjects;
            }
        });
    }

    public interface MovieService{

        //获取豆瓣Top250 榜单
        @GET("top250")
        Observable<MovieSubject> getTop250(@Query("start") int start, @Query("count") int count);

    }
}
