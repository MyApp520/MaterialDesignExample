package com.zhy.view.data_adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.smile.searchfunction.R;
import com.zhy.view.bean.TagDataBean;

import java.util.List;

/**
 * 搜索记录--列表展示数据--使用的适配器
 */
public class SearchLabelListAdapter extends BaseQuickAdapter<TagDataBean, BaseViewHolder> {

    public SearchLabelListAdapter(int layoutResId, @Nullable List<TagDataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TagDataBean item) {
        helper.setText(R.id.tv_item_name, item.getName());
        // 添加删除按钮监听事件
        helper.addOnClickListener(R.id.image_view_delete);
    }
}
