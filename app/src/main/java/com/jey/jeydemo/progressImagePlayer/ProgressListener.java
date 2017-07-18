package com.jey.jeydemo.progressImagePlayer;

/**
 * Created by jey on 2017/6/7.
 */

public interface ProgressListener {

    void progress(long bytesRead, long contentLength, boolean done);

}
