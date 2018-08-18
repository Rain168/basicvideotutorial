package me.yangzhe.basicvideotutorial.image;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * 自定义View 入门 小球滑动
 */
public class BallView extends View {
    private Paint mPaint;
    private float cx = 30;//x 坐标
    private float cy = 30;//y 坐标
    private float radius = 60;//小球半径

    // 自定义颜色数组
    private int[] clolorArray = {Color.BLACK, Color.RED, Color.GREEN};
    // 默认画笔颜色
    private int paintColor = clolorArray[0];

    private int screenW;//屏幕宽度
    private int screenH;// 屏幕高度

    public BallView(Context context, int screenW, int screenH) {
        super(context);


        this.screenW = screenW;
        this.screenH = screenH;
        iniPaint();
    }

    private void iniPaint() {
        mPaint = new Paint();
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        //设置画笔颜色
        mPaint.setColor(paintColor);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置屏幕为白色
        canvas.drawColor(Color.WHITE);
        //修正坐标
        revise();
        //随机设置颜色
        setPaintRandomColor();
        //绘制圆球
        canvas.drawCircle(cx, cy, radius, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cx = event.getX();
                cy = event.getY();
                postInvalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                cx = event.getX();
                cy = event.getY();
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                cx = event.getX();
                cy = event.getY();
                postInvalidate();
                break;
        }
        return true;
//        return super.onTouchEvent(event);
    }

    private void setPaintRandomColor() {
        Random random = new Random();
        paintColor = clolorArray[random.nextInt(clolorArray.length)];
        mPaint.setColor(paintColor);
    }

    private void revise() {
        if (cx < radius) {
            cx = radius;
        } else if (cx > (screenW - radius)) {
            cx = screenW - radius;
        }

        if (cy < radius) {
            cy = radius;
        } else if (cy > (screenH - radius)) {
            cy = screenH - radius;
        }
    }
}
