package org.smile.mde.adapter;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.smile.mde.bean.PeopleInfoSectionEntity;

import java.util.List;

/**
 * Created by smile on 2019/7/6.
 */

public class PeopleInfoSectionEntityAdapter extends BaseSectionQuickAdapter<PeopleInfoSectionEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param layoutResId      The layout resource id of each item.
     * @param sectionHeadResId The section head layout id for each item
     * @param data             A new list is created out of this one to avoid mutable list
     */
    public PeopleInfoSectionEntityAdapter(int layoutResId, int sectionHeadResId, List<PeopleInfoSectionEntity> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PeopleInfoSectionEntity item) {

    }

    @Override
    protected void convertHead(BaseViewHolder helper, PeopleInfoSectionEntity item) {

    }
}
