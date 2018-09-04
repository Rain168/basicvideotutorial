package me.yangzhe.basicvideotutorial.audio;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import me.yangzhe.basicvideotutorial.utils.LogUtil;

/**
 * Author:    yangzhe
 * Version    V1.0
 * Date:      2018/9/4 下午11:11
 * Description: 音频文件转化为AAC格式文件
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2018/9/4       yangzhe              1.0                1.0
 * Why & What is modified:
 */
public class AACUtil {
    private String srcPath;
    private String dstPath;
    private MediaCodec mediaDecode;
    private MediaCodec mediaEncode;
    private MediaExtractor mediaExtractor;

    private ByteBuffer[] encodeInputBuffers;
    private ByteBuffer[] encodeOutputBuffers;
    private MediaCodec.BufferInfo encodeBufferInfo;
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private OnCompleteListener onCompleteListener;
    private OnProgressListener onProgressListener;
    private long fileTotalSize;

    private ArrayBlockingQueue<byte[]> queue;


    public static AACUtil newInstance() {
        return new AACUtil();
    }

    /**
     * 设置输入输出文件位置
     *
     * @param srcPath
     * @param dstPath
     */
    public void setIOPath(String srcPath, String dstPath) {
        this.srcPath = srcPath;
        this.dstPath = dstPath;
    }

    /**
     * 此类已经过封装
     * 调用prepare方法 会初始化Decode 、Encode 、输入输出流 等一些列操作
     */
    public void prepare() {

        if (srcPath == null) {
            throw new IllegalArgumentException("srcPath can't be null");
        }

        if (dstPath == null) {
            throw new IllegalArgumentException("dstPath can't be null");
        }

        try {
            fos = new FileOutputStream(new File(dstPath));
            bos = new BufferedOutputStream(fos, 200 * 1024);
            File file = new File(srcPath);
            fileTotalSize = file.length();
        } catch (IOException e) {
            e.printStackTrace();
        }
        queue = new ArrayBlockingQueue<byte[]>(10);

        initAACMediaEncode();//AAC编码器
    }

