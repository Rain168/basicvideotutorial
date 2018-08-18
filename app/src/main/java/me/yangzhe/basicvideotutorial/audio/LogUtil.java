package me.yangzhe.basicvideotutorial.audio;

import android.util.Log;

/**
 * Created by Jalen on 2018/8/18.
 */

public class LogUtil {
    public static final String TAG = LogUtil.class.getSimpleName();

    public static void e(String message) {
        Log.e(TAG, message);
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }
}
