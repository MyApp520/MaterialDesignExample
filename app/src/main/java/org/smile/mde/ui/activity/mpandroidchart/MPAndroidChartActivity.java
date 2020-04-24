package org.smile.mde.ui.activity.mpandroidchart;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.smile.commonlib.base.BaseActivity;
import com.smile.commonlib.mpandroidchart.view.FixPieChart;
import com.smile.commonlib.util.MPChartUtils;
import com.smile.commonlib.util.TimeUitls;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.smile.mde.R;
import org.smile.mde.view.PickerViewTimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class MPAndroidChartActivity extends BaseActivity implements OnTimeSelectListener {

    @BindView(R.id.common_bar_chart)
    BarChart commonBarChart;
    @BindView(R.id.fix_pie_chart)
    FixPieChart fixPieChart;
    @BindView(R.id.face_collect_line_chart)
    LineChart faceCollectLineChart;
    @BindView(R.id.scroll_bar_chart)
    BarChart scrollBarChart;
    @BindView(R.id.alarm_count_pie_chart)
    PieChart alarmCountPieChart;
    @BindView(R.id.key_people_pie_chart)
    PieChart keyPeoplePieChart;
    @BindView(R.id.escapee_people_pie_chart)
    PieChart escapeePeoplePieChart;
    @BindView(R.id.temp_control_people_pie_chart)
    PieChart tempControlPeoplePieChart;
    @BindView(R.id.tv_face_statistics_date)
    TextView tvFaceStatisticsDate;
    @BindView(R.id.tv_inspect_people_count)
    TextView tvInspectPeopleCount;
    @BindView(R.id.tv_enter_people_count)
    TextView tvEnterPeopleCount;
    @BindView(R.id.tv_exit_people_count)
    TextView tvExitPeopleCount;
    @BindView(R.id.tv_alarm_statistics_date)
    TextView tvAlarmStatisticsDate;
    @BindView(R.id.tv_enter_statistics_date)
    TextView tvEnterStatisticsDate;

    private TimePickerView timePickerView;

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
        showLineChart();
        showScrollBarChart();
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
        yValues.add(new PieEntry(777f, "总人数85人"));
        yValues.add(new PieEntry(18f, "已抓捕1888人"));
        yValues.add(new PieEntry(67f, "已盘查6798人"));
        yValues.add(new PieEntry(812f, "击毙2人"));
//        yValues.add(new PieEntry(14f, "要塞"));
//        yValues.add(new PieEntry(6f, "陆军"));
//        yValues.add(new PieEntry(0.2f, "在线2"));
//        yValues.add(new PieEntry(0.3f, "离线2"));

        MPChartUtils.initFixPieChart(fixPieChart, getCenterText(), R.color.backgroup);
        fixPieChart.setData(MPChartUtils.getPieData(fixPieChart, colors, yValues, true));
        // undo all highlights
        fixPieChart.highlightValues(null);
        fixPieChart.invalidate();

        yValues.remove(0);
        yValues.remove(2);

        //预警总数
        MPChartUtils.initPieChart(alarmCountPieChart, "总预警人数(90人)");
        alarmCountPieChart.setData(MPChartUtils.getPieData(alarmCountPieChart, colors, yValues, false));
        // undo all highlights
        alarmCountPieChart.highlightValues(null);
        alarmCountPieChart.invalidate();

        //重点人员
        MPChartUtils.initPieChart(keyPeoplePieChart, "重点人员(36人)");
        keyPeoplePieChart.setData(MPChartUtils.getPieData(keyPeoplePieChart, colors, yValues, false));
        // undo all highlights
        keyPeoplePieChart.highlightValues(null);
        keyPeoplePieChart.invalidate();

        //在逃人员
        MPChartUtils.initPieChart(escapeePeoplePieChart, "在逃人员(88人)");
        escapeePeoplePieChart.setData(MPChartUtils.getPieData(escapeePeoplePieChart, colors, yValues, false));
        // undo all highlights
        escapeePeoplePieChart.highlightValues(null);
        escapeePeoplePieChart.invalidate();

        //临控人员
        MPChartUtils.initPieChart(tempControlPeoplePieChart, "临控人员(28人)");
        tempControlPeoplePieChart.setData(MPChartUtils.getPieData(tempControlPeoplePieChart, colors, yValues, false));
        // undo all highlights
        tempControlPeoplePieChart.highlightValues(null);
        tempControlPeoplePieChart.invalidate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.e(TAG, "showPieChart: width = " + alarmCountPieChart.getWidth() + ", " + alarmCountPieChart.getRadius());
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

        MPChartUtils.initBarChart(commonBarChart, xLabels, false);
        commonBarChart.setData(MPChartUtils.getBarData(commonBarChart, yVals, "镜头数", R.color.backgroup));
        commonBarChart.invalidate();
    }

    /**
     * 柱状图
     */
    private void showScrollBarChart() {
        Random random = new Random();
        List<String> xDataList = new ArrayList<>();// x轴数据源
        List<BarEntry> yDataListOne = new ArrayList<>();// y轴数据数据源
        //给上面的X、Y轴数据源做假数据测试
        for (int i = 0; i < 30; i++) {
            if (i < 10) {
                // x轴显示的数据
                xDataList.add("05-0" + (i + 1));
            } else {
                // x轴显示的数据
                xDataList.add("05-" + (i + 1));
            }

            //y轴生成float类型的随机数
            yDataListOne.add(new BarEntry(i, random.nextInt(88) + (random.nextInt(30) * random.nextInt(50))));
        }

        MPChartUtils.initBarChart(scrollBarChart, xDataList, true);
        scrollBarChart.setData(MPChartUtils.getBarData(scrollBarChart, yDataListOne, "镜头数", R.color.backgroup));
        //设置可左右滑动
        scrollBarChart.getXAxis().setAxisMinimum(0);
        scrollBarChart.getXAxis().setAxisMaximum(xDataList.size() - 1);
        scrollBarChart.setVisibleXRange(0, 6);
        scrollBarChart.invalidate();
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
        return spannableString;
    }

    /**
     * 折线图 或 曲线图
     */
    private void showLineChart() {
        Random random = new Random();
        List<String> xDataList = new ArrayList<>();// x轴数据源
        List<Entry> yDataListOne = new ArrayList<>();// y轴数据数据源
        List<Entry> yDataListTwo = new ArrayList<>();// y轴数据数据源
        List<Entry> yDataListThree = new ArrayList<>();// y轴数据数据源
        //给上面的X、Y轴数据源做假数据测试
        for (int i = 0; i < 31; i++) {
            if (i < 9) {
                // x轴显示的数据
                xDataList.add("05-0" + (i + 1));
            } else {
                // x轴显示的数据
                xDataList.add("05-" + (i + 1));
            }

            //y轴生成float类型的随机数
            yDataListOne.add(new Entry(i, random.nextInt(88) + (random.nextInt(30) * random.nextInt(50))));
            yDataListTwo.add(new Entry(i, random.nextInt(88) + (random.nextInt(30) * random.nextInt(50))));
            yDataListThree.add(new Entry(i, random.nextInt(88) + (random.nextInt(30) * random.nextInt(50))));
        }

        LineDataSet lineDataSetOne = new LineDataSet(yDataListOne, "巡查人数");
        LineDataSet lineDataSetTwo = new LineDataSet(yDataListTwo, "入关人数");
        LineDataSet lineDataSetThree = new LineDataSet(yDataListThree, "出关人数");
        List<LineDataSet> lineDataSetList = new ArrayList<>();
        lineDataSetList.add(MPChartUtils.initLineDataSet(faceCollectLineChart, lineDataSetOne, R.color.backgroup));
        lineDataSetList.add(MPChartUtils.initLineDataSet(faceCollectLineChart, lineDataSetTwo, R.color.yellow));
        lineDataSetList.add(MPChartUtils.initLineDataSet(faceCollectLineChart, lineDataSetThree, R.color.colorAccent));

        MPChartUtils.initLineChart(faceCollectLineChart, xDataList);
        faceCollectLineChart.setData(MPChartUtils.getLineData(faceCollectLineChart, lineDataSetList));
        setLineChartCanScroll(faceCollectLineChart, xDataList);
    }

    /**
     * 设置图表可以左右滑动，分三步
     *
     * @param lineChart
     * @param xDataList
     */
    private void setLineChartCanScroll(LineChart lineChart, List<String> xDataList) {
        // 第一步 设置x轴最大可见区域范围，超过范围就左右滑动图表显示其他数据，必须先设置xAxis.setAxisMaximum(); 否则，不会有滑动效果，还可能有异常问题出现；\
        lineChart.getXAxis().setAxisMinimum(0);
        lineChart.getXAxis().setAxisMaximum(xDataList.size());
        // 第二步
        lineChart.setVisibleXRange(0, 6);
        // 第三步
        lineChart.invalidate();
    }

    @OnClick({R.id.tv_face_statistics_date, R.id.tv_alarm_statistics_date, R.id.tv_enter_statistics_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_face_statistics_date:
                timePickerView = PickerViewTimeUtil.initTimePicker(MPAndroidChartActivity.this, this);
                timePickerView.setDate(Calendar.getInstance());
                timePickerView.show(tvFaceStatisticsDate);
                break;
            case R.id.tv_alarm_statistics_date:
                timePickerView = PickerViewTimeUtil.initCustomTimePicker(MPAndroidChartActivity.this, new PickerViewTimeUtil.OnSelectCustomTimeListener() {
                    @Override
                    public void onSelectCustomTime(View view, String startTime, String endTime) {
                        tvAlarmStatisticsDate.setText(startTime + " 至 " + endTime);
                    }
                });
                timePickerView.setDate(Calendar.getInstance());
                timePickerView.show(tvAlarmStatisticsDate);
                break;
            case R.id.tv_enter_statistics_date:
                timePickerView = PickerViewTimeUtil.initTimePicker(MPAndroidChartActivity.this, this);
                timePickerView.setDate(Calendar.getInstance());
                timePickerView.show(tvEnterStatisticsDate);
                break;
        }
    }

    @Override
    public void onTimeSelect(Date date, View view) {
        if (tvFaceStatisticsDate == view) {
            tvFaceStatisticsDate.setText(TimeUitls.getCurrentTime(4, date));
        } else if (tvEnterStatisticsDate == view) {
            tvEnterStatisticsDate.setText(TimeUitls.getCurrentTime(4, date));
        }
    }
}
