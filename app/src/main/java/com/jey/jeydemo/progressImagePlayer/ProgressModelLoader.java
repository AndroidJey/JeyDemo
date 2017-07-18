package com.jey.jeydemo.progressImagePlayer;

import android.os.Handler;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import java.io.InputStream;

/**
 * Created by jey on 2017/6/7.
 */

public class ProgressModelLoader implements StreamModelLoader<String> {
    private Handler handler;

    public ProgressModelLoader(Handler handler) {
        this.handler = handler;
    }
    @Override
    public DataFetcher<InputStream> getResourceFetcher(String model, int width, int height) {
        return new ProgressDataFetcher(model, handler);
    }
}
