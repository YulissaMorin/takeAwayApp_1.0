package com.yulissa.takeawayapp.util;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 获取指定日期所在周/月/年以及
 * 指定日期所在的周每一天的日期/指定日期所在的月的每一天的日期
 * 待完善
 * @author yulissa
 */
public class WhichDateUtil {

    /**
     *获取当前时间所在周的每天的对应日期
     * @param day 传入1-7代表周一至周日
     * @return
     */
    public static String getDateInWeek(int day) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cld = Calendar.getInstance(Locale.CHINA);
        cld.setTimeInMillis(System.currentTimeMillis());
        cld.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if (day > 1) {
            cld.add(Calendar.DATE, day - 1);
        }
        return df.format(cld.getTime());
    }

    public static int whatDayItIsToday() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cld = Calendar.getInstance(Locale.CHINA);
        cld.setTimeInMillis(System.currentTimeMillis());
        return cld.get(Calendar.DAY_OF_WEEK)-1;
    }

    @Test
    public void test() {
        System.out.println(getDateInWeek(5));
        System.out.println(String.valueOf(whatDayItIsToday()));
    }
}
