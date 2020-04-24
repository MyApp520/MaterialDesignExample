package com.smile.commonlib.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xh_peng on 2017/11/7.
 */
public class TimeUitls {

    /**
     * 获取当前时间
     *
     * @param flag
     * @return
     */
    public static String getCurrentTime(int flag, Date currentDate) {
        SimpleDateFormat formatter = null;
        switch (flag) {
            case 1:
                formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                break;
            case 2:
                formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                break;
            case 3:
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case 4:
                formatter = new SimpleDateFormat("yyyy年MM月");
                break;
            case 5:
                formatter = new SimpleDateFormat("yyyy年MM月dd日");
                break;
        }
        if (formatter == null) {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }
        if (currentDate != null) {
            return formatter.format(currentDate);
        }
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static Date getDateByStringTime(int flag, String time) {
        SimpleDateFormat formatter = null;
        switch (flag) {
            case 1:
                formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                break;
            case 2:
                formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                break;
            case 3:
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case 4:
                formatter = new SimpleDateFormat("yyyy年MM月");
                break;
        }
        if (formatter == null) {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }
        if (!TextUtils.isEmpty(time)) {
            try {
                return formatter.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return new Date(System.currentTimeMillis());
    }

    public static Calendar getCalendarByStringTime(int flag, String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDateByStringTime(flag, time));
        return calendar;
    }

    public static long getTimeMillisByString(int flag, String srcTime) {
        if (TextUtils.isEmpty(srcTime) || "".equals(srcTime.trim())) {
            return 0;
        }
        SimpleDateFormat formatter = null;
        switch (flag) {
            case 1:
                formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                break;
            case 2:
                formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                break;
            case 3:
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                break;
        }
        Date desDate = null;
        try {
            desDate = formatter.parse(srcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (desDate == null) {
            return 0;
        }
        return desDate.getTime();
    }

    /**
     * 计算指定时间和当前时间相差多少
     *
     * @param flag
     * @param endTime 指定时间
     * @return
     */
    public static String getTimestampToCurrentTime(int flag, String endTime) {
        if (StringUtils.judgeStringIsNull(endTime)) {
            return "";
        }
        long endTimeMillis = getTimeMillisByString(flag, endTime);
        Date currentDate = new Date(System.currentTimeMillis());
        long timestamp = endTimeMillis - currentDate.getTime();
        if (timestamp < 0) {
            return endTime;
        }
        if (timestamp < 3600) {
            return "1小时内结束";
        }
        long days = timestamp / (1000 * 60 * 60 * 24);
        long hours = (timestamp - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (timestamp - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (timestamp - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / (1000);
        if (days <= 0) {
            return hours + "小时后结束";
        }
        return days + "天" + hours + "小时后结束";
    }

    /**
     * 获取结束时间减去开始时间的时间戳的值
     *
     * @param flag
     * @param endTime   结束时间
     * @param startTime 开始时间
     * @return endTimeMillis - startTimeMillis（大于0：结束时间大于开始时间）
     */
    public static long getTimestamp(int flag, String endTime, String startTime) {
        if (StringUtils.judgeStringIsNull(startTime) || StringUtils.judgeStringIsNull(endTime)) {
            return 0;
        }
        long endTimeMillis = getTimeMillisByString(flag, endTime);
        long startTimeMillis = getTimeMillisByString(flag, startTime);
        return endTimeMillis - startTimeMillis;
    }

    /**
     * 根据当前时间，计算之前的时间
     *
     * @param preMonths 往前几个月
     * @return
     */
    public static String getBeforeMonthFromNow(int flag, int preMonths) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        if (month <= 0) {
            calendar.roll(Calendar.YEAR, -1);
        }
        calendar.roll(Calendar.MONTH, -preMonths);
        return getCurrentTime(flag, calendar.getTime());
    }

    /**
     * 根据当前时间，计算之前的时间
     *
     * @param preDays 往前几天
     * @return
     */
    public static String getBeforeDayFromNow(int flag, int preDays) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        if (month <= 0) {
            calendar.roll(Calendar.YEAR, -1);
        }
        calendar.roll(Calendar.DAY_OF_YEAR, -preDays);
        return getCurrentTime(flag, calendar.getTime());
    }

    /**
     * 获取相对于今天的某一天的0点时间
     *
     * @param amount 距离今天几天
     * @return
     */
    public static Date getDayZeroTime(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        if (amount != 0) {
            calendar.add(Calendar.DAY_OF_MONTH, amount);
        }
        return calendar.getTime();
    }

    /**
     * 获取相对于今天的某一天的24点时间
     *
     * @param amount 距离今天几天
     * @return
     */
    public static Date getDayEndTime(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        if (amount != 0) {
            calendar.add(Calendar.DAY_OF_MONTH, amount);
        }
        return calendar.getTime();
    }
}
