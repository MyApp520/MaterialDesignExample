package com.example.commonlib.util;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;

import com.example.commonlib.R;
import com.example.commonlib.mpandroidchart.PieValueFormatter;
import com.example.commonlib.mpandroidchart.view.FixPieChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smile on 2019/6/13.
 */

public class MPChartUtils {
    private static final String TAG = MPChartUtils.class.getSimpleName();

    /**
     * 不显示无数据的提示
     *
     * @param mChart
     */
    public static void NotShowNoDataText(Chart mChart) {
        mChart.clear();
        mChart.notifyDataSetChanged();
        mChart.setNoDataText("你还没有记录数据");
        mChart.setNoDataTextColor(Color.WHITE);
        mChart.invalidate();
    }

    /**
     * 配置Chart 基础设置
     *
     * @param mChart       图表
     * @param mLabels      x 轴标签
     * @param yMax         y 轴最大值
     * @param yMin         y 轴最小值
     * @param isShowLegend 是否显示图例
     */
    public static void configChart(CombinedChart mChart, List<String> mLabels, float yMax, float yMin, boolean isShowLegend) {

        mChart.setDrawGridBackground(false);
        mChart.setDrawBorders(false);
        mChart.setScaleEnabled(false);
        mChart.setDragEnabled(true);
        mChart.setNoDataText("");
        // 不显示描述数据
        mChart.getDescription().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);

        Legend legend = mChart.getLegend();
        // 是否显示图例
        if (isShowLegend) {
            legend.setEnabled(true);
            legend.setTextColor(Color.WHITE);
            legend.setForm(Legend.LegendForm.CIRCLE);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setYEntrySpace(20f);
            //图例的大小
            legend.setFormSize(7f);
            // 图例描述文字大小
            legend.setTextSize(10);
            legend.setXEntrySpace(20f);

        } else {
            legend.setEnabled(false);
        }


        XAxis xAxis = mChart.getXAxis();

        // 是否显示x轴线
        xAxis.setDrawAxisLine(true);
        // 设置x轴线的颜色
        xAxis.setAxisLineColor(Color.parseColor("#4cffffff"));
        // 是否绘制x方向网格线
        xAxis.setDrawGridLines(false);
        //x方向网格线的颜色
        xAxis.setGridColor(Color.parseColor("#30FFFFFF"));

        // 设置x轴数据的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 设置x轴文字的大小
        xAxis.setTextSize(12);

