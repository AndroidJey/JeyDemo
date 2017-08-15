package com.jey.jeydemo.movie;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.jey.jeydemo.R;
import com.jey.jeydemo.http.Fault;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class MovieActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{
    private MovieLoader mMovieLoader;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    @BindView(R.id.swipeRefreshMovie)
    SwipeRefreshLayout swipeRefreshMovie;
    public static final String BASE_URL = "https://api.douban.com/v2/movie/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);
        mMovieLoader = new MovieLoader();
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("电影列表");
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new MovieDecoration());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mMovieAdapter = new MovieAdapter();
        mRecyclerView.setAdapter(mMovieAdapter);
        swipeRefreshMovie.setOnRefreshListener(this);
        getMovieList();

    }

    /**
     * 获取电影列表
     */
    private void getMovieList(){
        mMovieLoader.getMovie(0,10).subscribe(new Action1<Object>() {
            @Override
            public void call(Object movies) {
                mMovieAdapter.setMovies((List<Movie>) movies);
                swipeRefreshMovie.setRefreshing(false);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("TAG","error message:"+throwable.getMessage());
                if(throwable instanceof Fault){
                    Fault fault = (Fault) throwable;
                    if(fault.getErrorCode() == 404){
                        //错误处理
                    }else if(fault.getErrorCode() == 500){
                        //错误处理
                    }else if(fault.getErrorCode() == 501){
                        //错误处理
                    }
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        //getMovieRx();
    }

    @Override
    public void onRefresh() {
        getMovieList();
    }

    public static class MovieDecoration extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0,0,0,20);
        }
    }
}
