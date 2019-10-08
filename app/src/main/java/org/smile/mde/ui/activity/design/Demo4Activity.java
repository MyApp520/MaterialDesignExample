package org.smile.mde.ui.activity.design;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.commonlib.base.BaseActivity;
import com.example.commonlib.util.StatusbarUtils;
import com.github.mikephil.charting.utils.Utils;

import org.smile.mde.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class Demo4Activity extends BaseActivity {

    @BindView(R.id.nested_scrollview)
    NestedScrollView observableNestedScrollView;
    @BindView(R.id.rl_top_content)
    RelativeLayout rlTopContent;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.iv_title_back_normal)
    ImageView ivTitleBackNormal;
    @BindView(R.id.iv_img_normal)
    ImageView ivImgNormal;
    @BindView(R.id.rl_normal_title_bar)
    RelativeLayout rlNormalTitleBar;
    @BindView(R.id.iv_title_back_select)
    ImageView ivTitleBackSelect;
    @BindView(R.id.tv_title_select)
    TextView tvTitleSelect;
    @BindView(R.id.iv_img_select)
    ImageView ivImgSelect;
    @BindView(R.id.rl_select_title_bar)
    RelativeLayout rlSelectTitleBar;

    /**
     * 获取topContent高度
     */
    private int mRlTopContentHeight;
    private int mRlTopContentHalfHeight;
    private int mRlTopContentTransitionDistance;
    private BaseQuickAdapter<String, BaseViewHolder> adapter;

    @Override
    protected int bindLayout() {
        //设置透明状态栏
        StatusbarUtils.enableTranslucentStatusbar(this);
        return R.layout.activity_demo4;
    }

    @Override
    protected void initView() {
        //获取topContent高度
        rlTopContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rlTopContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mRlTopContentTransitionDistance = (int) Utils.convertDpToPixel(8f);
                mRlTopContentHeight = rlTopContent.getHeight() - rlSelectTitleBar.getHeight() - mRlTopContentTransitionDistance;//这里取的高度应该为图片的高度-标题栏
                mRlTopContentHalfHeight = mRlTopContentHeight / 2;

                Log.e(TAG, "onGlobalLayout: rlTopContent.getHeight() = " + rlTopContent.getHeight()
                        + ", rlTopContent.getBottom() = " + rlTopContent.getBottom());
                Log.e(TAG, "onGlobalLayout: recyclerView.getTop() = " + recyclerView.getTop()
                        + ",  mRlTopContentTransitionDistance = " + mRlTopContentTransitionDistance);
                Log.e(TAG, "onGlobalLayout: mRlTopContentHeight = " + mRlTopContentHeight
                        + ", mRlTopContentHalfHeight = " + mRlTopContentHalfHeight);
            }
        });

        initRecyclerView();
    }

    @Override
    protected void initData() {
        //注册滑动监听
        observableNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            /**
             *
             * @param v
             * @param scrollX 控件x轴方向：当前位置到起始位置的距离
             * @param scrollY 控件y轴方向：当前位置到起始位置的距离
             * @param oldScrollX 控件x轴方向：当前位置的前一个位置到起始位置的距离
             * @param oldScrollY 控件y轴方向：当前位置的前一个位置到起始位置的距离
             */
            @SuppressLint("NewApi")
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.e(TAG, " observableNestedScrollView滑动监听: scrollX = " + scrollX + ", scrollY = " + scrollY
                        + ", oldScrollX = " + oldScrollX + ", oldScrollY = " + oldScrollY);
                if (scrollY <= 0) {
                    rlSelectTitleBar.getBackground().setAlpha(0);
                    ivTitleBackSelect.setImageAlpha(0);
                    ivImgSelect.setImageAlpha(0);


                    ivTitleBackNormal.getBackground().setAlpha(255);
                    ivImgNormal.getBackground().setAlpha(255);
                    ivTitleBackNormal.setImageAlpha(255);
                    ivImgNormal.setImageAlpha(255);
                } else if (scrollY > 0 && scrollY < mRlTopContentHeight) {
                    //滑动过程中，渐变
                    float scale = (float) scrollY / mRlTopContentHeight;//根据滑动距离比例，设置背景色的透明度
                    int alpha = (int) (scale * 255);
                    Log.e(TAG, "onScrollChange: scale = " + scale + ", alpha = " + alpha);
                    rlSelectTitleBar.getBackground().setAlpha(alpha);
                    ivTitleBackSelect.setImageAlpha(alpha);
                    ivImgSelect.setImageAlpha(alpha);


                    ivTitleBackNormal.getBackground().setAlpha(255 - alpha);
                    ivImgNormal.getBackground().setAlpha(255 - alpha);
                    ivTitleBackNormal.setImageAlpha(255 - alpha);
                    ivImgNormal.setImageAlpha(255 - alpha);
                } else {
                    rlSelectTitleBar.getBackground().setAlpha(255);
                    ivTitleBackSelect.setImageAlpha(255);
                    ivImgSelect.setImageAlpha(255);


                    ivTitleBackNormal.getBackground().setAlpha(0);
                    ivImgNormal.getBackground().setAlpha(0);
                    ivTitleBackNormal.setImageAlpha(0);
                    ivImgNormal.setImageAlpha(0);
                }
                if (scrollY <= 0) {
                    rlSelectTitleBar.getBackground().setAlpha(0);
                    ivTitleBackSelect.setImageAlpha(0);
                    ivImgSelect.setImageAlpha(0);


                    ivTitleBackNormal.getBackground().setAlpha(255);
                    ivImgNormal.getBackground().setAlpha(255);
                    ivTitleBackNormal.setImageAlpha(255);
                    ivImgNormal.setImageAlpha(255);
                } else if (scrollY > 0 && scrollY < mRlTopContentHalfHeight) {
                    //滑动过程中，渐变
                    float scale = (float) scrollY / mRlTopContentHalfHeight;//根据滑动距离比例，设置背景色的透明度
                    int alpha = (int) (scale * 255);
                } else if (scrollY >= mRlTopContentHalfHeight && scrollY < (mRlTopContentHalfHeight + mRlTopContentTransitionDistance)) {
                    ivTitleBackNormal.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.default_color));
                }
            }
        });
        rlSelectTitleBar.getBackground().setAlpha(0);
        ivTitleBackSelect.setImageAlpha(0);
        ivImgSelect.setImageAlpha(0);

        ivTitleBackNormal.getBackground().setAlpha(255);
        ivImgNormal.getBackground().setAlpha(255);
        ivTitleBackNormal.setImageAlpha(255);
        ivImgNormal.setImageAlpha(255);
    }

    private void initRecyclerView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_child_view, null) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_child_name, item);
            }
        };
        recyclerView.setAdapter(adapter);

        recyclerView.setNestedScrollingEnabled(false);

        List<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add("第 " + i + " 号");
        }
        adapter.setNewData(data);
    }

    @Override
    protected boolean isUseDagger() {
        return false;
    }

    @OnClick({R.id.iv_title_back_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back_select:
                finish();
                break;
        }
    }
}
