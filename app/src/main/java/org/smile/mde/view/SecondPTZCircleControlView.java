package org.smile.mde.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by smile on 2019/6/5.
 */

public class SecondPTZCircleControlView extends View {

    private final String TAG = getClass().getSimpleName();
    private Context context;
    /**
     * 圆环画笔
     */
    private Paint mPaint;
    /**
     * 圆心的x坐标，内外圆弧共用同一个中心坐标，只是半径不一样
     * 外圆弧 减去 内圆弧 得到的就是一个圆环
     */
    private int centerX;
    /**
     * 用于定义内外两个圆弧的形状和大小的界限
     */
    private RectF mInnerRectF, mOuterRectF;
    /**
     * 内外圆弧的半径
     */
    private int mInnerRadius, mOuterRadius;
    /**
     * 圆环等分成几段
     */
    private int dividedRing = 8;
    /**
     * 第一段弧形的起始角度为：-90 - 23f
     * 注意：屏幕水平向右为正X轴方向，角度是0度，顺时针方向角度为正，逆时针方向角度为负数
     */
    private float firstRingStartAngle = -90 - 23f;
    /**
     * 把圆环等分成dividedRing段，计算每一段所占的角度
     */
    private float singleRingAngle;
    /**
     * 三角形三个点的坐标
     */
    private PointF pointF1, pointF2, pointF3;
    /**
     * 一个三角形的路径
     */
    private Path trianglePath;
    /**
     * 保存每段圆环所表示的区域region
     * 由绘制每段弧度所需要的path转化为region
     */
    private ArrayList<Region> regionList;
    /**
     * 手指点击位置的坐标
     */
    private float downX, downY;
    /**
     * 圆环点击事件监听
     */
    private OnRingClickListener onRingClickListener;

    public SecondPTZCircleControlView(Context context) {
        this(context, null);
    }

    public SecondPTZCircleControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SecondPTZCircleControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        singleRingAngle = 360 / (float) dividedRing;//把圆环等分成dividedRing段，计算每一段所占的角度

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true); // 防抖动
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);

        pointF1 = new PointF();
        pointF2 = new PointF();
        pointF3 = new PointF();
        trianglePath = new Path();
        regionList = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = getWidth() / 2;
        mOuterRadius = centerX;
        mInnerRadius = (int) (0.6f * mOuterRadius);
        // 用于定义外圆弧的形状和大小的界限
        mOuterRectF = new RectF(centerX - mOuterRadius, centerX - mOuterRadius, centerX + mOuterRadius, centerX + mOuterRadius);
        // 用于定义内圆弧的形状和大小的界限
        mInnerRectF = new RectF(centerX - mInnerRadius, centerX - mInnerRadius, centerX + mInnerRadius, centerX + mInnerRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircleAndTriangle(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "onTouchEvent: 触摸事件 event.getAction() = " + event.getAction());

        if (getOnRingClickListener() != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    performClick();
                    break;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        for (int i = 0; i < regionList.size(); i++) {
            //通过region的contains方法判断点击位置的坐标位于哪个圆环的区域内
            if (regionList.get(i).contains((int) downX, (int) downY) && getOnRingClickListener() != null) {
                getOnRingClickListener().onRingClick(i);
            }
        }
        return super.performClick();
    }

    /**
     * 绘制圆环和圆环上的三角形
     * @param canvas
     */
    private void drawCircleAndTriangle(Canvas canvas) {
        regionList.clear();
        //每段弧形的起始角度
        float startAngle = firstRingStartAngle;
        //一个圆环360度，把圆环等分成dividedRing段
        for (int i = 0; i < dividedRing; i++) {
            Log.e(TAG, "onDraw: startAngle = " + startAngle + ", singleRingAngle = " + singleRingAngle);
            if (i % 2 == 0) {
                mPaint.setColor(Color.parseColor("#12b7f5"));
            } else {
                mPaint.setColor(Color.parseColor("#16BC49"));
            }
            //singleAngle + 1.0f 是为了让每个弧形之间没有间隙
            canvas.drawPath(getArcPath(mInnerRectF, mOuterRectF, startAngle, singleRingAngle + 1.0f), mPaint);

            //确定三角形三个点的坐标
            pointF1.x = (float) (centerX + 0.7 * mOuterRadius * Math.cos((startAngle + 10) * Math.PI / 180));
            pointF1.y = (float) (centerX + 0.7 * mOuterRadius * Math.sin((startAngle + 10) * Math.PI / 180));

            pointF2.x = (float) (centerX + 0.7 * mOuterRadius * Math.cos((startAngle + 35) * Math.PI / 180));
            pointF2.y = (float) (centerX + 0.7 * mOuterRadius * Math.sin((startAngle + 35) * Math.PI / 180));

            pointF3.x = (float) (centerX + 0.9 * mOuterRadius * Math.cos((startAngle + 22.5) * Math.PI / 180));
            pointF3.y = (float) (centerX + 0.9 * mOuterRadius * Math.sin((startAngle + 22.5) * Math.PI / 180));

            //绘制三角形
            trianglePath.moveTo(pointF1.x, pointF1.y);// 此点为多边形的起点
            trianglePath.lineTo(pointF2.x, pointF2.y);
            trianglePath.lineTo(pointF3.x, pointF3.y);
            trianglePath.close(); // 使这些点构成封闭的多边形，三个点组成的就是三角形
            mPaint.setColor(Color.WHITE);
            canvas.drawPath(trianglePath, mPaint);

            if (i < dividedRing) {
                startAngle += singleRingAngle;
            }
        }
        Log.e(TAG, "drawCircleAndTriangle: 有几段环形区域 regionList.size() = " + regionList.size());
    }

    /**
     * 获取绘制弧度所需要的path
     *
     * @param in
     * @param out
     * @param startAngle
     * @param angle
     * @return
     */
    private Path getArcPath(RectF in, RectF out, float startAngle, float angle) {
        Path path1 = new Path();
        path1.moveTo(in.centerX(), in.centerY());
        path1.arcTo(in, startAngle, angle);
        Path path2 = new Path();
        path2.moveTo(out.centerX(), out.centerY());
        path2.arcTo(out, startAngle, angle);
        Path path = new Path();
        if (Build.VERSION.SDK_INT >= 19) {
            Log.e(TAG, "getArcPath: 圆弧取交集 = " + path.op(path2, path1, Path.Op.DIFFERENCE));
        }
        setRegion(path);
        return path;
    }

    /**
     * path转化为区域Region
     *
     * @param path
     */
    private void setRegion(Path path) {
        Region re = new Region();
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        re.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        regionList.add(re);
    }

    /**
     * 圆环分成几段
     * @param dividedRing
     */
    public void setDividedRing(int dividedRing) {
        this.dividedRing = dividedRing;
    }

    /**
     * 圆环点击事件监听接口
     */
    public interface OnRingClickListener {
        void onRingClick(int clickPosition);
    }

    /**
     * 圆环点击事件
     *
     * @return
     */
    public OnRingClickListener getOnRingClickListener() {
        return onRingClickListener;
    }

    /**
     * 设置圆环的点击事件
     *
     * @param onRingClickListener
     */
    public void setOnRingClickListener(OnRingClickListener onRingClickListener) {
        this.onRingClickListener = onRingClickListener;
    }
}
