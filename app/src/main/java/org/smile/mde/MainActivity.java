package org.smile.mde;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.commonlib.base.BaseActivity;

import org.smile.mde.ui.activity.BaiDuMapTrackActivity;
import org.smile.mde.ui.activity.ConstraintLayoutActivity;
import org.smile.mde.ui.activity.ToolBarActivity;
import org.smile.mde.ui.activity.TwoRecyclerViewActivity;
import org.smile.mde.ui.activity.customize.CustomizeChartActivity;
import org.smile.mde.ui.activity.design.CoordinatorLayoutActivity;
import org.smile.mde.ui.activity.mpandroidchart.MPAndroidChartActivity;
import org.smile.mde.view.SecondPTZCircleControlView;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements SecondPTZCircleControlView.OnRingClickListener {

    @BindView(R.id.btn_two_recycler_view)
    Button btnTwoRecyclerView;
    @BindView(R.id.btn_toolbar)
    Button btnToolbar;
    @BindView(R.id.ptz_control_view)
    SecondPTZCircleControlView secondPTZCircleControlView;

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        secondPTZCircleControlView.setOnRingClickListener(this);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected boolean isUseDagger() {
        return false;
    }

    @OnClick({R.id.btn_face_rect, R.id.btn_two_recycler_view, R.id.btn_toolbar, R.id.btn_CoordinatorLayout, R.id.btn_ConstraintLayout,
            R.id.btn_mpandroidchart, R.id.btn_customize_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_face_rect:
//                startActivity(new Intent(this, FaceRectActivity.class));
//                startActivity(new Intent(this, AMapActivity.class));//高德地图
                startActivity(new Intent(this, BaiDuMapTrackActivity.class));//百度地图绘制运动轨迹
                break;
            case R.id.btn_two_recycler_view:
                startActivity(new Intent(this, TwoRecyclerViewActivity.class));
                break;
            case R.id.btn_toolbar:
                startActivity(new Intent(this, ToolBarActivity.class));
                break;
            case R.id.btn_CoordinatorLayout:
                startActivity(new Intent(this, CoordinatorLayoutActivity.class));
                break;
            case R.id.btn_ConstraintLayout:
                startActivity(new Intent(this, ConstraintLayoutActivity.class));
                break;
            case R.id.btn_mpandroidchart:
                startActivity(new Intent(this, MPAndroidChartActivity.class));
                break;
            case R.id.btn_customize_view:
                startActivity(new Intent(this, CustomizeChartActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onRingClick(int clickPosition) {
        String info = "";
        switch (clickPosition) {
            case 0:
                info = "北方";
                break;
            case 1:
                info = "东北";
                break;
            case 2:
                info = "东方";
                break;
            case 3:
                info = "东南";
                break;
            case 4:
                info = "南方";
                break;
            case 5:
                info = "西南";
                break;
            case 6:
                info = "西方";
                break;
            case 7:
                info = "西北";
                break;
        }
        Toast.makeText(getApplicationContext(), "点击的是 " + info, Toast.LENGTH_SHORT).show();
    }
}