    private static final int samples_per_frame = 2048;
    private static final int mSampleRateInHz = 44100;
    private static final int mChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
    /**
     * 初始化AAC编码器
     */
    private void initAACMediaEncode() {
        try {

            MediaFormat encodeFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, mSampleRateInHz, mChannelConfig);
            encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64000);//比特率
            encodeFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, mChannelConfig);
            encodeFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
            encodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, samples_per_frame);//作用于inputBuffer的大小

            mediaEncode = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            mediaEncode.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaEncode == null) {
            LogUtil.e("create mediaEncode failed");
            return;
        }
        mediaEncode.start();
        encodeInputBuffers = mediaEncode.getInputBuffers();
        encodeOutputBuffers = mediaEncode.getOutputBuffers();
        encodeBufferInfo = new MediaCodec.BufferInfo();
    }

    private boolean codeOver = false;

    /**
     * 开始转码
     * 音频数据{@link #srcPath}先解码成PCM  PCM数据在编码成MediaFormat.MIMETYPE_AUDIO_AAC音频格式
     * mp3->PCM->aac
     */
    public void startAsync() {
        LogUtil.w("start");

        new Thread(new DecodeRunnable()).start();
        new Thread(new EncodeRunnable()).start();

    }

    /**
     * 将PCM数据存入队列
     *
     * @param pcmChunk PCM数据块
     */
    private void putPCMData(byte[] pcmChunk) {
        try {
            LogUtil.d("put queue size:" + queue.size());
            queue.put(pcmChunk);
        } catch (InterruptedException e) {
            e.printStackTrace();
            LogUtil.e("queue put error");
        }
    }

    /**
     * 在Container中队列取出PCM数据
     *
     * @return PCM数据块
     */
    private byte[] getPCMData() {
        try {
            if (queue.isEmpty()) {
                return null;
            }
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解码{@link #srcPath}音频文件 得到PCM数据块
     *
     * @return 是否解码完所有数据
     */
    private void srcAudioFormatToPCM() {
        File file = new File(srcPath);// 指定要读取的文件
        FileInputStream fio = null;
        try {
            fio = new FileInputStream(file);
            byte[] bb = new byte[samples_per_frame];
            while (!codeOver) {
                if (fio.read(bb) != -1) {
                    LogUtil.e("============   putPCMData ============" + bb.length);
                    putPCMData(bb);
                } else {
                    codeOver = true;
                }
            }

            fio.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 编码PCM数据 得到MediaFormat.MIMETYPE_AUDIO_AAC格式的音频文件，并保存到{@link #dstPath}
     */
    private void dstAudioFormatFromPCM() {

        int inputIndex;
        ByteBuffer inputBuffer;
        int outputIndex;
        ByteBuffer outputBuffer;
        byte[] chunkAudio;
        int outBitSize;
        int outPacketSize;
        byte[] chunkPCM;

        for (int i = 0; i < encodeInputBuffers.length - 1; i++) {
            chunkPCM = getPCMData();//获取解码器所在线程输出的数据 代码后边会贴上
            if (chunkPCM == null) {
                break;
            }
            inputIndex = mediaEncode.dequeueInputBuffer(-1);//同解码器
            inputBuffer = encodeInputBuffers[inputIndex];//同解码器
            inputBuffer.clear();//同解码器
            inputBuffer.limit(chunkPCM.length);
            inputBuffer.put(chunkPCM);//PCM数据填充给inputBuffer
            mediaEncode.queueInputBuffer(inputIndex, 0, chunkPCM.length, 0, 0);//通知编码器 编码
        }

        outputIndex = mediaEncode.dequeueOutputBuffer(encodeBufferInfo, 10000);//同解码器
        while (outputIndex >= 0) {//同解码器

            outBitSize = encodeBufferInfo.size;
            outPacketSize = outBitSize + 7;//7为ADTS头部的大小
            outputBuffer = encodeOutputBuffers[outputIndex];//拿到输出Buffer
            outputBuffer.position(encodeBufferInfo.offset);
            outputBuffer.limit(encodeBufferInfo.offset + outBitSize);
            chunkAudio = new byte[outPacketSize];
            addADTStoPacket(chunkAudio, outPacketSize);//添加ADTS 代码后面会贴上
            outputBuffer.get(chunkAudio, 7, outBitSize);//将编码得到的AAC数据 取出到byte[]中 偏移量offset=7 你懂得
            outputBuffer.position(encodeBufferInfo.offset);
            try {
                bos.write(chunkAudio, 0, chunkAudio.length);//BufferOutputStream 将文件保存到内存卡中 *.aac
                LogUtil.d("write " + chunkAudio.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaEncode.releaseOutputBuffer(outputIndex, false);
            outputIndex = mediaEncode.dequeueOutputBuffer(encodeBufferInfo, 10000);

        }
    }

    /**
     * 添加ADTS头
     *
     * @param packet
     * @param packetLen
     */
    private void addADTStoPacket(byte[] packet, int packetLen) {
//        int profile = 2; // AAC LC
//        int freqIdx = sampleRateType; // 44.1KHz
//        int chanCfg = 2; // CPE
//
//
//// fill in ADTS data
//        packet[0] = (byte) 0xFF;
//        packet[1] = (byte) 0xF9;
//        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
//        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
//        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
//        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
//        packet[6] = (byte) 0xFC;

        int profile = 2; // AAC LC
        int freqIdx = mSampleRateInHz; // 16KHz
        int chanCfg = 1; // CPE

        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF1;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    /**
     * 释放资源
     */
    public void release() {
        try {
            if (bos != null) {
                bos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    bos = null;
                }
            }
        }

        try {
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fos = null;
        }

        if (mediaEncode != null) {
            mediaEncode.stop();
            mediaEncode.release();
            mediaEncode = null;
        }

        if (mediaDecode != null) {
            mediaDecode.stop();
            mediaDecode.release();
            mediaDecode = null;
        }

        if (mediaExtractor != null) {
            mediaExtractor.release();
            mediaExtractor = null;
        }

        if (onCompleteListener != null) {
            onCompleteListener = null;
        }

        if (onProgressListener != null) {
            onProgressListener = null;
        }
        LogUtil.w("release");
    }

    /**
     * 解码线程
     */
    private class DecodeRunnable implements Runnable {

        @Override
        public void run() {
//            while (!codeOver) {
            srcAudioFormatToPCM();
//            }
        }
    }

    /**
     * 编码线程
     */
    private class EncodeRunnable implements Runnable {

        @Override
        public void run() {
            long t = System.currentTimeMillis();
            while (!codeOver || !queue.isEmpty()) {
                dstAudioFormatFromPCM();
            }
            if (onCompleteListener != null) {
                onCompleteListener.completed();
            }
            LogUtil.w("size:" + fileTotalSize  + "time:" + (System.currentTimeMillis() - t));
        }
    }


    /**
     * 转码完成回调接口
     */
    public interface OnCompleteListener {
        void completed();
    }

    /**
     * 转码进度监听器
     */
    public interface OnProgressListener {
        void progress(long current, long total);
    }

    /**
     * 设置转码完成监听器
     *
     * @param onCompleteListener
     */
    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

}