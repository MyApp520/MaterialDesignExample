package com.smile.commonlib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/*
    Path原理：
            1、首先是确定坐标点；
            2、然后path把这些点连成线；

    view的坐标原点：
            1、view的左上角，值为(0, 0)；

    起点坐标：Path的绘制起点，即Path路径从哪个坐标点开始绘制；
            1、默认是view的坐标原点；
            2、移动Path的绘制起点；
                1) moveTo(float x, float y)的参数是绝对坐标；
                2) rMoveTo(float x, float y)的参数是相对坐标；
                3）注意：改变的只是Path路径的绘制起点，不会影响到view的坐标原点；

    当前位置：即Path路径最后一个点的位置；

    目标位置：也就是Path各个方法的参数(x, y)；

    绝对坐标：(x, y)坐标点是根据view的坐标原点来确定的；

    相对坐标：(x, y)坐标点是根据相对当前位置的坐标来确定的；（换句话说：就是(x, y)坐标点把当前位置当做它的坐标原点）

   （一）画直线
        1、lineTo(x, y) 的参数是绝对坐标；
        2、rLineTo(x, y) 的参数是相对坐标；

   （二）画二次贝塞尔曲线
        1、quadTo(float x1, float y1, float x2, float y2)的参数是绝对坐标；
           参数： x1, y1 和 x2, y2 则分别是控制点和终点的坐标；
        2、rQuadTo(float dx1, float dy1, float dx2, float dy2)的参数是相对坐标；

   （三）画三次贝塞尔曲线(原理同二次一样)
        1、cubicTo(float x1, float y1, float x2, float y2, float x3, float y3)；
        2、rCubicTo(float x1, float y1, float x2, float y2, float x3, float y3)；

   （四）画弧形
        1、arcTo(RectF oval, float startAngle, float sweepAngle, boolean forceMoveTo)，其中：构造RectF的参数是绝对坐标；
        forceMoveTo 参数的意思是，绘制是要「抬一下笔移动过去」，还是「直接拖着笔过去」，区别在于是否留下移动的痕迹。
 */
public class PathView extends View {

    private final String TAG = getClass().getSimpleName();

    /**
     * 画笔的宽度
     */
    private int paintWidth = 6;
    /**
     * 画笔的颜色
     */
    private int paintColor = 0xff67aae4;
    /**
     * 画笔
     */
    private Paint mPaint = new Paint();

    /**
     * 路径
     */
    private Path mPath;

    /**
     * 代码中直接new PathView的时候自动调用
     * @param context
     */
    public PathView(Context context) {
        super(context);
    }

    /**
     * xml布局文件中使用PathView的时候自动调用
     * @param context
     * @param attrs
     */
    public PathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 不会自动调用，必须要手动调用
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setBackgroundColor(Color.parseColor("#90000000"));
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(paintColor);
        mPaint.setStrokeWidth(paintWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        // 设置线条端点形状的方法。端点有圆头 (ROUND)、平头 (BUTT) 和方头 (SQUARE) 三种
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.moveTo(20, 20);

        mPath.lineTo(50, 50);

        mPath.rLineTo(50, 10);

        mPath.rLineTo(50, 30);

        mPath.lineTo(50, 130);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            @SuppressLint("DrawAllocation")
            RectF rectF = new RectF(100, 100, 300, 300);
            mPath.arcTo(rectF, -90, 90, true);// 强制移动到弧形起点（无痕迹）
        }

        canvas.drawPath(mPath, mPaint);

        mPaint.setTypeface(Typeface.DEFAULT);
        mPaint.setTextSize(50);
        canvas.drawText("Smile每一天", 50, 50, mPaint);
    }
}
