package org.smile.mde.ui.activity;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.smile.commonlib.base.BaseActivity;
import com.smile.commonlib.util.ShowToast;

import org.smile.mde.R;
import org.smile.mde.bean.TwoBean;
import org.smile.mde.bean.VerticalBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TwoRecyclerViewActivity extends BaseActivity {

    @BindView(R.id.vertical_recyclerView)
    RecyclerView verticalRecyclerView;

    private BaseQuickAdapter<VerticalBean, BaseViewHolder> verticalAdapter;
    private List<VerticalBean> verticalListData;

    @Override
    protected int bindLayout() {
        return R.layout.activity_two_recycler_view;
    }

    @Override
    protected void initView() {
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        verticalRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        verticalAdapter = new BaseQuickAdapter<VerticalBean, BaseViewHolder>(R.layout.item_recyclerview_vertical) {
            @Override
            protected void convert(BaseViewHolder verticalHelper, VerticalBean item) {
                RecyclerView itemHorizontalRecyclerView = verticalHelper.getView(R.id.horizontal_recycler_view);
                BaseQuickAdapter<TwoBean, BaseViewHolder> twoBeanAdapter = (BaseQuickAdapter<TwoBean, BaseViewHolder>) itemHorizontalRecyclerView.getAdapter();

                if (twoBeanAdapter == null) {
                    twoBeanAdapter = new BaseQuickAdapter<TwoBean, BaseViewHolder>(R.layout.item_recyclerview_horizontal, item.getTwoBeanList()) {
                        @Override
                        protected void convert(final BaseViewHolder twoBeanHelper, TwoBean item) {
                            Log.e(TAG, "convert: twoBeanHelper适配器 twoBeanHelper.getAdapterPosition() = " + twoBeanHelper.getAdapterPosition());
                            twoBeanHelper.setText(R.id.tv_name, item.getName());
                            twoBeanHelper.setText(R.id.tv_address, "陕西汉中风雷镇辅仁中学 -> " + twoBeanHelper.getAdapterPosition());
                            twoBeanHelper.setText(R.id.tv_sex, twoBeanHelper.getAdapterPosition() % 2 == 0 ? "男" : "女");
                            twoBeanHelper.setText(R.id.tv_identify_card, "526701190708162568" + twoBeanHelper.getAdapterPosition());

                            RecyclerView itemRecyclerView = twoBeanHelper.getView(R.id.item_list_child_recycler_view);
                            Log.e(TAG, "itemRecyclerView = " + itemRecyclerView);
                            Log.e(TAG, "itemRecyclerView.getAdapter() = " + itemRecyclerView.getAdapter());

                            BaseQuickAdapter<String, BaseViewHolder> childAdapter = (BaseQuickAdapter<String, BaseViewHolder>) itemRecyclerView.getAdapter();
                            if (childAdapter == null) {
                                final List<String> childListData = item.getChildListData();
                                childAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_child_view, childListData) {
                                    @Override
                                    protected void convert(BaseViewHolder childHelper, String item) {
                                        Log.e(TAG, "convert: 子view适配器 helper = " + twoBeanHelper + ", string = " + item);
                                        childHelper.setText(R.id.tv_child_name, item + " -> " + twoBeanHelper.getAdapterPosition());
                                    }
                                };
                                childAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                        if (childListData != null && position >= 0 && position < childListData.size()) {
                                            ShowToast.showToast(getApplicationContext(), childListData.get(position));
                                        }
                                    }
                                });
                                itemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                itemRecyclerView.setAdapter(childAdapter);
                            } else {
                                childAdapter.setNewData(item.getChildListData());
                            }
                        }
                    };
                    itemHorizontalRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                    itemHorizontalRecyclerView.setAdapter(twoBeanAdapter);
                } else {
                    twoBeanAdapter.setNewData(item.getTwoBeanList());
                }
            }
        };
        verticalRecyclerView.setAdapter(verticalAdapter);
    }

    @Override
    protected void initData() {
        verticalListData = new ArrayList<>();
        VerticalBean verticalBean;
        TwoBean twoBean;
        List<TwoBean> twoBeanListData = null;
        List<String> childListData = null;

        for (int n = 1; n < 131; n++) {
            twoBeanListData = new ArrayList<>();
            String verticalName = n + " 号战区";
            for (int i = 132; i < 192; i++) {
                childListData = new ArrayList<>();
                String name = verticalName + ", 第 " + i + " 集团军";
                for (int j = 193; j < 228; j++) {
                    if (j % 2 == 0) {
                        childListData.add(name + ", 第 " + j + " 师");
                    } else if (j % 3 == 0) {
                        childListData.add(name + ", 第 " + j + " 旅");
                    } else {
                        childListData.add(name + ", 第 " + j + " 军");
                    }
                }

                twoBean = new TwoBean();
                twoBean.setName(name);
                twoBean.setChildListData(childListData);
                twoBeanListData.add(twoBean);
            }
            verticalBean = new VerticalBean();
            verticalBean.setName(verticalName);
            verticalBean.setTwoBeanList(twoBeanListData);
            verticalListData.add(verticalBean);
        }

        verticalAdapter.setNewData(verticalListData);
    }

    @Override
    protected boolean isUseDagger() {
        return false;
    }
}
