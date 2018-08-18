package me.yangzhe.basicvideotutorial.opengl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;

/**
 * Created by Jalen on 2018/8/19.
 */

public class Utils {
    public static boolean supportGlEs20(Context context) {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo deviceConfigurationInfo = am.getDeviceConfigurationInfo();
        final boolean supportEs2 = deviceConfigurationInfo.reqGlEsVersion >= 0x20000;
        return supportEs2;
    }
}
