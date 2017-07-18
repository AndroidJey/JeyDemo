package com.jey.jeydemo.videoPlayer.JeyVideo;

/**
 * Put JCVideoPlayer into layout
 * From a JCVideoPlayer to another JCVideoPlayer
 * Created by Jey
 * On 2017/05/02 00:01
 */
public class JeyVideoPlayerManager {

    public static JeyVideoPlayer FIRST_FLOOR_JCVD;
    public static JeyVideoPlayer SECOND_FLOOR_JCVD;

    public static void setFirstFloor(JeyVideoPlayer jeyVideoPlayer) {
        FIRST_FLOOR_JCVD = jeyVideoPlayer;
    }

    public static void setSecondFloor(JeyVideoPlayer jeyVideoPlayer) {
        SECOND_FLOOR_JCVD = jeyVideoPlayer;
    }

    public static JeyVideoPlayer getFirstFloor() {
        return FIRST_FLOOR_JCVD;
    }

    public static JeyVideoPlayer getSecondFloor() {
        return SECOND_FLOOR_JCVD;
    }

    public static JeyVideoPlayer getCurrentJcvd() {
        if (getSecondFloor() != null) {
            return getSecondFloor();
        }
        return getFirstFloor();
    }

    public static void completeAll() {
        if (SECOND_FLOOR_JCVD != null) {
            SECOND_FLOOR_JCVD.onCompletion();
            SECOND_FLOOR_JCVD = null;
        }
        if (FIRST_FLOOR_JCVD != null) {
            FIRST_FLOOR_JCVD.onCompletion();
            FIRST_FLOOR_JCVD = null;
        }
    }
}
