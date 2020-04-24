package com.zhy.view.data_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.smile.searchfunction.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.util.List;

/**
 * 搜索记录--流式布局展示数据--使用的适配器
 */

public class SearchLabelFlowAdapter extends TagAdapter<String> {
    private FlowLayout mFlowLayout;
    private LayoutInflater mLayoutInflater;

    public SearchLabelFlowAdapter(Context mContext, List<String> listData, FlowLayout mFlowLayout) {
        super(listData);
        this.mFlowLayout = mFlowLayout;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(FlowLayout parent, int position, String labelText) {
        TextView tv = (TextView) mLayoutInflater.inflate(R.layout.item_flowlayout_label, mFlowLayout, false);
        tv.setText(labelText);
        return tv;
    }
}
