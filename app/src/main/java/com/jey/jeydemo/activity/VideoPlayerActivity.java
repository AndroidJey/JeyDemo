package com.jey.jeydemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.jey.jeydemo.R;
import com.jey.jeydemo.videoPlayer.JeyVideo.JeyVideoPlayer;
import com.jey.jeydemo.videoPlayer.JeyVideo.JeyVideoPlayerStandard;
import com.squareup.picasso.Picasso;

public class VideoPlayerActivity extends AppCompatActivity {
//    private RecyclerView rlVideoList;
//    private List<String> videos = new ArrayList<String>();
//    private String videoUrl = "http://v1.mukewang.com/a45016f4-08d6-4277-abe6-bcfd5244c201/L.mp4";

    private JeyVideoPlayerStandard mJcVideoPlayerStandard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        JCVideo();
        CustomerVideo();
    }

    private void CustomerVideo() {
//        rlVideoList = (RecyclerView) findViewById(R.id.rv_vieo_list);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(VideoPlayerActivity.this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        rlVideoList.setLayoutManager(layoutManager);
//
//        /**故意报错**/
//        videos.add(videoUrl);
//        videos.add(videoUrl);
//        videos.add(videoUrl);
//        videos.add(videoUrl);
//        videos.add(videoUrl);
//        videos.add(videoUrl);
//        VideoAdapter adapter = new VideoAdapter(VideoPlayerActivity.this, videos);
//        rlVideoList.setAdapter(adapter);
    }

    private void JCVideo() {
        mJcVideoPlayerStandard = (JeyVideoPlayerStandard) findViewById(R.id.jc_video);
        mJcVideoPlayerStandard.setUp("http://v1.mukewang.com/a45016f4-08d6-4277-abe6-bcfd5244c201/L.mp4"
                , JeyVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "嫂子别摸我~~~");
        Glide.with(this)
                .load("http://img4.jiecaojingxuan.com/2016/11/23/00b026e7-b830-4994-bc87-38f4033806a6.jpg@!640_360")
                .into(mJcVideoPlayerStandard.thumbImageView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JeyVideoPlayer.releaseAllVideos();//释放所有播放的视频
    }
}
