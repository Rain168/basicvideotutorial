package me.yangzhe.basicvideotutorial.mediacodec;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import me.yangzhe.basicvideotutorial.R;
import me.yangzhe.basicvideotutorial.mediacodec.audio_aac.AudioCodecActivity;

public class CodecActivity extends AppCompatActivity {
    private Button mBtH264Decode;
    private Button mBtH264Encode;
    private Button mBtAACEncode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codec);
        initView();
        initEvent();
    }


    private void initView() {
        mBtH264Encode = findViewById(R.id.bt_h264_encode);
        mBtH264Decode = findViewById(R.id.bt_h264_decode);
        mBtAACEncode = findViewById(R.id.bt_aac_decode);
    }

    private void initEvent() {
        mBtH264Encode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CodecActivity.this, MediaCodecActivity.class));
            }

        });
        mBtH264Decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CodecActivity.this, DecodeActivity.class));
            }
        });
        mBtAACEncode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CodecActivity.this, AudioCodecActivity.class));
            }
        });
    }
}
