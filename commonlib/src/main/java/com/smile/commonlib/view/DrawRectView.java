package com.smile.commonlib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.smile.commonlib.R;


/**
 * 手动绘制矩形
 */
@SuppressLint("LogNotTimber")
public class DrawRectView extends View {

    private final String TAG = getClass().getSimpleName();

    private IDrawRectListener mIDrawRectListener;

    /**
     * 矩形框画笔
     */
    private Paint mPaint;

    /**
     * 矩形框画笔线条宽度
     */
    private int mPaintStrokeWidth = 3;

    /**
     * 手动绘制矩形
     */
    private Rect mTargetRect = new Rect(0, 0, 0, 0);

    public DrawRectView(Context context) {
        this(context, null);
    }

    public DrawRectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setBackgroundColor(ContextCompat.getColor(context, R.color.bg_shadow));

        //构建对象
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//设置无锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPaintStrokeWidth);
        mPaint.setAlpha(100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.RED);
        canvas.drawRect(mTargetRect, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTargetRect.right += mPaintStrokeWidth;
                mTargetRect.bottom += mPaintStrokeWidth;
                invalidate(mTargetRect);
                mTargetRect.left = x;
                mTargetRect.top = y;
                mTargetRect.right = mTargetRect.left;
                mTargetRect.bottom = mTargetRect.top;
            case MotionEvent.ACTION_MOVE:
                Rect old = new Rect(mTargetRect.left, mTargetRect.top
                        , mTargetRect.right + mPaintStrokeWidth
                        , mTargetRect.bottom + mPaintStrokeWidth);
                mTargetRect.right = x;
                mTargetRect.bottom = y;
                old.union(x, y);
                invalidate(old);
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "ACTION_UP: mTargetRect = " + mTargetRect.toString());
                if (mIDrawRectListener != null) {
                    mIDrawRectListener.drawFinish(mTargetRect);
                }
                break;
            default:
                break;
        }
        return true;//处理了触摸信息，消息不再传递
    }

    public void setIDrawRectListener(IDrawRectListener IDrawRectListener) {
        mIDrawRectListener = IDrawRectListener;
    }

    public interface IDrawRectListener {
        void drawFinish(Rect rect);
    }
}