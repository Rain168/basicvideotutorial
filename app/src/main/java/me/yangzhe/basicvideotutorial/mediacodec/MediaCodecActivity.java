package me.yangzhe.basicvideotutorial.mediacodec;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.yangzhe.basicvideotutorial.R;

public class MediaCodecActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String TAG = MediaCodecActivity.class.getSimpleName();


    AvcEncoder avcCodec;
    public Camera m_camera;
    SurfaceView m_prevewview;
    SurfaceHolder m_surfaceHolder;
    int width = 1280;
    int height = 720;
    int framerate = 15;
    int bitrate = 125000;
    //int bitRate = camera.getFpsRange()[1] * currentSize.width * currentSize.height / 15;

    byte[] h264 = new byte[width * height * 3 / 2];

    private FileOutputStream file = null;
    private String filename = "camera.h264";
    private int byteOffset = 0;
    private long lastTime = 0;

    private String mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.activity_media_codec);
        initEvent();
    }

    private void initData() {
        mImagePath = Environment.getExternalStorageDirectory().getPath() + "/avcCodec/";
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectAll()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }


    private void initEvent() {
        Log.d("ws", "width=" + width + ", height=" + height + ", framerate=" + framerate + ", bitrate=" + bitrate);
        try {
            //初始化编码器，在有的机器上失败 fuck!
            avcCodec = new AvcEncoder(width, height, framerate, bitrate);
        } catch (IOException e1) {
            Log.d("Fuck", "Fail to AvcEncoder");
        }
        m_prevewview = findViewById(R.id.sf_mediacodec);
        m_surfaceHolder = m_prevewview.getHolder();
        m_surfaceHolder.setFixedSize(width, height);
        m_surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        m_surfaceHolder.addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            m_camera = Camera.open();
            m_camera.setPreviewDisplay(m_surfaceHolder);
            Camera.Parameters parameters = m_camera.getParameters();
            parameters.setPreviewSize(width, height);
            parameters.setPictureSize(width, height);
            parameters.setPreviewFormat(ImageFormat.YV12);
            parameters.set("rotation", 90);
            //parameters.set("orientation", "portrait");
            m_camera.setParameters(parameters);
            m_camera.setDisplayOrientation(90);
            m_camera.setPreviewCallback((Camera.PreviewCallback) this);
            m_camera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            File fileFolder = new File(mImagePath);
            if (!fileFolder.exists())
                fileFolder.mkdirs();
            File files = new File(mImagePath, filename);
            if (!files.exists()) {
                Log.e(TAG, "file create success ");
                files.createNewFile();
            }
            file = new FileOutputStream(files);
            Log.e(TAG, "file save success ");
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        m_camera.setPreviewCallback(null);
        m_camera.release();
        m_camera = null;
        avcCodec.close();
        try {
            file.flush();
            file.close();
        } catch (IOException e) {
            Log.d("Fuck", "File close error");
            e.printStackTrace();
        }
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        long newTime = System.currentTimeMillis();
        long diff = newTime - lastTime;
        lastTime = newTime;
        //把摄像头的数据传给编码器
        int ret = avcCodec.offerEncoder(data, h264);

        if (ret > 0) {
            try {
                byte[] length_bytes = intToBytes(ret);
                file.write(length_bytes);
                file.write(h264, 0, ret);

            } catch (IOException e) {
                Log.d("ws", "exception: " + e.toString());
            }
        }

    }

    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }
}
