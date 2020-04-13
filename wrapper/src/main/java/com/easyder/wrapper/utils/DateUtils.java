package com.easyder.wrapper.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author 刘琛慧
 *         date 2016/7/22.
 */
public class DateUtils {
    public final static String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy/MM/dd HH:mm:ss";
    public final static String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND2 = "yyyy-MM-dd HH:mm";
    public final static String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND4 = "yyyy-MM-dd HH:mm:ss";
    public final static String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND3 = "HH:mm:ss";
    public final static String YEAR_MONTH_DAY_HOUR_MINUTE = "yyyy/MM/dd HH:mm";
    public final static String YEAR_MONTH_DAY = "yyyy-MM-dd";
    public final static String HOUR_MINUTE = "HH:mm";

    public static String format(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return dateFormat.format(new Date(time));
    }
    public static String formatDateByLong(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND2);
        return dateFormat.format(new Date(time));
    }

    public static String format(long time, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = new Date(time);
        return sdf.format(date);
    }


    public static Date parse(String date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static Date parse(String date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatCurrentDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    /**
     * 格式指定格式的时间戳
     *
     * @param pattern
     * @param time
     * @return
     */
    public static String getFormatTime(String pattern, Object time) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(pattern);
        return sdf.format(Long.parseLong(String.valueOf(time)));
    }



    /**
     * 判断是否是当天
     *
     * @param time
     * @return
     */
    public static boolean isToday(Object time) {
        return getFormatTime(YEAR_MONTH_DAY).equals(getFormatTime(YEAR_MONTH_DAY, time));
    }

    /**
     * 指定格式化的当前时间
     *
     * @param pattern
     * @return
     */
    public static String getFormatTime(String pattern) {
        return getFormatTime(pattern, System.currentTimeMillis());
    }

    /**
     * 格式化特定格式的时间戳
     * 当天 HOUR_MINUTE 样式
     * 非当天 YEAR_MONTH_DAY_HOUR_MINUTE 样式
     *
     * @param time
     * @return
     */
    public static String getFormatAppointedTime(String time) {
        time = String.format("%1$s%2$s", time, "000");
        return getFormatTime(isToday(time) ? HOUR_MINUTE : YEAR_MONTH_DAY_HOUR_MINUTE, time);
    }

    public static String getFormatTime2(String time) {
        time = String.format("%1$s%2$s", time, "000");
        return getFormatTime(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, time);
    }

    public static String getFNBFormantTime(String time){
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return simpleDateFormat2.format(Long.parseLong(time));
    }
    public static long getFNBFormantTimeToLong(String time){
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long timeMillis = System.currentTimeMillis();
        try {
           Date date = simpleDateFormat2.parse(time);
           timeMillis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeMillis;
    }
    public static String getFNBFormantTimeNoTime(String time){
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
        return simpleDateFormat2.format(Long.parseLong(time));
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsToday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }
    //今天的日期
    public static String getTodayDate() {
         return   new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
    public static String getTodayDate(String format) {
        return   new SimpleDateFormat(format).format(new Date());
    }

    public static String getNowTime(){
        return new SimpleDateFormat(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND4).format(new Date());
    }
    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    /**取本月第一天或最后一天*/
    public static String getThisMonthFirstDay(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

    }

    /**
     * 判断两个日期的前后
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static int compareDate(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断两个时间的前后
     * @param time1
     * @param time2
     * @return
     */
    public static int compareDateTime(String time1, String time2) {
        DateFormat df = new SimpleDateFormat(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND2);
        try {
            Date dt1 = df.parse(time1);
            Date dt2 = df.parse(time2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 更晚");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1 更早");
                return -1;
            } else {
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();

    //  缓存列表时间的格式化
    public static String formatSaveTime(String time){
        StringBuilder sb=new StringBuilder();
        if(time!=null&&time.length()>7){
            sb.append(time.substring(0,4)+"-");
            sb.append(time.substring(4,6)+"-");
            sb.append(time.substring(6,8));
        }
        return sb.toString();
    }
    /**
     * 将时间字符串转为时间戳
     * <p>time格式为pattern</p>
     *
     * @param time    时间字符串
     * @param pattern 时间格式
     * @return 毫秒时间戳
     */
    public static long string2Millis(String time, String pattern) {
        try {
            return new SimpleDateFormat(pattern, Locale.getDefault()).parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static Date string2Date(String time, String pattern) {
        return new Date(string2Millis(time, pattern));
    }
    /**
     * 将Date类型转为时间字符串
     * <p>格式为pattern</p>
     *
     * @param date    Date类型时间
     * @param pattern 时间格式
     * @return 时间字符串
     */
    public static String date2String(Date date, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }

    public static String date2String(Date date) {
        return date2String(date, "yyyy-MM-dd HH:mm:ss");
    }
    public static String formatIncomingTime(String time){
        if(!TextUtils.isEmpty(time)){
            Date date=string2Date(time,"yyyyMMddHHmmss");
            return date2String(date);
        }
        return "";
    }

}


