package me.yangzhe.basicvideotutorial.audio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import me.yangzhe.basicvideotutorial.R;
import me.yangzhe.basicvideotutorial.mediacodec.audio_aac.AudioCodec;
import me.yangzhe.basicvideotutorial.utils.FileUtil;
import me.yangzhe.basicvideotutorial.utils.IOUtil;
import me.yangzhe.basicvideotutorial.utils.ToastUtil;

/**
 * Author:    yangzhe
 * Version    V1.0
 * Date:      2018/9/4 下午10:58
 * Description:  音频采集、PCM、WAV、AAC 格式
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2018/9/4       yangzhe              1.0                1.0
 * Why & What is modified:
 */

public class AudioRecordActivity extends AppCompatActivity implements Runnable {
    private static final String TAG = AudioRecordActivity.class.getSimpleName();

    //指定音频源 这个和MediaRecorder是相同的 MediaRecorder.AudioSource.MIC指的是麦克风
    private static final int mAudioSource = MediaRecorder.AudioSource.MIC;
    //指定采样率 （MediaRecoder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
    private static final int mSampleRateInHz = 44100;
    //指定捕获音频的声道数目。在AudioFormat类中指定用于此的常量
    private static final int mChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
    //指定音频量化位数 ,在AudioFormaat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private int mBufferSizeInBytes;

    private File mRecordingFile;//储存AudioRecord录下来的文件
    private boolean isRecording = false; //true表示正在录音
    private AudioRecord mAudioRecord = null;// 声明 AudioRecord 对象
    private File mFileRoot = null;//文件目录
    //存放的目录路径名称
    private static final String mPathName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudiioRecordFile";
    //保存的音频文件名
    private static final String mFileName = "audiorecordtest.pcm";
    //缓冲区中数据写入到数据，因为需要使用IO操作，因此读取数据的过程应该在子线程中执行。
    private Thread mThread;

