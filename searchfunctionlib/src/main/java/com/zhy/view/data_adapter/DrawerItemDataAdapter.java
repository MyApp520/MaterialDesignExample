package com.zhy.view.data_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.smile.searchfunction.R;
import com.zhy.view.bean.TagDataBean;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.util.List;

/**
 * 侧滑菜单item--流式布局展示数据--使用的适配器
 */
public class DrawerItemDataAdapter extends TagAdapter<TagDataBean> {

    private FlowLayout mFlowLayout;
    private LayoutInflater mLayoutInflater;

    public DrawerItemDataAdapter(List<TagDataBean> datas, Context mContext, FlowLayout mFlowLayout) {
        super(datas);
        this.mFlowLayout = mFlowLayout;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(FlowLayout parent, int position, TagDataBean tagDataBean) {
        TextView tv = (TextView) mLayoutInflater.inflate(R.layout.item_flowlayout_tab, mFlowLayout, false);
        tv.setText(tagDataBean.getName());
        return tv;
    }
}
