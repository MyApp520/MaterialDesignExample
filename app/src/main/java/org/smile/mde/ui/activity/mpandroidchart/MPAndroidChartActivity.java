package org.smile.mde.ui.activity.mpandroidchart;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.example.commonlib.base.BaseActivity;
import com.example.commonlib.mpandroidchart.view.FixPieChart;
import com.example.commonlib.util.MPChartUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;

import org.smile.mde.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MPAndroidChartActivity extends BaseActivity {

    @BindView(R.id.bar_chart)
    BarChart barChart;
    @BindView(R.id.pie_chart)
    FixPieChart pieChart;
    @BindView(R.id.tv_name)
    TextView tv_name;

    @Override
    protected int bindLayout() {
        return R.layout.activity_mpandroid_chart;
    }

    @Override
    protected void initView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Log.e(TAG, "initData: density = " + displayMetrics.density + ", densityDpi = " + displayMetrics.densityDpi
                + ", widthPixels = " + displayMetrics.widthPixels + ", heightPixels = " + displayMetrics.heightPixels);
    }

    @Override
    protected void initData() {
        showBarChart();
        showPieChart();
    }

    @Override
    protected boolean isUseDagger() {
        return false;
    }

    /**
     * 饼状图：饼图的半径由控件宽高中更小的那个决定
     */
    private void showPieChart() {
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(205, 205, 205));
        colors.add(Color.rgb(114, 188, 223));
        colors.add(Color.rgb(255, 123, 124));
        colors.add(Color.rgb(57, 135, 200));
//        colors.add(Color.rgb(157, 235, 112));
//        colors.add(Color.rgb(77, 15, 100));
//        colors.add(Color.rgb(105, 75, 95));
//        colors.add(Color.rgb(21, 188, 223));
//        colors.add(Color.rgb(211, 88, 193));
//        colors.add(Color.rgb(112, 208, 23));
//        colors.add(Color.rgb(155, 223, 224));
//        colors.add(Color.rgb(157, 105, 100));
//        colors.add(Color.rgb(117, 135, 212));
//        colors.add(Color.rgb(177, 165, 200));
//        colors.add(Color.rgb(25, 13, 222));
//        colors.add(Color.rgb(188, 235, 20));
//        colors.add(Color.rgb(100, 235, 12));
//        colors.add(Color.rgb(207, 215, 90));

        // TODO: 2019/6/14 将一个饼形图分成yValues.size()个部分， 每个部分所占百分比分别为：12:16:33:39
        // PieEntry第一个参数：表示该部分所占百分比；第二个参数：该部分在饼图中要显示的文字内容(也就是饼图每个部分的文本标签)
        List<PieEntry> yValues = new ArrayList<>();  //yVals用来表示封装每个饼块的实际数据
//        yValues.add(new PieEntry(5, "小五"));
//        yValues.add(new PieEntry(7, "七七"));
//        yValues.add(new PieEntry(2.5f, "二五"));
//        yValues.add(new PieEntry(10, "双十"));
//        yValues.add(new PieEntry(11, "单端"));
//        yValues.add(new PieEntry(11.5f, "在线率"));
//        yValues.add(new PieEntry(8f, "巴士"));
//        yValues.add(new PieEntry(5f, "舞"));
//        yValues.add(new PieEntry(0.1f, "零点1"));
//        yValues.add(new PieEntry(0f, "零"));
        yValues.add(new PieEntry(777f, "器"));
        yValues.add(new PieEntry(0.3f, "离线"));
        yValues.add(new PieEntry(0.2f, "完好"));
        yValues.add(new PieEntry(812f, "师部"));
//        yValues.add(new PieEntry(14f, "要塞"));
//        yValues.add(new PieEntry(6f, "陆军"));
//        yValues.add(new PieEntry(0.2f, "在线2"));
//        yValues.add(new PieEntry(0.3f, "离线2"));

        MPChartUtils.initPieChart(pieChart, getCenterText(), R.color.backgroup);
        pieChart.setData(MPChartUtils.getPieData(pieChart, colors, yValues));
        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    /**
     * 柱状图
     */
    private void showBarChart() {
        //X轴方向
        List<String> xLabels = new ArrayList<>();
        xLabels.add("在线总数");
        xLabels.add("离线总数");
        xLabels.add("完好总数");
        xLabels.add("故障总数");
        //Y轴方向
        ArrayList<BarEntry> yVals = new ArrayList<>();
        yVals.add(new BarEntry(0, 1988));
        yVals.add(new BarEntry(1, 6));
        yVals.add(new BarEntry(2, 1797));
        yVals.add(new BarEntry(3, 203));

        MPChartUtils.initBarChart(barChart, xLabels);
        barChart.setData(MPChartUtils.getBarData(barChart, yVals, "镜头数", R.color.backgroup));
        barChart.invalidate();
    }

    /**
     * Spanned.SPAN_INCLUSIVE_EXCLUSIVE 从起始下标到终了下标，包括起始下标
     * Spanned.SPAN_INCLUSIVE_INCLUSIVE 从起始下标到终了下标，同时包括起始下标和终了下标
     * Spanned.SPAN_EXCLUSIVE_EXCLUSIVE 从起始下标到终了下标，但都不包括起始下标和终了下标
     * Spanned.SPAN_EXCLUSIVE_INCLUSIVE 从起始下标到终了下标，包括终了下标
     *
     * @return
     */
    private CharSequence getCenterText() {
        SpannableString spannableString = new SpannableString("占比分析\n坪山街道");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#888888")), 5, 9, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.6f), 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.2f), 5, 9, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        tv_name.setText(spannableString);
        return spannableString;
    }
}
