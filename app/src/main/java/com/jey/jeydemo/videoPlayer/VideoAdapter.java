package com.jey.jeydemo.videoPlayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jey.jeydemo.R;

import java.util.List;

/**
 * Created by jey on 2017/5/22.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    List<String> mVideos;
    Context mContext;
    private VideoViewHolder lastPlayVideo=null;
    public VideoAdapter(Context context,List<String> videos) {
        mContext=context;
        mVideos=videos;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoViewHolder(View itemView) {
            super(itemView);
        }

        TextureVideoView videoView;
        ImageView imvPreview;
        ImageView imvPlay;
        ProgressBar pbWaiting;
        ProgressBar pbProgressBar;
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder viewHolder, final int position) {
        viewHolder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastPlayVideo==null)
                {
                    lastPlayVideo=viewHolder;
                }else
                {
                    if(!viewHolder.equals(lastPlayVideo))
                    {
                        lastPlayVideo.videoView.stop();
                        lastPlayVideo.pbWaiting.setVisibility(View.GONE);
                        lastPlayVideo.imvPlay.setVisibility(View.VISIBLE);
                        lastPlayVideo=viewHolder;
                    }
                }
                TextureVideoView textureView = (TextureVideoView) v;
                if(textureView.getState()== TextureVideoView.MediaState.INIT||textureView.getState()== TextureVideoView.MediaState.RELEASE)
                {
                    textureView.play(mVideos.get(position));
                    viewHolder.pbWaiting.setVisibility(View.VISIBLE);
                    viewHolder.imvPlay.setVisibility(View.GONE);
                }else if(textureView.getState()== TextureVideoView.MediaState.PAUSE)
                {
                    textureView.start();
                    viewHolder.pbWaiting.setVisibility(View.GONE);
                    viewHolder.imvPlay.setVisibility(View.GONE);
                }else if(textureView.getState()== TextureVideoView.MediaState.PLAYING)
                {
                    textureView.pause();
                    viewHolder.pbWaiting.setVisibility(View.GONE);
                    viewHolder.imvPlay.setVisibility(View.VISIBLE);
                }else if(textureView.getState()== TextureVideoView.MediaState.PREPARING)
                {
                    textureView.stop();
                    viewHolder.pbWaiting.setVisibility(View.GONE);
                    viewHolder.imvPlay.setVisibility(View.VISIBLE);
                }
            }
        });
        viewHolder.videoView.setOnStateChangeListener(new TextureVideoView.OnStateChangeListener() {
            @Override
            public void onSurfaceTextureDestroyed(SurfaceTexture surface) {
                viewHolder.videoView.stop();
                viewHolder.pbWaiting.setVisibility(View.GONE);
                viewHolder.imvPlay.setVisibility(View.VISIBLE);
                viewHolder.pbProgressBar.setMax(1);
                viewHolder.pbProgressBar.setProgress(0);
            }

            @Override
            public void onPlaying() {
                viewHolder.pbWaiting.setVisibility(View.GONE);
                viewHolder.imvPlay.setVisibility(View.GONE);
            }

            @Override
            public void onBuffering() {
                viewHolder.pbWaiting.setVisibility(View.VISIBLE);
                viewHolder.imvPlay.setVisibility(View.GONE);
            }

            @Override
            public void onSeek(int max,int progress){
                viewHolder.pbProgressBar.setMax(max);
                viewHolder.pbProgressBar.setProgress(progress);
            }

            @Override
            public void onStop() {
                viewHolder.pbProgressBar.setMax(1);
                viewHolder.pbProgressBar.setProgress(0);
            }
        });
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup root, int position) {
        View containerView= LayoutInflater.from(mContext).inflate(R.layout.videoitem, root, false);
        VideoViewHolder videoViewHolder=new VideoViewHolder(containerView);
        videoViewHolder.videoView=(TextureVideoView) containerView.findViewById(R.id.textureview);
        videoViewHolder.imvPreview=(ImageView) containerView.findViewById(R.id.imv_preview);
        videoViewHolder.imvPlay=(ImageView) containerView.findViewById(R.id.imv_video_play);
        videoViewHolder.pbWaiting=(ProgressBar) containerView.findViewById(R.id.pb_waiting);
        videoViewHolder.pbProgressBar=(ProgressBar) containerView.findViewById(R.id.progress_progressbar);
        return videoViewHolder;
    }
}
