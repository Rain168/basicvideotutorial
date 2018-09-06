package me.yangzhe.basicvideotutorial.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Author:    yangzhe
 * Version    V1.0
 * Date:      2018/9/5 下午10:42
 * Description: IO 关闭工具类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2018/9/5       yangzhe              1.0                1.0
 * Why & What is modified:
 */

public class IOUtil {
    public static void close(Closeable... closeableList) {
        try {
            for (Closeable closeable : closeableList) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
