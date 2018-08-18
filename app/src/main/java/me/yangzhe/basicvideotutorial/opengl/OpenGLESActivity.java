package me.yangzhe.basicvideotutorial.opengl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class OpenGLESActivity extends AppCompatActivity {
    // 记住GLSurfaceView 的有效状态
    private boolean redererSet = false;
    private GLSurfaceView mGlSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        checkOpenGlVertion();
    }

    private void checkOpenGlVertion() {
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo deviceConfigurationInfo = am.getDeviceConfigurationInfo();
        final boolean supportEs2 = deviceConfigurationInfo.reqGlEsVersion >= 0x20000;

        if (supportEs2) {
            //请求编译为 opengGlES 2.0 版本
            mGlSurfaceView.setEGLContextClientVersion(2);
            //注入自己的渲染器
            mGlSurfaceView.setRenderer(new FirstOpenglRenderer());
            redererSet = true;
        } else {
            Toast.makeText(this, "不支持OpenGLES 2.0", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (redererSet) {
            mGlSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (redererSet) {
            mGlSurfaceView.onResume();
        }
    }

    private void initView() {
        mGlSurfaceView = new GLSurfaceView(this);
        setContentView(mGlSurfaceView);
    }
}
