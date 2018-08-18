package me.yangzhe.basicvideotutorial.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import me.yangzhe.basicvideotutorial.audio.LogUtil;

/**
 * A basic Camera preview class
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private VideoCameraActivity mActivity;


    private ImageView icon;
    private ByteArrayOutputStream mBaos;
    private byte[] mImageBytes;
    private Bitmap mBitmap;
    private Camera.Size mPreviewSize;//预览尺寸大小


    public CameraPreview(Context context, Camera camera, VideoCameraActivity activity, ImageView imageView) {
        super(context);
        mActivity = activity;
        mCamera = camera;
        this.icon = imageView;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //设置常量
        setKeepScreenOn(true);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mCamera.setDisplayOrientation(90);
        } catch (IOException e) {
            LogUtil.e("Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        doChange(holder);
    }


    //当我们的程序开始运行，即使我们没有开始录制视频，我们的surFaceView中也要显示当前摄像头显示的内容
    private void doChange(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);//设置摄像机的预览界面  一般都是与SurfaceView#SurfaceHolder进行绑定
            //设置surfaceView旋转的角度，系统默认的录制是横向的画面
            mCamera.setDisplayOrientation(getDegree());

            if (mCamera != null) {
                try {
                    Camera.Parameters parameters = mCamera.getParameters(); //获取摄像头参数
                    parameters.setPreviewFormat(ImageFormat.NV21);
//                    可以根据情况设置参数
//                    parameters.setZoom();  //镜头缩放
                    // 设置预览照片的大小
//                    parameters.setPreviewSize(200, 200);
                    // 设置预览照片时每秒显示多少帧的最小值和最大值
//                    parameters.setPreviewFpsRange(4, 10);
                    // 设置图片格式
//                    parameters.setPictureFormat(ImageFormat.JPEG);
                    // 设置JPG照片的质量  图片的质量[0-100],100最高
//                    parameters.set("jpeg-quality", 85);
                    // 设置照片的大小
//                    parameters.setPictureSize(200, 200);
                    mCamera.setParameters(parameters);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("lu", "Camera设置的参数错误:" + e.getMessage());
                }
            }

            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    //处理data
                    mPreviewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
                    //取发YUVIMAGE
                    YuvImage yuvimage = new YuvImage(
                            data,
                            ImageFormat.NV21,
                            mPreviewSize.width,
                            mPreviewSize.height,
                            null);
                    mBaos = new ByteArrayOutputStream();
                    //yuvimage 转换成jpg格式
                    yuvimage.compressToJpeg(new Rect(0, 0, mPreviewSize.width, mPreviewSize.height), 100, mBaos);// 80--JPG图片的质量[0-100],100最高
                    mImageBytes = mBaos.toByteArray();

                    //将mImageBytes转换成bitmap
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;

                    mBitmap = BitmapFactory.decodeByteArray(mImageBytes, 0, mImageBytes.length, options);
//                    icon.setImageBitmap(mBitmap);
                    icon.setImageBitmap(rotateBitmap(mBitmap, getDegree()));
                }
            });
            mCamera.startPreview();//开始预览

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("lu", "Camera预览变化错误:" + e.getMessage());
        }
    }

    private int getDegree() {
        //获取当前屏幕旋转的角度
        int rotating = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;//度数
        //根据手机旋转的角度，来设置surfaceView的显示的角度
        switch (rotating) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }


    /**
     * 选择变换
     *
     * @param origin 原图
     * @param degree 旋转角度，可正可负
     * @return 旋转后的图片
     */
    private Bitmap rotateBitmap(Bitmap origin, float degree) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

}