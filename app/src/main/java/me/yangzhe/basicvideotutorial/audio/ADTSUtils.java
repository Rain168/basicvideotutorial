package me.yangzhe.basicvideotutorial.audio;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:    yangzhe
 * Version    V1.0
 * Date:      2018/9/4 下午11:08
 * Description:
 * Modification  History:  添加ADTS头
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2018/9/4       yangzhe              1.0                1.0
 * Why & What is modified:
 */
public class ADTSUtils {
    private static Map<String, Integer> SAMPLE_RATE_TYPE;

    static {
        SAMPLE_RATE_TYPE = new HashMap<>();
        SAMPLE_RATE_TYPE.put("96000", 0);
        SAMPLE_RATE_TYPE.put("88200", 1);
        SAMPLE_RATE_TYPE.put("64000", 2);
        SAMPLE_RATE_TYPE.put("48000", 3);
        SAMPLE_RATE_TYPE.put("44100", 4);
        SAMPLE_RATE_TYPE.put("32000", 5);
        SAMPLE_RATE_TYPE.put("24000", 6);
        SAMPLE_RATE_TYPE.put("22050", 7);
        SAMPLE_RATE_TYPE.put("16000", 8);
        SAMPLE_RATE_TYPE.put("12000", 9);
        SAMPLE_RATE_TYPE.put("11025", 10);
        SAMPLE_RATE_TYPE.put("8000", 11);
        SAMPLE_RATE_TYPE.put("7350", 12);
    }

    public static int getSampleRateType(int sampleRate) {
        return SAMPLE_RATE_TYPE.get(sampleRate + "");
    }
}