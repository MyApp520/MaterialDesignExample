package com.zhy.view.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.smile.searchfunction.R;
import com.zhy.view.data_adapter.SearchLabelFlowAdapter;
import com.zhy.view.data_adapter.SearchLabelListAdapter;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * 自定义SearchView
 */

public class FairySearchView extends LinearLayout {
    private final String TAG = FairySearchView.class.getSimpleName();
    private final String SP_NAME = "xh_history_search";

    /**
     * 应用上下文对象
     */
    private Context mContext;

    /**
     * 使用SharedPreferences存储历史搜索记录
     */
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;

    /**
     * 使用SharedPreferences存储历史搜索记录时，使用的key，不同的key存储不同的数据
     */
    private String saveSearchHistoryDataKey;

    /**
     * 是否显示左侧[返回]按钮
     */
    private boolean showBackButton = false;

    /**
     * 是否显示输入框左侧的[搜索]图标
     */
    private boolean isShowEditTextLeftSearchIcon = true;

    /**
     * 是否在输入内容后展示[清除]按钮
     */
    private boolean isShowClearButton = true;

    /**
     * 是否显示右侧的[取消]按钮
     */
    private boolean showCancelButton = true;

    /**
     * 是否允许显示历史搜索的内容
     */
    private boolean isAllowShowSearchHistory = true;

    /**
     * 清除历史记录操作按钮类型：
     * <p>
     * 1、清除搜索历史控件--文字按钮 tvDeleteHistory
     * 2、清除搜索历史控件--图标按钮 imageDeleteHistory
     */
    private int deleteHistoryRecordViewType;

    private int searchViewHeight = getResources().getDimensionPixelSize(R.dimen.search_view_height);//搜索框的高度
    private int searchViewBackground;//搜索框的背景色
    private int btnBackArrowIcon = R.drawable.ic_back;//返回按钮图标
    private int searchIcon = R.drawable.ic_search_gray;//搜索按钮图标
    private int btnDeleteIcon = R.drawable.ic_clear;//清除按钮图标

    // btnSearch搜索按钮参数
    private String btnSearchTextHint = getResources().getString(R.string.btn_search_Text_hint);
    private int btnSearchTextSize = getResources().getDimensionPixelSize(R.dimen.btn_search_text_size);
    private int btnSearchTextColor = getResources().getColor(R.color.btn_search_text_color);

    // editText参数
    private int editTextSize = getResources().getDimensionPixelSize(R.dimen.edit_text_size);
    private int editTextColor = getResources().getColor(R.color.edit_text);
    private String editTextHint = getResources().getString(R.string.edit_hint);
    private int editTextHintColor = getResources().getColor(R.color.edit_hint_text);
    private int maxEditTextLength = -1;//输入内容最大长度（默认不设限制）
    private int editTextPaddingLeft = getResources().getDimensionPixelSize(R.dimen.edit_padding_left);//输入框左侧内边距（在没有左侧搜索图标时使用）

    private OnBtnBackArrowIconListener onBtnBackArrowIconListener;
    private OnDeleteEditTextClickListener onDeleteEditTextClickListener;
    private OnBtnSearchClickListener onBtnSearchClickListener;
    private OnEditChangeListener onEditChangeListener;
    private OnDeleteHistorySearchDataClickListener onDeleteHistorySearchDataClickListener;
    private OnShowSearchListListener mOnShowSearchListListener;//监听是否显示搜索数据列表

    private RelativeLayout rlSearchView;//搜索框View
    private ImageButton btnBackArrow;
    private EditText editText;
    private ImageButton btnDelete;
    private Button btnSearch;

    /**
     * 历史搜索View
     */
    private RelativeLayout rlSearchHistoryView;

    /**
     * 清除搜索历史控件--图标按钮
     */
    private ImageView imageDeleteHistory;

    /**
     * 清除搜索历史控件--文字按钮
     */
    private TextView tvDeleteHistory;

    /**
     * 历史搜索流式布局
     */
    private TagFlowLayout searchHistoryTagFlowLayout;

