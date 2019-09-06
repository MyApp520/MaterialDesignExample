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
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.commonlib.util.UIUtils;

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

    /**
     * 水平虚线路径
     */
    private Path mHorizontalDottedLinePath;

    private List<String> xAxisDataList;
    private List<Integer> yAxisDataList;
    private List<Integer> yAxisLabelList;

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
        mAxisPaint.setColor(ContextCompat.getColor(mContext, R.color.black));
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
    }

    public void setXAxisDataList(List<String> xAxisDataList) {
        this.xAxisDataList = xAxisDataList;
        if (this.xAxisDataList == null) {
            this.xAxisDataList = new ArrayList<>();
        }
        this.xAxisDataList.add("1月");
        this.xAxisDataList.add("2月");
        this.xAxisDataList.add("3月");
        this.xAxisDataList.add("4月");
        this.xAxisDataList.add("5月");
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

        float axisDip = 40f;
        int xAxisLineStartX = UIUtils.dp2px(mContext, axisDip);
        int xAxisLineStartY = mViewHeight - UIUtils.dp2px(mContext, axisDip);
        int xAxisLineStopX = mViewWidth - UIUtils.dp2px(mContext, 16);
        int xAxisLineStopY = mViewHeight - UIUtils.dp2px(mContext, axisDip);
        // 绘制x轴轴线
        canvas.drawLine(xAxisLineStartX, xAxisLineStartY, xAxisLineStopX, xAxisLineStopY, mAxisPaint);

        int yAxisLineStartX = UIUtils.dp2px(mContext, axisDip);
        int yAxisLineStartY = mViewHeight - UIUtils.dp2px(mContext, axisDip);
        int yAxisLineStopX = UIUtils.dp2px(mContext, axisDip);
        int yAxisLineStopY = UIUtils.dp2px(mContext, 16);
        // 绘制y轴轴线
        canvas.drawLine(yAxisLineStartX, yAxisLineStartY, yAxisLineStopX, yAxisLineStopY, mAxisPaint);

        // y轴高度
        float yAxisHeight = Math.abs(yAxisLineStopY - yAxisLineStartY);
        // 把y轴分成yAxisMaxValue个刻度
        float yAxisScaleValue = yAxisHeight / yAxisMaxValue;
        PathEffect pathEffect = null;
        if (mHorizontalDottedLinePath == null) {
            mHorizontalDottedLinePath = new Path();
        }
        // 是否绘制水平虚线
        boolean isDrawHorizontalDottedLine = true;
        Rect yAxisLabelValueRect = new Rect();
        String yAxisLabelValue;
        for (int i = 1; i < 7; i++) {
            if (i < 6) {
                if (isDrawHorizontalDottedLine) {
                    if (pathEffect == null) {
                        mAxisPaint.setColor(ContextCompat.getColor(mContext, R.color.default_color));
                        // 设置画虚线
                        pathEffect = new DashPathEffect(new float[]{20f, 10f}, 0);
                        mAxisPaint.setPathEffect(pathEffect);
                    }
                    mHorizontalDottedLinePath.moveTo(xAxisLineStartX, yAxisLineStartY - i * yAxisHeight / 6);
                    mHorizontalDottedLinePath.lineTo(xAxisLineStopX, yAxisLineStartY - i * yAxisHeight / 6);
                    // 绘制y轴水平刻度线 虚线
                    canvas.drawPath(mHorizontalDottedLinePath, mAxisPaint);
                } else {
                    // 绘制y轴水平刻度线 直线
                    canvas.drawLine(xAxisLineStartX, yAxisLineStartY - i * yAxisHeight / 6,
                            xAxisLineStopX, yAxisLineStartY - i * yAxisHeight / 6, mAxisPaint);
                }
            }

            // 绘制y轴的坐标标签
            yAxisLabelValue = String.valueOf(yAxisLabelList.get(i - 1));
            mYAxisTextPaint.getTextBounds(yAxisLabelValue, 0, yAxisLabelValue.length(), yAxisLabelValueRect);
            canvas.drawText(yAxisLabelValue, xAxisLineStartX - UIUtils.dp2px(mContext, 4),
                    yAxisLineStartY - i * yAxisHeight / 6 + (float) yAxisLabelValueRect.height() / 2, mYAxisTextPaint);
        }

        // x轴的坐标标签占的宽度，设置初始值为axisDip = 40f；
        float xAxisValueOffset = UIUtils.dp2px(mContext, axisDip);
        // x轴的坐标单个标签的占的宽度
        float itemXAxisLabelWidth = UIUtils.dp2px(mContext, 50);
        for (int i = 0; i < xAxisDataList.size(); i++) {
            String xAxisValue = xAxisDataList.get(i);
            // 绘制x轴的坐标标签
            canvas.drawText(xAxisValue, xAxisValueOffset + itemXAxisLabelWidth / 2,
                    xAxisLineStartY + mXAxisTextPaint.getFontSpacing(), mXAxisTextPaint);

            // 绘制y轴的柱状图
            if (mBarRectF == null) {
                mBarRectF = new RectF();
            }
            mBarRectF.left = xAxisValueOffset + itemXAxisLabelWidth / 2 - itemBarWidth / 2;
            mBarRectF.right = mBarRectF.left + itemBarWidth;
            mBarRectF.bottom = xAxisLineStartY;
            mBarRectF.top = mBarRectF.bottom - yAxisDataList.get(i) * yAxisScaleValue;
            canvas.drawRect(mBarRectF, mBarPaint);

            // 更新x轴的坐标标签占的宽度
            xAxisValueOffset += itemXAxisLabelWidth;
        }
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
        Log.e(TAG, "onDraw: " + mTextPaint.breakText(textContent, true, 50, textWidths));
        Log.e(TAG, "onDraw: " + textWidths[0] + ", " + textWidths[1] + ", " + textWidths[2]);
    }
}
