package org.smile.mde.ui.activity.design;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.example.commonlib.base.BaseActivity;

import org.smile.mde.R;

import butterknife.BindView;
import butterknife.OnClick;

public class CoordinatorLayoutActivity extends BaseActivity {

    @BindView(R.id.btn_demo1)
    Button btnDemo1;
    @BindView(R.id.btn_demo2)
    Button btnDemo2;
    @BindView(R.id.btn_demo3)
    Button btnDemo3;

    @Override
    protected int bindLayout() {
        return R.layout.activity_coordinator_layout;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected boolean isUseDagger() {
        return false;
    }

    @OnClick({R.id.btn_demo1, R.id.btn_demo2, R.id.btn_demo3, R.id.btn_demo4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_demo1:
                startActivity(new Intent(this, Demo1Activity.class));
                break;
            case R.id.btn_demo2:
                startActivity(new Intent(this, Demo2Activity.class));
                break;
            case R.id.btn_demo3:
                startActivity(new Intent(this, Demo3Activity.class));
                break;
            case R.id.btn_demo4:
                startActivity(new Intent(this, Demo4Activity.class));
                break;
        }
    }
}