        // 设置x轴数据偏移量
        xAxis.setYOffset(5);
        final List<String> labels = mLabels;
        // 显示x轴标签
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                if (index < 0 || index >= labels.size()) {
                    return "";
                }
                return labels.get(index);
                // return labels.get(Math.min(Math.max((int) value, 0), labels.size() - 1));
            }

        };
        // 引用标签
        xAxis.setValueFormatter(formatter);
        // 设置x轴文字颜色
        xAxis.setTextColor(mChart.getResources().getColor(R.color.char_text_color));
        // 设置x轴每最小刻度 interval
        xAxis.setGranularity(1f);

        YAxis yAxis = mChart.getAxisLeft();
        //设置x轴的最大值
        yAxis.setAxisMaximum(yMax);
        // 设置y轴的最大值
        yAxis.setAxisMinimum(yMin);
        // 不显示y轴
        yAxis.setDrawAxisLine(false);
        // 设置y轴数据的位置
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // 不从y轴发出横向直线
        yAxis.setDrawGridLines(false);
        // 是否显示y轴坐标线
        yAxis.setDrawZeroLine(true);
        // 设置y轴的文字颜色
        yAxis.setTextColor(mChart.getResources().getColor(R.color.char_text_color));
        // 设置y轴文字的大小
        yAxis.setTextSize(12);
        // 设置y轴数据偏移量
        //yAxis.setXOffset(30);
        // yAxis.setYOffset(-3);
        yAxis.setXOffset(15);
        // yAxis.setGranularity(yGranularity);
        yAxis.setLabelCount(5, false);
        //yAxis.setGranularity(5);//interval

        Matrix matrix = new Matrix();
        // 根据数据量来确定 x轴缩放大倍
        if (mLabels.size() <= 10) {
            matrix.postScale(1.0f, 1.0f);
        } else if (mLabels.size() <= 15) {
            matrix.postScale(1.5f, 1.0f);
        } else if (mLabels.size() <= 20) {
            matrix.postScale(2.0f, 1.0f);
        } else {
            matrix.postScale(3.0f, 1.0f);
        }

        // 在图表动画显示之前进行缩放
        mChart.getViewPortHandler().refresh(matrix, mChart, false);
        // x轴执行动画
        mChart.animateX(500);

    }

    /**
     * 初始化数据
     *
     * @param chart
     * @param lineDatas
     */
    public static void initData(CombinedChart chart, LineData... lineDatas) {
        CombinedData combinedData = new CombinedData();
        for (LineData lineData : lineDatas) {
            combinedData.setData(lineData);
        }
        chart.setData(combinedData);
        chart.invalidate();
    }

    /**
     * 获取LineDataSet
     *
     * @param entries
     * @param label
     * @param textColor
     * @param lineColor
     * @return
     */
    public static LineDataSet getLineData(List<Entry> entries, String label, int textColor, int lineColor, boolean isFill) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        // 设置曲线的颜色
        dataSet.setColor(lineColor);
        //数值文字颜色
        dataSet.setValueTextColor(textColor);
        // 模式为贝塞尔曲线
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        // 是否绘制数据值
        dataSet.setDrawValues(false);
        // 是否绘制圆点
        dataSet.setDrawCircles(true);
        dataSet.setDrawCircleHole(false);
        // 这里有一个坑，当我们想隐藏掉高亮线的时候，MarkerView 跟着不见了
        // 因此只有将它设置成透明色
        dataSet.setHighlightEnabled(true);// 隐藏点击时候的高亮线
        //设置高亮线为透明色
        dataSet.setHighLightColor(Color.TRANSPARENT);

        if (isFill) {
            //是否设置填充曲线到x轴之间的区域
            dataSet.setDrawFilled(true);
            // 填充颜色
            dataSet.setFillColor(lineColor);
        }
        //设置圆点的颜色
        dataSet.setCircleColor(lineColor);
        // 设置圆点半径
        dataSet.setCircleRadius(3.5f);
        // 设置线的宽度
        dataSet.setLineWidth(1f);
        return dataSet;
    }

    /**
     * 获取barDataSet
     *
     * @param entries
     * @param label
     * @param textColor
     * @param lineColor
     * @return
     */
    public static BarDataSet getBarDataSet(List<BarEntry> entries, String label, int textColor, int lineColor) {
        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setBarBorderWidth(5);
        dataSet.setBarShadowColor(lineColor);
        dataSet.setValueTextColor(textColor);
        dataSet.setDrawValues(false);
        return dataSet;
    }

    /**
     * 初始化柱状图基础设置
     *
     * @param barChart
     * @param xLabels
     */
    public static void initBarChart(BarChart barChart, final List<String> xLabels) {
        barChart.getDescription().setEnabled(false);//设置不显示图形描述信息
        barChart.setPinchZoom(true);//设置按比例放缩柱状图
        barChart.setScaleEnabled(false);
        barChart.setDragEnabled(false);
        // 设置是否可触摸
        barChart.setTouchEnabled(false);
        barChart.setNoDataText("咋的了，没数据啊"); // 没有数据时的提示文案

        //x坐标轴设置
        XAxis xAxis = barChart.getXAxis();//获取x轴
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴标签显示位置
        xAxis.setDrawAxisLine(false);//是否绘制坐标轴轴线
        xAxis.setDrawGridLines(false);//是否绘制格网线
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        // 显示x轴标签
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabels.get(Math.min(Math.max((int) value, 0), xLabels.size() - 1));
            }
        });
        xAxis.setTextSize(10);//设置标签字体大小
        xAxis.setTextColor(barChart.getResources().getColor(R.color.char_text_color));
        xAxis.setAxisLineColor(Color.parseColor("#e56ebc"));//设置轴线的颜色
        xAxis.setAxisLineWidth(1.0f);//设置轴线宽度
        xAxis.setLabelCount(xLabels.size());//设置标签显示的个数

        //y轴设置
        YAxis leftAxis = barChart.getAxisLeft();//获取左侧y轴
        leftAxis.setEnabled(true);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);//设置y轴标签显示在外侧
        leftAxis.setDrawLabels(true);//是否绘制y轴标签
        leftAxis.setDrawAxisLine(false);//是否绘制坐标轴轴线
        leftAxis.setDrawGridLines(true);//是否绘制格网线
        leftAxis.setGridColor(ContextCompat.getColor(barChart.getContext(), R.color.chart_grid_line_color));//网格线颜色
        leftAxis.setLabelCount(5);//Y轴分成几个层级
        leftAxis.setAxisMaximum(2000f);
        leftAxis.setAxisMinimum(0f);//设置Y轴最小值
        leftAxis.setAxisLineWidth(1.0f);//设置Y轴轴线宽度
        leftAxis.setAxisLineColor(Color.parseColor("#e56ebc"));
        leftAxis.setTextColor(barChart.getResources().getColor(R.color.char_text_color));
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);//Y轴标签值
            }
        });

        barChart.getAxisRight().setEnabled(false);//禁用右侧y轴
        barChart.getLegend().setEnabled(false);//是否显示图例示意图

