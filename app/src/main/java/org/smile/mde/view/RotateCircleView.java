package org.smile.mde.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.commonlib.util.UIUtils;

import org.smile.mde.R;

public class RotateCircleView extends View {

    private final String TAG = getClass().getSimpleName();
    private Context mContext;

    private int mViewWidth, mViewHeight;
    private int centerX, centerY;
    private int mViewRadius;
    /**
     * 用于定义的圆弧的形状和大小的界限
     */
    private RectF mRectF = new RectF();
    /**
     * 刻度线的长度
     */
    private int mScaleLineLength;

    private TextPaint mTextPaint;
    private Paint mScalePaint;
    private Paint mBackgroundRoundPaint;

    public RotateCircleView(Context context) {
        super(context);
    }

    public RotateCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        // 画圆圈
        mBackgroundRoundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundRoundPaint.setStyle(Paint.Style.STROKE);//画线条，线条有宽度
        mBackgroundRoundPaint.setColor(ContextCompat.getColor(mContext, R.color.default_color));
        mBackgroundRoundPaint.setAntiAlias(true);
        mBackgroundRoundPaint.setStrokeWidth(2);

        //绘制刻度线的画笔
        mScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScalePaint.setStyle(Paint.Style.STROKE);//画线条，线条有宽度
        mScalePaint.setColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        mScalePaint.setAntiAlias(true);
        mScalePaint.setStrokeWidth(2);

        //文字画笔
        mTextPaint = new TextPaint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(30);
        mTextPaint.setFakeBoldText(false);
        mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.backgroup));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(6);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        centerX = mViewWidth / 2;
        centerY = mViewHeight / 2;
        mViewRadius = Math.min(mViewWidth - getPaddingLeft() - getPaddingRight(), mViewHeight - getPaddingTop() - getPaddingBottom()) / 2;
        mScaleLineLength = UIUtils.dp2px(mContext, 8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackgroundRound(canvas);
        drawRoundScale(canvas);
    }

    private void drawBackgroundRound(Canvas canvas) {
        mRectF.left = centerX - mViewRadius;
        mRectF.top = centerY - mViewRadius;
        mRectF.right = centerX + mViewRadius;
        mRectF.bottom = centerY + mViewRadius;
        canvas.drawArc(mRectF, 0, 360, false, mBackgroundRoundPaint);
    }

    private void drawRoundScale(Canvas canvas) {
        for (int angle = 0; angle < 360; angle += 6) {
            canvas.drawLine(getArcPointX(angle, mViewRadius - mScaleLineLength / 2)
                    , getArcPointY(angle, mViewRadius - mScaleLineLength / 2)
                    , getArcPointX(angle, mViewRadius + mScaleLineLength / 2)
                    , getArcPointY(angle, mViewRadius + mScaleLineLength / 2)
                    , mScalePaint);
        }

        Path path = new Path();
        path.addArc(mRectF, 50, 2);
        canvas.drawTextOnPath("深圳", path, 0, 0, mTextPaint);
    }

    /**
     * 根据角度和半径，求一个点的X轴坐标
     *
     * @param angle
     * @return
     */
    private float getArcPointX(int angle, int radius) {
//        return (float) (centerX + radius * Math.cos(angle * Math.PI / 180));//方式一
        return (float) (centerX + radius * Math.cos((angle) * Math.PI / 180));//方式二
    }

    /**
     * 根据角度和半径，求一个点的X轴坐标
     *
     * @param angle
     * @return
     */
    private float getArcPointY(int angle, int radius) {
//        return (float) (centerY + radius * Math.sin(angle * Math.PI / 180));//方式一
        return (float) (centerY + radius * Math.sin((angle) * Math.PI / 180));//方式二
    }
}
