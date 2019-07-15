package com.example.commonlib.mpandroidchart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.example.commonlib.mpandroidchart.renderer.SecondPieChartRendererFixCover;
import com.github.mikephil.charting.charts.PieChart;

/**
 * Created by smile on 2019/6/15.
 */

public class FixPieChart extends PieChart {

    public FixPieChart(Context context) {
        this(context, null);
    }

    public FixPieChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixPieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        if (mRenderer != null) {
            mRenderer = null;
        }
        Log.e("FixPieChart", "init: mRenderer2 = " + mRenderer);
        //此处把mRenderer替换成我们自己的PieChartRenderer
//        mRenderer = new PieChartRendererFixCover(this, mAnimator, mViewPortHandler);//第一种方式
        mRenderer = new SecondPieChartRendererFixCover(this, mAnimator, mViewPortHandler);//第二种方式
        Log.e("FixPieChart", "init: mRenderer3 = " + mRenderer);
    }
}
