package com.example.commonlib.util;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.example.commonlib.R;
import com.example.commonlib.mpandroidchart.LineChartMarkView;
import com.example.commonlib.mpandroidchart.PieValueFormatter;
import com.example.commonlib.mpandroidchart.view.FixPieChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Utils;
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
     * 初始化柱状图基础设置
     *
     * @param barChart
     * @param xLabels
     */
    public static void initBarChart(BarChart barChart, final List<String> xLabels, boolean isCanScroll) {
        barChart.getDescription().setEnabled(false);//设置不显示图形描述信息
        barChart.setPinchZoom(true);//设置按比例放缩柱状图
        barChart.setScaleEnabled(false);
        barChart.setDragEnabled(isCanScroll);
        // 设置是否可触摸
        barChart.setTouchEnabled(isCanScroll);
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
        YAxis leftYAxis = barChart.getAxisLeft();//获取左侧y轴
        leftYAxis.setEnabled(true);
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);//设置y轴标签显示在外侧
        leftYAxis.setDrawLabels(true);//是否绘制y轴标签
        leftYAxis.setDrawAxisLine(false);//是否绘制坐标轴轴线
        leftYAxis.setDrawGridLines(true);//是否绘制格网线
        leftYAxis.setLabelCount(6);//Y轴分成几个层级
        leftYAxis.setAxisMinimum(0f);//设置Y轴最小值
        leftYAxis.setAxisLineWidth(1.0f);//设置Y轴轴线宽度
        leftYAxis.setAxisLineColor(Color.parseColor("#e56ebc"));
        leftYAxis.setTextColor(barChart.getResources().getColor(R.color.char_text_color));
        leftYAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);//Y轴标签值
            }
        });
        //网格线颜色
        leftYAxis.setGridColor(ContextCompat.getColor(barChart.getContext(), R.color.chart_grid_line_color));
        //设置网格线为虚线效果
        leftYAxis.enableGridDashedLine(10f, 10f, 0f);
        //是否绘制0所在的网格线
        leftYAxis.setDrawZeroLine(false);
        //设置绘制零线宽度
        leftYAxis.setZeroLineWidth(0.5f);
        //绘制零线颜色
        leftYAxis.setZeroLineColor(Color.parseColor("#c0c0c0"));

        barChart.getAxisRight().setEnabled(false);//禁用右侧y轴
        barChart.getLegend().setEnabled(false);//是否显示图例示意图

        if (isCanScroll) {
            Matrix matrix = new Matrix();
            // 根据数据量来确定 x轴缩放大倍
            if (xLabels.size() <= 10) {
                matrix.postScale(1.0f, 1.0f);
            } else if (xLabels.size() <= 15) {
                matrix.postScale(1.5f, 1.0f);
            } else if (xLabels.size() <= 20) {
                matrix.postScale(2.0f, 1.0f);
            } else {
                matrix.postScale(3.0f, 1.0f);
            }
            barChart.getViewPortHandler().refresh(matrix, barChart, false);
            barChart.animateX(1500);//数据显示动画，从左往右依次显示
        }
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
     * 初始化饼状图基础设置 PieChart
     * @param pieChart
     */
    public static void initPieChart(PieChart pieChart, String descriptionText) {
        pieChart.setLogEnabled(AppDebugUtil.isDebug());//打开图表的日志

        Description description = pieChart.getDescription();
        description.setPosition(Utils.convertDpToPixel(76f), Utils.convertDpToPixel(28f));
        description.setText(descriptionText);//图形描述信息
        description.setTextColor(ContextCompat.getColor(pieChart.getContext(), R.color.black));
        description.setTextSize(16f);
        description.setTextAlign(Paint.Align.CENTER);// 对齐方式：居中对齐
        description.setEnabled(!TextUtils.isEmpty(descriptionText));//是否显示图形描述信息

        pieChart.setRotationAngle(-90); // 设置饼图从哪个角度开始绘制
        pieChart.setUsePercentValues(false);  //显示成百分比
        //设置部分手势事件
        pieChart.setTouchEnabled(false);// 设置是否可触摸，饼图要想响应手势事件，必须设为true
        pieChart.setRotationEnabled(false); // 是否可以手动旋转
        pieChart.setHighlightPerTapEnabled(false);//设置为false之后，点击每一块不能向外突出

        //饼状图中间文字不显示
        pieChart.setDrawCenterText(false);
        //设置显示饼图还是显示圆环
        pieChart.setDrawHoleEnabled(true); //false:设置实心；   true:关闭实心，饼图由圆环和中心孔两部分组成
        pieChart.setHoleRadius(80f);//当饼图以圆环显示时，设置中心孔的半径与饼图半径的百分比
        //默认情况下，会绘制一个半透明圆覆盖在饼图上，这里设置这个圆的半径与饼图半径的百分比，这个圆的背景色默认是半透明的
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
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);//设置水平方向显示位置，legend.setXOffset设置偏移量
        legend.setYOffset(-18);//单位dp
        legend.setXOffset(16);
        legend.setForm(Legend.LegendForm.SQUARE);  //设置图例示意图形状，默认是方形
        legend.setFormSize(10);//设置图例示意图图形大小（单位：dp）
        legend.setTextSize(12);//文字大小
        legend.setTextColor(ContextCompat.getColor(pieChart.getContext(), R.color.insideCircle));
        legend.setXEntrySpace(8f);//示意图水平显示时，设置各个条目的间隔，默认为6dp
        legend.setYEntrySpace(2f);
        legend.setWordWrapEnabled(true);//设置允许示意图的内容换行，否则数据过多的时候会超出屏幕范围
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);//内容排列方向
        legend.setDrawInside(false);//可自行设置为true后查看效果
        legend.setEnabled(true);//是否显示图例示意图

        pieChart.setExtraOffsets(0, 22f, 0, 28f);//注意：查源码可知，此处8f指的是8dp，而不是8个像素
    }

    /**
     * 初始化饼状图基础设置 FixPieChart
     * @param pieChart
     * @param centerText
     * @param centerTextColor
     */
    public static void initFixPieChart(FixPieChart pieChart, CharSequence centerText, int centerTextColor) {
        pieChart.setLogEnabled(AppDebugUtil.isDebug());//打开图表的日志

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
        //默认情况下，会绘制一个半透明圆覆盖在饼图上，这里设置这个圆的半径与饼图半径的百分比，这个圆的背景色默认是半透明的
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
        legend.setXEntrySpace(7f);//示意图水平显示时，设置各个条目的间隔，默认为6dp
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
    public static PieData getPieData(PieChart pieChart, List<Integer> colors, List<PieEntry> yValues, boolean isDrawValues) {
        // y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "");//第二个参数：示例图的描述信息，可以通过pieChart.getLegend()关掉
        pieDataSet.setSliceSpace(0f); //设置每个饼状图之间的距离
        // 饼图颜色
        pieDataSet.setColors(colors);
        pieDataSet.setSelectionShift(0f); //选中某部分时，这部分图形比其他图形突出的长度

        if (isDrawValues) {
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
        }

        PieData pieData = new PieData(pieDataSet);
        // 是否在饼图上显示每个部分的百分比数值，也就是PieEntry中的value值
        pieData.setDrawValues(isDrawValues);
        if (isDrawValues) {
            // 文字的大小
            pieData.setValueTextSize(10);
            // 文字的颜色
            pieData.setValueTextColor(ContextCompat.getColor(pieChart.getContext(), R.color.colorAccent));
            // 字体的样式，加粗DEFAULT_BOLD
            pieData.setValueTypeface(Typeface.DEFAULT);
            pieData.setValueFormatter(new PieValueFormatter(true));//格式化显示的数据为%百分比
        }
        return pieData;
    }

    /**
     * 初始化LineChart
     * @param lineChart
     */
    public static void initLineChart(LineChart lineChart, List<String> xDataList) {
        /** 开始配置 X轴 **/
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(Color.parseColor("#333333"));
        xAxis.setTextSize(11f);
        xAxis.setDrawAxisLine(false);//是否绘制轴线
        xAxis.setDrawGridLines(false);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setGranularity(1f);//禁止放大x轴标签重绘
        xAxis.setDrawGridLines(false);//是否绘制竖直网格线
        xAxis.setGridColor(ContextCompat.getColor(lineChart.getContext(), R.color.chart_grid_line_color));//网格线颜色
//        xAxis.setSpaceMin(0);// 设置数据之间的间距
        xAxis.setLabelCount(xDataList.size());
        IndexAxisValueFormatter indexAxisValueFormatter = new IndexAxisValueFormatter(xDataList);
        xAxis.setValueFormatter(indexAxisValueFormatter);
        // 设置 MarkerView
        LineChartMarkView mv = new LineChartMarkView(lineChart.getContext(), indexAxisValueFormatter);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);

        /** 开始配置 Y轴 **/
        YAxis leftYAxis = lineChart.getAxisLeft();
        //是否绘制轴线
        leftYAxis.setDrawAxisLine(false);
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // 保证Y轴从0开始，不然会上移一点
        leftYAxis.setAxisMinimum(0f);
        // s设置字体大小
        leftYAxis.setTextSize(10f);
        // 设置Y轴最多显示的数据个数
        leftYAxis.setLabelCount(6);
        leftYAxis.setDrawGridLines(true);//是否绘制格水平网格线
        leftYAxis.setGridColor(ContextCompat.getColor(lineChart.getContext(), R.color.chart_grid_line_color));//网格线颜色
        //设置网格线为虚线效果
        leftYAxis.enableGridDashedLine(10f, 10f, 0f);
        //是否绘制0所在的网格线
        leftYAxis.setDrawZeroLine(false);
        //设置绘制零线宽度
        leftYAxis.setZeroLineWidth(0.5f);
        //绘制零线颜色
        leftYAxis.setZeroLineColor(Color.parseColor("#c0c0c0"));
//        // 设置警戒线
//        leftYAxis.addLimitLine(ll1);

        /** 开始配置图表 **/
        lineChart.setDrawBorders(true);// 是否在折线图上添加边框
        // 曲线描述 -标题
        Description description = lineChart.getDescription();
        description.setText("我是Description()");//图形描述信息
        description.setTextSize(16f);// 标题字体大小
        description.setTextColor(ContextCompat.getColor(lineChart.getContext(), R.color.backgroup));// 标题字体颜色
        description.setEnabled(false);//是否显示图形描述信息
        // 设置是否启动触摸响应
        lineChart.setTouchEnabled(true);
        // 是否可以拖拽
        lineChart.setDragEnabled(true);
        lineChart.setDragDecelerationFrictionCoef(0.9f);
        // 是否可以缩放
        lineChart.setScaleEnabled(true);
        // 如果禁用，可以在x和y轴上分别进行缩放
        lineChart.setPinchZoom(false);
        //设置是否可以通过双击屏幕放大图表。默认是true
        lineChart.setDoubleTapToZoomEnabled(false);
//        // 设置背景色
//        lineChart.setBackgroundColor(ContextCompat.getColor(lineChart.getContext(), R.color.colorAccent));
        // 如果没有数据的时候，会显示这个
        lineChart.setNoDataText("暂无数据");
        // 禁止绘制图表边框的线
        lineChart.setDrawBorders(false);
        // 是否显示网格区域的背景色
        lineChart.setDrawGridBackground(false);
        // 设置网格区域的背景色
        lineChart.setGridBackgroundColor(ContextCompat.getColor(lineChart.getContext(), R.color.backgroup));
//        //设置整个坐标系的上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
//        lineChart.setExtraOffsets(20,20f,0f,0f);

        //图例示意图相关设置
        Legend legend = lineChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);//设置竖直方向显示位置,legend.setYOffset设置偏移量
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);//设置水平方向显示位置，legend.setXOffset设置偏移量
        legend.setForm(Legend.LegendForm.CIRCLE);  //设置图例示意图形状，默认是方形
        legend.setXEntrySpace(7f);//示意图水平显示时，设置各个条目的间隔，默认为6dp
        legend.setYEntrySpace(5f);
        legend.setWordWrapEnabled(true);//设置允许示意图的内容换行，否则数据过多的时候会超出屏幕范围
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);//内容排列方向
        legend.setDrawInside(false);//可自行设置为true后查看效果
        legend.setEnabled(true);//是否显示图例示意图

        // 隐藏右侧Y轴（只在左侧的Y轴显示刻度）
        lineChart.getAxisLeft().setEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
    }

    /**
     * 曲线赋值与设置
     *
     * @param lineChart
     * @param yDataList y轴数据
     *
     * @return LineData
     */
    public static LineData getLineData(LineChart lineChart, List<Entry> yDataList, String curveLabel, int lineColor) {
        // 一个LineDataSet表示一条曲线数据对象
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yDataList, curveLabel);
        initLineDataSet(lineChart, lineDataSet, lineColor);

        // y轴的数据
        LineData lineData = lineChart.getLineData();
        if (lineData == null) {
            lineData = new LineData(lineDataSet);
        } else {
            lineData.addDataSet(lineDataSet);
        }
        return lineData;
    }

    /**
     *
     * @param lineChart
     * @param lineDataSetList
     * @return
     */
    public static LineData getLineData(LineChart lineChart, List<LineDataSet> lineDataSetList) {
        LineData lineData = new LineData();
        // 一个LineDataSet表示一条曲线数据对象
        for (LineDataSet lineDataSet : lineDataSetList) {
            lineData.addDataSet(lineDataSet);
        }

        return lineData;
    }

    public static LineDataSet initLineDataSet(LineChart lineChart, LineDataSet lineDataSet, int lineColor) {
        // 坐标轴在左侧
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        //设置显示曲线CUBIC_BEZIER、折线LINEAR等模式
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        // 是否显示坐标点的数据
        lineDataSet.setDrawValues(false);
        // 是否显示折线上坐标的圆点
        lineDataSet.setDrawCircles(true);
        // 折线上坐标圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        // 线宽
        lineDataSet.setLineWidth(2.0f);
        // 显示折线上坐标圆点大小
        lineDataSet.setCircleRadius(4.0f);
        // 折线的颜色
        lineDataSet.setColor(ContextCompat.getColor(lineChart.getContext(), lineColor));
        // 折线上坐标点的颜色
        lineDataSet.setCircleColor(ContextCompat.getColor(lineChart.getContext(), lineColor));
        // 高亮的线的设置（高亮线：点击折线的某个坐标，会在这个点上显示横纵两条直线）
        // 这里有一个坑，当我们想隐藏掉高亮线的时候，MarkerView 跟着不见了, 因此只有将setHighLightColor设置成透明色
        lineDataSet.setHighlightEnabled(true);//false则不显示
        lineDataSet.setHighLightColor(ContextCompat.getColor(lineChart.getContext(), R.color.transparent));
        // 设置显示曲线和X轴围成的区域阴影
        lineDataSet.setDrawFilled(false);
        lineDataSet.setFillAlpha(65);
        lineDataSet.setFillColor(ContextCompat.getColor(lineChart.getContext(), R.color.transparent));

        // 设置每条曲线图例标签名
        // lineDataSet.setLabel("标签");
        lineDataSet.setValueTextSize(14f);
        // 曲线弧度（区间0.05f-1f，默认0.2f）
        lineDataSet.setCubicIntensity(0.2f);

        return lineDataSet;
    }
}