    private DataOutputStream mDataOutputStream;
    private Button mBtStartRecord;
    private Button mBtStopRecord;
    private Button mBtPlayRecord;
    private Button mBtStopPlayRecord;
    private Button mBtPlayWAV;
    private Button mBtPcm2wav;
    private Button mBtPlayAAC;
    private Button mBtPcm2aac;
    private TextView mTvEncodeProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        initData();
        initView();
        iniEvent();
    }

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecord();
            } else {
                ToastUtil.show(getString(R.string.audio_permisson_denied_tips));
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initData() {
        //初始化数据
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat);//计算最小缓冲区
        //创建AudioRecorder对象
        mAudioRecord = new AudioRecord(mAudioSource, mSampleRateInHz, mChannelConfig,
                mAudioFormat, mBufferSizeInBytes);
        //创建文件夹
        mFileRoot = new File(mPathName);
        if (!mFileRoot.exists())
            mFileRoot.mkdirs();
    }

    private void initView() {
        mBtStartRecord = findViewById(R.id.bt_start_record);
        mBtStopRecord = findViewById(R.id.bt_stop_record);
        mBtPlayRecord = findViewById(R.id.bt_play_record);
        mBtStopPlayRecord = findViewById(R.id.bt_stop_play_record);
        mBtPcm2wav = findViewById(R.id.bt_pcm2wav);
        mBtPlayWAV = findViewById(R.id.bt_play_wav);
        mBtPcm2aac = findViewById(R.id.bt_pcm2aac);
        mBtPlayAAC = findViewById(R.id.bt_play_aac);
        mTvEncodeProcess = findViewById(R.id.tv_encode_process);
    }


    private void iniEvent() {
        mBtStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AudioRecordActivity.this,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AudioRecordActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                } else {
                    startRecord();
                }
            }
        });
        mBtStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
            }
        });

        mBtPlayRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecord();
            }
        });
        mBtStopPlayRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayRecord();
            }
        });
        mBtPcm2wav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pcm2wav();
            }
        });
        mBtPlayWAV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playWav();
            }
        });

        mBtPcm2aac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pcm2aac();
            }
        });
        mBtPlayAAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAAC();
            }
        });
    }

    private void pcm2aac() {
        String path = mFileRoot + File.separator + mFileName;
        String result = path.substring(0, path.lastIndexOf(".")) + ".aac";
        final AACUtil aacUtil = AACUtil.newInstance();

        aacUtil.setIOPath(path, result);

        aacUtil.prepare();
        aacUtil.startAsync();
        aacUtil.setOnCompleteListener(new AACUtil.OnCompleteListener() {
            @Override
            public void completed() {
                aacUtil.release();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvEncodeProcess.setText("100%");
                    }
                });
            }
        });
        final DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.applyPattern("##.##%");
        aacUtil.setOnProgressListener(new AACUtil.OnProgressListener() {

            @Override
            public void progress(final long current, final long total) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvEncodeProcess.setText(current + "/" + total + "  " + df.format((double) current / total));
                    }
                });
            }
        });
    }

    private void playAAC() {
        String path = mFileRoot + File.separator + mFileName;
        String result = path.substring(0, path.lastIndexOf(".")) + ".aac";
        AudioTrackManager.getInstance().startPlay(result);
    }

    /**
     * 播放转化后的 WAV
     */
    private void playWav() {
        String path = mFileRoot + File.separator + mFileName;
        String result = path.substring(0, path.lastIndexOf(".")) + ".wav";
        AudioTrackManager.getInstance().startPlay(result);
    }

    /**
     * PCM 转化为 WAV
     */
    private void pcm2wav() {
        String path = mFileRoot + File.separator + mFileName;
        String result = path.substring(0, path.lastIndexOf(".")) + ".wav";
        WAVUtil.convertPcm2Wav(path, result, mSampleRateInHz, mChannelConfig, mAudioFormat);
    }

    /**
     * 停止播放录音
     */
    private void stopPlayRecord() {
        AudioTrackManager.getInstance().stopPlay();
    }

    /**
     * 开始播放录音
     */
    private void playRecord() {
        String path = mFileRoot + File.separator + mFileName;
        AudioTrackManager.getInstance().startPlay(path);
    }

    /**
     * 开始录音
     */
    private void startRecord() {
        //AudioRecord.getMinBufferSize的参数是否支持当前的硬件设备
        if (AudioRecord.ERROR_BAD_VALUE == mBufferSizeInBytes || AudioRecord.ERROR == mBufferSizeInBytes) {
            throw new RuntimeException("Unable to getMinBufferSize");
        } else {
            destroyThread();
            isRecording = true;
            if (mThread == null) {
                mThread = new Thread(this);
                mThread.start();//开启线程
            }
        }

    }

    /**
     * 销毁线程方法
     */
    private void destroyThread() {
        try {
            isRecording = false;
            if (null != mThread && Thread.State.RUNNABLE == mThread.getState()) {
                try {
                    Thread.sleep(500);
                    mThread.interrupt();
                } catch (Exception e) {
                    mThread = null;
                }
            }
            mThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mThread = null;
        }
    }

    /**
     * 停止录音
     */
    private void stopRecord() {
        isRecording = false;
        //停止录音，回收AudioRecord对象，释放内存
        if (mAudioRecord != null) {
            //初始化成功
            if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                mAudioRecord.stop();
            }
            if (mAudioRecord != null) {
                mAudioRecord.release();
            }
        }
    }


    @Override
    public void run() {
        //标记为开始采集状态
        isRecording = true;
        //创建文件
        createFile();

        try {

            //判断AudioRecord未初始化，停止录音的时候释放了，状态就为STATE_UNINITIALIZED
            if (mAudioRecord.getState() == mAudioRecord.STATE_UNINITIALIZED) {
                initData();
            }

            //最小缓冲区
            byte[] buffer = new byte[mBufferSizeInBytes];
            //获取到文件的数据流
            mDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mRecordingFile)));

            //开始录音
            mAudioRecord.startRecording();
            //getRecordingState获取当前AudioReroding是否正在采集数据的状态
            while (isRecording && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                int bufferReadResult = mAudioRecord.read(buffer, 0, mBufferSizeInBytes);
                for (int i = 0; i < bufferReadResult; i++) {
                    mDataOutputStream.write(buffer[i]);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Recording Failed");
        } finally {
            stopRecord();
            IOUtil.close(mDataOutputStream);
        }

    }

    private void createFile() {
        //创建一个流，存放从AudioRecord读取的数据
        mRecordingFile = new File(mFileRoot, mFileName);
        if (mRecordingFile.exists()) {//音频文件保存过了删除
            mRecordingFile.delete();
        }
        try {
            mRecordingFile.createNewFile();//创建新文件
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "创建储存音频文件出错");
        }
    }
}
