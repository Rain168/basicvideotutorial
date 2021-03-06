package me.yangzhe.basicvideotutorial.mediacodec.audio_aac;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import me.yangzhe.basicvideotutorial.R;
import me.yangzhe.basicvideotutorial.utils.FileUtil;

/**
 * Author:    yangzhe
 * Version    V1.0
 * Date:      2018/9/4 下午11:10
 * Description:
 * Modification  History: 转码工具类
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2018/9/4       yangzhe              1.0                1.0
 * Why & What is modified:
 */
public class AudioCodecActivity extends AppCompatActivity {
    private TextView tvInfo;
    public static final String Path = Environment.getExternalStorageDirectory().getPath() + "/audioCodec/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_codec);
        initView();
    }

    private void initView() {
        tvInfo = findViewById(R.id.tv_info);
    }

    public void btnStart(View view) {
        startRecord();
    }

    private void startRecord() {
        final AudioCodec audioCodec = AudioCodec.newInstance();

        audioCodec.setIOPath(FileUtil.getAbosultePath("dream.mp3"),
                FileUtil.getAbosultePath("dream.aac"));

        audioCodec.prepare();
        audioCodec.startAsync();
        audioCodec.setOnCompleteListener(new AudioCodec.OnCompleteListener() {
            @Override
            public void completed() {
                audioCodec.release();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvInfo.setText("100%");
                    }
                });
            }
        });
        final DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.applyPattern("##.##%");
        audioCodec.setOnProgressListener(new AudioCodec.OnProgressListener() {

            @Override
            public void progress(final long current, final long total) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvInfo.setText(current + "/" + total + "  " + df.format((double) current / total));
                    }
                });
            }
        });
    }
}