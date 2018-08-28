package me.yangzhe.basicvideotutorial.utils;

import android.widget.Toast;

import me.yangzhe.basicvideotutorial.APP;

/**
 * Created by Jalen on 2018/8/18.
 */

public  class ToastUtil {
    public static void show(String message) {
        Toast.makeText(APP.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void show(int message) {
        Toast.makeText(APP.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
