package org.easyblog.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class CalendarUtil {


    volatile private static CalendarUtil calendarUtil = null;
    private static final ThreadLocal<SimpleDateFormat> threadLocal=new ThreadLocal<>();
    private final Object lock=new Object();

    private CalendarUtil() {

    }

    //DCL双检查锁
    public static CalendarUtil getInstance() {
        if (Objects.isNull(calendarUtil)) {
            synchronized (CalendarUtil.class) {
                if (Objects.isNull(calendarUtil)) {
                    calendarUtil = new CalendarUtil();
                }
            }
        }
        return calendarUtil;
    }

    /**
     * 根据一个日期串获得Date实例，支持的日期串形式:
     *  YYYY-MM-DD
     *  YYYY/MM/DD
     *  YYYY_MM_DD
     *  YYYYMMDD
     *  YYYY.MM.DD
     * @param dateStr
     * @return
     */
    public Date getDate(String dateStr){
        Calendar calendar=Calendar.getInstance();
        if(RegexUtil.isDate(dateStr)){
            int year=1971;
            int month=1;
            int day=1;
            if(dateStr.length()==8){
                year=Integer.parseInt(dateStr.substring(0, 4));
                month=Integer.parseInt(dateStr.substring(4, 6));
                day=Integer.parseInt(dateStr.substring(6, 8));
            }else{
                year=Integer.parseInt(dateStr.substring(0, 4));
                month=Integer.parseInt(dateStr.substring(5, 7));
                day=Integer.parseInt(dateStr.substring(8, 10));
            }
            calendar.clear();
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month-1);
            calendar.set(Calendar.DAY_OF_MONTH,day);
        }
        return calendar.getTime();
    }

    /**
     * 得到线程安全的SimpleDateFormat实例
     * @param pattern
     * @return
     */
    public SimpleDateFormat getSafeInstance(String pattern){
        SimpleDateFormat sdf=threadLocal.get();
        if(sdf==null){
            synchronized (lock){
                if(sdf==null){
                    sdf=new SimpleDateFormat(pattern);
                    threadLocal.set(sdf);
                }
            }
        }
        return sdf;
    }
}