    /**
     * 历史搜索数据适配器（流式布局展示时使用）
     * 搜索记录--流式布局展示数据--使用的适配器
     */
    private SearchLabelFlowAdapter mSearchLabelFlowAdapter;

    /**
     * 历史搜索数据适配器（列表布局展示时使用）
     * 搜索记录--列表布局展示数据--使用的适配器
     */
    private SearchLabelListAdapter mSearchLabelListAdapter;

    private ArrayList<String> mTagAdapterList = new ArrayList<>();

    /**
     * 具体要搜索的内容
     */
    private String editTextSearchContent = "";

    /**
     * 历史搜索记录
     */
    private StringBuilder searchHistoryContentBuilder = new StringBuilder();

    /**
     * searchHistoryContentBuilder字符串分隔符，多个搜索记录使用分隔符隔开
     */
    private final String HISTORY_DATA_SEPARATOR = ",";

    public FairySearchView(Context context) {
        this(context, null);
    }

    public FairySearchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FairySearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttrs(attrs);
        initSearchViews();
        initSearchHistoryView();
    }

    //初始化属性
    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            Resources resources = getResources();
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.FairySearchView);
            showBackButton = typedArray.getBoolean(R.styleable.FairySearchView_showBackButton, true);
            isShowEditTextLeftSearchIcon = typedArray.getBoolean(R.styleable.FairySearchView_showEditTextLeftSearchIcon, true);
            showCancelButton = typedArray.getBoolean(R.styleable.FairySearchView_showCancelButton, true);
            isShowClearButton = typedArray.getBoolean(R.styleable.FairySearchView_showClearButton, true);
            isAllowShowSearchHistory = typedArray.getBoolean(R.styleable.FairySearchView_isAllowShowSearchHistory, true);
            deleteHistoryRecordViewType = typedArray.getInteger(R.styleable.FairySearchView_deleteHistoryRecordType, 1);

            btnBackArrowIcon = typedArray.getResourceId(R.styleable.FairySearchView_backIcon, R.drawable.ic_back);
            searchIcon = typedArray.getResourceId(R.styleable.FairySearchView_searchIcon, R.drawable.ic_search_gray);
            btnDeleteIcon = typedArray.getResourceId(R.styleable.FairySearchView_clearIcon, R.drawable.ic_clear);
            btnSearchTextHint = getOrDefault(typedArray.getString(R.styleable.FairySearchView_btnSearchHint), resources.getString(R.string.btn_search_Text_hint));
            btnSearchTextSize = typedArray.getDimensionPixelSize(R.styleable.FairySearchView_btnSearchSize, resources.getDimensionPixelSize(R.dimen.btn_search_text_size));
            btnSearchTextColor = typedArray.getColor(R.styleable.FairySearchView_btnSearchColor, resources.getColor(R.color.btn_search_text_color));

            editTextSize = typedArray.getDimensionPixelSize(R.styleable.FairySearchView_searchTextSize, resources.getDimensionPixelSize(R.dimen.edit_text_size));
            editTextColor = typedArray.getColor(R.styleable.FairySearchView_searchTextColor, resources.getColor(R.color.edit_text));
            editTextHint = getOrDefault(typedArray.getString(R.styleable.FairySearchView_searchHint), resources.getString(R.string.edit_hint));
            editTextHintColor = typedArray.getColor(R.styleable.FairySearchView_searchHintColor, resources.getColor(R.color.edit_hint_text));
            searchViewBackground = typedArray.getColor(R.styleable.FairySearchView_searchViewBackground, resources.getColor(R.color.search_view_background));
            searchViewHeight = typedArray.getDimensionPixelSize(R.styleable.FairySearchView_searchViewHeight, resources.getDimensionPixelSize(R.dimen.search_view_height));
            maxEditTextLength = typedArray.getInteger(R.styleable.FairySearchView_maxSearchLength, -1);//默认不限制

            //回收资源，否则再次使用会出错
            typedArray.recycle();
        }
    }

    //初始化Views
    private void initSearchViews() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_fairy_search_view, this, true);
        rlSearchView = findViewById(R.id.rl_search_view);
        rlSearchHistoryView = findViewById(R.id.rl_search_history_cview);
        btnBackArrow = findViewById(R.id.btn_back);
        editText = findViewById(R.id.edit_search);
        btnDelete = findViewById(R.id.btn_delete);
        btnSearch = findViewById(R.id.btn_search);
        imageDeleteHistory = findViewById(R.id.image_delete_history);
        tvDeleteHistory = findViewById(R.id.tv_delete_history);
        searchHistoryTagFlowLayout = findViewById(R.id.search_history_tag_flow_layout);

        // 设置搜索框背景色
        rlSearchView.setBackgroundColor(searchViewBackground);

        // 设置返回按钮
        btnBackArrow.setImageResource(btnBackArrowIcon);

        // 设置delete按钮
        btnDelete.setImageResource(btnDeleteIcon);

        // 设置btnSearch
        btnSearch.setText(btnSearchTextHint);
        btnSearch.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSearchTextSize);
        btnSearch.setTextColor(btnSearchTextColor);

        // 设置EditText
        editText.setTextColor(editTextColor);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, editTextSize);
        editText.setHintTextColor(editTextHintColor);
        editText.setHint(editTextHint);
        limitEditLength(maxEditTextLength);//限制输入内容最大长度（默认不限制）
        limitSearchViewHeight(searchViewHeight);//设置输入框高度

        //显示或隐藏控件
        btnBackArrow.setVisibility(showBackButton ? VISIBLE : GONE);
        btnSearch.setVisibility(showCancelButton ? VISIBLE : GONE);
        // 是否显示输入框左侧的[搜索]图标
        showOrHideEditTextLeftSearchIcon(editText, isShowEditTextLeftSearchIcon, searchIcon);
        showOrHideDeleteButton(btnDelete, isShowClearButton, editTextSearchContent);

        //点击搜索框返回按钮，关闭搜索页面
        btnBackArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBtnBackArrowIconListener != null) {
                    onBtnBackArrowIconListener.onClick();
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && isShowClearButton) {
                    btnDelete.setVisibility(VISIBLE);
                } else {
                    btnDelete.setVisibility(GONE);
                }
                if (TextUtils.isEmpty(s)) {
                    // 当编辑框的内容为空的时候，显示历史搜索
                    showOrHideSearchHistory(true);
                }
                if (onEditChangeListener != null) {
                    onEditChangeListener.onEditChanged(s.toString());
                }
            }
        });
        //输入法右下角回车/搜索按钮被点击
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 开始搜索
                    startSearch(editText.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        // 点击编辑框里面的删除按钮，清除搜索框内容
        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteEditTextClickListener != null) {
                    onDeleteEditTextClickListener.onClick(editText.getText().toString().trim());
                    clearEditTextSearchContent();//默认实现
                } else {
                    clearEditTextSearchContent();//默认实现
                }
            }
        });
        // 点击搜索按钮
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开始搜索
                startSearch(editText.getText().toString().trim());
            }
        });
    }

    /**
     * 初始化搜索历史控件
     */
    private void initSearchHistoryView() {
        if (!isAllowShowSearchHistory) {
            return;
        }
        // 接下来开始初始化历史搜索view的相关控件
        imageDeleteHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTagAdapterList == null || mTagAdapterList.isEmpty()) {
                    Toast.makeText(mContext, "没有搜索记录，无法删除", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (onDeleteHistorySearchDataClickListener != null) {
                    onDeleteHistorySearchDataClickListener.onDeleteHistorySearchDataClick();
                }
            }
        });
        tvDeleteHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTagAdapterList == null || mTagAdapterList.isEmpty()) {
                    Toast.makeText(mContext, "没有搜索记录，无法删除", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (onDeleteHistorySearchDataClickListener != null) {
                    onDeleteHistorySearchDataClickListener.onDeleteHistorySearchDataClick();
                }
            }
        });
        // 控制清除记录按钮是否显示
        controlDeleteHistoryRecordView(false);


        // 历史搜索数据适配器
        mSearchLabelFlowAdapter = new SearchLabelFlowAdapter(mContext, mTagAdapterList, searchHistoryTagFlowLayout);
        // 历史搜索流式布局
        searchHistoryTagFlowLayout.setAdapter(mSearchLabelFlowAdapter);
        searchHistoryTagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Log.e(TAG, "onTagClick: 点击的流式布局位置 position = " + position + ", " + mTagAdapterList.get(position));
                // 开始搜索
                startSearch(mTagAdapterList.get(position));
                return false;
            }
        });
    }

    /**
     * 刷新历史搜索记录
     */
    private void refreshHistorySearchData() {
        try {
            if (mSearchLabelFlowAdapter != null) {
                mTagAdapterList.clear();
                String tempHistoryData = searchHistoryContentBuilder.toString();
                if (!TextUtils.isEmpty(tempHistoryData)) {
                    String[] tempHistoryDataArray = tempHistoryData.split(HISTORY_DATA_SEPARATOR);
                    mTagAdapterList.addAll(Arrays.asList(tempHistoryDataArray));
                    mSearchLabelFlowAdapter.notifyDataChanged();
                }
                if (mTagAdapterList == null || mTagAdapterList.isEmpty()) {
                    // 没有历史记录，则不显示删除历史记录按钮控件
                    controlDeleteHistoryRecordView(false);
                } else {
                    controlDeleteHistoryRecordView(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除历史搜索
     */
    public void deleteHistorySearchData() {
        mTagAdapterList.clear();
        mSearchLabelFlowAdapter.notifyDataChanged();
        // 清空历史记录
        searchHistoryContentBuilder.delete(0, searchHistoryContentBuilder.length());
        saveHistorySearchContent();
    }

    /**
     * 是否显示编辑框内的删除按钮
     *
     * @param button
     * @param isShow
     * @param text
     */
    private void showOrHideDeleteButton(ImageButton button, boolean isShow, String text) {
        if (isShow && !TextUtils.isEmpty(text)) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    /**
     * 显示/隐藏历史搜索
     *
     * @param isShow
     */
    private void showOrHideSearchHistory(boolean isShow) {
        if (isAllowShowSearchHistory) {
            // 只有允许显示搜索历史的情况下，才可以操作
            rlSearchHistoryView.setVisibility(isShow ? View.VISIBLE : View.GONE);
            if (isShow) {
                // 刷新数据
                refreshHistorySearchData();
            }
        }
        if (mOnShowSearchListListener != null) {
            // 根据历史搜索的显示/隐藏确定数据列表是否显示
            // 如果显示历史搜索，就不显示数据列表
            mOnShowSearchListListener.onIsShowSearchList(!isShow);
        }
    }

    /**
     * 是否显示输入框左侧的[搜索]图标
     *
     * @param view
     * @param isShow
     * @param imageRes
     */
    private void showOrHideEditTextLeftSearchIcon(EditText view, boolean isShow, int imageRes) {
        if (isShow) {
            view.setCompoundDrawablesWithIntrinsicBounds(imageRes, 0, 0, 0);
            view.setPadding(0, 0, 0, 0);//清除内边距
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            view.setPadding(editTextPaddingLeft, 0, 0, 0);//设置左侧内边距
        }
    }

    /**
     * 开始搜索
     *
     * @param content
     */
    private void startSearch(String content) {
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.empty_search_content), Toast.LENGTH_SHORT).show();
            return;
        }
        if (onBtnSearchClickListener != null) {
            // 之所以前后加上HISTORY_DATA_SEPARATOR，目的是为了保证，判断的是一个独立的字符串
            // 例如：content = "6";但是searchHistoryContentBuilder包含了字符串"165"，
            // 这个时候把数据content变成",6,"的话，就能保证content不会被字符串"165"包含；判断结果就不会误判；
            // 注意：还要考虑searchHistoryContentBuilder的第一个数据(下标为0)，这个数据没有前缀","，只有后缀","：content + HISTORY_DATA_SEPARATOR
            if (searchHistoryContentBuilder.toString().contains(HISTORY_DATA_SEPARATOR + content + HISTORY_DATA_SEPARATOR)
                    || (0 == searchHistoryContentBuilder.toString().indexOf(content + HISTORY_DATA_SEPARATOR)
                    && searchHistoryContentBuilder.toString().contains(content + HISTORY_DATA_SEPARATOR))) {
                editText.setText(content);
                editText.setSelection(content.length());
            } else {
                // 添加历史搜索记录到searchHistoryContentBuilder，在合适的地方调用saveHistorySearchContent()方法保存历史搜索记录
                searchHistoryContentBuilder.append(content).append(HISTORY_DATA_SEPARATOR);
            }
            // 当开始搜索的时候，隐藏历史搜索view
            showOrHideSearchHistory(false);
            onBtnSearchClickListener.onClick(content);
        }
    }

    //如果目标字符串为空，就获取一个默认字符
    private String getOrDefault(String target, String defaultStr) {
        if (TextUtils.isEmpty(target)) {
            return defaultStr;
        }
        return target;
    }

    //清除输入框中的内容
    private void clearEditTextSearchContent() {
        editTextSearchContent = "";
        editText.setText(editTextSearchContent);
    }

    //设置输入框的最大内容长度
    private void limitEditLength(int length) {
        if (length > 0) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxEditTextLength)});
        }
    }

    //设置输入框高度
    private void limitSearchViewHeight(int searchViewHeight) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlSearchView.getLayoutParams();
        layoutParams.height = searchViewHeight;
        rlSearchView.setLayoutParams(layoutParams);
    }

    /**
     * 控制清除记录按钮是否显示
     *
     * @param isShowView true：显示
     */
    private void controlDeleteHistoryRecordView(boolean isShowView) {
        if (1 == deleteHistoryRecordViewType) {
            // 使用文字按钮，隐藏图片按钮
            imageDeleteHistory.setVisibility(GONE);
            tvDeleteHistory.setVisibility(isShowView ? View.VISIBLE : View.GONE);
        } else if (2 == deleteHistoryRecordViewType) {
            // 使用图片按钮，隐藏文字按钮
            tvDeleteHistory.setVisibility(GONE);
            imageDeleteHistory.setVisibility(isShowView ? View.VISIBLE : View.GONE);
        }
    }

    /************************************向外界暴露的方法*******************************/

    /**
     * 获取清除记录--文字按钮控件
     *
     * @return
     */
    private TextView getTvDeleteHistory() {
        return tvDeleteHistory;
    }

    /**
     * 设置SearchView的高度（单位为px）
     */
    public void setSearchViewHeight(int searchViewHeight) {
        this.searchViewHeight = searchViewHeight;
        limitSearchViewHeight(searchViewHeight);
    }

    //设置输入内容
    public void setSearchContentText(String text) {
        editTextSearchContent = text;
        editText.setText(editTextSearchContent);
    }

    /**
     * 设置输入内容的文字大小
     *
     * @param searchTextSize 文字大小（单位为px）
     */
    public void setSearchContentTextSize(int searchTextSize) {
        this.editTextSize = searchTextSize;
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, searchTextSize);
    }

    public void setSearchContentTextColor(@ColorInt int searchTextColor) {
        this.editTextColor = searchTextColor;
        editText.setTextColor(searchTextColor);
    }

    public void setSearchContentHint(String searchHint) {
        this.editTextHint = searchHint;
        editText.setHint(searchHint);
    }

    public void setSearchContentHintColor(int searchHintColor) {
        this.editTextHintColor = searchHintColor;
        editText.setHintTextColor(searchHintColor);
    }

    /**
     * 设置搜索按钮的文字
     *
     * @param btnSearchTextHint
     */
    public void setBtnSearchText(String btnSearchTextHint) {
        this.btnSearchTextHint = btnSearchTextHint;
        btnSearch.setText(btnSearchTextHint);
    }

    /**
     * 设置搜索按钮的文字大小
     *
     * @param btnSearchTextSize 文字大小（单位为px）
     */
    public void setBtnSearchTextSize(int btnSearchTextSize) {
        this.btnSearchTextSize = btnSearchTextSize;
        btnSearch.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSearchTextSize);
    }

    /**
     * 设置搜索按钮的文字颜色
     */
    public void setBtnSearchTextColor(@ColorInt int btnSearchTextColor) {
        this.btnSearchTextColor = btnSearchTextColor;
        btnSearch.setTextColor(btnSearchTextColor);
    }

    public int getMaxSearchLength() {
        return maxEditTextLength;
    }

    //限制输入内容的最大长度
    public void setMaxSearchLength(int maxSearchLength) {
        if (maxSearchLength > 0) {
            this.maxEditTextLength = maxSearchLength;
            limitEditLength(maxSearchLength);
        }
    }

    /**
     * 设置是否允许显示历史搜索的内容
     *
     * @param allowShowSearchHistory
     */
    public void setAllowShowSearchHistory(boolean allowShowSearchHistory) {
        this.isAllowShowSearchHistory = allowShowSearchHistory;
    }

    public void setBackIcon(@DrawableRes int btnBackArrowIcon) {
        this.btnBackArrowIcon = btnBackArrowIcon;
        btnBackArrow.setImageResource(btnBackArrowIcon);
    }

    public void setSearchIcon(@DrawableRes int searchIcon) {
        this.searchIcon = searchIcon;
        editText.setCompoundDrawablesWithIntrinsicBounds(searchIcon, 0, 0, 0);
    }

    public void setClearIcon(@DrawableRes int clearIcon) {
        this.btnDeleteIcon = clearIcon;
        btnDelete.setImageResource(clearIcon);
    }

    public boolean isShowBackButton() {
        return showBackButton;
    }

    public void setShowBackButton(boolean showBackButton) {
        this.showBackButton = showBackButton;
        btnBackArrow.setVisibility(showBackButton ? VISIBLE : GONE);
    }

    public boolean isShowSearchIcon() {
        return isShowClearButton;
    }

    public void setShowSearchIcon(boolean isShowEditTextLeftSearchIcon) {
        this.isShowEditTextLeftSearchIcon = isShowEditTextLeftSearchIcon;
        showOrHideEditTextLeftSearchIcon(editText, isShowEditTextLeftSearchIcon, searchIcon);
    }

    public boolean isShowClearButton() {
        return isShowClearButton;
    }

    public void setShowClearButton(boolean showClearButton) {
        this.isShowClearButton = showClearButton;
        btnDelete.setVisibility(showClearButton ? VISIBLE : GONE);
    }

    public boolean isShowCancelButton() {
        return showCancelButton;
    }

    public void setShowCancelButton(boolean showCancelButton) {
        this.showCancelButton = showCancelButton;
        btnSearch.setVisibility(showCancelButton ? VISIBLE : GONE);
    }

    public void setDeleteHistoryRecordViewType(int deleteHistoryRecordViewType) {
        this.deleteHistoryRecordViewType = deleteHistoryRecordViewType;
    }

    /***************************设置监听器********************************/
    public void setOnBtnBackArrowIconListener(OnBtnBackArrowIconListener onBtnBackArrowIconListener) {
        this.onBtnBackArrowIconListener = onBtnBackArrowIconListener;
    }

    public void setOnDeleteEditTextClickListener(OnDeleteEditTextClickListener onDeleteEditTextClickListener) {
        this.onDeleteEditTextClickListener = onDeleteEditTextClickListener;
    }

    public void setOnBtnSearchClickListener(OnBtnSearchClickListener onBtnSearchClickListener) {
        this.onBtnSearchClickListener = onBtnSearchClickListener;
    }

    public void setOnEditChangeListener(OnEditChangeListener onEditChangeListener) {
        this.onEditChangeListener = onEditChangeListener;
    }

    /**
     * 清除历史搜索按钮监听事件
     *
     * @param onDeleteHistorySearchDataClickListener
     */
    public void setOnDeleteHistorySearchDataClickListener(OnDeleteHistorySearchDataClickListener onDeleteHistorySearchDataClickListener) {
        this.onDeleteHistorySearchDataClickListener = onDeleteHistorySearchDataClickListener;
    }

    /**
     * 监听是否显示搜索数据列表
     *
     * @param onShowSearchListListener
     */
    public void setOnShowSearchListListener(OnShowSearchListListener onShowSearchListListener) {
        mOnShowSearchListListener = onShowSearchListListener;
    }

    //清除所有监听器
    public void clearAllListeners() {
        onBtnBackArrowIconListener = null;
        onDeleteEditTextClickListener = null;
        onBtnSearchClickListener = null;
        onEditChangeListener = null;
        onDeleteHistorySearchDataClickListener = null;
    }

    /**
     * 使用SharedPreferences存储历史搜索记录时，使用的key，不同的key存储不同的数据
     *
     * @param saveSearchHistoryDataKey
     */
    public void setSaveSearchHistoryDataKey(String saveSearchHistoryDataKey) {
        this.saveSearchHistoryDataKey = saveSearchHistoryDataKey;
        if (isAllowShowSearchHistory) {
            // 获取历史搜索记录
            String tempHistoryData = getHistorySearchContent();
            if (!TextUtils.isEmpty(tempHistoryData)) {
                searchHistoryContentBuilder.append(tempHistoryData);
            }
            // 设置key时：判断是否显示历史搜索记录view
            showOrHideSearchHistory(true);
        }
    }

    /**
     * 保存搜索内容
     */
    public void saveHistorySearchContent() {
        if (TextUtils.isEmpty(saveSearchHistoryDataKey)) {
            return;
        }
        try {
            getSharedPreferencesEditor().putString(saveSearchHistoryDataKey, searchHistoryContentBuilder.toString()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 输入框内容
     */
    public String getEditTextSearchContent() {
        return editText.getText().toString();
    }

    /**
     * 获取历史搜索内容
     *
     * @return
     */
    public String getHistorySearchContent() {
        if (TextUtils.isEmpty(saveSearchHistoryDataKey)) {
            return "";
        }
        return getSharedPreferences().getString(saveSearchHistoryDataKey, "");
    }

    /**
     * 获取SharedPreferences对象
     *
     * @return
     */
    public SharedPreferences getSharedPreferences() {
        if (mContext == null) {
            return null;
        }
        if (mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    /**
     * 获取SharedPreferences.Editor对象
     *
     * @return
     */
    public SharedPreferences.Editor getSharedPreferencesEditor() {
        if (mSharedPreferencesEditor == null) {
            mSharedPreferencesEditor = getSharedPreferences().edit();
        }
        return mSharedPreferencesEditor;
    }

    /**
     * 监听返回按钮点击事件
     */
    public interface OnBtnBackArrowIconListener {
        void onClick();
    }

    /**
     * 监听清除按钮点击事件
     */
    public interface OnDeleteEditTextClickListener {
        /**
         * @param oldContent 被删除的输入框内容
         */
        void onClick(String oldContent);
    }

    /**
     * 监听搜索按钮点击事件
     */
    public interface OnBtnSearchClickListener {
        /**
         * 搜索的内容
         *
         * @param searchContent
         */
        void onClick(String searchContent);
    }

    /**
     * 监听输入框内容变化
     */
    public interface OnEditChangeListener {
        /**
         * @param nowContent 输入框当前的内容
         */
        void onEditChanged(String nowContent);
    }

    /**
     * 监听用户点击了虚拟键盘中右下角的回车/搜索键
     * 此时可以选择执行搜索操作
     */
    public interface OnEditTextEnterClickListener {
        /**
         * @param content 输入框中的内容
         */
        void onEditTextEnterClick(String content);
    }

    /**
     * 监听用户点击了删除历史搜索按钮
     */
    public interface OnDeleteHistorySearchDataClickListener {
        /**
         * 用户点击了删除历史搜索按钮
         */
        void onDeleteHistorySearchDataClick();
    }

    /**
     * 监听是否显示搜索数据列表
     */
    public interface OnShowSearchListListener {
        /**
         * 监听是否显示搜索数据列表
         *
         * @param isShowSearchList
         */
        void onIsShowSearchList(boolean isShowSearchList);
    }
}