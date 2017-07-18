package com.jey.jeydemo.videoPlayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

/**
 * Created by jey on 2017/5/22.
 */

public class TextureVideoView extends TextureView implements TextureView.SurfaceTextureListener {
    private MediaPlayer mediaPlayer;
    private Context context;
    MediaState mediaState;
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public interface OnStateChangeListener{
        public void onSurfaceTextureDestroyed(SurfaceTexture surface);
        public void onBuffering();
        public void onPlaying();
        public void onSeek(int max,int progress);
        public void onStop();
    }
    OnStateChangeListener onStateChangeListener;
    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    //监听视频的缓冲状态
    private MediaPlayer.OnInfoListener onInfoListener=new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if(onStateChangeListener!=null)
            {
                onStateChangeListener.onPlaying();
                if(what==MediaPlayer.MEDIA_INFO_BUFFERING_START)
                {
                    onStateChangeListener.onBuffering();
                }else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    onStateChangeListener.onPlaying();
                }
            }
            return false;
        }
    };

    //视频缓冲进度更新
    private MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener=new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if(onStateChangeListener!=null)
            {
                if(mediaState==MediaState.PLAYING)
                {
                    onStateChangeListener.onSeek(mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition());
                }
            }
        }
    };

    public TextureVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void init() {
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width,
                                          int height) {
        Surface surface = new Surface(surfaceTexture);
        if(mediaPlayer==null)
        {
            if(mediaPlayer == null){
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    mediaState=MediaState.PLAYING;
                }
            });
            mediaPlayer.setOnInfoListener(onInfoListener);
            mediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
        }
        mediaPlayer.setSurface(surface);
        mediaState=MediaState.INIT;
    }

    //停止播放
    public void stop()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(mediaState==MediaState.INIT)
                    {
                        return;
                    }
                    if(mediaState==MediaState.PREPARING)
                    {
                        mediaPlayer.reset();
                        mediaState=MediaState.INIT;
                        System.out.println("prepare->reset");
                        return;
                    }
                    if(mediaState==MediaState.PAUSE)
                    {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaState=MediaState.INIT;
                        System.out.println("pause->init");
                        return ;
                    }
                    if(mediaState==MediaState.PLAYING)
                    {
                        mediaPlayer.pause();
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaState=MediaState.INIT;
                        System.out.println("playing->init");
                        return ;
                    }
                } catch (Exception e) {
                    mediaPlayer.reset();
                    mediaState=MediaState.INIT;
                }finally{
                    if(onStateChangeListener!=null)
                    {
                        onStateChangeListener.onStop();
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if(onStateChangeListener!=null)
        {
            onStateChangeListener.onSurfaceTextureDestroyed(surface);
        }
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                            int height) {
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    //开始播放视频
    public void play(String videoUrl) {
        if(mediaState==MediaState.PREPARING){
            stop();
            return;
        }
        mediaPlayer.reset();
        mediaPlayer.setLooping(true);
        try {
            mediaPlayer.setDataSource(videoUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        mediaState=MediaState.PREPARING;
    }

    //暂停播放
    public void pause()
    {
        mediaPlayer.pause();
        mediaState=MediaState.PAUSE;
    }

    //播放视频
    public void start()
    {
        mediaPlayer.start();
        mediaState=MediaState.PLAYING;
    }
    //状态（初始化、正在准备、正在播放、暂停、释放）
    public enum MediaState
    {
        INIT,PREPARING,PLAYING,PAUSE,RELEASE;
    }
    //获取播放状态
    public MediaState getState()
    {
        return mediaState;
    }
}
