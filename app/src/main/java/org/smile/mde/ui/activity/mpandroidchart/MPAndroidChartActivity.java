package org.smile.mde.ui.activity.mpandroidchart;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.example.commonlib.base.BaseActivity;
import com.example.commonlib.mpandroidchart.PieValueFormatter;
import com.example.commonlib.util.AppDebugUtil;
import com.example.commonlib.util.MPChartUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.smile.mde.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MPAndroidChartActivity extends BaseActivity {

    @BindView(R.id.bar_chart)
    BarChart barChart;
    @BindView(R.id.pie_chart)
    PieChart pieChart;
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
        setBarChartData();
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
        pieChart.setCenterText(getCenterText());
        pieChart.setCenterTextColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroup));
        pieChart.setCenterTextSize(12f);

        //设置显示饼图还是显示圆环
        pieChart.setDrawHoleEnabled(true); //false:设置实心；   true:关闭实心，饼图由圆环和中心孔两部分组成
        pieChart.setHoleRadius(56f);//当饼图以圆环显示时，设置中心孔的半径与饼图半径的百分比
        //默认情况下，会绘制一个透明圆覆盖在饼图上，这里设置这个圆的半径与饼图半径的百分比，这个圆的背景色默认是半透明的
        pieChart.setTransparentCircleRadius(1f);//如果不想显示这个透明圆，半径设置成一个很小的值（看源码，好像是小于setHoleRadius即可）
        pieChart.setTransparentCircleColor(ContextCompat.getColor(getApplicationContext(), R.color.white));//设置透明圆的背景色
        pieChart.setTransparentCircleAlpha(122);//设置透明圆背景色的透明度（取值范围0-255）

        //设置饼图上面的文字，也就是PieEntry的label是否显示
        pieChart.setDrawEntryLabels(false);
        //设置饼图每一块的说明的字体颜色
        pieChart.setEntryLabelColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroup));
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
        pieChart.setData(getPieData());
        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    /**
     * 获取饼状图数据
     */
    private PieData getPieData() {
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(205, 205, 205));
        colors.add(Color.rgb(114, 188, 223));
        colors.add(Color.rgb(255, 123, 124));
        colors.add(Color.rgb(57, 135, 200));
        colors.add(Color.rgb(157, 235, 112));
        colors.add(Color.rgb(77, 15, 100));
        colors.add(Color.rgb(105, 75, 95));
        colors.add(Color.rgb(214, 188, 123));
        colors.add(Color.rgb(155, 223, 224));
        colors.add(Color.rgb(157, 105, 100));
        colors.add(Color.rgb(117, 135, 212));
        colors.add(Color.rgb(177, 165, 200));
        colors.add(Color.rgb(25, 13, 222));
        colors.add(Color.rgb(188, 235, 20));
        colors.add(Color.rgb(100, 235, 12));
        colors.add(Color.rgb(207, 215, 90));

        // TODO: 2019/6/14 将一个饼形图分成yValues.size()个部分， 每个部分所占百分比分别为：12:16:33:39
        // PieEntry第一个参数：表示该部分所占百分比；第二个参数：该部分在饼图中要显示的文字内容(也就是饼图每个部分的文本标签)
        List<PieEntry> yValues = new ArrayList<>();  //yVals用来表示封装每个饼块的实际数据
        yValues.add(new PieEntry(5, "小五"));
        yValues.add(new PieEntry(7, "七七"));
        yValues.add(new PieEntry(2.5f, "二五"));
        yValues.add(new PieEntry(10, "双十"));
        yValues.add(new PieEntry(11, "单端"));
        yValues.add(new PieEntry(11.5f, "在线率"));
        yValues.add(new PieEntry(8f, "巴士"));
        yValues.add(new PieEntry(5f, "舞"));
        yValues.add(new PieEntry(7f, "器"));
        yValues.add(new PieEntry(0.3f, "离线"));
        yValues.add(new PieEntry(0.2f, "完好"));
        yValues.add(new PieEntry(12f, "师部"));
        yValues.add(new PieEntry(14f, "要塞"));
        yValues.add(new PieEntry(6f, "陆军"));
        yValues.add(new PieEntry(0.2f, "在线2"));
        yValues.add(new PieEntry(0.3f, "离线2"));

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
        // 当值位置为外边线时，设置线的宽度
        pieDataSet.setValueLineWidth(1f);
        // 当值位置为外边线时，表示线的颜色。
        pieDataSet.setValueLineColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroup));
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
        pieData.setValueTextColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroup));
        // 字体的样式，加粗DEFAULT_BOLD
        pieData.setValueTypeface(Typeface.DEFAULT);
        pieData.setValueFormatter(new PieValueFormatter(true));//格式化显示的数据为%百分比
        return pieData;
    }

    /**
     * 柱状图
     */
    private void setBarChartData() {
        List<String> xLabels = new ArrayList<>();
        xLabels.add("在线总数");
        xLabels.add("离线总数");
        xLabels.add("完好总数");
        xLabels.add("故障总数");
        // 1.配置基础图表配置
        MPChartUtils.configBarChart(barChart, xLabels);
        // 2,初始化数据并绘制
        ArrayList<BarEntry> yVals = new ArrayList<>();//Y轴方向第一组数组
        yVals.add(new BarEntry(0, 1388));
        yVals.add(new BarEntry(1, 612));
        yVals.add(new BarEntry(2, 1797));
        yVals.add(new BarEntry(3, 203));
        MPChartUtils.initBarChart(barChart, yVals, "镜头数", R.color.backgroup);
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
