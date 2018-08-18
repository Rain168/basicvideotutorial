package me.yangzhe.basicvideotutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import me.yangzhe.basicvideotutorial.image.DrawImageViewActivity;
import me.yangzhe.basicvideotutorial.audio.AudioRecordActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBt_start01;
    private Button mBt_start02;
    private Button mBt_start03;

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
    }


    private void initEventListener() {
        mBt_start01.setOnClickListener(this);
        mBt_start02.setOnClickListener(this);
        mBt_start03.setOnClickListener(this);
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
                break;
            default:
                break;

        }
    }
}
