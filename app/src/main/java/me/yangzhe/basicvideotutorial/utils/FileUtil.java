package me.yangzhe.basicvideotutorial.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Jalen on 2018/8/19.
 */

public class FileUtil {
    public static String getAbosultePath(String path) {
        //+ "avcCodec/" + File.separator
        String sdcardPath = Environment.getExternalStorageDirectory().getPath() + File.separator;
        return sdcardPath + path;
    }
}