//        Matrix matrix = new Matrix();
//        // 根据数据量来确定 x轴缩放大倍
//        if (xLabels.size() <= 10) {
//            matrix.postScale(1.0f, 1.0f);
//        } else if (xLabels.size() <= 15) {
//            matrix.postScale(1.5f, 1.0f);
//        } else if (xLabels.size() <= 20) {
//            matrix.postScale(2.0f, 1.0f);
//        } else {
//            matrix.postScale(3.0f, 1.0f);
//        }
//        barChart.getViewPortHandler().refresh(matrix, barChart, false);
//        barChart.setExtraBottomOffset(10);//距视图窗口底部的偏移，类似与paddingbottom
//        barChart.setExtraTopOffset(30);//距视图窗口顶部的偏移，类似与paddingtop
//        barChart.setFitBars(true);//使两侧的柱图完全显示
//        barChart.animateX(1500);//数据显示动画，从左往右依次显示
    }

    /**
     * 获取柱状图图表数据
     *
     * @param chart
     * @param entries
     * @param title
     * @param barColor
     */
    public static BarData getBarData(BarChart chart, List<BarEntry> entries, String title, int barColor) {
        BarDataSet set = new BarDataSet(entries, title);
        //设置柱状的颜色
        set.setColor(ContextCompat.getColor(chart.getContext(), barColor));
//        set.setBarBorderColor();//给每个柱状设置一个边框，这个边框的颜色
//        set.setBarBorderWidth(3f);//给每个柱状设置一个边框，这个边框的宽度
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        BarData data = new BarData(dataSets);
        // 设置柱状bar的宽度，但是点很多少的时候好像没作用，会拉得很宽
        data.setBarWidth(0.3f);
        //柱状头顶的文本内容的字体大小
        data.setValueTextSize(10f);
        //柱状头顶的文本内容的字体颜色
        data.setValueTextColor(ContextCompat.getColor(chart.getContext(), barColor));
        //设置每个柱状头顶显示的文本内容
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf(((int) (entry.getY())));//设置每个柱状头顶显示的文本内容
            }
        });
        return data;
    }

    /**
     * 初始化饼状图基础设置
     * @param pieChart
     * @param centerText
     * @param centerTextColor
     */
    public static void initPieChart(FixPieChart pieChart, CharSequence centerText, int centerTextColor) {
        pieChart.setLogEnabled(AppDebugUtil.isDebug());//打开图表的日志

        pieChart.setDrawCenterText(false);  //饼状图中间文字不显示
        Description description = pieChart.getDescription();
        description.setText("我是Description()");//图形描述信息
        description.setEnabled(false);//是否显示图形描述信息
        pieChart.setRotationAngle(-90); // 设置饼图从哪个角度开始绘制
        pieChart.setUsePercentValues(true);  //显示成百分比
        //设置部分手势事件
        pieChart.setTouchEnabled(false);// 设置是否可触摸，饼图要想响应手势事件，必须设为true
        pieChart.setRotationEnabled(false); // 是否可以手动旋转
        pieChart.setHighlightPerTapEnabled(false);//设置为false之后，点击每一块不能向外突出

        //设置饼图中心孔显示的文本内容
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字
        pieChart.setCenterText(centerText);
        pieChart.setCenterTextColor(ContextCompat.getColor(pieChart.getContext(), centerTextColor));
        pieChart.setCenterTextSize(12f);

        //设置显示饼图还是显示圆环
        pieChart.setDrawHoleEnabled(true); //false:设置实心；   true:关闭实心，饼图由圆环和中心孔两部分组成
        pieChart.setHoleRadius(56f);//当饼图以圆环显示时，设置中心孔的半径与饼图半径的百分比
        //默认情况下，会绘制一个透明圆覆盖在饼图上，这里设置这个圆的半径与饼图半径的百分比，这个圆的背景色默认是半透明的
        pieChart.setTransparentCircleRadius(1f);//如果不想显示这个透明圆，半径设置成一个很小的值（看源码，好像是小于setHoleRadius即可）
        pieChart.setTransparentCircleColor(ContextCompat.getColor(pieChart.getContext(), R.color.white));//设置透明圆的背景色
        pieChart.setTransparentCircleAlpha(122);//设置透明圆背景色的透明度（取值范围0-255）

        //设置饼图上面的文字，也就是PieEntry的label是否显示
        pieChart.setDrawEntryLabels(false);
        //设置饼图每一块的说明的字体颜色
        pieChart.setEntryLabelColor(ContextCompat.getColor(pieChart.getContext(), R.color.backgroup));
        //设置饼图每一块说明的字体大小
        pieChart.setEntryLabelTextSize(14f);
        //设置字体，加粗Typeface.DEFAULT_BOLD
        pieChart.setEntryLabelTypeface(Typeface.DEFAULT);

        //图例示意图相关设置
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);//设置竖直方向显示位置,legend.setYOffset设置偏移量
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);//设置水平方向显示位置，legend.setXOffset设置偏移量
        legend.setForm(Legend.LegendForm.SQUARE);  //设置图例示意图形状，默认是方形
        legend.setXEntrySpace(7f);//设置距离饼图的距离，防止与饼图重合
        legend.setYEntrySpace(5f);
        legend.setWordWrapEnabled(true);//设置允许示意图的内容换行，否则数据过多的时候会超出屏幕范围
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);//内容排列方向
        legend.setDrawInside(false);//可自行设置为true后查看效果
        legend.setEnabled(false);//是否显示图例示意图

        pieChart.setExtraOffsets(0, 8f, 0, 8f);//注意：查源码可知，此处8f指的是8dp，而不是8个像素
    }

    /**
     * 获取饼状图所需数据
     * @param pieChart
     * @param colors
     * @param yValues
     * @return
     */
    public static PieData getPieData(FixPieChart pieChart, List<Integer> colors, List<PieEntry> yValues) {
        // y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "");//第二个参数：示例图的描述信息，可以通过pieChart.getLegend()关掉
        pieDataSet.setSliceSpace(0f); //设置每个饼状图之间的距离
        // 饼图颜色
        pieDataSet.setColors(colors);
        pieDataSet.setSelectionShift(0f); //选中某部分时，这部分图形比其他图形突出的长度

        //这一段代码就是实现加一个横线然后将模块的数据放在外面的效果
        // 当值位置为外边线时，折线的前半段长度。
        pieDataSet.setValueLinePart1Length(0.3f);
        // 当值位置为外边线时，折线的后半段长度。
        pieDataSet.setValueLinePart2Length(0.4f);
        // 当ValuePosits为OutsiDice时，饼图显示：折线起始位置偏移占饼图半径大小的百分比；圆环显示：折线起始位置偏移占圆环半径大小的百分比；
        pieDataSet.setValueLinePart1OffsetPercentage(70f);
        //当值显示在界面外面的时候是否允许改变折线的长度
        pieDataSet.setValueLineVariableLength(true);
        // 当值位置为外边线时，设置线的宽度dp
        pieDataSet.setValueLineWidth(1f);
        // 当值位置为外边线时，表示线的颜色。
        pieDataSet.setValueLineColor(ContextCompat.getColor(pieChart.getContext(), R.color.backgroup));
        //设置项X值拿出去
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //设置将Y轴的值拿出去
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData pieData = new PieData(pieDataSet);
        // 是否在饼图上显示每个部分的百分比数值，也就是PieEntry中的value值
        pieData.setDrawValues(true);
        // 文字的大小
        pieData.setValueTextSize(10);
        // 文字的颜色
        pieData.setValueTextColor(ContextCompat.getColor(pieChart.getContext(), R.color.backgroup));
        // 字体的样式，加粗DEFAULT_BOLD
        pieData.setValueTypeface(Typeface.DEFAULT);
        pieData.setValueFormatter(new PieValueFormatter(true));//格式化显示的数据为%百分比
        return pieData;
    }
}
