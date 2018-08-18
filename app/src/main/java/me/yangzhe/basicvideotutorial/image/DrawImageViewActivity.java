package me.yangzhe.basicvideotutorial.image;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
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
        // 自定义ImageView
//        CustomImageView viewById = findViewById(R.id.img_custom);
//        viewById.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.prettygirl));

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
