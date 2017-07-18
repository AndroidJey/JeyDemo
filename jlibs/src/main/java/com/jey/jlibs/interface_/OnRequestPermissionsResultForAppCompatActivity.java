package com.jey.jlibs.interface_;

import android.support.annotation.NonNull;

/**
 * Created by sevnce on 16/9/26.
 */

public interface OnRequestPermissionsResultForAppCompatActivity {

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
