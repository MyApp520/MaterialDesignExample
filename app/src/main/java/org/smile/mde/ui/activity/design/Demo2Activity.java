package org.smile.mde.ui.activity.design;

import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.smile.commonlib.base.BaseActivity;
import com.smile.commonlib.util.ShowToast;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.smile.mde.R;

import butterknife.BindView;

public class Demo2Activity extends BaseActivity {

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar1)
    Toolbar toolbar1;

    @Override
    protected int bindLayout() {
        return R.layout.activity_demo2;
    }

    @Override
    protected void initView() {
//        collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);//设置收缩后标题的位置
//        collapsingToolbarLayout.setExpandedTitleGravity(Gravity.CENTER);////设置展开后标题的位置
        collapsingToolbarLayout.setTitle("标题1");//设置标题的名字
        collapsingToolbarLayout.setExpandedTitleColor(Color.RED);//设置展开后标题的颜色
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后标题的颜色
        initToolbar1();
    }

    @Override
    protected void initData() {

    }

    private void initToolbar1() {
        toolbar1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowToast.showToast(getApplicationContext(), "我是 mNavButtonView");
            }
        });

        // 设置溢出菜单的图标（点击按钮弹出menu菜单，这里设置这个点击按钮的图标） 或  也可以在布局文件中通过OverFlowButtonStyle来设置
        toolbar1.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.title_menu_list));
        toolbar1.inflateMenu(R.menu.common_toolbar4_menu);
        toolbar1.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_collect:
                        //点击收藏菜单
                        ShowToast.showToast(getApplicationContext(), "点击 menu = " + item.getTitle());
                        break;
                    case R.id.item_setting:
                        //点击设置菜单
                        ShowToast.showToast(getApplicationContext(), "点击 menu = " + item.getTitle());
                        break;
                    case R.id.item_model:
                        //点击夜间模式菜单
                        ShowToast.showToast(getApplicationContext(), "点击 menu = " + item.getTitle());
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected boolean isUseDagger() {
        return false;
    }
}
