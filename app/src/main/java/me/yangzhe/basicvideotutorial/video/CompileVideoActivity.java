package me.yangzhe.basicvideotutorial.video;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;

import me.yangzhe.basicvideotutorial.R;
import me.yangzhe.basicvideotutorial.utils.LogUtil;


public class CompileVideoActivity extends AppCompatActivity {

    private Button mBt_separate_mp4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compile_video);
        initView();
        initEvent();
    }

    private void initEvent() {
        mBt_separate_mp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoClip clip = new VideoClip();
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test.mp4";
                LogUtil.e("path:  " + path);
                clip.clipVideo(path, 0, 20000000);
            }
        });
    }


    private void initView() {
        mBt_separate_mp4 = findViewById(R.id.bt_separate_mp4);
    }
}
