package org.smile.mde.ui.activity.design;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.commonlib.adapter.CommonViewPagerAdapter;
import com.example.commonlib.base.BaseActivity;
import com.example.commonlib.util.ShowToast;
import com.example.commonlib.view.NoScrollViewPager;

import org.smile.mde.R;
import org.smile.mde.ui.activity.design.fragment.Demo1Fragment;

import java.util.ArrayList;

import butterknife.BindView;

public class Demo3Activity extends BaseActivity {

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar1)
    Toolbar toolbar1;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    NoScrollViewPager noScrollViewPager;
    @BindView(R.id.appbar3)
    AppBarLayout appBarLayout;

    private final String TAG = getClass().getSimpleName();
    private CollapsingToolbarLayoutState state;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_demo3;
    }

    @Override
    protected void initView() {
//        collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);//设置收缩后标题的位置
//        collapsingToolbarLayout.setExpandedTitleGravity(Gravity.CENTER);////设置展开后标题的位置
//        collapsingToolbarLayout.setTitle("标题1");//设置标题的名字
//        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);//设置展开后标题的颜色
//        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后标题的颜色
        initToolbar1();
        initTablayout();
        initAppBarLayout();
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

    private void initTablayout() {
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add("tab111");
        titleList.add("tab222");
        titleList.add("tab333");

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(Demo1Fragment.getInstance("fragment111"));
        fragmentList.add(Demo1Fragment.getInstance("fragment222"));
        fragmentList.add(Demo1Fragment.getInstance("fragment333"));
        noScrollViewPager.setNoScroll(true);
        noScrollViewPager.setAdapter(new CommonViewPagerAdapter(getSupportFragmentManager(), titleList, fragmentList));
        noScrollViewPager.setOffscreenPageLimit(titleList.size() - 1);

        tablayout.addTab(tablayout.newTab().setText(titleList.get(0)));
        tablayout.addTab(tablayout.newTab().setText(titleList.get(1)));
        tablayout.addTab(tablayout.newTab().setText(titleList.get(2)));
        tablayout.setupWithViewPager(noScrollViewPager);
    }

    private void initAppBarLayout() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        //修改状态标记为展开
                        state = CollapsingToolbarLayoutState.EXPANDED;
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        //修改状态标记为折叠
                        state = CollapsingToolbarLayoutState.COLLAPSED;
                    }
                } else {
                    //此时的状态：介于折叠和展开两个状态之间
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if (state == CollapsingToolbarLayoutState.COLLAPSED) {
                            //由折叠变为中间状态时隐藏播放按钮
                        }
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });
    }

    @Override
    protected boolean isUseDagger() {
        return false;
    }
}
