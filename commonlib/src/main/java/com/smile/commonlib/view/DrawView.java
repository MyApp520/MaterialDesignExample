package com.smile.commonlib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.smile.commonlib.R;

/**
 * DrawView的背景是半透明，手指移动在这个界面上画一个透明框
 */
public class DrawView extends View {

    private final String TAG = getClass().getSimpleName();
    private Canvas mViewCanvas, mTempCanvas;
    private Bitmap mBitmap;
    /**
     * 手指滑动时，在屏幕上画一个透明框
     */
    private RectF mTransparentRectf;
    /**
     * 透明框画笔
     */
    private Paint mTransparentRectfPaint;
    /**
     * 透明框的起点坐标
     */
    private int x_begin;
    private int y_begin;
    /**
     * 透明框的终点坐标
     */
    private int x_end;
    private int y_end;
    /**
     * DrawView画笔
     */
    private Paint mViewPaint;
    private int mViewWidth;
    private int mViewHeight;

    private RectFSelectListener mRectFSelectListener;

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG, "onSizeChanged: ");
        if (mTransparentRectf == null) {
            invalidate();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x_begin = (int) event.getX();
            y_begin = (int) event.getY();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            x_end = (int) event.getX();
            y_end = (int) event.getY();
            invalidate();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.e(TAG, "onTouchEvent() 抬起手指: left = " + mTransparentRectf.left + ", top = " + mTransparentRectf.top
                    + ", right = " + mTransparentRectf.right + ", bottom = " + mTransparentRectf.bottom);
            if (mRectFSelectListener != null) {
                mRectFSelectListener.rectFSelectEnd(mTransparentRectf);
                reset();
                setVisibility(GONE);
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "onDraw: canvas = " + canvas);
        mViewCanvas = canvas;
        if (mTransparentRectf == null) {
            init();
        } else {
            drawTranslateRect(x_begin, y_begin, x_end, y_end);
        }
    }

    private void init() {
        mViewWidth = mViewCanvas.getWidth();
        mViewHeight = mViewCanvas.getHeight();
        Log.e(TAG, "init() 初始化各个参数 mViewWidth = " + mViewWidth + ", mViewHeight = " + mViewHeight);
        // 先创建出一个bitmap，该bitmap的宽高就是整个DrawView的宽高
        mBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888);
        mTempCanvas = new Canvas(mBitmap);

        // 背景画笔
        mViewPaint = new Paint();
        mViewPaint.setColor(getResources().getColor(R.color.bg_shadow));
        // 通过mTempCanvas给mBitmap画个边框，这个边框的宽高就是整个mBitmap的宽高
        mTempCanvas.drawRect(0, 0, mTempCanvas.getWidth(), mTempCanvas.getHeight(), mViewPaint);

        // 透明框画笔
        mTransparentRectfPaint = new Paint();
        // 透明效果
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);//SRC_OUT或者CLEAR都可以
        mTransparentRectfPaint.setXfermode(porterDuffXfermode);
        mTransparentRectfPaint.setAntiAlias(true);

        mTransparentRectf = new RectF();//透明框
        // 绘制完bitmap后，再将bitmap绘制到屏幕上
        mViewCanvas.drawBitmap(mBitmap, 0, 0, mViewPaint);
    }

    /**
     * 画透明框
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void drawTranslateRect(float left, float top, float right, float bottom) {
        Log.e(TAG, "drawTranslateRect: left = " + left + ", top = " + top + ", right = " + right + ", bottom = " + bottom);
        if (left == right && left == 0) {
            return;
        }
        if (top == bottom && top == 0) {
            return;
        }
        mTransparentRectf.left = left;
        mTransparentRectf.top = top;
        mTransparentRectf.right = right;
        mTransparentRectf.bottom = bottom;
        mTempCanvas.drawRect(mTransparentRectf, mTransparentRectfPaint);
        // 绘制到屏幕
        mViewCanvas.drawBitmap(mBitmap, 0, 0, mViewPaint);
    }

    /**
     * 重置DrawView界面
     */
    public void reset() {
        release();
        invalidate();
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mViewPaint = null;
        mTransparentRectfPaint = null;
        mTransparentRectf = null;
        mTempCanvas = null;
        mViewCanvas = null;
    }

    /**
     * 设置手指画框结束监听器
     * @param rectFSelectListener
     */
    public void setRectFSelectListener(RectFSelectListener rectFSelectListener) {
        mRectFSelectListener = rectFSelectListener;
    }

    /**
     * 手指画框结束监听器
     */
    public interface RectFSelectListener {
        /**
         * @param selectRectF 透明框RectF值
         */
        void rectFSelectEnd(RectF selectRectF);
    }
}
