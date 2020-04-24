package com.smile.commonlib.mpandroidchart;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by smile on 2019/6/15.
 */

public class PieValueFormatter extends PercentFormatter {
    private boolean isShowLabel;

    /**
     *
     * @param isShowLabel true：同时显示文本标签和百分比，  false：只显示百分比
     */
    public PieValueFormatter(boolean isShowLabel) {
        this.isShowLabel = isShowLabel;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        Log.e(PieValueFormatter.class.getSimpleName(), "getFormattedValue: " + entry.getData());
        if (isShowLabel) {
            return ((PieEntry) entry).getLabel() + " " + mFormat.format(value) + " %";
        } else {
            return mFormat.format(value) + " %";
        }
    }
}