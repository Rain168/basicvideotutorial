package me.yangzhe.basicvideotutorial.audio;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.io.FileInputStream;

import me.yangzhe.basicvideotutorial.utils.LogUtil;

/**
 * Author:    yangzhe
 * Version    V1.0
 * Date:      2018/9/6 下午10:57
 * Description: 音频信息
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2018/9/6       yangzhe              1.0                1.0
 * Why & What is modified:
 */
public class AudioFormatUtil {
    /**
     * 获取 音频的信息
     *
     * @param musicFileUrl
     * @param decodeFileUrl
     * @param startMicroseconds
     * @param endMicroseconds
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static boolean getFormatInfo(String musicFileUrl, String decodeFileUrl,
                                        long startMicroseconds, long endMicroseconds) {
        //采样率，声道数，时长，音频文件类型
        int sampleRate = 0;
        int channelCount = 0;
        long duration = 0;
        String mime = null;

        //MediaExtractor, MediaFormat, MediaCodec
        MediaExtractor mediaExtractor = new MediaExtractor();
        MediaFormat mediaFormat = null;
        MediaCodec mediaCodec = null;

        //给媒体信息提取器设置源音频文件路径
        try {
            LogUtil.e("musicFileUrl:   " + musicFileUrl);
            mediaExtractor.setDataSource(musicFileUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                mediaExtractor.setDataSource(new FileInputStream(musicFileUrl).getFD());
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("设置解码音频文件路径错误");
            }
        }

        //获取音频格式轨信息
        mediaFormat = mediaExtractor.getTrackFormat(0);

        //从音频格式轨信息中读取 采样率，声道数，时长，音频文件类型
        sampleRate = mediaFormat.containsKey(MediaFormat.KEY_SAMPLE_RATE) ? mediaFormat.getInteger(
                MediaFormat.KEY_SAMPLE_RATE) : 44100;
        channelCount = mediaFormat.containsKey(MediaFormat.KEY_CHANNEL_COUNT) ? mediaFormat.getInteger(
                MediaFormat.KEY_CHANNEL_COUNT) : 1;
        duration = mediaFormat.containsKey(MediaFormat.KEY_DURATION) ? mediaFormat.getLong(
                MediaFormat.KEY_DURATION) : 0;
        mime = mediaFormat.containsKey(MediaFormat.KEY_MIME) ? mediaFormat.getString(MediaFormat.KEY_MIME)
                : "";

        LogUtil.i("歌曲信息Track info: mime:"
                + mime
                + " 采样率sampleRate:"
                + sampleRate
                + " channels:"
                + channelCount
                + " duration:"
                + duration);

        if (TextUtils.isEmpty(mime) || !mime.startsWith("audio/")) {
            LogUtil.e("解码文件不是音频文件mime:" + mime);
            return false;
        }

        if (mime.equals("audio/ffmpeg")) {
            mime = "audio/mpeg";
            mediaFormat.setString(MediaFormat.KEY_MIME, mime);
        }

        if (duration <= 0) {
            LogUtil.e("音频文件duration为" + duration);
            return false;
        }

        //解码的开始时间和结束时间
        startMicroseconds = Math.max(startMicroseconds, 0);
        endMicroseconds = endMicroseconds < 0 ? duration : endMicroseconds;
        endMicroseconds = Math.min(endMicroseconds, duration);

        if (startMicroseconds >= endMicroseconds) {
            return false;
        }

        //创建一个解码器
        try {
            mediaCodec = MediaCodec.createDecoderByType(mime);

            mediaCodec.configure(mediaFormat, null, null, 0);
        } catch (Exception e) {
            LogUtil.e("解码器configure出错");
            return false;
        }

        //得到输出PCM文件的路径
//        decodeFileUrl = decodeFileUrl.substring(0, decodeFileUrl.lastIndexOf("."));
//        String pcmFilePath = decodeFileUrl + ".pcm";
        return true;
    }
}
