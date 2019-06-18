package org.smile.mde.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.commonlib.util.UIUtils;

/**
 * Created by smile on 2019/6/5.
 */

public class PTZCircleControlView extends View {

    private final String TAG = getClass().getSimpleName();
    private Context context;
    private Paint mPaint;
    private RectF mRectF;
    private float roundWidth; // 圆环的宽度
    private int centerX;// 获取圆心的x坐标
    private int radius;// 圆环的半径
    private float startAngle;// 每段弧形的起始角度
    private float singleAngle;//把圆环等分成8段，计算每一段所占的角度
    private PointF pointF1, pointF2, pointF3;//三角形三个点的坐标
    private Path trianglePath;//一个三角形的路径

    public PTZCircleControlView(Context context) {
        this(context, null);
        init();
    }

    public PTZCircleControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public PTZCircleControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        roundWidth = UIUtils.dp2px(context, 20.0f);
        singleAngle = 360 * 12.5f / 100;//把圆环等分成8段，计算每一段所占的角度

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true); // 防抖动
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(roundWidth); // 设置画笔的宽度使用进度条的宽度

        pointF1 = new PointF();
        pointF2 = new PointF();
        pointF3 = new PointF();
        trianglePath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = getWidth() / 2;
        radius = (int) (centerX - roundWidth / 2);
        mRectF = new RectF(centerX - radius, centerX - radius, centerX + radius, centerX + radius); // 用于定义的圆弧的形状和大小的界限
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircleAndTrans(canvas);
    }

    private void drawCircleAndTrans(Canvas canvas) {
        //每段弧形的起始角度，其中第一段弧形起始角度为：-90 - 23f
        startAngle = -90 - 23f;
        //一个圆环360度，把圆环等分成8段，每段45度
        for (int i = 1; i < 9; i++) {
            Log.e(TAG, "onDraw: startAngle = " + startAngle + ", singleAngle = " + singleAngle);
            if (i % 2 == 0) {
                mPaint.setColor(Color.parseColor("#12b7f5"));
            } else {
                mPaint.setColor(Color.parseColor("#16BC49"));
            }
            mPaint.setStrokeWidth(roundWidth);
            //singleAngle + 1.0f 是为了让每个扇形之间没有间隙
            canvas.drawArc(mRectF, startAngle, singleAngle + 1.0f, false, mPaint);

            //确定三角形三个点的坐标
            pointF1.x = (float) (centerX + 0.9 * radius * Math.cos((startAngle + 10) * Math.PI / 180));
            pointF1.y = (float) (centerX + 0.9 * radius * Math.sin((startAngle + 10) * Math.PI / 180));

            pointF2.x = (float) (centerX + 0.9 * radius * Math.cos((startAngle + 35) * Math.PI / 180));
            pointF2.y = (float) (centerX + 0.9 * radius * Math.sin((startAngle + 35) * Math.PI / 180));

            pointF3.x = (float) (centerX + 1.1 * radius * Math.cos((startAngle + 22.5) * Math.PI / 180));
            pointF3.y = (float) (centerX + 1.1 * radius * Math.sin((startAngle + 22.5) * Math.PI / 180));

            //仅仅画出三个点
//            canvas.drawPoint(pointF1.x, pointF1.y, mPaint);
//            canvas.drawPoint(pointF2.x, pointF2.y, mPaint);
//            mPaint.setStrokeWidth(12);
//            mPaint.setColor(Color.YELLOW);
//            canvas.drawPoint(pointF3.x, pointF3.y, mPaint);

            //绘制三角形
            trianglePath.moveTo(pointF1.x, pointF1.y);// 此点为多边形的起点
            trianglePath.lineTo(pointF2.x, pointF2.y);
            trianglePath.lineTo(pointF3.x, pointF3.y);
            trianglePath.close(); // 使这些点构成封闭的多边形，封闭的三个点组成的就是三角形
            mPaint.setStrokeWidth(5);
            mPaint.setColor(Color.WHITE);
            canvas.drawPath(trianglePath, mPaint);

            if (i < 9) {
                startAngle += singleAngle;
            }
        }
    }
}
