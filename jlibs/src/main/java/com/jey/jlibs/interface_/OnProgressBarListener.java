package com.jey.jlibs.interface_;


/**
 * Created by lelexxx on 15-4-23.
 */
public interface OnProgressBarListener extends DownloadListener {

    void onProgressChange(int current, int max);
}
