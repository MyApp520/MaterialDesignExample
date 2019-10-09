package org.smile.mde.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.commonlib.util.ShowToast;
import com.example.commonlib.util.UIUtils;

import org.smile.mde.R;

import java.util.HashMap;
import java.util.Map;

public class RotateCircleView extends View {

    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private Paint mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int[] sectorColor = new int[]{Color.parseColor("#EE82EE"), Color.parseColor("#FFDEAD")};

    private int mViewWidth, mViewHeight;
    private int centerX, centerY;
    private int mViewRadius;
    /**
     * 用于定义最外面那个圆弧的形状和大小的界限
     */
    private RectF mOuterArcRectF = new RectF();
    /**
     * 用于定义刻度线组成的圆弧的形状和大小的界限
     */
    private RectF mScaleLineArcRectF = new RectF();
    /**
     * 刻度线的长度
     */
    private int mScaleLineLength;

    private TextPaint mTextPaint;
    private Paint mScalePaint;
    private Paint mBackgroundRoundPaint;

    /**
     * "军队", "陆军", "航空兵", "古田", "大型机场", "坦克", "护卫舰"
     */
    private String[] addressArray = {"军队", "陆军", "航空兵", "古田", "大型机场", "坦克", "护卫舰"};
    /**
     * 保存path路径所表示的区域region
     */
    private Map<Integer, Region> mRegionMap = new HashMap<>();
    /**
     * 手指点击位置的坐标
     */
    private float downX, downY;

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
        mTextPaint.setTextSize(50);
        mTextPaint.setFakeBoldText(false);
        mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.backgroup));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(6);

        mScaleLineLength = UIUtils.dp2px(mContext, 8);
        setPadding(getLeft(), (int) (getTop() + 3 * mScaleLineLength), getRight(), (int) (getBottom() + 3 * mScaleLineLength));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        centerX = mViewWidth / 2;
        centerY = mViewHeight / 2;
        mViewRadius = Math.min(mViewWidth - getPaddingLeft() - getPaddingRight(), mViewHeight - getPaddingTop() - getPaddingBottom()) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackgroundRound(canvas);
        drawRoundScale(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "onTouchEvent: 触摸事件 event.getAction() = " + event.getAction());

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

    @Override
    public boolean performClick() {
        for (Map.Entry<Integer, Region> entry : mRegionMap.entrySet()) {
            //通过region的contains方法判断点击位置的坐标位于哪个圆环的区域内
            if (entry.getValue().contains((int) downX, (int) downY)) {
                ShowToast.showToast(mContext, "点击的是：" + addressArray[entry.getKey()]);
            }
        }
        return super.performClick();
    }

    private void drawBackgroundRound(Canvas canvas) {
        mScaleLineArcRectF.left = centerX - mViewRadius;
        mScaleLineArcRectF.top = centerY - mViewRadius;
        mScaleLineArcRectF.right = centerX + mViewRadius;
        mScaleLineArcRectF.bottom = centerY + mViewRadius;
        canvas.drawArc(mScaleLineArcRectF, 0, 360, false, mBackgroundRoundPaint);

        mOuterArcRectF.left = centerX - mViewRadius - 2 * mScaleLineLength;
        mOuterArcRectF.top = centerY - mViewRadius - 2 * mScaleLineLength;
        mOuterArcRectF.right = centerX + mViewRadius + 2 * mScaleLineLength;
        mOuterArcRectF.bottom = centerY + mViewRadius + 2 * mScaleLineLength;
        mBackgroundRoundPaint.setColor(ContextCompat.getColor(mContext, R.color.backgroup));
        canvas.drawArc(mOuterArcRectF, 0, 360, false, mBackgroundRoundPaint);
    }

    private void drawRoundScale(Canvas canvas) {
        mRegionMap.clear();
        // 画圆弧刻度线时，一个刻度所占的角度
        int theAngleOfOneScale = 3;
        int startAngle = 180 - 2 * theAngleOfOneScale;
        int endAngle;
        Path path;
        // 绘制的时候，分阶段绘制，分成addressArray.length个阶段
        for (int i = 0; i < addressArray.length; i++) {
            // 一个文本标签所占用的角度
            int sweepAngle = addressArray[i].length() * 3 * theAngleOfOneScale;
            // 扇形的弧长 = 2πr×角度/360 = πr×角度/180
            float arcLength = (float) (Math.PI * mViewRadius * sweepAngle / 180);
            // 测量文字的宽度
            float textWidth = mTextPaint.measureText(addressArray[i]);
            // 水平偏移
            int hOffset = (int) (arcLength / 2 - textWidth / 2);
            // 计算文字的路径
            path = new Path();
            path.addArc(mScaleLineArcRectF, startAngle, sweepAngle);
            addRegion(i, path);
            // 测试画扇形
            mArcPaint.setColor(sectorColor[i % 2]);
            canvas.drawArc(mScaleLineArcRectF, startAngle, sweepAngle, true, mArcPaint);
            // 开始绘制文字
            // mTextPaint.setTextAlign(Paint.Align.CENTER);这一点很重要，直接影响到startAngle和endAngle是否需要增加theAngleOfOneScale
            canvas.drawTextOnPath(addressArray[i], path, 0, mScaleLineLength / 1.5f, mTextPaint);

            // 每个阶段绘制刻度线时：对应的起始角度，之所以加theAngleOfOneScale，是为了间隔文字和刻度线，免得挨在一起不好看
            startAngle = startAngle + sweepAngle + theAngleOfOneScale;
            // 每个阶段绘制刻度线时：对应的终止角度（7 * theAngleOfOneScale表示该阶段刻度线占用的角度）
            endAngle = startAngle + 7 * theAngleOfOneScale;
            // 开始绘制刻度线
            for (int angle = startAngle; angle < endAngle; angle += theAngleOfOneScale) {
                canvas.drawLine(getArcPointX(angle, (int) (mViewRadius - mScaleLineLength / 2.5f))
                        , getArcPointY(angle, (int) (mViewRadius - mScaleLineLength / 2.5f))
                        , getArcPointX(angle, (int) (mViewRadius + mScaleLineLength / 2.5f))
                        , getArcPointY(angle, (int) (mViewRadius + mScaleLineLength / 2.5f))
                        , mScalePaint);
            }
            startAngle = endAngle;
        }
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

    /**
     * path转化为区域Region
     *
     * @param path
     */
    private void addRegion(int index, Path path) {
        Region region = new Region();
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        mRegionMap.put(index, region);
    }

    public void recycle() {
        if (mRegionMap != null) {
            for (Region value : mRegionMap.values()) {
                value.setEmpty();
            }
        }
    }

    private Paint generatePaint(int color, Paint.Style style, int width) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(style);
        paint.setStrokeWidth(width);
        return paint;
    }
}
//RectF(270.0, 255.57793, 280.45935, 330.0)
//RectF(352.44226, 91.60416, 413.24265, 135.77824)
//RectF(549.4228, 60.16446, 658.36017, 87.32559)
//RectF(763.8401, 179.01785, 796.7853, 246.56543)
//RectF(734.22174, 386.13614, 804.09985, 517.5578)
//RectF(540.0, 589.54065, 614.4221, 600.0)
//RectF(321.56546, 488.70206, 404.99994, 563.82684)