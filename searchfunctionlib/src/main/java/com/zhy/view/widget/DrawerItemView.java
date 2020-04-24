package com.zhy.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.smile.searchfunction.R;
import com.zhy.view.bean.TagDataBean;
import com.zhy.view.data_adapter.DrawerItemDataAdapter;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

/**c
 * Created by xh_smile on 2019/11/9.
 */

public class DrawerItemView extends LinearLayout {

    private final String TAG = DrawerItemView.class.getSimpleName();

    private Context mContext;

    /**
     * 标题
     */
    private String mTitleText;

    /**
     * mTextViewItemTitle最右边显示的图片
     */
    private RotateDrawable mTextViewItemTitleRightDrawable;

    /**
     * 是否展示数据，默认展示
     */
    private boolean isShowData = true;

    /**
     * 最多可以选择几个标签
     */
    private int maxSelectTabCount = 1;

    /**
     * 数据标题
     */
    private TextView mTextViewItemTitle;

    /**
     * 流式布局显示数据内容
     */
    private TagFlowLayout mTagFlowLayout;

    /**
     * 流式布局数据适配器
     */
    private TagAdapter<TagDataBean> mTagAdapter;

    /**
     * 流式布局数据集
     */
    private List<TagDataBean> mTagDataBeanList;

    /**
     * 标签选择结果监听事件
     */
    private SelectTabResultListener mSelectTabResultListener;

    public DrawerItemView(Context context) {
        super(context);
    }

    public DrawerItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
        this.mContext = context;
        initAttrs(attrs);
        initTextViewItemTitle();
        initTagFlowLayout();
    }

    /**
     * 初始化属性值
     *
     * @param attrs
     */
    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.DrawerItemView);
            mTitleText = typedArray.getString(R.styleable.DrawerItemView_item_view_title_text);
            maxSelectTabCount = typedArray.getInteger(R.styleable.DrawerItemView_max_select_tab_count, 2);
            //回收资源，否则再次使用会出错
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "onMeasure333: params = " + mTextViewItemTitle.getLayoutParams());
    }


    public void setTitle(String title){
        mTextViewItemTitle.setText(title);
    }

    /**
     * 初始化item Title
     */
    private void initTextViewItemTitle() {
        mTextViewItemTitle = new TextView(mContext);
        mTextViewItemTitle.setPadding(0, dpToPixel(12), 0, dpToPixel(12));
        mTextViewItemTitle.setTextColor(ContextCompat.getColor(mContext, R.color.common_black));
        mTextViewItemTitle.setTextSize(16f);
        mTextViewItemTitle.setText(mTitleText);
        mTextViewItemTitle.setGravity(Gravity.CENTER_VERTICAL);
        // 字体加粗
        mTextViewItemTitle.setTypeface(Typeface.DEFAULT_BOLD);
        addView(mTextViewItemTitle, 0);
        setItemImage(R.drawable.arror_up, 0, 0);

        mTextViewItemTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowData) {
                    // 不显示数据
                    isShowData = false;
                    setItemImage(R.drawable.arror_up, 0, 0);
                    mTagFlowLayout.setVisibility(View.GONE);
                } else {
                    // 显示数据
                    isShowData = true;
                    setItemImage(R.drawable.arror_down, 0, 0);
                    mTagFlowLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 初始化流式布局
     */
    private void initTagFlowLayout() {
        // 初始化一条数据
        mTagDataBeanList = new ArrayList<>();
        mTagAdapter = new DrawerItemDataAdapter(mTagDataBeanList, mContext, mTagFlowLayout);
        // 初始化布局
        mTagFlowLayout = new TagFlowLayout(mContext);
        mTagFlowLayout.setMaxSelectCount(maxSelectTabCount);
        addView(mTagFlowLayout, 1);
        mTagFlowLayout.setAdapter(mTagAdapter);
        mTagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (position < 0 || position >= mTagDataBeanList.size()) {
                    return false;
                }
                if (mSelectTabResultListener != null) {
                    // 返回最终的选择标签数据对象TagFlowDataBean
                    mSelectTabResultListener.selectTabResult(mTagDataBeanList.get(position));
                }
                return false;
            }
        });
    }

    /**
     * 设置数据
     *
     * @param datas
     */
    public void setNewData(List<TagDataBean> datas) {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        mTagDataBeanList.clear();
        mTagDataBeanList.addAll(datas);
        // 默认选择第一项
        // setSelectedList方法里面会自动调用notifyDataChanged()方法刷新数据
        mTagAdapter.setSelectedList(0);
    }

    /**
     * 重置
     */
    public void reset() {
        // 默认选择第一项
        // setSelectedList方法里面会自动调用notifyDataChanged()方法刷新数据
        mTagAdapter.setSelectedList(0);
    }

    /**
     * 设置标签选择结果监听事件
     *
     * @param selectTabResultListener
     */
    public void setSelectTabResultListener(SelectTabResultListener selectTabResultListener) {
        mSelectTabResultListener = selectTabResultListener;
    }

    public TextView getTextViewItemTitle() {
        return mTextViewItemTitle;
    }

    public TagFlowLayout getTagFlowLayout() {
        return mTagFlowLayout;
    }

    /**
     * 设置mTextViewItemTitle最右边显示的图片
     *
     * @param drawableResId
     * @param width         限制图片的宽度，单位dp
     * @param height        限制图片的高度，单位dp
     */
    public void setItemImage(@DrawableRes int drawableResId, int width, int height) {
        Drawable drawable = getResources().getDrawable(drawableResId);
        mTextViewItemTitleRightDrawable = new RotateDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTextViewItemTitleRightDrawable.setDrawable(drawable);
        }

        if (width < 1 || height < 1) {
            width = mTextViewItemTitleRightDrawable.getMinimumWidth();
            height = mTextViewItemTitleRightDrawable.getMinimumHeight();
        }

        // 一定要加这行！！！！！！！！！！！
        mTextViewItemTitleRightDrawable.setBounds(0, 0, dpToPixel(18), dpToPixel(10));
        mTextViewItemTitle.setCompoundDrawables(null, null, mTextViewItemTitleRightDrawable, null);
    }

    /**
     * dip转pix
     *
     * @param dpValue
     * @return
     */
    public int dpToPixel(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 标签选择结果监听事件
     */
    public interface SelectTabResultListener {
        void selectTabResult(TagDataBean tagDataBean);
    }
}
