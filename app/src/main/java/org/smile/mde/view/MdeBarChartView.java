package org.smile.mde.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.smile.commonlib.util.UIUtils;

import org.smile.mde.R;

import java.util.ArrayList;
import java.util.List;

public class MdeBarChartView extends View {

    private final String TAG = getClass().getSimpleName();

    private Context mContext;

    // view的宽高
    private int mViewWidth, mViewHeight;

    // x坐标轴线宽度
    private int xAxisWidth = 6;

    // y坐标轴线宽度
    private int yAxisWidth = 6;

    // y轴的最大值
    private int yAxisMaxValue = 600;

    // x轴的标签占的总宽度
    private float xAxisLabelWidth;

    // x轴的单个标签占的宽度
    private float itemXAxisLabelWidth;

    // 轴线画笔
    private Paint mAxisPaint;

    // 状态图画笔
    private Paint mBarPaint;

    // x轴标签画笔
    private Paint mXAxisTextPaint;

    // y轴标签画笔
    private Paint mYAxisTextPaint;

    // 柱状图图形
    private RectF mBarRectF;

    // 单条柱状图宽度
    float itemBarWidth;

    // 手指触摸点在view中的坐标
    float mTouchX, mTouchY;

    // 柱状图最后一次发生touch事件的时间
    private long lastTouchTimeMillis;

    // 是否绘制水平虚线
    private boolean isDrawHorizontalDottedLine = false;

    /**
     * 水平虚线路径
     */
    private Path mHorizontalDottedLinePath;

    private List<String> xAxisLabelList;
    private List<Integer> yAxisDataList;
    private List<Integer> yAxisLabelList;

    private float axisStartOffset = 40f;//坐标轴起点偏移量
    private int minimumVelocity;
    private int maximumVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    public MdeBarChartView(Context context) {
        super(context);
    }

