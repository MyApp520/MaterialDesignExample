package org.smile.mde.ui.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.commonlib.base.BaseActivity;
import com.example.commonlib.util.ShowToast;

import org.smile.mde.R;

import butterknife.BindView;

public class ToolBarActivity extends BaseActivity {

    @BindView(R.id.toolbar1)
    Toolbar toolbar1;
    @BindView(R.id.toolbar2)
    Toolbar toolbar2;
    @BindView(R.id.toolbar3)
    Toolbar toolbar3;

    private final String TAG = getClass().getSimpleName();

    @Override
    protected int bindLayout() {
        return R.layout.activity_tool_bar;
    }

    @Override
    protected void initView() {
        initToolbar1();
        initToolbar2();
        initToolbar3();
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
        toolbar1.inflateMenu(R.menu.common_toolbar_menu);
        toolbar1.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
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

    private void initToolbar2() {
        // contentInsetStart 的作用类似于paddingStart: 默认值是16dp  --->  在布局文件中修改
        // contentInsetEnd   的作用类似于paddingEnd: 默认值是0dp  --->  在布局文件中修改
        // contentInsetStartWithNavigation：表示其他内容与navigationIcon之间的间隔  --->  在布局文件中修改
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowToast.showToast(getApplicationContext(), "我是 toolbar2 mNavButtonView");
            }
        });

        // 设置溢出菜单的图标（点击按钮弹出menu菜单，这里设置这个点击按钮的图标） 或  也可以在布局文件中通过OverFlowButtonStyle来设置
        toolbar2.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.title_menu_list));
        toolbar2.inflateMenu(R.menu.common_toolbar2_menu);
        toolbar2.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_collect:
                        //点击收藏菜单
                        ShowToast.showToast(getApplicationContext(), "点击 toolbar2 menu = " + item.getTitle());
                        break;
                    case R.id.item_setting:
                        //点击设置菜单
                        ShowToast.showToast(getApplicationContext(), "点击 toolbar2 menu = " + item.getTitle());
                        break;
                    case R.id.item_model:
                        //点击夜间模式菜单
                        ShowToast.showToast(getApplicationContext(), "点击 toolbar2 menu = " + item.getTitle());
                        break;
                }
                return false;
            }
        });
    }

    private void initToolbar3() {
        // contentInsetStart 的作用类似于paddingStart: 默认值是16dp  --->  在布局文件中修改
        // contentInsetEnd   的作用类似于paddingEnd: 默认值是0dp  --->  在布局文件中修改
        toolbar3.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowToast.showToast(getApplicationContext(), "我是 toolbar3 mNavButtonView");
            }
        });

        // 设置溢出菜单的图标（点击按钮弹出menu菜单，这里设置这个点击按钮的图标） 或  也可以在布局文件中通过OverFlowButtonStyle来设置
        toolbar3.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.title_menu_list));
        toolbar3.inflateMenu(R.menu.common_toolbar3_menu);
        toolbar3.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_search:
                        //点击收藏菜单
                        ShowToast.showToast(getApplicationContext(), "点击 toolbar3 menu = " + item.getTitle());
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
