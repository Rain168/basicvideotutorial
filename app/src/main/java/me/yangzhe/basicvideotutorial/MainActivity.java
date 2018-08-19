package me.yangzhe.basicvideotutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import me.yangzhe.basicvideotutorial.image.DrawImageViewActivity;
import me.yangzhe.basicvideotutorial.audio.AudioRecordActivity;
import me.yangzhe.basicvideotutorial.mediacodec.CodecActivity;
import me.yangzhe.basicvideotutorial.opengl.TrialgleActivity;
import me.yangzhe.basicvideotutorial.camera.VideoCameraActivity;
import me.yangzhe.basicvideotutorial.video.CompileVideoActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBt_start01;
    private Button mBt_start02;
    private Button mBt_start03;
    private Button mBt_start04;
    private Button mBt_start05;
    private Button mBt_start06;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEventListener();
    }

    private void initView() {
        mBt_start01 = findViewById(R.id.bt_start01);
        mBt_start02 = findViewById(R.id.bt_start02);
        mBt_start03 = findViewById(R.id.bt_start03);
        mBt_start04 = findViewById(R.id.bt_start04);
        mBt_start05 = findViewById(R.id.bt_start05);
        mBt_start06 = findViewById(R.id.bt_start06);
    }


    private void initEventListener() {
        mBt_start01.setOnClickListener(this);
        mBt_start02.setOnClickListener(this);
        mBt_start03.setOnClickListener(this);
        mBt_start04.setOnClickListener(this);
        mBt_start05.setOnClickListener(this);
        mBt_start06.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start01:
                startActivity(new Intent(this, DrawImageViewActivity.class));
                break;
            case R.id.bt_start02:
                startActivity(new Intent(this, AudioRecordActivity.class));
                break;
            case R.id.bt_start03:
                startActivity(new Intent(this, VideoCameraActivity.class));
                break;
            case R.id.bt_start04:
                startActivity(new Intent(this, TrialgleActivity.class));
                break;
            case R.id.bt_start05:
                startActivity(new Intent(this, CompileVideoActivity.class));
                break;
            case R.id.bt_start06:
                startActivity(new Intent(this, CodecActivity.class));
                break;
            default:
                break;

        }
    }
}
