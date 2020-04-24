package org.smile.mde.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.smile.commonlib.util.ShowToast;
import com.smile.commonlib.util.UIUtils;

import org.smile.mde.R;

/**
 * Created by smile on 2019/7/10.
 */

public class CommonEditText extends RelativeLayout implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener {
    private final String TAG = CommonEditText.class.getSimpleName();
    private Context context;

    private ImageView leftIconImageView, rightDeleteImageView, rightVisibleImageView;
    private EditText editText;
    private View verticalView;
    /**
     * 是否 是 密码 输入 框
     */
    private boolean editTextIsPassWordType;
    /**
     * 是否 显示 密码
     */
    private boolean passwordDisplayTag;

    public CommonEditText(Context context) {
        this(context, null);
    }

    public CommonEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getAttrs(attrs);
        initView();
        // 先默认 隐藏
        rightDeleteImageView.setVisibility(GONE);
        rightVisibleImageView.setVisibility(GONE);
    }

    private void getAttrs(AttributeSet attrs) {
        @SuppressLint("CustomViewStyleable")
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonEditTextStyle);

        editTextIsPassWordType = typedArray.getBoolean(R.styleable.CommonEditTextStyle_editTextIsPassWordType, true);
        Drawable iconDrawable = typedArray.getDrawable(R.styleable.CommonEditTextStyle_leftIconDrawable);
        Drawable visibleDrawable = typedArray.getDrawable(R.styleable.CommonEditTextStyle_rightVisibleDrawable);
        Drawable deleteDrawable = typedArray.getDrawable(R.styleable.CommonEditTextStyle_rightDeleteDrawable);

        typedArray.recycle();
    }

    private void initView() {
        addLeftImageView();
        addVerticalLine();
        addRightVisibleImageView();
        addRightDeleteImageView();
        addEditText();

        rightDeleteImageView.setOnClickListener(this);
        rightVisibleImageView.setOnClickListener(this);
        editText.addTextChangedListener(this);
        editText.setOnFocusChangeListener(this);
    }

    private void addLeftImageView() {
        leftIconImageView = new ImageView(context);
        leftIconImageView.setId(R.id.edit_text_left_icon);
        leftIconImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        leftIconImageView.setImageResource(R.drawable.password_unclick);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.width = UIUtils.dp2px(context, 20);
        params.height = UIUtils.dp2px(context, 20);
        params.leftMargin = UIUtils.dp2px(context, 8);
        params.rightMargin = UIUtils.dp2px(context, 4);
        params.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);

        addView(leftIconImageView, 0, params);
    }

    private void addVerticalLine() {
        verticalView = new View(context);
        verticalView.setId(R.id.edit_text_line_view);
        verticalView.setBackgroundResource(R.color.backgroup);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.width = UIUtils.dp2px(context, 1);
        params.topMargin = UIUtils.dp2px(context, 12);
        params.bottomMargin = UIUtils.dp2px(context, 12);
        params.addRule(RelativeLayout.RIGHT_OF, leftIconImageView.getId());

        addView(verticalView, 1, params);
    }

    private void addRightVisibleImageView() {
        rightVisibleImageView = new ImageView(context);
        rightVisibleImageView.setId(R.id.edit_text_delete_icon);
        rightVisibleImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        rightVisibleImageView.setImageResource(R.drawable.ic_launcher);
        rightVisibleImageView.setBackgroundResource(R.color.colorAccent);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.width = UIUtils.dp2px(context, 20);
        params.height = UIUtils.dp2px(context, 20);
        params.leftMargin = UIUtils.dp2px(context, 8);
        params.rightMargin = UIUtils.dp2px(context, 8);
        params.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
        addView(rightVisibleImageView, 2, params);
    }

    private void addRightDeleteImageView() {
        rightDeleteImageView = new ImageView(context);
        rightDeleteImageView.setId(R.id.edit_text_visible_icon);
        rightDeleteImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        rightDeleteImageView.setImageResource(R.drawable.delete);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.width = UIUtils.dp2px(context, 20);
        params.height = UIUtils.dp2px(context, 20);
        params.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
        if (editTextIsPassWordType) {
            params.addRule(RelativeLayout.LEFT_OF, rightVisibleImageView.getId());
        } else {
            params.leftMargin = UIUtils.dp2px(context, 8);
            params.rightMargin = UIUtils.dp2px(context, 8);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
        }

        addView(rightDeleteImageView, 3, params);
    }

    private void addEditText() {
        editText = new EditText(context);
        editText.setId(R.id.edit_text_content);
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setBackground(null);
        editText.setHint("请输入内容");
        if (editTextIsPassWordType) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//最多输入20个字符

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.RIGHT_OF, verticalView.getId());
        params.addRule(RelativeLayout.LEFT_OF, rightDeleteImageView.getId());

        addView(editText, 4, params);
    }

    public EditText getEditText() {
        return editText;
    }

    @Override
    public void onClick(View v) {
        if (rightDeleteImageView.getId() == v.getId()) {
            editText.setText("");
        } else if (rightVisibleImageView.getId() == v.getId()) {
            if (!passwordDisplayTag) {
                // 设置 EditText 的 input type  显示 密码
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // 隐藏 密码
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            passwordDisplayTag = !passwordDisplayTag;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (hasFocus()) {
            // 当 输入 字符 长度 不为0 时 显示 删除 按钮
            if (s.length() > 19) {
                ShowToast.showToast(context.getApplicationContext(), "密码长度不能超过20");
            } else if (s.length() > 0) {
                rightDeleteImageView.setVisibility(VISIBLE);
                if (editTextIsPassWordType) {
                    rightVisibleImageView.setVisibility(VISIBLE);
                }
            } else {
                rightDeleteImageView.setVisibility(GONE);
                rightVisibleImageView.setVisibility(GONE);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // 如果 EditText的 焦点 改变了  则相应的 隐藏 显示 功能 按钮
        if (!hasFocus) {
            rightDeleteImageView.setVisibility(GONE);
            rightVisibleImageView.setVisibility(GONE);
        } else if (editText.getText().length() > 0) {
            rightDeleteImageView.setVisibility(VISIBLE);
            if (editTextIsPassWordType) {
                rightVisibleImageView.setVisibility(VISIBLE);
            }
        }
    }
}