    public MdeBarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MdeBarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
        setXAxisDataList(null);
        setYAxisDataList(null);
        setYAxisLabelList(null);
    }

    private void init() {
        //设置边缘特殊效果
        BlurMaskFilter PaintBGBlur = new BlurMaskFilter(1, BlurMaskFilter.Blur.INNER);
        //绘制柱状图的画笔
        mBarPaint = new Paint();
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setStrokeWidth(4);
        mBarPaint.setColor(ContextCompat.getColor(mContext, R.color.backgroup));
        mBarPaint.setMaskFilter(PaintBGBlur);

        //绘制直线的画笔
        mAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAxisPaint.setStyle(Paint.Style.STROKE);//画线条，线条有宽度
        mAxisPaint.setColor(ContextCompat.getColor(mContext, R.color.default_color));
        mAxisPaint.setAntiAlias(true);
        mAxisPaint.setStrokeWidth(2);

        //x轴标签画笔
        mXAxisTextPaint = new Paint();
        mXAxisTextPaint.setTextAlign(Paint.Align.CENTER);
        mXAxisTextPaint.setTextSize(30);
        mXAxisTextPaint.setFakeBoldText(false);
        mXAxisTextPaint.setColor(ContextCompat.getColor(mContext, R.color.backgroup));
        mXAxisTextPaint.setAntiAlias(true);
        mXAxisTextPaint.setStrokeWidth(1);

        //y轴标签画笔
        mYAxisTextPaint = new Paint();
        mYAxisTextPaint.setTextAlign(Paint.Align.RIGHT);
        mYAxisTextPaint.setTextSize(30);
        mYAxisTextPaint.setFakeBoldText(false);
        mYAxisTextPaint.setColor(ContextCompat.getColor(mContext, R.color.backgroup));
        mYAxisTextPaint.setAntiAlias(true);
        mYAxisTextPaint.setStrokeWidth(1);

        //单条柱状图宽度
        itemBarWidth = UIUtils.dp2px(mContext, 22);
        //x轴的单个标签占的宽度
        itemXAxisLabelWidth = UIUtils.dp2px(mContext, 50);

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        maximumVelocity = configuration.getScaledMaximumFlingVelocity();

        mScroller = new Scroller(getContext(), new LinearInterpolator());
    }

    public void setXAxisDataList(List<String> xAxisLabelList) {
        this.xAxisLabelList = xAxisLabelList;
        if (this.xAxisLabelList == null) {
            this.xAxisLabelList = new ArrayList<>();
        }
        this.xAxisLabelList.add("1月");
        this.xAxisLabelList.add("2月");
        this.xAxisLabelList.add("3月");
        this.xAxisLabelList.add("4月");
        this.xAxisLabelList.add("5月");
        this.xAxisLabelList.add("6月");
        this.xAxisLabelList.add("7月");
        this.xAxisLabelList.add("8月");
        this.xAxisLabelList.add("9月");
        this.xAxisLabelList.add("10月");
        this.xAxisLabelList.add("11月");
        this.xAxisLabelList.add("12月");

        this.xAxisLabelList.add("21月");
        this.xAxisLabelList.add("22月");
        this.xAxisLabelList.add("23月");
        this.xAxisLabelList.add("24月");
        this.xAxisLabelList.add("25月");
        this.xAxisLabelList.add("26月");
        this.xAxisLabelList.add("27月");
        this.xAxisLabelList.add("28月");
        this.xAxisLabelList.add("29月");
        this.xAxisLabelList.add("30月");
        this.xAxisLabelList.add("31月");
        this.xAxisLabelList.add("32月");

        this.xAxisLabelList.add("51月");
        this.xAxisLabelList.add("52月");
        this.xAxisLabelList.add("53月");
        this.xAxisLabelList.add("54月");
        this.xAxisLabelList.add("55月");
        this.xAxisLabelList.add("56月");
        this.xAxisLabelList.add("57月");
        this.xAxisLabelList.add("58月");
        this.xAxisLabelList.add("59月");
        this.xAxisLabelList.add("60月");
        this.xAxisLabelList.add("61月");
        this.xAxisLabelList.add("62月");

        this.xAxisLabelList.add("71月");
        this.xAxisLabelList.add("72月");
        this.xAxisLabelList.add("73月");
        this.xAxisLabelList.add("74月");
        this.xAxisLabelList.add("75月");
        this.xAxisLabelList.add("76月");
        this.xAxisLabelList.add("77月");
        this.xAxisLabelList.add("78月");
        this.xAxisLabelList.add("79月");
        this.xAxisLabelList.add("80月");
        this.xAxisLabelList.add("81月");
        this.xAxisLabelList.add("82月");

        this.xAxisLabelList.add("91月");
        this.xAxisLabelList.add("92月");
        this.xAxisLabelList.add("93月");
        this.xAxisLabelList.add("94月");
        this.xAxisLabelList.add("95月");
        this.xAxisLabelList.add("96月");
        this.xAxisLabelList.add("97月");
        this.xAxisLabelList.add("98月");
        this.xAxisLabelList.add("99月");
        this.xAxisLabelList.add("30月");
        this.xAxisLabelList.add("31月");
        this.xAxisLabelList.add("32月");
    }

    public void setYAxisDataList(List<Integer> yAxisDataList) {
        this.yAxisDataList = yAxisDataList;
        if (this.yAxisDataList == null) {
            this.yAxisDataList = new ArrayList<>();
        }
        this.yAxisDataList.add(90);
        this.yAxisDataList.add(170);
        this.yAxisDataList.add(321);
        this.yAxisDataList.add(456);
        this.yAxisDataList.add(500);
        this.yAxisDataList.add(35);
        this.yAxisDataList.add(190);
        this.yAxisDataList.add(17);
        this.yAxisDataList.add(121);
        this.yAxisDataList.add(256);
        this.yAxisDataList.add(300);
        this.yAxisDataList.add(135);

        this.yAxisDataList.add(390);
        this.yAxisDataList.add(370);
        this.yAxisDataList.add(21);
        this.yAxisDataList.add(56);
        this.yAxisDataList.add(200);
        this.yAxisDataList.add(15);
        this.yAxisDataList.add(290);
        this.yAxisDataList.add(217);
        this.yAxisDataList.add(421);
        this.yAxisDataList.add(256);
        this.yAxisDataList.add(100);
        this.yAxisDataList.add(535);

        this.yAxisDataList.add(290);
        this.yAxisDataList.add(170);
        this.yAxisDataList.add(221);
        this.yAxisDataList.add(456);
        this.yAxisDataList.add(300);
        this.yAxisDataList.add(235);
        this.yAxisDataList.add(190);
        this.yAxisDataList.add(127);
        this.yAxisDataList.add(121);
        this.yAxisDataList.add(256);
        this.yAxisDataList.add(300);
        this.yAxisDataList.add(535);

        this.yAxisDataList.add(590);
        this.yAxisDataList.add(270);
        this.yAxisDataList.add(321);
        this.yAxisDataList.add(156);
        this.yAxisDataList.add(300);
        this.yAxisDataList.add(535);
        this.yAxisDataList.add(390);
        this.yAxisDataList.add(227);
        this.yAxisDataList.add(121);
        this.yAxisDataList.add(456);
        this.yAxisDataList.add(200);
        this.yAxisDataList.add(135);

        this.yAxisDataList.add(190);
        this.yAxisDataList.add(270);
        this.yAxisDataList.add(321);
        this.yAxisDataList.add(456);
        this.yAxisDataList.add(500);
        this.yAxisDataList.add(235);
        this.yAxisDataList.add(390);
        this.yAxisDataList.add(17);
        this.yAxisDataList.add(221);
        this.yAxisDataList.add(56);
        this.yAxisDataList.add(100);
        this.yAxisDataList.add(335);
    }

    public void setYAxisLabelList(List<Integer> yAxisLabelList) {
        this.yAxisLabelList = yAxisLabelList;
        if (this.yAxisLabelList == null) {
            this.yAxisLabelList = new ArrayList<>();
        }
        this.yAxisLabelList.add(100);
        this.yAxisLabelList.add(200);
        this.yAxisLabelList.add(300);
        this.yAxisLabelList.add(400);
        this.yAxisLabelList.add(500);
        this.yAxisLabelList.add(600);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = getWidth();
        mViewHeight = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int scrollX = getScrollX();
        Log.e(TAG, "onDraw: 开始绘制了 canvas = " + canvas + ",  getScrollX() = " + scrollX);

        int xAxisLineStartX = UIUtils.dp2px(mContext, axisStartOffset);
        int xAxisLineStartY = mViewHeight - UIUtils.dp2px(mContext, axisStartOffset);
        int xAxisLineStopX = mViewWidth - UIUtils.dp2px(mContext, 16);
        int xAxisLineStopY = xAxisLineStartY;
        // 绘制x轴轴线
        canvas.drawLine(xAxisLineStartX + scrollX, xAxisLineStartY, xAxisLineStopX + scrollX, xAxisLineStopY, mAxisPaint);

        int yAxisLineStartX = UIUtils.dp2px(mContext, axisStartOffset);
        int yAxisLineStartY = mViewHeight - UIUtils.dp2px(mContext, axisStartOffset);
        int yAxisLineStopX = yAxisLineStartX;
        int yAxisLineStopY = UIUtils.dp2px(mContext, 16);
        // 绘制y轴轴线
        canvas.drawLine(yAxisLineStartX + scrollX, yAxisLineStartY, yAxisLineStopX + scrollX, yAxisLineStopY, mAxisPaint);

        // y轴高度
        float yAxisHeight = Math.abs(yAxisLineStopY - yAxisLineStartY);
        // 把y轴分成yAxisMaxValue个刻度
        float yAxisScaleValue = yAxisHeight / yAxisMaxValue;
        PathEffect pathEffect = null;
        if (mHorizontalDottedLinePath == null) {
            mHorizontalDottedLinePath = new Path();
        }
        Rect yAxisLabelValueRect = new Rect();
        String yAxisLabelValue;
        for (int i = 1; i < 7; i++) {
            if (i < 6) {
                if (isDrawHorizontalDottedLine) {
                    if (pathEffect == null) {
                        // 设置画虚线
                        pathEffect = new DashPathEffect(new float[]{20f, 10f}, 0);
                        mAxisPaint.setPathEffect(pathEffect);
                    }
                    mHorizontalDottedLinePath.moveTo(xAxisLineStartX + scrollX, yAxisLineStartY - i * yAxisHeight / 6);
                    mHorizontalDottedLinePath.lineTo(xAxisLineStopX + scrollX, yAxisLineStartY - i * yAxisHeight / 6);
                    // 绘制y轴水平刻度线 虚线
                    canvas.drawPath(mHorizontalDottedLinePath, mAxisPaint);
                } else {
                    // 绘制y轴水平刻度线 直线
                    canvas.drawLine(xAxisLineStartX + scrollX, yAxisLineStartY - i * yAxisHeight / 6,
                            xAxisLineStopX + scrollX, yAxisLineStartY - i * yAxisHeight / 6, mAxisPaint);
                }
            }

            // 绘制y轴的坐标标签
            yAxisLabelValue = String.valueOf(yAxisLabelList.get(i - 1));
            mYAxisTextPaint.getTextBounds(yAxisLabelValue, 0, yAxisLabelValue.length(), yAxisLabelValueRect);
            canvas.drawText(yAxisLabelValue, xAxisLineStartX - UIUtils.dp2px(mContext, 4) + scrollX,
                    yAxisLineStartY - i * yAxisHeight / 6 + (float) yAxisLabelValueRect.height() / 2, mYAxisTextPaint);
        }
        // 初始化x轴的标签占的总宽度
        xAxisLabelWidth = xAxisLineStartX;
        for (int i = 0; i < xAxisLabelList.size(); i++) {
            String xAxisValue = xAxisLabelList.get(i);
            // 绘制x轴的坐标标签
            canvas.drawText(xAxisValue, xAxisLabelWidth + itemXAxisLabelWidth / 2,
                    xAxisLineStartY + mXAxisTextPaint.getFontSpacing(), mXAxisTextPaint);

            // 计算y轴的柱状图图形RectF值
            if (mBarRectF == null) {
                mBarRectF = new RectF();
            }
            mBarRectF.left = xAxisLabelWidth + itemXAxisLabelWidth / 2 - itemBarWidth / 2;
            mBarRectF.right = mBarRectF.left + itemBarWidth;
            mBarRectF.bottom = xAxisLineStartY;
            mBarRectF.top = mBarRectF.bottom - yAxisDataList.get(i) * yAxisScaleValue;

            if (mTouchX > 0 && mTouchY > 0 && mBarRectF.contains(mTouchX, mTouchY)) {
                // 选中了这条柱状图，柱状图颜色加深（left <= x < right and top <= y < bottom）
                mBarPaint.setColor(ContextCompat.getColor(mContext, R.color.backgroup_select));
            } else {
                mBarPaint.setColor(ContextCompat.getColor(mContext, R.color.backgroup));
            }
            // 绘制y轴的柱状图
            canvas.drawRect(mBarRectF, mBarPaint);

            // 更新x轴标签占的宽度
            xAxisLabelWidth += itemXAxisLabelWidth;
        }
    }

    private float lastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Log.e(TAG, "onTouchEvent 触摸事件---ACTION_DOWN:  mScroller.isFinished() = " + mScroller.isFinished());
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                lastX = event.getX();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = (int) (event.getX() - lastX);
                lastX = event.getX();
                Log.e(TAG, "onTouchEvent 触摸事件---ACTION_MOVE:  deltaX = " + deltaX);
                if (deltaX > 0 && canScrollHorizontally(-1)) {
                    // 手指从左向右滑动
                    scrollBy(-Math.min(getMaxCanScrollX(-1), deltaX), 0);
                } else if (deltaX < 0 && canScrollHorizontally(1)) {
                    // 手指从右向左滑动
                    scrollBy(Math.min(getMaxCanScrollX(1), -deltaX), 0);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                Log.e(TAG, "onTouchEvent 触摸事件---ACTION_UP: ");
                actionUp();
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                recycleVelocityTracker();
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 当调用View.invalidate()或其他方法刷新view时，就会触发自己的View.computeScroll()方法
     */
    @Override
    public void computeScroll() {
        Log.e(TAG, "computeScroll: ");
        // 实时计算滚动的偏移量，同时：判断滚动是否还在继续，true继续，false结束
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();//注意：此处必须手动调用invalidate()刷新控件
        }
    }

    /**
     * 判断是否可以水平滑动
     *
     * @param direction 标识滑动方向  正数：右滑(手指从右至左移动)；负数：左滑(手指由左向右移动)
     */
    @Override
    public boolean canScrollHorizontally(int direction) {
        if (direction > 0) {
            return xAxisLabelWidth - getScrollX() - mViewWidth > 0;
        } else {
            return getScrollX() > 0;
        }
    }

    private void fling(int velocityX) {
        if (Math.abs(velocityX) > minimumVelocity) {
            if (Math.abs(velocityX) > maximumVelocity) {
                velocityX = maximumVelocity * velocityX / Math.abs(velocityX);
            }
            mScroller.fling(getScrollX(), getScrollY(), -velocityX, 0, 0, (int) (xAxisLabelWidth - mViewWidth), 0, 0);
        }
    }

    private void actionUp() {
        mVelocityTracker.computeCurrentVelocity(1000, maximumVelocity);
        int velocityX = (int) mVelocityTracker.getXVelocity();
        fling(velocityX);
//        invalidate();//这一行可以注释掉，如果觉得滑动时有卡顿，那就不注释；
        recycleVelocityTracker();
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 根据滑动方向获取最大可滑动距离
     *
     * @param direction 标识滑动方向  正数：右滑(手指从右至左移动)；负数：左滑(手指由左向右移动)
     */
    private int getMaxCanScrollX(int direction) {
        if (direction > 0) {
            return xAxisLabelWidth - getScrollX() - mViewWidth > 0 ? (int) (xAxisLabelWidth - getScrollX() - mViewWidth) : 0;
        } else if (direction < 0) {
            return getScrollX();
        }
        return 0;
    }

    /**
     * 1、mTextPaint.measureText(textContent)测量字符串宽度，测出来的值比getTextBounds稍微大一点
     * 2、mTextPaint.getTextBounds(textContent, 0, textContent.length(), rect)测量字符串宽度
     * <p>
     * 3、int count = mTextPaint.breakText(textContent, true, 50, textWidths)
     * 备注：
     * 第一个参数：待测量的字符串
     * 第二个参数：true表示从左到右测量
     * 第三个参数：表示测量的最大宽度，超过这个宽度，测量的时候就会截取字符串，然后再测量
     * 第四个参数：表示最终测量的字符串宽度；比如：字符串本来有9个字符，由于宽度限制，只测量了3个字符，textWidths表示的就是这3个字符测出来的宽度；
     * <p>
     * 返回值count：表示breakText()方法最终测量了几个字符
     * <p>
     * 4、获取行间距，这个行距是系统帮我们计算的
     * float fontSpacing = mTextPaint.getFontSpacing();
     *
     * @param canvas
     */
    private void testDrawText(Canvas canvas) {
        Paint mTextPaint = new Paint();
        mTextPaint.setTextSize(30);
        mTextPaint.setFakeBoldText(false);
        mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.backgroup));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(1);

        String textContent = "testSmile";
        Rect rect = new Rect();
        mTextPaint.getTextBounds(textContent, 0, textContent.length(), rect);
        int textHeight = rect.height();
        Log.e(TAG, "onDraw: textHeight = " + textHeight + ", textWidth = " + rect.width() + ", " + mTextPaint.measureText(textContent));
        mTextPaint.setStrokeWidth(18);
        mTextPaint.setColor(Color.RED);
        canvas.drawPoint(500, 150, mTextPaint);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.backgroup));
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(textContent, 500, 150, mTextPaint);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(textContent, 500, 150 + textHeight, mTextPaint);
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(textContent, 500, 150 + textHeight * 2, mTextPaint);

        mTextPaint.setStrokeWidth(18);
        mTextPaint.setColor(Color.RED);
        canvas.drawPoint(700, 350, mTextPaint);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.backgroup));
        // 获取字体行距，这个行距是系统帮我们计算的
        float fontSpacing = mTextPaint.getFontSpacing();
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(textContent, 700, 350, mTextPaint);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(textContent, 700, 350 + fontSpacing, mTextPaint);
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(textContent, 700, 350 + fontSpacing * 2, mTextPaint);

        float[] textWidths = new float[9];
        Log.e(TAG, "testDrawText: " + mTextPaint.breakText(textContent, true, 50, textWidths));
        Log.e(TAG, "testDrawText: " + textWidths[0] + ", " + textWidths[1] + ", " + textWidths[2]);
    }
}
