package com.example.commonlib.mpandroidchart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.example.commonlib.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

/**
 * Created by smile on 2019/7/12.
 */

public class LineChartMarkView extends MarkerView {

    private TextView tvDate;
    private TextView tv_inspect_people_count, tv_enter_people_count, tv_exit_people_count;
    private IndexAxisValueFormatter indexAxisValueFormatter;

    public LineChartMarkView(Context context, IndexAxisValueFormatter indexAxisValueFormatter) {
        super(context, R.layout.linechart_markview_layout);
        this.indexAxisValueFormatter = indexAxisValueFormatter;

        tvDate = findViewById(R.id.tv_date);
        tv_inspect_people_count = findViewById(R.id.tv_inspect_people_count);
        tv_enter_people_count = findViewById(R.id.tv_enter_people_count);
        tv_exit_people_count = findViewById(R.id.tv_exit_people_count);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //展示自定义X轴值 后的X轴内容

        Chart chart = getChartView();
        if (chart instanceof LineChart) {
            LineData lineData = ((LineChart) chart).getLineData();
            //获取到图表中的所有曲线
            List<ILineDataSet> dataSetList = lineData.getDataSets();
            for (int i = 0; i < dataSetList.size(); i++) {
                LineDataSet dataSet = (LineDataSet) dataSetList.get(i);
                //获取到曲线的所有在Y轴的数据集合，根据当前X轴的位置 来获取对应的Y轴值
                float y = dataSet.getValues().get((int) e.getX()).getY();
                if (i == 0) {
                    tv_inspect_people_count.setText(dataSet.getLabel() + "：" + (int) y);
                }
                if (i == 1) {
                    tv_enter_people_count.setText(dataSet.getLabel() + "：" + (int) y);
                }
                if (i == 2) {
                    tv_exit_people_count.setText(dataSet.getLabel() + "：" + (int) y);
                }
            }
            tvDate.setText(indexAxisValueFormatter.getValues()[(int) e.getX()]);
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
