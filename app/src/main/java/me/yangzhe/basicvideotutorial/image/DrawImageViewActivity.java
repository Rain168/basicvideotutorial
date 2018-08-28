package me.yangzhe.basicvideotutorial.image;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import me.yangzhe.basicvideotutorial.R;

/**
 * 在 Android 平台绘制一张图片，使用至少 3 种不同的 API
 * 1. ImageView
 * 2.SurfaceView(推荐)
 * 3.自定义 View
 */
public class DrawImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_image_view);
        initView();
    }

    private void initView() {
        // 加载 ImageView
//        loadImageView();
        //加载自定义View
//        loadCustemImageView();
        //加载 SurfaceView
        loadSurfaceView();
        //加载 随手指移动的小球
//        loadBallView();
    }

    private void loadSurfaceView() {
        CustomSurfaceView customImageView = findViewById(R.id.sf_custom);
        customImageView.setVisibility(View.VISIBLE);
    }


    private void loadCustemImageView() {
        CustomImageView customImageView = findViewById(R.id.img_custom);
        customImageView.setBitmap(Util.getImageFromAssetsFile(this, "prettygirl.png"));
    }

    private void loadImageView() {
        // ImageView加载几种来源
        // （1） drawable/mipmap 中通过 R.drawabe.xxx 加载图片资源
        // （2） assests或者sdcard的路径的资源
        //   这里我以 assests 代表来加载资源
        ImageView customImageView = findViewById(R.id.img_middle);
        customImageView.setImageBitmap(Util.getImageFromAssetsFile(this, "prettygirl.png"));
    }

    private void loadBallView() {
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;

        BallView ballView = new BallView(this, screenWidth, screenHeigh);
        RelativeLayout root = findViewById(R.id.rl_root);
        root.addView(ballView);
    }


}
