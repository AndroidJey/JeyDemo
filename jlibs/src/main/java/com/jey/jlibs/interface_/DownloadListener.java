package com.jey.jlibs.interface_;

/**
 * Created by superchen on 2017/2/14.
 */

public interface DownloadListener {

    void onStart();

    void onFailed(Exception e);

    void onCompleted();
}
