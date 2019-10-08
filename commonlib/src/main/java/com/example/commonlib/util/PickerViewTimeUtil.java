package com.example.commonlib.util;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.commonlib.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by smile on 2019/7/16.
 */

public class PickerViewTimeUtil {

    private static final String TAG = PickerViewTimeUtil.class.getSimpleName();

    public static TimePickerView initTimePicker(Activity activity, OnTimeSelectListener onTimeSelectListener) {//Dialog 模式下，在底部弹出
        Calendar endDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1988, Calendar.OCTOBER, 1);

        TimePickerView pvTime = new TimePickerBuilder(activity, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Log.i("pvTime", "onTimeSelect");
                if (onTimeSelectListener != null) {
                    onTimeSelectListener.onTimeSelect(date, v);
                }

            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        Log.i("pvTime", "onTimeSelectChanged");
                    }
                })
                .setType(new boolean[]{true, true, false, false, false, false})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("pvTime", "onCancelClickListener");
                    }
                })
                .setRangDate(startDate, endDate)
                .setContentTextSize(18)
                .setLineSpacingMultiplier(1.8f)
                .setTitleBgColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.white))
                .setTitleColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.backgroup))
                .setSubmitColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.backgroup))
                .setCancelColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.default_hint_color))
                .build();

        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.1f);
            }
        }
        return pvTime;
    }


    /********************************************************* 自定义时间选择器的布局 ********************************************************************/
    private static TimePickerView pvCustomTime;
    private static Button btn_cancel, btn_submit;
    private static TextView tv_start_time, tv_end_time;
    private static LinearLayout ll_set_time;
    private static RelativeLayout rl_select_start_time, rl_select_end_time;
    private static boolean isSetStartTime;
    private static OnSelectCustomTimeListener onSelectCustomTimeListener;

    public static TimePickerView initCustomTimePicker(Activity activity, final String initStartTime, final String initEndTime,
                                                      final OnSelectCustomTimeListener onTimeSelectListener) {
        return initCustomTimePicker(activity, initStartTime, initEndTime, true, true, true, true, true, true, onTimeSelectListener);
    }

    public static TimePickerView initCustomTimePicker(Activity activity, final String initStartTime, final String initEndTime,
                                                      boolean isShowYear, boolean isShowMonth, boolean isShowDay,
                                                      boolean isShowHour, boolean isShowMinute, boolean isShowSecond,
                                                      final OnSelectCustomTimeListener onTimeSelectListener) {
        onSelectCustomTimeListener = onTimeSelectListener;
        Calendar endDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1988, Calendar.OCTOBER, 1);
        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerBuilder(activity, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                if (onSelectCustomTimeListener != null) {
                    onSelectCustomTimeListener.onSelectCustomTime(v, tv_start_time.getText().toString().trim(), tv_end_time.getText().toString().trim());
                }
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        if (isSetStartTime) {
                            tv_start_time.setText(TimeUitls.getCurrentTime(3, date));
                        } else {
                            tv_end_time.setText(TimeUitls.getCurrentTime(3, date));
                        }
                    }
                })
                .setLayoutRes(R.layout.custom_picker_time_layout, new CustomListener() {

                    @Override
                    public void customLayout(View customView) {
                        btn_cancel = customView.findViewById(R.id.btn_cancel);
                        btn_submit = customView.findViewById(R.id.btn_submit);
                        ll_set_time = customView.findViewById(R.id.ll_set_time);
                        tv_start_time = customView.findViewById(R.id.tv_start_time);
                        tv_end_time = customView.findViewById(R.id.tv_end_time);
                        rl_select_start_time = customView.findViewById(R.id.rl_select_start_time);
                        rl_select_end_time = customView.findViewById(R.id.rl_select_end_time);

                        isSetStartTime = true;
                        ll_set_time.setBackground(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.begin_time_bg));
                        tv_start_time.setText(initStartTime);
                        tv_end_time.setText(initEndTime);

                        rl_select_start_time.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isSetStartTime = true;
                                ll_set_time.setBackground(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.begin_time_bg));
                                pvCustomTime.setDate(TimeUitls.getCalendarByStringTime(3, tv_start_time.getText().toString().trim()));
                            }
                        });
                        rl_select_end_time.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isSetStartTime = false;
                                ll_set_time.setBackground(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.end_time_bg));
                                pvCustomTime.setDate(TimeUitls.getCalendarByStringTime(3, tv_end_time.getText().toString().trim()));
                            }
                        });
                        btn_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (TimeUitls.getTimestamp(3, tv_end_time.getText().toString().trim(), tv_start_time.getText().toString().trim()) >= 0) {
                                    pvCustomTime.returnData();
                                    pvCustomTime.dismiss();
                                } else {
                                    ShowToast.showToast(activity.getApplicationContext(), "结束时间不能比开始时间更早");
                                }
                            }
                        });
                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .isCyclic(true)//是否循环滚动
                .setRangDate(startDate, endDate)//时间显示范围
                .setContentTextSize(18)
                .setType(new boolean[]{isShowYear, isShowMonth, isShowDay, isShowHour, isShowMinute, isShowSecond})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(1.8f)
                // setTextXOffset设置数据在x轴方向的倾斜角度
                .setTextXOffset(0, 0, 0, 40, 0, -40)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDate(TimeUitls.getCalendarByStringTime(3, initStartTime))//设置当前选中的时间
                .build();
        return pvCustomTime;
    }

    public interface OnSelectCustomTimeListener {
        void onSelectCustomTime(View view, String startTime, String endTime);
    }
}
