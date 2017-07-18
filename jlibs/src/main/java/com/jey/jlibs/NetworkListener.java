package com.jey.jlibs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jey.jlibs.base.AsyHttp;
import com.jey.jlibs.base.BroadcastCenter;
import com.jey.jlibs.utils.CommonFunction;


public class NetworkListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        int connected = CommonFunction.getNetworkAvailableType(context);
        if (connected != -1)
            AsyHttp.setNetworkConnnected(true);
        else
            AsyHttp.setNetworkConnnected(false);

        BroadcastCenter.publish(BroadcastCenter.TITLE.NETWORKCHANGED, connected);
    }
}