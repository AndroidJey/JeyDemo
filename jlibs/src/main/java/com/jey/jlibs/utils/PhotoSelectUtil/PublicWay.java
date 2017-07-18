package com.jey.jlibs.utils.PhotoSelectUtil;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 存放所有的list在最后退出时一起关闭
 * <p/>
 * author: Kevin.Li
 * created at 2016/3/25 13:47
 */
public class PublicWay {
    public static List<Activity> activityList = new ArrayList<>();

    public static void finishActivitys() {
        if (activityList == null)
            return;
        for (Activity activity : activityList) {
            activity.finish();
        }
    }
}
