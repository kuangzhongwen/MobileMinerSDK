package waterhole.commonlibs.utils;

import android.annotation.SuppressLint;
import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具
 *
 * @author kzw on 2016/02/25.
 */
public final class DateUtils {

    // 一小时的分钟数
    private static final long MINUTES_IN_A_HOUR = 60;
    // 一天的分钟数
    public static final long MINUTES_IN_A_DAY = 24 * MINUTES_IN_A_HOUR;
    // 一天的毫秒数
    private static final long MILLS_IN_A_DAY = MINUTES_IN_A_DAY * 60 * 1000L;

    public static final String DEFAULT_DATE_FORMAT0 = "yyyy-MM-dd HH:mm:ss";
    private static final String YEAR_MONTH_FORMATTER = "yyyy-MM";
    public static final String FORMAT_yyyy_MM_dd_hh_mm = "yyyy-MM-dd HH:mm";
    private static final String TIME_FORMAT_0 = "--:--:--";

    private DateUtils() {
    }

    /**
     * 判断两个日期是否为同一天
     */
    public static boolean isToday(Date currentDate, Date compareDate) {
        return !compareObjectIsNull(currentDate, compareDate) && getTimeDifference(currentDate,
                compareDate) <= MILLS_IN_A_DAY && isEqualsDay(currentDate, compareDate);
    }

    /**
     * 判读日期是否为昨天
     */
    public static boolean isYesterday(Date currentDate, Date compareDate) {
        return !compareObjectIsNull(currentDate, compareDate) && getTimeDifference(currentDate,
                compareDate) <= 2 * MILLS_IN_A_DAY && getDayDifference(currentDate, compareDate) == 1;
    }

    /**
     * 判断两个日期是否在同一周
     */
    public static boolean inSameWeek(Date currentDate, Date compareDate) {
        if (compareObjectIsNull(currentDate, compareDate)) {
            return false;
        }
        Calendar currentCalendar = getCalendar(currentDate);
        Calendar compareCalendar = getCalendar(compareDate);
        return getTimeDifference(currentDate, compareDate) <= 7 * MILLS_IN_A_DAY && isEqualsWeek(
                currentCalendar, compareCalendar);
    }

    private static Calendar getCalendar(Date currentDate) {
        if (currentDate != null) {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeInMillis(currentDate.getTime());
            currentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            return currentCalendar;
        }
        return null;
    }

    /**
     * 获取时间差
     */
    private static long getTimeDifference(Date currentDate, Date compareDate) {
        return currentDate.getTime() - compareDate.getTime();
    }

    /**
     * 获取日期差
     */
    private static int getDayDifference(Date currentDate, Date compareDate) {
        return currentDate.getDay() - compareDate.getDay();
    }

    /**
     * 日是否相同
     */
    private static boolean isEqualsDay(Date currentDate, Date compareDate) {
        return currentDate.getDay() == compareDate.getDay();
    }

    /**
     * 周是否相同
     */
    private static boolean isEqualsWeek(Calendar currentCalendar, Calendar compareCalendar) {
        return currentCalendar.get(Calendar.WEEK_OF_YEAR) == compareCalendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static String getLongAgoDisplayDesc(String bubbleText, Date msgDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(msgDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        bubbleText += (year / 10) % 10 + "" + year % 10 + "";
        bubbleText += "/";
        bubbleText += month / 10 + "" + month % 10;
        bubbleText += "/";
        bubbleText += day / 10 + "" + day % 10;
        bubbleText += "  ";

        return bubbleText;
    }

    public static long getDateTime() {
        return new Date().getTime();
    }

    public static String formatDate(long time) {
        return formatDate(time, DEFAULT_DATE_FORMAT0);
    }

    public static String formatDate(long time, String format) {
        Date date = new Date(time);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 格式化倒计时时间  格式为 HH...:mm:ss
     */
    public static String formatCounterTime(long time) {
        if (time < 0) {
            return TIME_FORMAT_0;
        }
        long hours = time / 3600;
        long minutes = (time % 3600) / 60;
        long seconds = time % 60;

        return new StringBuilder(36).append(formatNumberToTwoPos(hours)).append(" : ")
                .append(formatNumberToTwoPos(minutes)).append(" : ")
                .append(formatNumberToTwoPos(seconds)).toString();
    }

    /**
     * 格式化数字 将数字格式化为至少2位
     */
    private static String formatNumberToTwoPos(long time) {
        StringBuilder sb = new StringBuilder(36);
        return time < 10 ? sb.append(0).append(time).toString() : sb.append(time).toString();
    }

    private static boolean compareObjectIsNull(Date currentDate, Date compareDate) {
        return currentDate == null || compareDate == null;
    }

    public static int getMonthFromTime(long time) {
        return new Date(time).getMonth() + 1;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getYearMonthStrFromTime(long time) {
        return new SimpleDateFormat(YEAR_MONTH_FORMATTER).format(new Date(time));
    }

    public static long convertLinuxTime(long linuxTime) {
        return linuxTime * 1000;
    }

    public static long getCurrentTimeMillis() {
        return SystemClock.currentThreadTimeMillis();
    }
}
