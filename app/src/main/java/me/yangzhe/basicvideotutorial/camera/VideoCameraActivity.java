package me.yangzhe.basicvideotutorial.camera;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import me.yangzhe.basicvideotutorial.R;

public class VideoCameraActivity extends AppCompatActivity {
    private Camera mCamera = null;
    private FrameLayout mFrameLayout;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_camera);
        initView();

        mCamera = CameraFactory.getCameraInstance();
        // SurfaceView 预览
        CameraPreview cameraPreview = new CameraPreview(this, mCamera, this, mImageView);
        // TextureView 预览
//        TextureViewPreview textureViewPreview = new TextureViewPreview(this, mCamera);

        mFrameLayout.addView(cameraPreview);
    }


    private void initView() {
        mFrameLayout = findViewById(R.id.video_rootview);
        mImageView = findViewById(R.id.img_preview);
    }


}
