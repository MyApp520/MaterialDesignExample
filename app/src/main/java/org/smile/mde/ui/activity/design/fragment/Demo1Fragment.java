package org.smile.mde.ui.activity.design.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.smile.commonlib.base.BaseFragment;

import org.smile.mde.R;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * A simple {@link androidx.fragment.app.Fragment} subclass.
 */
public class Demo1Fragment extends BaseFragment {

    @BindView(R.id.msg)
    TextView textView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private Activity activity;
    private Context context;
    private BaseQuickAdapter<String, BaseViewHolder> baseQuickAdapter;

    public static Demo1Fragment getInstance(String msg) {
        // Required empty public constructor
        Demo1Fragment demo1Fragment = new Demo1Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);
        demo1Fragment.setArguments(bundle);
        return demo1Fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        context = activity.getApplicationContext();
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_demo1;
    }

    @Override
    protected void initView() {
        textView.setText(getArguments().getString("msg", "没有信息啊!"));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onLazyInitEvent() {
        ArrayList<String> data = new ArrayList<>();
        int length = 20;
        if (textView.getText().toString().contains("222")) {
            length = 10;
        } else if (textView.getText().toString().contains("333")) {
            length = 5;
        }
        for (int i = 0; i < length; i++) {
            data.add(textView.getText().toString() + "---" + i);
        }

        baseQuickAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_child_view, data) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_child_name, item);
            }
        };
        recyclerView.setAdapter(baseQuickAdapter);
    }

    @Override
    protected boolean isUserDagger() {
        return false;
    }
}
