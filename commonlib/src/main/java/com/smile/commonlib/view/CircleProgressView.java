package com.smile.commonlib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleProgressView extends View {

    private final String TAG = getClass().getSimpleName();

    /**
     * 进度条画笔的宽度
     */
    private int paintProgressWidth = 3;

    /**
     * 文字百分比的字体大小（sp）
     */
    private int paintTextSize = 60;

    /**
     * 百分比文字的颜色
     */
    private int paintTextColor = 0xffff0077;

    /**
     * 未完成进度条的颜色
     */
    private int paintUndoneColor = 0xffaaaaaa;

    /**
     * 已完成进度条的颜色
     */
    private int paintDoneColor = 0xff67aae4;

    /**
     * 设置进度条画笔的宽度(px)
     */
    private int paintProgressWidthPx = 20;

    /**
     * 调用者设置的进程 0 - 100
     */
    private int progress;

    /**
     * 画未完成进度圆弧的画笔
     */
    private Paint paintUndone = new Paint();

    /**
     * 画已经完成进度条的画笔
     */
    private Paint paintDone = new Paint();

    /**
     * 画文字的画笔
     */
    private Paint paintText = new Paint();

    /**
     * 包围进度条圆弧的矩形
     */
    private RectF mRectF = new RectF();
    private Path mPathUndone;
    private Path mPathDone;

    public CircleProgressView(Context context) {
        super(context);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 未完成进度圆环的画笔的属性
        paintUndone.setColor(paintUndoneColor);
        paintUndone.setStrokeWidth(paintProgressWidthPx);
        paintUndone.setAntiAlias(true);
        paintUndone.setStyle(Paint.Style.STROKE);
        // 设置线条端点形状的方法。端点有圆头 (ROUND)、平头 (BUTT) 和方头 (SQUARE) 三种
        paintUndone.setStrokeCap(Paint.Cap.ROUND);

        // 已经完成进度条的画笔的属性
        paintDone.setColor(paintDoneColor);
        paintDone.setStrokeWidth(paintProgressWidthPx);
        paintDone.setAntiAlias(true);
        paintDone.setStyle(Paint.Style.STROKE);
        // 设置线条端点形状的方法。端点有圆头 (ROUND)、平头 (BUTT) 和方头 (SQUARE) 三种
        paintDone.setStrokeCap(Paint.Cap.ROUND);

        // 文字的画笔的属性
        paintText.setColor(paintTextColor);
        paintText.setTextSize(paintTextSize);
        paintText.setAntiAlias(true);//true：开启抗锯齿；false：关闭抗锯齿；有些情况下，开启抗锯齿反而会适得其反；
        paintText.setStyle(Paint.Style.STROKE);
        paintText.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG, "onSizeChanged: w = " + w + ", oldw = " + oldw);
        if (mRectF.left == 0) {
            int width = Math.min(getWidth(), getHeight()) / 3;
            mRectF.left = width / 2;
            mRectF.top = width / 2;
            mRectF.right = width * 2f;
            mRectF.bottom = width * 2f;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "onDraw: 开始绘制了 canvas = " + canvas);
        drawCircleArc(canvas, 2);
    }

    /**
     * 绘制圆环
     *
     * @param canvas
     * @param drawWay 有两种绘制方法，随便你选择哪一中方式
     */
    private void drawCircleArc(Canvas canvas, int drawWay) {
        if (mRectF.right - mRectF.left < 1) {
            return;
        }
        if (1 == drawWay) {
            // 第一种方式
            canvas.drawArc(mRectF, 120, 300, false, paintUndone);
            canvas.drawArc(mRectF, 120, 201, false, paintDone);
        } else if (2 == drawWay) {
            // 第二种方式
            if (mPathUndone == null) {
                mPathUndone = new Path();
            }
            mPathUndone.addArc(mRectF, 120, 300);
            canvas.drawPath(mPathUndone, paintUndone);

            if (mPathDone == null) {
                mPathDone = new Path();
            }
            mPathDone.addArc(mRectF, 120, 101);
            canvas.drawPath(mPathDone, paintDone);
        }
        canvas.drawText("33%", (mRectF.right - mRectF.left) / 3 + mRectF.left ,
                (mRectF.bottom - mRectF.top) / 2 + mRectF.top , paintText);
    }
}
